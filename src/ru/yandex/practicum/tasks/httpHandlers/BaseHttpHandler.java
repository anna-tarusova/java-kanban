package ru.yandex.practicum.tasks.httpHandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tasks.logic.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {

    protected TaskManager manager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    public void sendText(HttpExchange httpExchange, String text) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, 0);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(text.getBytes(StandardCharsets.UTF_8));
        }
        httpExchange.close();
    }

    public void sendCreated(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(201, 0);
        httpExchange.close();
    }

    public void sendNoContent(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }

    public Optional<Integer> parseId(String text) {
        try {
            int result = Integer.parseInt(text);
            return Optional.of(result);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }
}
