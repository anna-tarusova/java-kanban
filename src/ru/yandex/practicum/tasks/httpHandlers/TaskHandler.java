package ru.yandex.practicum.tasks.httpHandlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.tasks.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.logic.TaskManager;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.Status;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler  {

    public TaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (path.equals("/tasks") && method.equals("GET")) {
            List<Task> tasks = this.manager.getListTasks();
            String response = gson.toJson(tasks);
            sendText(exchange, response);
            return;
        }

        if (path.startsWith("/tasks") && parts.length == 3 && method.equals("GET")) {
            Optional<Integer> id = parseId(parts[2]);

            if (id.isEmpty()) {
                throw new JsonSyntaxException("");
            }

            Task task = this.manager.getTask(id.get());
            String response = gson.toJson(task);
            sendText(exchange, response);
            return;
        }

        if (path.equals("/tasks") && method.equals("POST")) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            ru.yandex.practicum.tasks.pojo.Task task = gson.fromJson(body, ru.yandex.practicum.tasks.pojo.Task.class);

            Task taskToAddOrUpdate = new Task(task.getName(), task.getDescription());
            taskToAddOrUpdate.setStatus(task.getStatus() == null ? Status.NEW : task.getStatus());
            if (task.getDuration() != null) {
                taskToAddOrUpdate.setDuration(Duration.ofMinutes(task.getDuration()));
            }
            if (task.getStartTime() != null) {
                taskToAddOrUpdate.setStartTime(task.getStartTime());
            }

            if (task.getId() == null || task.getId().isEmpty()) {
                manager.add(taskToAddOrUpdate);
            } else {
                taskToAddOrUpdate.setId(task.getId().getAsInt());
                manager.update(taskToAddOrUpdate);
            }

            sendCreated(exchange);
            return;
        }

        if (path.startsWith("/tasks") && parts.length == 3 && method.equals("DELETE")) {
            Optional<Integer> id = parseId(parts[2]);
            if (id.isEmpty()) {
                throw new JsonSyntaxException("");
            }
            manager.removeTask(id.get());
            sendNoContent(exchange);
            return;
        }

        throw new TaskNotFoundException("");//вызываем 404
    }
}
