package at.technikum.application.common;

import at.technikum.application.controller.Controller;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Router {
    private List<Route> routes = new ArrayList<>();

    public Optional<Controller> findController(Request request) {
        for (Route route : routes) {
            if (request.getPath().startsWith(route.getPath()) &&
                    request.getMethod().equals(route.getMethod().getVerb())) {
                return Optional.of(route.getController());
            }
        }
        return Optional.empty();
    }

    public void addRoute(String path, Method method, Controller controller) {
        routes.add(new Route(path, method, controller));
    }
}