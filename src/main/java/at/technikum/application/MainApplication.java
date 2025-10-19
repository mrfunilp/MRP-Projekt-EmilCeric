package at.technikum.application;

import at.technikum.application.common.Application;
import at.technikum.application.common.Router;
import at.technikum.application.controller.MediaController;
import at.technikum.application.controller.UserController;
import at.technikum.application.service.AuthService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import at.technikum.server.http.ContentType;

public class MainApplication implements Application {
    private Router router = new Router();
    private AuthService authService = AuthService.getInstance();

    public MainApplication() {
        // Userroutes
        UserController userController = new UserController();
        router.addRoute("/users/register", Method.POST, userController);
        router.addRoute("/users/login", Method.POST, userController);
        router.addRoute("/users/profile", Method.GET, userController);
        router.addRoute("/users/logout", Method.POST, userController);

        // Mediaroutes
        MediaController mediaController = new MediaController();
        router.addRoute("/media", Method.POST, mediaController);
        router.addRoute("/media", Method.GET, mediaController);
        router.addRoute("/media/", Method.GET, mediaController);
        router.addRoute("/media/", Method.PUT, mediaController);
        router.addRoute("/media/", Method.DELETE, mediaController);
    }

    @Override
    public Response handle(Request request) {
        return router.findController(request)
                .map(controller -> controller.handle(request))
                .orElse(new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, "Route not found"));
    }
}