package courseManagment.app.token;

import courseManagment.app.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(generateSecureToken());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(()-> new RuntimeException("Refresh token found"));

        if(!refreshToken.isActive()){
            throw new RuntimeException("Runtime token is expired or revoked");
        }
        return refreshToken;
    }

    private String generateSecureToken(){
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes((randomBytes));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }


    @Transactional
    public void revokeRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(()-> new RuntimeException("Refresh token not found"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String token){
        RefreshToken oldRefreshToken = validateRefreshToken(token);

        oldRefreshToken.setRevoked(true);
        refreshTokenRepository.save(oldRefreshToken);

        return createRefreshToken(oldRefreshToken.getUser());
    }
    @Transactional
    public int cleanupExpiredOrRevokedTokens() {
        return refreshTokenRepository.deleteExpiredOrRevokedTokens(LocalDateTime.now());
    }

}
