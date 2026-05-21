package pelayo.proyecto.galeiraibq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pelayo.proyecto.galeiraibq.model.RefreshToken;
import pelayo.proyecto.galeiraibq.model.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
        // @Modifying indica que esta query modifica datos (no es un SELECT)
        // @Query nos permite escribir JPQL personalizado cuando el nombre del método no es suficiente
    void revokeAllUserTokens(User user);
}
