package at.technikum.application.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import at.technikum.application.database.UserRepository;

public class AuthServiceTest {
    private AuthService authService;
    private UserRepository userRepository;

    @Before
    public void setUp() {
        userRepository = new UserRepository();

        authService = new AuthService(userRepository);
    }

    @Test
    public void testRegisterValidUser() {
        String username = "testuser_" + System.currentTimeMillis();
        String password = "password123";

        boolean result = authService.register(username, password);

        assertTrue("Registration should succeed", result);
    }

    @Test
    public void testRegisterDuplicateUser() {
        String username = "duplicateuser";
        String password = "password123";

        authService.register(username, password);

        boolean result = authService.register(username, password);

        assertFalse("Duplicate registration should fail", result);
    }

    @Test
    public void testLoginValidCredentials() {
        String username = "loginuser";
        String password = "password123";
        authService.register(username, password);

        String token = authService.login(username, password);

        assertNotNull("Login should return token", token);
        assertTrue("Token should be valid", authService.validateToken(token));
    }

    @Test
    public void testLoginInvalidPassword() {
        String username = "loginuser2";
        String password = "password123";
        authService.register(username, password);

        String token = authService.login(username, "wrongpassword");

        assertNull("Login with wrong password should fail", token);
    }

    @Test
    public void testLogout() {
        String username = "logoutuser";
        String password = "password123";
        authService.register(username, password);
        String token = authService.login(username, password);

        authService.logout(token);

        assertFalse("Token should be invalid after logout", authService.validateToken(token));
    }

    @Test
    public void testGetUsernameFromValidToken() {
        String username = "tokenuser_" + System.currentTimeMillis();
        String password = "password123";
        authService.register(username, password);
        String token = authService.login(username, password);

        String retrievedUsername = authService.getUsernameFromToken(token);

        assertNotNull("Should retrieve username from token", retrievedUsername);
        assertEquals("Username should match", username, retrievedUsername);
    }

    @Test
    public void testGetUsernameFromInvalidToken() {
        String username = authService.getUsernameFromToken("invalid-token-12345");

        assertNull("Should return null for invalid token", username);
    }
}