package at.technikum.application.controller;

import at.technikum.application.model.MediaEntry;
import at.technikum.application.model.Rating;
import at.technikum.application.service.AuthServiceInterface;
import at.technikum.application.service.MediaServiceInterface;
import at.technikum.application.service.RatingServiceInterface;
import at.technikum.application.service.FavoriteServiceInterface;
import at.technikum.application.model.MediaSearch;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import java.util.List;

public class MediaController extends Controller {
    private MediaServiceInterface mediaService;
    private AuthServiceInterface authService;
    private RatingServiceInterface ratingService;
    private FavoriteServiceInterface favoriteService;

    // Constructor Injection
    public MediaController(MediaServiceInterface mediaService,
                           AuthServiceInterface authService,
                           RatingServiceInterface ratingService,
                           FavoriteServiceInterface favoriteService) {
        this.mediaService = mediaService;
        this.authService = authService;
        this.ratingService = ratingService;
        this.favoriteService = favoriteService;
    }

    @Override
    public Response handle(Request request) {
        try {
            String path = request.getPath();
            String method = request.getMethod();

            System.out.println("MediaController handling: " + method + " " + path);

            if (path.equals("/media")) {
                if (method.equals("POST")) {
                    return createMedia(request);
                } else if (method.equals("GET")) {
                    return getAllMedia(request);
                }
            }
            else if (path.equals("/media/search")) {
                if (method.equals("POST")) {
                    return handleSearch(request);
                }
            }
            else if (path.startsWith("/media/") && !path.contains("/favorite") && !path.contains("/ratings")) {
                String mediaId = extractIdFromPath(path, "/media/");

                if (method.equals("GET")) {
                    return getMedia(request);
                } else if (method.equals("PUT")) {
                    return updateMedia(request);
                } else if (method.equals("DELETE")) {
                    return deleteMedia(request);
                }
            }
            else if (path.contains("/favorite") && path.startsWith("/media/")) {
                return handleFavorite(request);
            }
            else if (path.contains("/ratings") && path.startsWith("/media/")) {
                if (method.equals("GET")) {
                    return getMediaRatings(request);
                }
            }
            else if (path.equals("/ratings")) {
                if (method.equals("POST")) {
                    return createRating(request);
                }
            }
            else if (path.startsWith("/ratings/") && !path.endsWith("/like") && !path.endsWith("/confirm")) {
                if (method.equals("PUT")) {
                    return updateRating(request);
                } else if (method.equals("DELETE")) {
                    return deleteRating(request);
                }
            }
            else if (path.endsWith("/like")) {
                if (method.equals("POST")) {
                    return likeRating(request);
                }
            }
            else if (path.endsWith("/confirm")) {
                if (method.equals("POST")) {
                    return confirmRatingComment(request);
                }
            }
            else if (path.contains("/favorites") && !path.contains("/media/") && method.equals("GET")) {
                return getUserFavorites(request);
            }

            return text("Not found", Status.NOT_FOUND);

        } catch (Exception e) {
            e.printStackTrace();
            return text("Error: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }
    private Response handleSearch(Request request) throws Exception {
        String username = validateToken(request);
        if (username == null) return unauthorized();

        // Search-Kriterien aus Request Body (JSON)
        MediaSearch search = toObject(request.getBody(), MediaSearch.class);

        System.out.println("Search request with: " + search);

        List<MediaEntry> results = mediaService.searchMedia(search);
        return json(results, Status.OK);
    }

    private Response createMedia(Request request) throws Exception {
        String username = validateToken(request);
        if (username == null) return unauthorized();

        MediaEntry media = toObject(request.getBody(), MediaEntry.class);
        media.setCreatorUsername(username);

        MediaEntry created = mediaService.createMedia(media);
        return json(created, Status.CREATED);
    }

    private Response getMedia(Request request) {
        try {
            String mediaId = extractIdFromPath(request.getPath(), "/media/");
            MediaEntry media = mediaService.getMedia(mediaId);

            if (media == null) {
                return text("Media not found", Status.NOT_FOUND);
            }
            return json(media, Status.OK);
        } catch (Exception e) {
            return text("Error getting media: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response getAllMedia(Request request) {
        try {
            return json(mediaService.getAllMedia(), Status.OK);
        } catch (Exception e) {
            return text("Error getting media: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response updateMedia(Request request) throws Exception {
        String username = validateToken(request);
        if (username == null) return unauthorized();

        String mediaId = extractIdFromPath(request.getPath(), "/media/");
        MediaEntry updatedMedia = toObject(request.getBody(), MediaEntry.class);

        MediaEntry result = mediaService.updateMedia(mediaId, updatedMedia, username);
        if (result == null) {
            return text("Media not found or not authorized", Status.BAD_REQUEST);
        }
        return json(result, Status.OK);
    }

    private Response deleteMedia(Request request) {
        try {
            String username = validateToken(request);
            if (username == null) return unauthorized();

            String mediaId = extractIdFromPath(request.getPath(), "/media/");
            boolean success = mediaService.deleteMedia(mediaId, username);

            if (!success) {
                return text("Media not found or not authorized", Status.BAD_REQUEST);
            }
            return text("Media deleted successfully", Status.OK);
        } catch (Exception e) {
            return text("Error deleting media: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response createRating(Request request) throws Exception {
        String username = validateToken(request);
        if (username == null) return unauthorized();

        Rating rating = toObject(request.getBody(), Rating.class);
        rating.setUsername(username);

        if (rating.getMediaId() == null && request.getPath().startsWith("/ratings/media/")) {
            String path = request.getPath();
            String mediaId = path.substring("/ratings/media/".length());
            rating.setMediaId(mediaId);
        }

        Rating created = ratingService.createRating(rating);
        if (created == null) {
            return text("Could not create rating. Media not found or already rated.", Status.BAD_REQUEST);
        }
        return json(created, Status.CREATED);
    }

    private Response getMediaRatings(Request request) {
        try {
            String mediaId = extractIdFromPath(request.getPath(), "/media/");
            mediaId = mediaId.replace("/ratings", "");

            return json(ratingService.getRatingsByMedia(mediaId), Status.OK);
        } catch (Exception e) {
            return text("Error getting ratings: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response updateRating(Request request) throws Exception {
        String username = validateToken(request);
        if (username == null) return unauthorized();

        String path = request.getPath();
        String ratingId = path.substring("/ratings/".length());
        Rating rating = toObject(request.getBody(), Rating.class);
        rating.setId(ratingId);

        Rating updated = ratingService.updateRating(rating, username);
        if (updated == null) {
            return text("Rating not found or not authorized", Status.BAD_REQUEST);
        }
        return json(updated, Status.OK);
    }

    private Response deleteRating(Request request) {
        try {
            String username = validateToken(request);
            if (username == null) return unauthorized();

            String path = request.getPath();
            String ratingId = path.substring("/ratings/".length());
            boolean success = ratingService.deleteRating(ratingId, username);

            if (!success) {
                return text("Rating not found or not authorized", Status.BAD_REQUEST);
            }
            return text("Rating deleted successfully", Status.OK);
        } catch (Exception e) {
            return text("Error deleting rating: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response likeRating(Request request) {
        try {
            String username = validateToken(request);
            if (username == null) return unauthorized();

            String path = request.getPath();
            String ratingId = path.substring("/ratings/".length());
            ratingId = ratingId.replace("/like", "");

            boolean success = ratingService.likeRating(ratingId);
            if (!success) {
                return text("Rating not found", Status.NOT_FOUND);
            }
            return text("Rating liked successfully", Status.OK);
        } catch (Exception e) {
            return text("Error liking rating: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response confirmRatingComment(Request request) {
        try {
            String username = validateToken(request);
            if (username == null) return unauthorized();

            String path = request.getPath();
            String ratingId = path.substring("/ratings/".length());
            ratingId = ratingId.replace("/confirm", "");

            boolean success = ratingService.confirmComment(ratingId, username);
            if (!success) {
                return text("Rating not found or not authorized", Status.BAD_REQUEST);
            }
            return text("Comment confirmed", Status.OK);
        } catch (Exception e) {
            return text("Error confirming comment: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response handleFavorite(Request request) { //URL mit leeren
        try {
            String username = validateToken(request);
            if (username == null) return unauthorized();

            String path = request.getPath();
            String mediaId = path.substring("/media/".length());
            mediaId = mediaId.replace("/favorite", "");

            String method = request.getMethod();

            if (method.equals("POST")) {
                boolean success = favoriteService.addFavorite(username, mediaId);
                if (!success) {
                    return text("Media not found or already favorited", Status.BAD_REQUEST);
                }
                return text("Media added to favorites", Status.OK);
            } else if (method.equals("DELETE")) {
                boolean success = favoriteService.removeFavorite(username, mediaId);
                if (!success) {
                    return text("Media not found in favorites", Status.BAD_REQUEST);
                }
                return text("Media removed from favorites", Status.OK);
            } else if (method.equals("GET")) {
                boolean isFavorite = favoriteService.isFavorite(username, mediaId);
                return json(isFavorite, Status.OK);
            }

            return text("Method not allowed", Status.BAD_REQUEST);
        } catch (Exception e) {
            return text("Error handling favorite: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response getUserFavorites(Request request) {
        try {
            String username = validateToken(request);
            if (username == null) return unauthorized();

            return json(favoriteService.getUserFavorites(username), Status.OK);
        } catch (Exception e) {
            return text("Error getting favorites: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private String validateToken(Request request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            return null;
        }

        String token = authHeader;
        boolean isValid = authService.validateToken(token);

        if (!isValid) {
            return null;
        }

        return authService.getUsernameFromToken(token);
    }

    private Response unauthorized() {
        return text("Unauthorized - valid token required", Status.BAD_REQUEST);
    }

    private String extractIdFromPath(String path, String prefix) {
        return path.substring(prefix.length());
    }
}