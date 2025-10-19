package at.technikum.application.controller;

import at.technikum.application.model.AuthRequest;
import at.technikum.application.model.AuthResponse;
import at.technikum.application.service.AuthService;
import at.technikum.server.util.TokenUtility;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

public class UserController extends Controller {
    //Als singelton aufgerufe, weil mehrere instanzen zu errors geführt haben
    private AuthService authService = AuthService.getInstance();

    @Override
    public Response handle(Request request) {
        try {
            if (request.getPath().equals("/users/register") && request.getMethod().equals("POST")) {
                return register(request);
            } else if (request.getPath().equals("/users/login") && request.getMethod().equals("POST")) {
                return login(request);
            } else if (request.getPath().equals("/users/profile") && request.getMethod().equals("GET")) {
                return getProfile(request);
            } else if (request.getPath().equals("/users/logout") && request.getMethod().equals("POST")) {
                return logout(request);
            }
            return text("Not found", Status.NOT_FOUND);
        } catch (Exception e) {
            return text("Error: " + e.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
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

    private Response getProfile(Request request) throws Exception {
        String authHeader = request.getHeader("Authorization");

        if (!TokenUtility.isValidAuthHeader(authHeader)) {
            return text("Authorization header required", Status.BAD_REQUEST);
        }

        String token = TokenUtility.extractTokenFromHeader(authHeader);
        if (!authService.validateToken(token)) {
            return text("Invalid or expired token", Status.BAD_REQUEST);
        }

        String username = authService.getUsernameFromToken(token);
        return json(new AuthResponse(null, "Profile data for: " + username), Status.OK);
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