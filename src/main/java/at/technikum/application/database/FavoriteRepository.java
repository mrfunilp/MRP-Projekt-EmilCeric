package at.technikum.application.database;

import at.technikum.server.util.DatabaseConnection;
import at.technikum.application.model.MediaEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriteRepository {

    public boolean addFavorite(String username, String mediaId) {
        String sql = "INSERT INTO favorites (username, media_id) VALUES (?, ?) ON CONFLICT (username, media_id) DO NOTHING";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, mediaId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFavorite(String username, String mediaId) {
        String sql = "DELETE FROM favorites WHERE username = ? AND media_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, mediaId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isFavorite(String username, String mediaId) {
        String sql = "SELECT 1 FROM favorites WHERE username = ? AND media_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, mediaId);
            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<MediaEntry> getUserFavorites(String username) {
        List<MediaEntry> favorites = new ArrayList<>();
        String sql = "SELECT m.* FROM media_entries m " +
                "JOIN favorites f ON m.id = f.media_id " +
                "WHERE f.username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MediaEntry media = new MediaEntry();
                media.setId(rs.getString("id"));
                media.setTitle(rs.getString("title"));
                media.setDescription(rs.getString("description"));
                media.setMediaType(at.technikum.application.model.MediaType.valueOf(rs.getString("media_type")));
                media.setReleaseYear(rs.getInt("release_year"));
                media.setAgeRestriction(rs.getInt("age_restriction"));
                media.setCreatorUsername(rs.getString("creator_username"));
                favorites.add(media);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favorites;
    }

    public int getFavoriteCount(String mediaId) {
        String sql = "SELECT COUNT(*) as count FROM favorites WHERE media_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mediaId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
