package ru.yandex.practicum.tasks.httpHandlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.tasks.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.logic.TaskManager;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.enums.Status;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (path.equals("/subtasks") && method.equals("GET")) {
            List<Subtask> subtasks = manager.getListSubtasks();
            String response = gson.toJson(subtasks);
            sendText(exchange, response);
            return;
        }

        if (path.startsWith("/subtasks/") && parts.length == 3 && method.equals("GET")) {
            Optional<Integer> id = parseId(parts[2]);

            if (id.isEmpty()) {
                throw new JsonSyntaxException("");
            }

            Subtask subtask = this.manager.getSubtask(id.get());
            String response = gson.toJson(subtask);
            sendText(exchange, response);
            return;
        }

        if (path.startsWith("/subtasks/") && parts.length == 3 && method.equals("DELETE")) {
            Optional<Integer> id = parseId(parts[2]);
            if (id.isEmpty()) {
                throw new JsonSyntaxException("");
            }

            manager.removeSubTask(id.get());
            sendNoContent(exchange);
            return;
        }

        if (path.equals("/subtasks") && method.equals("POST")) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            ru.yandex.practicum.tasks.pojo.Subtask subtask = gson.fromJson(body, ru.yandex.practicum.tasks.pojo.Subtask.class);

            Subtask subtaskToAddOrUpdate = new Subtask(subtask.getName(), subtask.getDescription());
            subtaskToAddOrUpdate.setEpicId(subtask.getEpicId());
            subtaskToAddOrUpdate.setStatus(subtask.getStatus() == null ? Status.NEW : subtask.getStatus());
            if (subtask.getDuration() != null) {
                subtaskToAddOrUpdate.setDuration(Duration.ofMinutes(subtask.getDuration()));
            }
            if (subtask.getStartTime() != null) {
                subtaskToAddOrUpdate.setStartTime(subtask.getStartTime());
            }

            if (subtask.getId() == null || subtask.getId().isEmpty()) {
                manager.add(subtaskToAddOrUpdate);
            } else {
                subtaskToAddOrUpdate.setId(subtask.getId().getAsInt());
                manager.update(subtaskToAddOrUpdate);
            }

            sendCreated(exchange);
            return;
        }

        throw new TaskNotFoundException("");//вызываем 404
    }
}
