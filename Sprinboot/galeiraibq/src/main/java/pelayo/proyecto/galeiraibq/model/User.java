package pelayo.proyecto.galeiraibq.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity                          // Le dice a JPA que esta clase mapea a una tabla
@Table(name = "users")           // El nombre de la tabla en MySQL
@Data                            // Lombok: genera getters, setters, equals, hashCode, toString
@NoArgsConstructor               // Lombok: constructor vacío — JPA lo requiere obligatoriamente
@AllArgsConstructor              // Lombok: constructor con todos los campos
@Builder                         // Lombok: permite crear objetos con patrón Builder
public class User implements UserDetails {
    // UserDetails es la interfaz de Spring Security que representa un usuario autenticable
    // Al implementarla, Spring Security sabe cómo trabajar con nuestra entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // IDENTITY le dice a JPA que MySQL gestionará el auto_increment
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;     // Aquí irá el hash BCrypt, nunca texto plano

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    // EAGER significa: cuando cargues un User, carga también sus roles inmediatamente
    // Para roles, EAGER es aceptable porque los necesitamos en cada verificación de permisos
    @JoinTable(
            name = "user_roles",                          // tabla intermedia
            joinColumns = @JoinColumn(name = "user_id"),  // columna que apunta a users
            inverseJoinColumns = @JoinColumn(name = "role_id") // columna que apunta a roles
    )
    private Set<Role> roles = new HashSet<>();

    // ── Métodos que exige la interfaz UserDetails ──────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security llama a este método para saber los permisos del usuario
        // Convertimos nuestros Role en GrantedAuthority, que es lo que Spring entiende
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return email;            // Usamos el email como "username"
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
    // Este es el campo enabled de nuestra tabla — si es false, Spring rechaza el login
}
