package at.technikum.application.controller;

import at.technikum.server.http.ContentType;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Controller {
    public abstract Response handle(Request request);

    protected <T> T toObject(String content, Class<T> valueType) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(content, valueType);
    }

    protected Response ok() {
        return status(Status.OK);
    }

    protected Response status(Status status) {
        return text(status.getMessage(), status);
    }

    protected Response text(String text) {
        return text(text, Status.OK);
    }

    protected Response text(String text, Status status) {
        return r(status, ContentType.TEXT_PLAIN, text);
    }

    protected Response json(Object o, Status status) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(o);
        return r(status, ContentType.APPLICATION_JSON, json);
    }

    private Response r(Status status, ContentType contentType, String body) {
        Response response = new Response();
        response.setStatus(status);
        response.setContentType(contentType);
        response.setBody(body);
        return response;
    }
}