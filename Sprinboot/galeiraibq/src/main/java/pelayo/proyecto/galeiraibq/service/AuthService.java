package pelayo.proyecto.galeiraibq.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pelayo.proyecto.galeiraibq.model.RefreshToken;
import pelayo.proyecto.galeiraibq.model.Role;
import pelayo.proyecto.galeiraibq.model.User;
import pelayo.proyecto.galeiraibq.repository.RefreshTokenRepository;
import pelayo.proyecto.galeiraibq.repository.RoleRepository;
import pelayo.proyecto.galeiraibq.repository.UserRepository;
import pelayo.proyecto.galeiraibq.requestDTO.LoginRequest;
import pelayo.proyecto.galeiraibq.requestDTO.RegisterRequest;
import pelayo.proyecto.galeiraibq.responseDTO.AuthResponse;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    // Spring inyecta el BCryptPasswordEncoder que definimos en SecurityConfig

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Transactional
    // Si algo falla en este método, toda la operación se revierte
    // No queremos un usuario creado sin rol, o con token sin usuario
    public AuthResponse register(RegisterRequest request) {

        // 1. Verificar que el email no esté ya registrado
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
            // En producción usarías una excepción personalizada
        }

        // 2. Buscar el rol por defecto
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado. ¿Ejecutaste el seed?"));

        // 3. Construir el usuario con la contraseña hasheada
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                // encode() aplica BCrypt — la contraseña nunca se guarda en texto plano
                .roles(Set.of(userRole))
                .enabled(true)
                .build();

        // 4. Persistir el usuario
        userRepository.save(user);

        // 5. Generar tokens y devolver respuesta
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {

        // 1. Delegar la verificación de credenciales a Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        // Si las credenciales son incorrectas, authenticate() lanza BadCredentialsException
        // Si el usuario está disabled, lanza DisabledException
        // No necesitamos comparar manualmente — Spring lo hace por nosotros

        // 2. Si llegamos aquí, las credenciales son válidas — cargar el usuario
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // 3. Revocar tokens anteriores (un usuario solo debería tener un refresh token activo)
        refreshTokenRepository.revokeAllUserTokens(user);

        // 4. Generar nuevos tokens
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshToken(String refreshTokenValue) {

        // 1. Buscar el refresh token en la BD
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh token no encontrado"));

        // 2. Verificar que no está revocado
        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revocado");
        }

        // 3. Verificar que no ha expirado
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expirado");
        }

        // 4. Obtener el usuario asociado y generar nuevo access token
        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateAccessToken(user);

        // Nota: devolvemos el mismo refresh token — no lo rotamos aquí por simplicidad
        // En producción implementarías Refresh Token Rotation por seguridad adicional
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenValue)
                .build();
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    // Soft delete — recordamos que el token existió y fue revocado
                    refreshTokenRepository.save(token);
                });
    }

    // ── Método privado compartido por register y login ────────────────────

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);

        // Generar un refresh token como UUID aleatorio
        // No usamos JWT para el refresh token porque queremos poder revocarlo en BD
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}