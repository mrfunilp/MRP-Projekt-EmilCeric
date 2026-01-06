package at.technikum.server.util;

public class TokenUtility {
    public static String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return null;
        }

        // Entferne "Bearer " falls vorhanden
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return authHeader.trim();
    }

    public static boolean isValidAuthHeader(String authHeader) {
        return authHeader != null;
    }
}