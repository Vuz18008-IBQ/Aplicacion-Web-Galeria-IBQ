package pelayo.proyecto.galeiraibq.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pelayo.proyecto.galeiraibq.service.JwtService;

import java.io.IOException;

@Component           // Spring lo registra como componente
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter garantiza que este filtro se ejecuta UNA sola vez por petición
    // Aunque la cadena de filtros pueda invocar el mismo filtro varias veces internamente

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Leer el header Authorization de la petición
        final String authHeader = request.getHeader("Authorization");

        // 2. Si no hay header o no empieza por "Bearer ", dejamos pasar sin autenticar
        //    El filtro de autorización posterior se encargará de rechazar si el endpoint lo requiere
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            // doFilter pasa la petición al siguiente filtro de la cadena
            return;
        }

        // 3. Extraer el token quitando el prefijo "Bearer "
        //    "Bearer eyJhbGc..." → "eyJhbGc..."
        final String jwt = authHeader.substring(7);

        // 4. Extraer el username (email) del payload del token
        //    Si el token está malformado, expirado o con firma inválida saltará una excepción
        //    En ese caso no autenticamos: el AuthorizationFilter posterior devolverá 401 si toca
        try {
            final String userEmail = jwtService.extractUsername(jwt);

            // 5. Si hay email y el usuario NO está ya autenticado en este contexto
            if (userEmail != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Cargar el usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // 7. Validar el token
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // 8. Crear el objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // 9. Añadir detalles de la petición (IP, session ID, etc.)
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 10. Registrar la autenticación en el SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token invalido, expirado, manipulado o usuario no encontrado.
            // No autenticamos y seguimos: la ruta decidira si exige login.
        }

        // 11. Continuar con el siguiente filtro de la cadena
        filterChain.doFilter(request, response);
    }
}
