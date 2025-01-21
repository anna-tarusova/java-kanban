package ru.yandex.practicum.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.tasks.adapters.*;
import ru.yandex.practicum.tasks.deserializers.BaseTaskDeserializer;
import ru.yandex.practicum.tasks.httpHandlers.*;
import ru.yandex.practicum.tasks.logic.FileBackedTaskManager;
import ru.yandex.practicum.tasks.logic.TaskManager;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.OptionalInt;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final String filename = "filename";
    private final HttpServer httpServer;
    private static Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0); // связываем сервер с сетевым портом

        GsonBuilder gb = new GsonBuilder();
        gb.setPrettyPrinting();
        gb.registerTypeAdapter(Duration.class, new DurationAdapter());
        gb.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gb.registerTypeAdapter(OptionalInt.class, new OptionalIntAdapter());
        gb.registerTypeAdapter(Status.class, new StatusAdapter());
        gb.registerTypeAdapter(TaskType.class, new TaskTypeAdapter());
        gb.registerTypeAdapter(BaseTask.class, new BaseTaskDeserializer());
        gson = gb.create();

        httpServer.createContext("/tasks", new ExceptionHandler(new TaskHandler(taskManager, gson)));
        httpServer.createContext("/subtasks", new ExceptionHandler(new SubtaskHandler(taskManager, gson)));
        httpServer.createContext("/epics", new ExceptionHandler(new EpicHandler(taskManager, gson)));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static Gson getGson() {
        return gson;
    }

    // IOException могут сгенерировать методы create() и bind(...)
    public static void main() throws IOException {
        Path path = Path.of(filename);
        File f;
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        f = new File(filename);

        FileBackedTaskManager ts = FileBackedTaskManager.loadFromFile(f);
        new HttpTaskServer(ts).start();
    }
}