package ru.yandex.practicum.tasks.httpHandlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tasks.exceptions.TaskAddException;
import ru.yandex.practicum.tasks.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.exceptions.WrongTaskTypeException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;

public class ExceptionHandler implements HttpHandler {

    private final HttpHandler handler;

    public ExceptionHandler(HttpHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            handler.handle(exchange);
        } catch (TaskNotFoundException | WrongTaskTypeException e) {
            sendNotFound(exchange);
        } catch (JsonSyntaxException | DateTimeParseException | IllegalArgumentException e) {
            sendBadRequest(exchange);
        } catch (TaskAddException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    public void sendNotFound(HttpExchange httpExchange) throws IOException {
        String response = "Not found";
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        httpExchange.sendResponseHeaders(404, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        httpExchange.close();
    }

    public void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        String response = "Has Interactions";
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        httpExchange.sendResponseHeaders(406, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        httpExchange.close();
    }

    public void sendBadRequest(HttpExchange httpExchange) throws IOException {
        String response = "Bad request";
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        httpExchange.sendResponseHeaders(400, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        httpExchange.close();
    }

    public void sendInternalServerError(HttpExchange httpExchange) throws IOException {
        String response = "Internal Server Error";
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        httpExchange.sendResponseHeaders(500, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        httpExchange.close();
    }
}
