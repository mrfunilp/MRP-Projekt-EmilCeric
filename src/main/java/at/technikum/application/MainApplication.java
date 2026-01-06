package at.technikum.application;

import at.technikum.application.common.Application;
import at.technikum.application.common.Router;
import at.technikum.application.controller.MediaController;
import at.technikum.application.controller.UserController;
import at.technikum.application.database.*;
import at.technikum.application.service.*;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import at.technikum.server.http.ContentType;

public class MainApplication implements Application {
    private Router router = new Router();


    private UserRepository userRepository = new UserRepository();
    private MediaRepository mediaRepository = new MediaRepository();
    private RatingRepository ratingRepository = new RatingRepository();
    private FavoriteRepository favoriteRepository = new FavoriteRepository();

    private AuthServiceInterface authService = new AuthService(userRepository);
    private MediaServiceInterface mediaService = new MediaService(mediaRepository);
    private RatingServiceInterface ratingService = new RatingService(ratingRepository, mediaRepository);
    private FavoriteServiceInterface favoriteService = new FavoriteService(favoriteRepository, mediaRepository);
    private UserServiceInterface userService = new UserService(userRepository);

    private UserController userController = new UserController(authService, userService);
    private MediaController mediaController = new MediaController(mediaService, authService, ratingService, favoriteService);

    public MainApplication() {
        setupRoutes();
    }

    private void setupRoutes() {
        router.addRoute("/users/register", Method.POST, userController);
        router.addRoute("/users/login", Method.POST, userController);
        router.addRoute("/users/profile", Method.GET, userController);
        router.addRoute("/users/logout", Method.POST, userController);
        router.addRoute("/leaderboard", Method.GET, userController);
        router.addRoute("/users/{username}/profile", Method.GET, userController);

        router.addRoute("/media", Method.POST, mediaController);
        router.addRoute("/media", Method.GET, mediaController);
        router.addRoute("/media/{id}", Method.GET, mediaController);
        router.addRoute("/media/{id}", Method.PUT, mediaController);
        router.addRoute("/media/{id}", Method.DELETE, mediaController);
        router.addRoute("/media/search", Method.POST, mediaController);
        router.addRoute("/favorites", Method.GET, mediaController);
        router.addRoute("/media/{id}/favorite", Method.POST, mediaController);
        router.addRoute("/media/{id}/favorite", Method.DELETE, mediaController);
        router.addRoute("/media/{id}/favorite", Method.GET, mediaController);
        router.addRoute("/ratings", Method.POST, mediaController);
        router.addRoute("/ratings/{id}", Method.PUT, mediaController);
        router.addRoute("/ratings/{id}", Method.DELETE, mediaController);
        router.addRoute("/ratings/{id}/like", Method.POST, mediaController);
        router.addRoute("/ratings/{id}/confirm", Method.POST, mediaController);
        router.addRoute("/media/{id}/ratings", Method.GET, mediaController);
    }

    @Override
    public Response handle(Request request) {
        return router.findController(request)
                .map(controller -> controller.handle(request))
                .orElse(new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, "Route not found"));
    }
}