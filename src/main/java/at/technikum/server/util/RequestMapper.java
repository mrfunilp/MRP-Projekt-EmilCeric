package at.technikum.server.util;

import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class RequestMapper {

    public Request fromExchange(HttpExchange exchange) {
        Request request = new Request();


        request.setMethod(Method.valueOf(exchange.getRequestMethod()));

        request.setPath(exchange.getRequestURI().getPath());

        exchange.getRequestHeaders().forEach((key, values) -> {
            if (!values.isEmpty()) {
                request.addHeader(key, values.get(0));
                System.out.println("Header: " + key + " = " + values.get(0)); // Debug
            }
        });

        try {
            String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines()
                    .collect(Collectors.joining("\n"));
            request.setBody(body);
        } catch (Exception e) {
            request.setBody("");
        }

        return request;
    }
}