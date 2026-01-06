package at.technikum.application.database;

import at.technikum.server.util.DatabaseConnection;
import at.technikum.application.model.MediaEntry;
import at.technikum.application.model.MediaSearch;
import at.technikum.application.model.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaRepository {

    public boolean createMedia(MediaEntry media) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Transaction starten

            // 1. Media-Entry speichern
            String mediaSql = "INSERT INTO media_entries (id, title, description, media_type, release_year, age_restriction, creator_username) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(mediaSql)) {
                stmt.setString(1, media.getId());
                stmt.setString(2, media.getTitle());
                stmt.setString(3, media.getDescription());
                stmt.setString(4, media.getMediaType().name());
                stmt.setInt(5, media.getReleaseYear());
                stmt.setInt(6, media.getAgeRestriction());
                stmt.setString(7, media.getCreatorUsername());
                stmt.executeUpdate();
            }

            // 2. Genres speichern, weil List
            String genreSql = "INSERT INTO media_genres (media_id, genre) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(genreSql)) {
                for (String genre : media.getGenres()) {
                    stmt.setString(1, media.getId());
                    stmt.setString(2, genre);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public MediaEntry getMediaById(String mediaId) {
        String mediaSql = "SELECT * FROM media_entries WHERE id = ?";
        String genreSql = "SELECT genre FROM media_genres WHERE media_id = ? ORDER BY genre";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement mediaStmt = conn.prepareStatement(mediaSql);
             PreparedStatement genreStmt = conn.prepareStatement(genreSql)) {

            // 1. Media-Entry laden
            mediaStmt.setString(1, mediaId);
            ResultSet mediaRs = mediaStmt.executeQuery();

            if (mediaRs.next()) {
                MediaEntry media = mapResultSetToMedia(mediaRs);

                // 2. Genres laden
                genreStmt.setString(1, mediaId);
                ResultSet genreRs = genreStmt.executeQuery();

                List<String> genres = new ArrayList<>();
                while (genreRs.next()) {
                    genres.add(genreRs.getString("genre"));
                }
                media.setGenres(genres);

                return media;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MediaEntry> getAllMedia() {
        List<MediaEntry> mediaList = new ArrayList<>();
        String mediaSql = "SELECT * FROM media_entries ORDER BY title";
        String genreSql = "SELECT mg.media_id, mg.genre FROM media_genres mg " +
                "JOIN media_entries me ON mg.media_id = me.id " +
                "ORDER BY mg.media_id, mg.genre";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement mediaStmt = conn.createStatement();
             PreparedStatement genreStmt = conn.prepareStatement(genreSql)) {

            ResultSet mediaRs = mediaStmt.executeQuery(mediaSql);

            ResultSet genreRs = genreStmt.executeQuery();
            java.util.Map<String, List<String>> genreMap = new java.util.HashMap<>();

            while (genreRs.next()) {
                String mediaId = genreRs.getString("media_id");
                String genre = genreRs.getString("genre");

                genreMap.computeIfAbsent(mediaId, k -> new ArrayList<>()).add(genre);
            }

            while (mediaRs.next()) {
                MediaEntry media = mapResultSetToMedia(mediaRs);
                String mediaId = media.getId();

                if (genreMap.containsKey(mediaId)) {
                    media.setGenres(genreMap.get(mediaId));
                }

                mediaList.add(media);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mediaList;
    }

    public boolean updateMedia(MediaEntry media) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            String mediaSql = "UPDATE media_entries SET title = ?, description = ?, media_type = ?, release_year = ?, age_restriction = ? WHERE id = ? AND creator_username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(mediaSql)) {
                stmt.setString(1, media.getTitle());
                stmt.setString(2, media.getDescription());
                stmt.setString(3, media.getMediaType().name());
                stmt.setInt(4, media.getReleaseYear());
                stmt.setInt(5, media.getAgeRestriction());
                stmt.setString(6, media.getId());
                stmt.setString(7, media.getCreatorUsername());

                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            String deleteGenresSql = "DELETE FROM media_genres WHERE media_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteGenresSql)) {
                stmt.setString(1, media.getId());
                stmt.executeUpdate();
            }

            if (media.getGenres() != null && !media.getGenres().isEmpty()) {
                String insertGenreSql = "INSERT INTO media_genres (media_id, genre) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertGenreSql)) {
                    for (String genre : media.getGenres()) {
                        stmt.setString(1, media.getId());
                        stmt.setString(2, genre);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean deleteMedia(String mediaId, String username) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String deleteFavoritesSql = "DELETE FROM favorites WHERE media_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteFavoritesSql)) {
                stmt.setString(1, mediaId);
                stmt.executeUpdate();
            }

            String deleteRatingsSql = "DELETE FROM ratings WHERE media_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteRatingsSql)) {
                stmt.setString(1, mediaId);
                stmt.executeUpdate();
            }

            String deleteGenresSql = "DELETE FROM media_genres WHERE media_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteGenresSql)) {
                stmt.setString(1, mediaId);
                stmt.executeUpdate();
            }


            String deleteMediaSql = "DELETE FROM media_entries WHERE id = ? AND creator_username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteMediaSql)) {
                stmt.setString(1, mediaId);
                stmt.setString(2, username);

                int deleted = stmt.executeUpdate();
                if (deleted == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public List<MediaEntry> searchMedia(MediaSearch search) {
        List<MediaEntry> mediaList = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT DISTINCT m.* 
        FROM media_entries m
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

        if (search.getTitle() != null && !search.getTitle().trim().isEmpty()) {
            sql.append(" AND LOWER(m.title) LIKE LOWER(?)");
            params.add("%" + search.getTitle() + "%");
        }

        if (search.getMediaType() != null) {
            sql.append(" AND m.media_type = ?");
            params.add(search.getMediaType().name());
        }

        if (search.getAgeRestriction() != null) {
            sql.append(" AND m.age_restriction <= ?");
            params.add(search.getAgeRestriction());
        }

        if (search.getGenre() != null && !search.getGenre().trim().isEmpty()) {
            sql.append(" AND EXISTS (SELECT 1 FROM media_genres mg WHERE mg.media_id = m.id AND mg.genre = ?)");
            params.add(search.getGenre());
        }

        if (search.getMinRating() != null) {
            sql.append(" AND (SELECT COALESCE(AVG(r.stars), 0) FROM ratings r WHERE r.media_id = m.id AND r.comment_confirmed = true) >= ?");
            params.add(search.getMinRating());
        }

        sql.append(" ORDER BY m.title");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof Double) {
                    stmt.setDouble(i + 1, (Double) param);
                }
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MediaEntry media = mapResultSetToMedia(rs);

                List<String> genres = new ArrayList<>();
                String genreSql = "SELECT genre FROM media_genres WHERE media_id = ? ORDER BY genre";
                try (PreparedStatement genreStmt = conn.prepareStatement(genreSql)) {
                    genreStmt.setString(1, media.getId());
                    ResultSet genreRs = genreStmt.executeQuery();

                    while (genreRs.next()) {
                        genres.add(genreRs.getString("genre"));
                    }
                }
                media.setGenres(genres);

                mediaList.add(media);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mediaList;
    }

    private MediaEntry mapResultSetToMedia(ResultSet rs) throws SQLException {
        MediaEntry media = new MediaEntry();
        media.setId(rs.getString("id"));
        media.setTitle(rs.getString("title"));
        media.setDescription(rs.getString("description"));
        media.setMediaType(MediaType.valueOf(rs.getString("media_type")));
        media.setReleaseYear(rs.getInt("release_year"));
        media.setAgeRestriction(rs.getInt("age_restriction"));
        media.setCreatorUsername(rs.getString("creator_username"));
        // Genres werden separat geladen
        return media;
    }
}