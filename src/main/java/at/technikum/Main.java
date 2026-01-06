package at.technikum.server;


import at.technikum.application.MainApplication;
import at.technikum.server.Server;

public class Main {
    public static void main(String[] args) {
        int port = 8080;
        MainApplication application = new MainApplication();
        Server server = new Server(port, application);
        server.start();
        System.out.println("Server running on http://localhost:" + port);
    }
}