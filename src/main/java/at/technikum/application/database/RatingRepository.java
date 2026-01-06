package at.technikum.application.database;

import at.technikum.server.util.DatabaseConnection;
import at.technikum.application.model.Rating;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingRepository {

    public boolean createRating(Rating rating) {
        String sql = "INSERT INTO ratings (id, media_id, username, stars, comment, comment_confirmed, likes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rating.getId());
            stmt.setString(2, rating.getMediaId());
            stmt.setString(3, rating.getUsername());
            stmt.setInt(4, rating.getStars());
            stmt.setString(5, rating.getComment());
            stmt.setBoolean(6, rating.isCommentConfirmed());
            stmt.setInt(7, rating.getLikes());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Rating getRatingById(String ratingId) {
        String sql = "SELECT * FROM ratings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ratingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRating(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Rating> getRatingsByMedia(String mediaId) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE media_id = ? " +
                "ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mediaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ratings.add(mapResultSetToRating(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public List<Rating> getRatingsByUser(String username) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE username = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ratings.add(mapResultSetToRating(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public Rating getUserRatingForMedia(String username, String mediaId) {
        String sql = "SELECT * FROM ratings WHERE username = ? AND media_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, mediaId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRating(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateRating(Rating rating) {
        String sql = "UPDATE ratings SET stars = ?, comment = ?, comment_confirmed = ?, likes = ? " +
                "WHERE id = ? AND username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rating.getStars());
            stmt.setString(2, rating.getComment());
            stmt.setBoolean(3, rating.isCommentConfirmed());
            stmt.setInt(4, rating.getLikes());
            stmt.setString(5, rating.getId());
            stmt.setString(6, rating.getUsername());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRating(String ratingId, String username) {
        String sql = "DELETE FROM ratings WHERE id = ? AND username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ratingId);
            stmt.setString(2, username);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean likeRating(String ratingId) {
        String sql = "UPDATE ratings SET likes = likes + 1 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ratingId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean confirmComment(String ratingId, String username) {
        String sql = "UPDATE ratings SET comment_confirmed = true WHERE id = ? AND username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ratingId);
            stmt.setString(2, username);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getAverageRating(String mediaId) {
        String sql = "SELECT AVG(stars) as average FROM ratings WHERE media_id = ? AND comment_confirmed = true";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mediaId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("average");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int getRatingCount(String mediaId) {
        String sql = "SELECT COUNT(*) as count FROM ratings WHERE media_id = ? AND comment_confirmed = true";

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

    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getString("id"));
        rating.setMediaId(rs.getString("media_id"));
        rating.setUsername(rs.getString("username"));
        rating.setStars(rs.getInt("stars"));
        rating.setComment(rs.getString("comment"));
        rating.setTimestamp(rs.getTimestamp("timestamp").toString());
        rating.setCommentConfirmed(rs.getBoolean("comment_confirmed"));
        rating.setLikes(rs.getInt("likes"));
        return rating;
    }
}
