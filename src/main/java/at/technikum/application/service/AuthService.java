package at.technikum.application.service;

import at.technikum.application.database.UserRepository;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthService implements AuthServiceInterface {
    private UserRepository userRepository;
    private Map<String, String> tokens = new HashMap<>(); // token -> username

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }

        if (userRepository.userExists(username)) {
            return false;
        }

        String hashedPassword = hashPassword(password);
        return userRepository.createUser(username, hashedPassword);
    }

    @Override
    public String login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        String storedHash = userRepository.getPasswordHash(username);
        if (storedHash == null) {
            return null;
        }

        String inputHash = hashPassword(password);
        if (!storedHash.equals(inputHash)) {
            return null;
        }

        String token = generateToken(username);
        tokens.put(token, username);
        return token;
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.userExists(username);
    }

    @Override
    public boolean validateToken(String token) {
        return tokens.containsKey(token);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return tokens.get(token);
    }

    @Override
    public void logout(String token) {
        tokens.remove(token);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    private String generateToken(String username) {
        return username + "-mrpToken-" + UUID.randomUUID().toString().substring(0, 8);
    }
}