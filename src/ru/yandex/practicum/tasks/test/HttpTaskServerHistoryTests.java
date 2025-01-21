package ru.yandex.practicum.tasks.test;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerHistoryTests extends HttpTaskServerBaseTests {
    URI urlHistory = URI.create("http://localhost:8080/history");

    public HttpTaskServerHistoryTests() throws IOException {
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Task task = new Task("task", "task descr");
        task.setStatus(Status.IN_PROGRESS);
        manager.add(task);
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic", "epic descr");
        manager.add(epic);
        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("subtask", "subtask descr");
        subtask.setEpicId(2);
        subtask.setStatus(Status.DONE);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        manager.add(subtask);

        manager.getListTasks();
        manager.getListSubtasks();
        manager.getListEpics();

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlHistory).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<ru.yandex.practicum.tasks.model.BaseTask>>(){}.getType();
        List<ru.yandex.practicum.tasks.model.BaseTask> tasks = gson.fromJson(response.body(), listType);

        // Assert
        assertEquals(3, tasks.size());

        BaseTask firstTask = tasks.getFirst();
        assertEquals(TaskType.TASK, firstTask.getTaskType());
        assertEquals(Status.IN_PROGRESS, firstTask.getStatus());

        BaseTask secondTask = tasks.get(1);
        assertEquals(TaskType.SUBTASK, secondTask.getTaskType());
        assertEquals(Status.DONE, secondTask.getStatus());
        assertEquals(Duration.ofMinutes(15), secondTask.getDuration());
        assertEquals(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter), secondTask.getStartTime());

        BaseTask thirdTask = tasks.get(2);
        assertEquals(TaskType.EPIC, thirdTask.getTaskType());
        assertEquals(Status.DONE, thirdTask.getStatus());
        assertEquals(Duration.ofMinutes(15), thirdTask.getDuration());
        assertEquals(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter), thirdTask.getStartTime());
    }

    @Test
    public void getHistory_shouldContainsCopy() throws IOException, InterruptedException {
        // Arrange && Act
        ru.yandex.practicum.tasks.model.Task task = new Task("task", "task descr");
        task.setStatus(Status.IN_PROGRESS);
        task.setDuration(Duration.ofMinutes(15));
        task.setStartTime(LocalDateTime.parse("01.01.2024 00:00:00", dateTimeFormatter));

        manager.add(task);
        manager.getListTasks();

        ru.yandex.practicum.tasks.model.Task updateTask = new Task("task 1", "task descr 1");
        updateTask.setId(1);
        updateTask.setStatus(Status.DONE);
        updateTask.setDuration(Duration.ofMinutes(30));
        updateTask.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));

        manager.update(updateTask);

        // Assert
        HttpRequest request = HttpRequest.newBuilder().uri(urlHistory).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<ru.yandex.practicum.tasks.model.BaseTask>>(){}.getType();
        List<ru.yandex.practicum.tasks.model.BaseTask> tasks = gson.fromJson(response.body(), listType);

        assertEquals(1, tasks.size());

        BaseTask firstTask = tasks.getFirst();
        assertEquals(TaskType.TASK, firstTask.getTaskType());
        assertEquals(Status.IN_PROGRESS, firstTask.getStatus());
        assertEquals(Duration.ofMinutes(15), firstTask.getDuration());
        assertEquals(LocalDateTime.parse("01.01.2024 00:00:00", dateTimeFormatter), firstTask.getStartTime());
    }

    @Test
    public void getHistory_shouldContainsCopy2() throws IOException, InterruptedException {
        // Arrange && Act
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic1", "epic descr");
        ru.yandex.practicum.tasks.model.Subtask subtask = new Subtask("subtask1", "descr");
        subtask.setEpicId(1);
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.parse("01.01.2024 00:00:00", dateTimeFormatter));
        manager.add(epic);
        manager.add(subtask);
        manager.getListEpics();

        ru.yandex.practicum.tasks.model.Subtask updateSubtask = new Subtask("subtask1 updated", "descr updated");
        updateSubtask.setId(2);
        updateSubtask.setEpicId(1);
        updateSubtask.setStatus(Status.DONE);
        updateSubtask.setDuration(Duration.ofMinutes(30));
        updateSubtask.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));

        manager.update(updateSubtask);

        // Assert
        HttpRequest request = HttpRequest.newBuilder().uri(urlHistory).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<ru.yandex.practicum.tasks.model.BaseTask>>(){}.getType();
        List<ru.yandex.practicum.tasks.model.BaseTask> tasks = gson.fromJson(response.body(), listType);

        assertEquals(1, tasks.size());

        BaseTask firstTask = tasks.getFirst();
        assertEquals(TaskType.EPIC, firstTask.getTaskType());
        assertEquals(Status.IN_PROGRESS, firstTask.getStatus());
        assertEquals(Duration.ofMinutes(15), firstTask.getDuration());
        assertEquals(LocalDateTime.parse("01.01.2024 00:00:00", dateTimeFormatter), firstTask.getStartTime());

        List<ru.yandex.practicum.tasks.model.Subtask> subtasks = ((ru.yandex.practicum.tasks.model.Epic)firstTask).getSubtasks();
        Subtask firstSubtask = subtasks.getFirst();
        assertEquals(TaskType.SUBTASK, firstSubtask.getTaskType());
        assertEquals(Status.IN_PROGRESS, firstSubtask.getStatus());
        assertEquals(Duration.ofMinutes(15), firstSubtask.getDuration());
        assertEquals(LocalDateTime.parse("01.01.2024 00:00:00", dateTimeFormatter), firstTask.getStartTime());
    }
}
