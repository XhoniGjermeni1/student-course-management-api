package courseManagment.app.token;

import courseManagment.app.exception.InvalidTokenException;
import courseManagment.app.user.entity.Role;
import courseManagment.app.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", 604800000L);
    }

    @Test
    void shouldCreateRefreshToken() {
        User user = createUser();
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        assertNotNull(refreshToken.getToken());
        assertSame(user, refreshToken.getUser());
        assertFalse(refreshToken.isRevoked());
        assertTrue(refreshToken.getExpiresAt().isAfter(LocalDateTime.now()));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldValidateActiveRefreshToken() {
        RefreshToken refreshToken = createActiveRefreshToken(createUser(), "token");
        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.validateRefreshToken("token");

        assertSame(refreshToken, result);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenIsExpired() {
        RefreshToken refreshToken = createActiveRefreshToken(createUser(), "token");
        refreshToken.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(refreshToken));

        assertThrows(InvalidTokenException.class, () -> refreshTokenService.validateRefreshToken("token"));
    }

    @Test
    void shouldRevokeRefreshToken() {
        RefreshToken refreshToken = createActiveRefreshToken(createUser(), "token");
        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(refreshToken));

        refreshTokenService.revokeRefreshToken("token");

        assertTrue(refreshToken.isRevoked());
        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void shouldRotateRefreshToken() {
        User user = createUser();
        RefreshToken oldRefreshToken = createActiveRefreshToken(user, "old-token");
        when(refreshTokenRepository.findByToken("old-token")).thenReturn(Optional.of(oldRefreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken("old-token");

        assertTrue(oldRefreshToken.isRevoked());
        assertSame(user, newRefreshToken.getUser());
        assertNotNull(newRefreshToken.getToken());
    }

    private RefreshToken createActiveRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);
        return refreshToken;
    }

    private User createUser() {
        return new User("john", "password", "john@example.com", Role.STUDENT);
    }
}
