package ru.yandex.practicum.tasks.test;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.pojo.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTasksTests extends HttpTaskServerBaseTests {

    URI urlTasks = URI.create("http://localhost:8080/tasks");
    URI urlTasks1 = URI.create("http://localhost:8080/tasks/1");

    public HttpTaskServerTasksTests() throws IOException {
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // Arrange && Act
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",5, LocalDateTime.parse("01.02.2024 01:23:45", dateTimeFormatter), null, Status.DONE);
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpRequest request = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        // проверяем код ответа
        assertEquals(201, response.statusCode(), "Некорректный код");

        // проверяем, что создалась одна задача с корректным именем
        List<ru.yandex.practicum.tasks.model.Task> tasks = manager.getListTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasks.getFirst().getName(), "Некорректное имя задачи");
        assertEquals(LocalDateTime.parse("01.02.2024 01:23:45", dateTimeFormatter), tasks.getFirst().getStartTime(), "Некорректное время старта задачи");
        assertEquals(Duration.ofMinutes(5), tasks.getFirst().getDuration(), "Некорректная продолжительность задачи");
        assertEquals(Status.DONE, tasks.getFirst().getStatus(), "Некорректный статус задачи");
    }
    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Task task = new ru.yandex.practicum.tasks.model.Task("aaa", "bbbb");
        task.setDuration(Duration.ofMinutes(15));
        task.setStatus(Status.IN_PROGRESS);
        task.setStartTime(LocalDateTime.parse("01.01.2024 10:10:10", dateTimeFormatter));
        manager.add(task);

        // Act
        Task updateTask = new Task("Updated aaa", "Updated bbbb",20, LocalDateTime.parse("10.10.2025 20:20:20", dateTimeFormatter), OptionalInt.of(1), Status.DONE);
        String taskJson = gson.toJson(updateTask);
        HttpRequest request = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Arrange
        assertEquals(201, response.statusCode());
        ru.yandex.practicum.tasks.model.Task outputTask = manager.getTask(1);

        assertEquals("Updated aaa", outputTask.getName(), "Некорректное имя задачи");
        assertEquals("Updated bbbb", outputTask.getDescription(), "Некорректное описание задачи");
        assertEquals(Duration.ofMinutes(20), outputTask.getDuration(), "Некорректная продолжительность задачи");
        assertEquals(LocalDateTime.parse("10.10.2025 20:20:20", dateTimeFormatter), outputTask.getStartTime(), "Некорректное время старта задачи");
        assertEquals(Status.DONE, outputTask.getStatus(), "Некорректный статус задачи");
    }

    @Test
    public void testAddTaskCheckInteractions() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Task task = new ru.yandex.practicum.tasks.model.Task("test1", "test1");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        task.setDuration(Duration.ofMinutes(15));
        manager.add(task);

        // Act
        Task newTask = new Task("Test 2", "Testing task 2",15, LocalDateTime.parse("01.01.2025 00:10:00", dateTimeFormatter), null, null);
        String taskJson = gson.toJson(newTask);
        HttpRequest request = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testUpdateTaskCheckInteractions() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Task task = new ru.yandex.practicum.tasks.model.Task("test1", "test1");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        task.setDuration(Duration.ofMinutes(15));
        manager.add(task);

        ru.yandex.practicum.tasks.model.Task task2 = new ru.yandex.practicum.tasks.model.Task("test1", "test1");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:15:00", dateTimeFormatter));
        task.setDuration(Duration.ofMinutes(15));
        manager.add(task2);

        // Act
        Task newTask = new Task("Test 2", "Testing task 2",15, LocalDateTime.parse("01.01.2025 00:10:00", dateTimeFormatter), OptionalInt.of(2), null);
        String taskJson = gson.toJson(newTask);
        HttpRequest request = HttpRequest.newBuilder().uri(urlTasks).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Task task = new ru.yandex.practicum.tasks.model.Task("test1", "test1");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        task.setDuration(Duration.ofMinutes(15));
        manager.add(task);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlTasks1).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode());
        List<ru.yandex.practicum.tasks.model.Task> tasks = manager.getListTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Task task = new ru.yandex.practicum.tasks.model.Task("test1", "descr1");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        task.setDuration(Duration.ofMinutes(15));
        manager.add(task);

        ru.yandex.practicum.tasks.model.Task task2 = new ru.yandex.practicum.tasks.model.Task("test2", "descr2");
        task2.setStartTime(LocalDateTime.parse("01.01.2025 00:15:00", dateTimeFormatter));
        task2.setDuration(Duration.ofMinutes(30));
        manager.add(task2);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlTasks).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        Type listType = new TypeToken<List<ru.yandex.practicum.tasks.model.Task>>(){}.getType();
        List<ru.yandex.practicum.tasks.model.Task> tasks = gson.fromJson(response.body(), listType);
        assertEquals(200, response.statusCode());
        assertEquals(2, tasks.size());
        assertEquals("test1", tasks.getFirst().getName());
        assertEquals("descr1", tasks.getFirst().getDescription());
        assertEquals(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter), tasks.getFirst().getStartTime());
        assertEquals(Duration.ofMinutes(15), tasks.getFirst().getDuration());

        assertEquals("test2", tasks.getLast().getName());
        assertEquals("descr2", tasks.getLast().getDescription());
        assertEquals(LocalDateTime.parse("01.01.2025 00:15:00", dateTimeFormatter), tasks.getLast().getStartTime());
        assertEquals(Duration.ofMinutes(30), tasks.getLast().getDuration());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Task task = new ru.yandex.practicum.tasks.model.Task("test1", "descr1");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        task.setDuration(Duration.ofMinutes(15));
        manager.add(task);

        ru.yandex.practicum.tasks.model.Task task2 = new ru.yandex.practicum.tasks.model.Task("test2", "descr2");
        task2.setStartTime(LocalDateTime.parse("01.01.2025 00:15:00", dateTimeFormatter));
        task2.setDuration(Duration.ofMinutes(30));
        manager.add(task2);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlTasks1).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        ru.yandex.practicum.tasks.model.Task outputTask = gson.fromJson(response.body(), ru.yandex.practicum.tasks.model.Task.class);
        assertEquals(200, response.statusCode());
        assertEquals("test1", outputTask.getName());
        assertEquals("descr1", outputTask.getDescription());
        assertEquals(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter), outputTask.getStartTime());
        assertEquals(Duration.ofMinutes(15), outputTask.getDuration());
    }

    @Test
    public void testGetTaskById404() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Task task = new ru.yandex.practicum.tasks.model.Task("test1", "descr1");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        task.setDuration(Duration.ofMinutes(15));
        manager.add(task);

        ru.yandex.practicum.tasks.model.Task task2 = new ru.yandex.practicum.tasks.model.Task("test2", "descr2");
        task2.setStartTime(LocalDateTime.parse("01.01.2025 00:15:00", dateTimeFormatter));
        task2.setDuration(Duration.ofMinutes(30));
        manager.add(task2);

        manager.removeTask(1);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlTasks1).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode());
    }
}
