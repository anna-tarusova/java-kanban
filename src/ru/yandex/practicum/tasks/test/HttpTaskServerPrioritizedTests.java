package ru.yandex.practicum.tasks.test;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerPrioritizedTests extends HttpTaskServerBaseTests {

    URI urlPrioritized = URI.create("http://localhost:8080/prioritized");

    public HttpTaskServerPrioritizedTests() throws IOException {
    }

    @Test
    public void getPrioritized() throws IOException, InterruptedException {
        // Arrange
        Task task = new Task("task", "task descr");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:15:00", dateTimeFormatter));
        manager.add(task);
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic", "epic descr");
        manager.add(epic);
        Subtask subtask = new Subtask("subtask", "subtask descr");
        subtask.setEpicId(2);
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        manager.add(subtask);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlPrioritized).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<BaseTask>>(){}.getType();
        List<BaseTask> tasks = gson.fromJson(response.body(), listType);

        // Assert
        assertEquals(2, tasks.size());

        BaseTask firstTask = tasks.getFirst();
        assertEquals(TaskType.SUBTASK, firstTask.getTaskType());
        assertEquals(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter), firstTask.getStartTime());

        BaseTask secondTask = tasks.get(1);
        assertEquals(TaskType.TASK, secondTask.getTaskType());
        assertEquals(LocalDateTime.parse("01.01.2025 00:15:00", dateTimeFormatter), secondTask.getStartTime());
   }

    @Test
    public void getPrioritized_shouldBeUpdatedAfterUpdate() throws IOException, InterruptedException {
        // Arrange
        Task task = new Task("task", "task descr");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:15:00", dateTimeFormatter));
        manager.add(task);
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic", "epic descr");
        manager.add(epic);
        Subtask subtask = new Subtask("subtask", "subtask descr");
        subtask.setEpicId(2);
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        manager.add(subtask);

        // Act
        subtask.setStartTime(LocalDateTime.parse("02.01.2025 00:00:00", dateTimeFormatter));
        manager.update(subtask);

        HttpRequest request = HttpRequest.newBuilder().uri(urlPrioritized).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<BaseTask>>(){}.getType();
        List<BaseTask> tasks = gson.fromJson(response.body(), listType);

        // Assert
        assertEquals(2, tasks.size());

        BaseTask firstTask = tasks.getFirst();
        assertEquals(TaskType.TASK, firstTask.getTaskType());
        assertEquals(LocalDateTime.parse("01.01.2025 00:15:00", dateTimeFormatter), firstTask.getStartTime());

        BaseTask secondTask = tasks.get(1);
        assertEquals(TaskType.SUBTASK, secondTask.getTaskType());
        assertEquals(LocalDateTime.parse("02.01.2025 00:00:00", dateTimeFormatter), secondTask.getStartTime());
    }
}
