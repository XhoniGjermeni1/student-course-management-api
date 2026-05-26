package courseManagment.app.token;

import courseManagment.app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    @Modifying
    @Transactional
    @Query("delete from RefreshToken rt where rt.expiresAt < :now or rt.revoked = true")
    int deleteExpiredOrRevokedTokens(LocalDateTime now);
}
