package pelayo.proyecto.galeiraibq.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pelayo.proyecto.galeiraibq.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    // Spring Data JPA genera la query automáticamente a partir del nombre del método
    // Equivale a: SELECT * FROM users WHERE email = ?

    boolean existsByEmail(String email);
    // Para verificar si un email ya está registrado sin cargar el objeto entero
}
