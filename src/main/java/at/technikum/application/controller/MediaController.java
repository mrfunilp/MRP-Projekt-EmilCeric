package at.technikum.application.controller;

import at.technikum.application.model.MediaEntry;
import at.technikum.application.model.Rating;
import at.technikum.application.service.AuthService;
import at.technikum.application.service.MediaService;
import at.technikum.server.util.TokenUtility;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class MediaController extends Controller {
    private MediaService mediaService = MediaService.getInstance();
    private AuthService authService = AuthService.getInstance();

    @Override
    public Response handle(Request request) {
        try {
            String path = request.getPath();
            String method = request.getMethod();

           // Mediapaths
            if (path.equals("/media") && method.equals("POST")) {
                return createMedia(request);
            } else if (path.startsWith("/media/") && method.equals("GET")) {
                return getMedia(request);
            } else if (path.equals("/media") && method.equals("GET")) {
                return getAllMedia(request);
            } else if (path.startsWith("/media/") && method.equals("PUT")) {
                return updateMedia(request);
            } else if (path.startsWith("/media/") && method.equals("DELETE")) {
                return deleteMedia(request);
            }


            return text("Not found", Status.NOT_FOUND);
        } catch (Exception e) {
            return text("Error: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response createMedia(Request request) throws Exception {
        String username = validateToken(request); //header enthält token von user
        if (username == null) return unauthorized();

        MediaEntry media = toObject(request.getBody(), MediaEntry.class);
        media.setCreatorUsername(username);

        MediaEntry created = mediaService.createMedia(media);
        return json(created, Status.CREATED);
    }

    private Response getMedia(Request request) {
        try {
            String mediaId = extractIdFromPath(request.getPath(), "/media/"); //setzt path für spezifische id
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



    // könnte vielleicht in util veschoben werden (token validation)
    private String validateToken(Request request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (!TokenUtility.isValidAuthHeader(authHeader)) {
                return null;
            }
            String token = TokenUtility.extractTokenFromHeader(authHeader);
            if (!authService.validateToken(token)) {
                return null;
            }
            return authService.getUsernameFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    private Response unauthorized() {
        return text("Unauthorized - valid token required", Status.BAD_REQUEST);
    }

    private String extractIdFromPath(String path, String prefix) {
        return path.substring(prefix.length());
    }
    

}