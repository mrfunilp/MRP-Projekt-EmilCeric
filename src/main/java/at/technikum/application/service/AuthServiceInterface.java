package at.technikum.application.service;

public interface AuthServiceInterface {
    boolean register(String username, String password);
    String login(String username, String password);
    boolean userExists(String username);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
    void logout(String token);
}