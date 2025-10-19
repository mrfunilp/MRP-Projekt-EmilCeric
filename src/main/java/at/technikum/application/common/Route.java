package at.technikum.application.common;

import at.technikum.application.controller.Controller;
import at.technikum.server.http.Method;

public class Route {
    private final String path;
    private final Method method;
    private final Controller controller;

    public Route(String path, Method method, Controller controller) {
        this.path = path;
        this.method = method;
        this.controller = controller;
    }

    public String getPath() { return path; }
    public Method getMethod() { return method; }
    public Controller getController() { return controller; }
}