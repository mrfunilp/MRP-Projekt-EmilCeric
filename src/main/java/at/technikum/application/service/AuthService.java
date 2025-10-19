package at.technikum.application.service;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthService {
    private static AuthService instance;

    private static Map<String, String> users = new HashMap<>(); // username -> hashedPassword
    private static Map<String, String> tokens = new HashMap<>(); // token -> username

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean register(String username, String password) {

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }

        if (users.containsKey(username)) {
            return false;
        }

        String hashedPassword = hashPassword(password);

        users.put(username, hashedPassword);
        return true;
    }

    public String login(String username, String password) {

        if (username == null || password == null) {
            return null;
        }

        String storedHash = users.get(username);

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

    public boolean validateToken(String token) {
        boolean isValid = token != null && tokens.containsKey(token);
        return isValid;
    }

    public String getUsernameFromToken(String token) {
        return tokens.get(token);
    }

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