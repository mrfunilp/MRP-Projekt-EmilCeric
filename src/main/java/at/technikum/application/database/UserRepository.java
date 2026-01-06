package at.technikum.application.database;


import at.technikum.application.model.UserProfile;
import at.technikum.server.util.DatabaseConnection;
import java.sql.*;
import at.technikum.application.model.LeaderboardEntry;
import java.util.List;
import java.util.ArrayList;

public class UserRepository {

    public UserProfile getUserProfile(String username) {
        //Dreifach Anführungszeichen wegen Textblock
        String sql = """
            WITH user_stats AS (
                SELECT 
                    u.username,
                    COUNT(DISTINCT r.id) as total_ratings,
                    COALESCE(AVG(r.stars), 0) as avg_score,
                    COUNT(DISTINCT m.id) as media_created,
                    COUNT(DISTINCT f.media_id) as favorites_count
                FROM users u
                LEFT JOIN ratings r ON u.username = r.username
                LEFT JOIN media_entries m ON u.username = m.creator_username
                LEFT JOIN favorites f ON u.username = f.username
                WHERE u.username = ?
                GROUP BY u.username
            ),
            favorite_genre AS (
                SELECT mg.genre, COUNT(*) as count 
                FROM ratings r 
                JOIN media_genres mg ON r.media_id = mg.media_id 
                WHERE r.username = ? 
                GROUP BY mg.genre 
                ORDER BY count DESC 
                LIMIT 1
            )
            SELECT 
                us.*,
                fg.genre as favorite_genre
            FROM user_stats us
            LEFT JOIN favorite_genre fg ON 1=1
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserProfile(
                        username,
                        rs.getInt("total_ratings"),
                        Math.round(rs.getDouble("avg_score") * 10.0) / 10.0, // 1 Dezimalstelle
                        rs.getString("favorite_genre"),
                        rs.getInt("media_created"),
                        rs.getInt("favorites_count")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createUser(String username, String passwordHash) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, passwordHash);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace(); //Druckt alle Errors vom Callstack (="Aufrufverflauf")
            return false;
        }
    }

    public String getPasswordHash(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("password_hash");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LeaderboardEntry> getLeaderboard() {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        //Zählt nur Ratings pro User
        String sql = "SELECT username, COUNT(*) as rating_count " +
                "FROM ratings " +
                "GROUP BY username " +
                "ORDER BY rating_count DESC, username";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LeaderboardEntry entry = new LeaderboardEntry(
                        rs.getString("username"),
                        rs.getInt("rating_count")
                );
                leaderboard.add(entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return leaderboard;
    }
}