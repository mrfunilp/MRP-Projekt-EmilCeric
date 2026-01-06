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
        String requestPath = request.getPath();
        String requestMethod = request.getMethod();

        System.out.println("Router looking for: " + requestMethod + " " + requestPath);

        for (Route route : routes) {
            String routePath = route.getPath();
            Method routeMethod = route.getMethod();

            System.out.println("  Checking route: " + routeMethod.getVerb() + " " + routePath);

            if (!requestMethod.equals(routeMethod.getVerb())) {
                continue;
            }

            if (matchesPath(routePath, requestPath)) {
                System.out.println("  -> MATCH!");
                return Optional.of(route.getController());
            }
        }

        System.out.println("  -> NO MATCH FOUND");
        return Optional.empty();
    }

    private boolean matchesPath(String routePath, String requestPath) {
        if (routePath.equals("/") && requestPath.equals("/")) {
            return true;
        }

        String regexPath = routePath
                .replace("/", "\\/")  // Escape forward slashes
                .replace("{", "(?<")
                .replace("}", ">[^\\/]+)");

        // Füge ^ und $ für exakten Match hinzu
        regexPath = "^" + regexPath + "$";

        return requestPath.matches(regexPath);
    }

    public Optional<String> extractPathParameter(String routePath, String requestPath, String paramName) {
        String regexPath = routePath
                .replace("/", "\\/")
                .replace("{" + paramName + "}", "(?<" + paramName + ">[^\\/]+)");

        regexPath = "^" + regexPath + "$";

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regexPath);
        java.util.regex.Matcher matcher = pattern.matcher(requestPath);

        if (matcher.matches()) {
            return Optional.of(matcher.group(paramName));
        }
        return Optional.empty();
    }

    public void addRoute(String path, Method method, Controller controller) {
        routes.add(new Route(path, method, controller));
    }
}