package ru.yandex.practicum.tasks.httpHandlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.tasks.exceptions.*;
import ru.yandex.practicum.tasks.logic.TaskManager;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (path.equals("/epics") && method.equals("GET")) {
            List<Epic> epics = this.manager.getListEpics();
            String response = gson.toJson(epics);
            sendText(exchange, response);
            return;
        }

        if (path.startsWith("/epics/") && parts.length == 3 && method.equals("GET")) {
            Optional<Integer> id = parseId(parts[2]);
            if (id.isEmpty()) {
                throw new JsonSyntaxException("");
            }
            try {
                Epic epic = this.manager.getEpic(id.get());
                String response = gson.toJson(epic);
                sendText(exchange, response);
            } catch (WrongTaskTypeException e) {
                throw new TaskNotFoundException("Задача найдена, но не является эпиком");
            }
            return;
        }

        if (path.equals("/epics") && method.equals("POST")) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            ru.yandex.practicum.tasks.pojo.Epic epic = gson.fromJson(body, ru.yandex.practicum.tasks.pojo.Epic.class);

            Epic epicToAddOrUpdate = new Epic(epic.getName(), epic.getDescription());

            if (epic.getId() == null || epic.getId().isEmpty()) {
                manager.add(epicToAddOrUpdate);
            } else {
                epicToAddOrUpdate.setId(epic.getId().getAsInt());
                manager.update(epicToAddOrUpdate);
            }

            sendCreated(exchange);
            return;
        }

        if (path.startsWith("/epics/") && path.endsWith("/subtasks") && parts.length == 4 && method.equals("GET")) {
            Optional<Integer> id = parseId(parts[2]);
            if (id.isEmpty()) {
                throw new JsonSyntaxException("");
            }

            List<Subtask> subtasks = manager.getSubtasksOfEpic(id.get());
            String response = gson.toJson(subtasks);
            sendText(exchange, response);
            return;
        }

        if (path.startsWith("/epics/") && parts.length == 3 && method.equals("DELETE")) {
            Optional<Integer> id = parseId(parts[2]);
            if (id.isEmpty()) {
                throw new JsonSyntaxException("");
            }

            manager.removeEpic(id.get());
            sendNoContent(exchange);
        }

        throw new TaskNotFoundException("");//вызываем 404
    }
}
