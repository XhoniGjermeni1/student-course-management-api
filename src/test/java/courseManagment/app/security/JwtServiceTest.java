package courseManagment.app.security;

import courseManagment.app.user.entity.Role;
import courseManagment.app.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "c3VwZXItc2VjdXJlLWRldmVsb3BtZW50LWp3dC1zZWNyZXQta2V5LWZvci1zdHVkZW50LWFwaS0zMi1ieXRlcw==");
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 900000L);
    }

    @Test
    void shouldGenerateTokenAndExtractUsername() {
        User user = createUser("john");

        String token = jwtService.generateAccessToken(user);

        assertNotNull(token);
        assertEquals("john", jwtService.extractUsername(token));
    }

    @Test
    void shouldValidateTokenForSameUser() {
        User user = createUser("john");
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("john")
                .password("password")
                .authorities("ROLE_STUDENT")
                .build();

        String token = jwtService.generateAccessToken(user);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldRejectTokenForDifferentUser() {
        User user = createUser("john");
        UserDetails anotherUser = org.springframework.security.core.userdetails.User
                .withUsername("anna")
                .password("password")
                .authorities("ROLE_STUDENT")
                .build();

        String token = jwtService.generateAccessToken(user);

        assertFalse(jwtService.isTokenValid(token, anotherUser));
    }

    private User createUser(String username) {
        return new User(username, "password", username + "@example.com", Role.STUDENT);
    }
}
