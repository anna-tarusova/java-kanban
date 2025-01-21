package ru.yandex.practicum.tasks.test;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.tasks.HttpTaskServer;
import ru.yandex.practicum.tasks.logic.InMemoryHistoryManager;
import ru.yandex.practicum.tasks.logic.InMemoryTaskManager;
import ru.yandex.practicum.tasks.logic.TaskManager;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.format.DateTimeFormatter;

public abstract class HttpTaskServerBaseTests {
    protected TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    protected HttpTaskServer taskServer = new HttpTaskServer(manager);
    protected Gson gson = HttpTaskServer.getGson();
    protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    protected HttpClient client = HttpClient.newHttpClient();

    protected HttpTaskServerBaseTests() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTasks();
        manager.clearSubTasks();
        manager.clearEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }
}
