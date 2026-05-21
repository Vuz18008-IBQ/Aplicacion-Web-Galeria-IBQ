package pelayo.proyecto.galeiraibq.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service  // Spring lo registra como un componente de servicio inyectable
@RequiredArgsConstructor
public class JwtService {


    @Value("${jwt.secret}")
    // @Value lee el valor desde application.properties
    // La clave secreta NUNCA debe estar hardcodeada en el código
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;  // en milisegundos, p.ej. 900000 (15 min)

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration; // p.ej. 604800000 (7 días)

    // ── Generar un Access Token ───────────────────────────────────────────

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        // Aquí puedes añadir datos adicionales al payload del JWT
        // Por ejemplo, los roles del usuario
        extraClaims.put("roles", userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return buildToken(extraClaims, userDetails, accessTokenExpiration);
    }

    // ── Construcción interna del token ────────────────────────────────────

    private String buildToken(Map<String, Object> extraClaims,
                              UserDetails userDetails,
                              long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                // Añade los claims personalizados (roles, etc.)

                .subject(userDetails.getUsername())
                // El "subject" es el identificador principal — usamos el email

                .issuedAt(new Date(System.currentTimeMillis()))
                // Fecha de emisión — el campo "iat" del payload

                .expiration(new Date(System.currentTimeMillis() + expiration))
                // Fecha de expiración — el campo "exp" del payload

                .signWith(getSigningKey())
                // Firma el token con nuestra clave secreta
                // Si alguien modifica el payload, esta firma no coincidirá

                .compact();
        // Construye y serializa el JWT como String
    }

    // ── Validar un token ──────────────────────────────────────────────────

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
        // El token es válido si el subject coincide con el usuario
        // Y si no ha expirado
    }

    // ── Extraer datos del token ───────────────────────────────────────────

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
        // Claims::getSubject es una referencia al método que obtiene el campo "sub"
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // Método genérico que extrae cualquier campo del payload
        // Function<Claims, T> es una función que recibe los claims y devuelve lo que quieras
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                // Le decimos con qué clave verificar la firma
                // Si la firma no coincide, lanza una excepción automáticamente
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ── Construir la clave de firma ───────────────────────────────────────

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // La clave secreta se guarda en Base64 en application.properties
        // Aquí la decodificamos a bytes
        return Keys.hmacShaKeyFor(keyBytes);
        // Creamos una clave HMAC-SHA a partir de esos bytes
        // HMAC-SHA256 es el algoritmo de firma que usaremos
    }
}
