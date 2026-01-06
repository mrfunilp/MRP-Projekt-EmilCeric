package at.technikum.application.controller;

import at.technikum.application.model.AuthRequest;
import at.technikum.application.model.UserProfile;
import at.technikum.application.model.LeaderboardEntry;
import at.technikum.application.model.AuthResponse;
import at.technikum.application.service.AuthServiceInterface;
import at.technikum.application.service.UserServiceInterface;
import at.technikum.server.util.TokenUtility;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import java.util.List;

public class UserController extends Controller {
    private AuthServiceInterface authService;
    private UserServiceInterface userService;

    // Constructor Injection
    public UserController(AuthServiceInterface authService, UserServiceInterface userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Override
    public Response handle(Request request) {
        try {
            String path = request.getPath();

            System.out.println("UserController handling path: " + path);

            if (path.equals("/users/register")) {
                return register(request);
            } else if (path.equals("/users/login")) {
                return login(request);
            } else if (path.equals("/users/profile")) {
                return getOwnProfile(request);
            } else if (path.startsWith("/users/") && path.endsWith("/profile")) {
                return getUserProfile(request);
            } else if (path.equals("/users/logout")) {
                return logout(request);
            } else if (path.equals("/leaderboard")) {
                return getLeaderboard(request);
            }

            return text("Not found", Status.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return text("Error: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response getOwnProfile(Request request) throws Exception {
        String authHeader = request.getHeader("Authorization");
        String token = TokenUtility.extractTokenFromHeader(authHeader);

        if (token == null || !authService.validateToken(token)) {
            return text("Invalid or expired token", Status.BAD_REQUEST);
        }

        String username = authService.getUsernameFromToken(token);
        System.out.println("Getting own profile for: " + username);

        UserProfile profile = userService.getUserProfile(username);
        if (profile == null) {
            return text("Could not load profile", Status.INTERNAL_SERVER_ERROR);
        }

        return json(profile, Status.OK);
    }

    private Response getUserProfile(Request request) throws Exception {
        String authHeader = request.getHeader("Authorization");
        String token = TokenUtility.extractTokenFromHeader(authHeader);

        if (token == null || !authService.validateToken(token)) {
            return text("Invalid or expired token", Status.BAD_REQUEST);
        }

        String path = request.getPath();
        System.out.println("Parsing path: " + path);

        if (path.length() <= "/users/".length() + "/profile".length()) {
            return text("Invalid profile path", Status.BAD_REQUEST);
        }

        String username = path.substring("/users/".length(), path.length() - "/profile".length());
        System.out.println("Getting profile for user: " + username);

        if (!authService.userExists(username)) {
            return text("User not found", Status.NOT_FOUND);
        }

        UserProfile profile = userService.getUserProfile(username);
        if (profile == null) {
            return text("Could not load profile", Status.INTERNAL_SERVER_ERROR);
        }

        return json(profile, Status.OK);
    }

    private Response register(Request request) throws Exception {
        AuthRequest authRequest = toObject(request.getBody(), AuthRequest.class);

        if (authRequest.getUsername() == null || authRequest.getPassword() == null) {
            return text("Username and password required", Status.BAD_REQUEST);
        }

        boolean success = authService.register(authRequest.getUsername(), authRequest.getPassword());
        if (success) {
            return json(new AuthResponse(null, "User registered successfully"), Status.CREATED);
        } else {
            return text("User already exists or invalid data", Status.BAD_REQUEST);
        }
    }

    private Response login(Request request) throws Exception {
        AuthRequest authRequest = toObject(request.getBody(), AuthRequest.class);

        if (authRequest.getUsername() == null || authRequest.getPassword() == null) {
            return text("Username and password required", Status.BAD_REQUEST);
        }

        String token = authService.login(authRequest.getUsername(), authRequest.getPassword());
        if (token != null) {
            return json(new AuthResponse(token, "Login successful"), Status.OK);
        } else {
            return text("Invalid credentials", Status.BAD_REQUEST);
        }
    }

    private Response getLeaderboard(Request request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = TokenUtility.extractTokenFromHeader(authHeader);

            if (token == null || !authService.validateToken(token)) {
                return text("Invalid or expired token", Status.BAD_REQUEST);
            }

            List<LeaderboardEntry> leaderboard = userService.getLeaderboard();

            return json(leaderboard, Status.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return text("Error loading leaderboard: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response logout(Request request) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (!TokenUtility.isValidAuthHeader(authHeader)) {
            return text("Authorization header required", Status.BAD_REQUEST);
        }

        String token = TokenUtility.extractTokenFromHeader(authHeader);
        authService.logout(token);
        return json(new AuthResponse(null, "Logout successful"), Status.OK);
    }


}