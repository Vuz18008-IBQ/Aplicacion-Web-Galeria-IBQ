package pelayo.proyecto.galeiraibq.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pelayo.proyecto.galeiraibq.filter.JwtAuthFilter;

import java.util.List;

@Configuration           // Le dice a Spring que esta clase contiene configuración
@EnableWebSecurity       // Activa Spring Security en la aplicación
@RequiredArgsConstructor // Lombok: genera constructor para los campos final (inyección de dependencias)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    // Filtro personalizado

    private final UserDetailsService userDetailsService;
    // Spring Security usa esto para cargar usuarios desde BD

    @Bean
    // @Bean le dice a Spring que el objeto retornado debe ser gestionado por el contenedor
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // Desactivamos CSRF porque usamos JWT, no cookies de sesión
                // CSRF protege contra ataques que explotan cookies — con JWT en headers no aplica

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Configuramos CORS (lo definiremos en un método aparte)

                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/obras/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/autores/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/tecnicas/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/materiales/**").permitAll()
                                .anyRequest().hasRole("ADMIN")
                )

                .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        // STATELESS: no usar sesiones HTTP
                        // Cada petición debe autenticarse por sí sola mediante JWT
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // Insertamos nuestro filtro JWT ANTES del filtro estándar de Spring
        // Así, cuando llegue una petición con JWT, la procesamos nosotros primero

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // Le decimos a Spring qué algoritmo usar para hashear contraseñas
        // Spring Security lo inyectará automáticamente donde lo necesitemos
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
        // AuthenticationManager es el componente que verifica credenciales (email + password)
        // Spring Boot lo configura automáticamente, solo necesitamos exponerlo como Bean
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // Solo permitimos peticiones desde nuestro frontend Angular
        // En producción, aquí iría tu dominio real

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // Permitimos el header Authorization (donde irá el JWT)

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
