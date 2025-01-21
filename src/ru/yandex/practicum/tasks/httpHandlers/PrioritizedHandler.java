package ru.yandex.practicum.tasks.httpHandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.tasks.logic.TaskManager;
import ru.yandex.practicum.tasks.model.BaseTask;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        List<BaseTask> tasks = manager.getPrioritizedTasks();
        String response = gson.toJson(tasks);
        sendText(exchange, response);
    }
}
