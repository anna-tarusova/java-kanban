package ru.yandex.practicum.tasks.test;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.pojo.Subtask;

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

public class HttpTaskServerSubtasksTests extends HttpTaskServerBaseTests {

    URI urlSubtasks = URI.create("http://localhost:8080/subtasks");
    URI urlSubtasks2 = URI.create("http://localhost:8080/subtasks/2");

    public HttpTaskServerSubtasksTests() throws IOException {
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // Arrange
        manager.add(new ru.yandex.practicum.tasks.model.Epic("epic1", "descr1"));

        // Act
        Subtask subtask = new Subtask("Test 2", "Testing task 2", null, 1, 15, LocalDateTime.parse("01.01.2025 11:23:34", dateTimeFormatter), Status.IN_PROGRESS);

        String subtaskJson = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtasks).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode(), "Неправильный код");
        List<ru.yandex.practicum.tasks.model.Subtask> subtasks = manager.getListSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество задач");
        assertEquals("Test 2", subtasks.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Testing task 2", subtasks.getFirst().getDescription(), "Некорректное описание задачи");
        assertEquals(Duration.ofMinutes(15), subtasks.getFirst().getDuration(), "Некорректная продолжительность задачи");
        assertEquals(LocalDateTime.parse("01.01.2025 11:23:34", dateTimeFormatter), subtasks.getFirst().getStartTime(), "Некорректное время старта задачи");
        assertEquals(Status.IN_PROGRESS, subtasks.getFirst().getStatus(), "Некорректный статус задачи");
    }

    @Test
    public void testAddSubtaskCheckInteractions() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic1", "epic1 descr");
        manager.add(epic);
        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("aaa", "bbbb");
        subtask.setEpicId(1);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        manager.add(subtask);

        // Act
        Subtask subtask2 = new Subtask("Test 2", "Testing task 2", null, 1, 15, LocalDateTime.parse("01.01.2025 00:12:00", dateTimeFormatter), Status.IN_PROGRESS);
        String subtaskJson = gson.toJson(subtask2);
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtasks).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(406, response.statusCode(), "Неправильный код");
    }

    @Test
    public void testAddSubtaskCheckInteractions2() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic1", "epic1 descr");
        manager.add(epic);
        ru.yandex.practicum.tasks.model.Epic epic2 = new ru.yandex.practicum.tasks.model.Epic("epic2", "epic2 descr");
        manager.add(epic2);
        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("aaa", "bbbb");
        subtask.setEpicId(1);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        manager.add(subtask);

        // Act
        Subtask subtask2 = new Subtask("Test 2", "Testing task 2", null, 2, 15, LocalDateTime.parse("01.01.2025 00:12:00", dateTimeFormatter), Status.IN_PROGRESS);
        String subtaskJson = gson.toJson(subtask2);
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtasks).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(406, response.statusCode(), "Неправильный код");
    }

    @Test
    public void testAddSubtask404() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic1", "epic1 descr");
        manager.add(epic);
        ru.yandex.practicum.tasks.model.Epic epic2 = new ru.yandex.practicum.tasks.model.Epic("epic2", "epic2 descr");
        manager.add(epic2);
        manager.removeEpic(2);

        // Act
        Subtask subtask2 = new Subtask("Test 2", "Testing task 2", null, 2, 15, LocalDateTime.parse("01.01.2025 00:12:00", dateTimeFormatter), Status.IN_PROGRESS);
        String subtaskJson = gson.toJson(subtask2);
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtasks).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Неправильный код");
    }


    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic1", "epic1 descr");
        manager.add(epic);
        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("aaa", "bbbb");
        subtask.setEpicId(1);
        subtask.setStatus(Status.NEW);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.parse("01.02.2024 01:23:45", dateTimeFormatter));
        manager.add(subtask);

        // Act
        Subtask updateSubtask = new Subtask("Updated aaa", "Updated bbbb", OptionalInt.of(2), 1, 30, LocalDateTime.parse("02.03.2025 12:34:56", dateTimeFormatter), Status.DONE);
        String subtaskJson = gson.toJson(updateSubtask);
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtasks).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Arrange
        assertEquals(201, response.statusCode(), "Неправильный статус");
        ru.yandex.practicum.tasks.model.Subtask outputSubtask = manager.getSubtask(2);

        assertEquals(2, outputSubtask.getId(), "Некорректный ид подзадачи");
        assertEquals(1, outputSubtask.getEpicId(), "Некорректный ид эпика подзадачи");
        assertEquals("Updated aaa", outputSubtask.getName(), "Некорректное имя подзадачи");
        assertEquals("Updated bbbb", outputSubtask.getDescription(), "Некорректное описание подзадачи");
        assertEquals(Duration.ofMinutes(30), outputSubtask.getDuration(), "Некорректная продолжительность подзадачи");
        assertEquals(LocalDateTime.parse("02.03.2025 12:34:56", dateTimeFormatter), outputSubtask.getStartTime(), "Некорректное время старта подзадачи");
        assertEquals(Status.DONE, outputSubtask.getStatus(), "Некорректный статус подзадачи");
    }

    @Test
    public void testUpdateSubtask404() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic1", "epic1 descr");
        manager.add(epic);
        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("aaa", "bbbb");
        subtask.setEpicId(1);
        subtask.setStatus(Status.NEW);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.parse("01.02.2024 01:23:45", dateTimeFormatter));
        manager.add(subtask);

        // Act
        Subtask updateSubtask = new Subtask("Updated aaa", "Updated bbbb", OptionalInt.of(2), 999, 30, LocalDateTime.parse("02.03.2025 12:34:56", dateTimeFormatter), Status.DONE);
        String subtaskJson = gson.toJson(updateSubtask);
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtasks).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Arrange
        assertEquals(404, response.statusCode(), "Неправильный статус");
    }

    @Test
    public void testUpdateSubtaskCheckInteractions() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic1", "epic1 descr");
        manager.add(epic);
        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("aaa", "bbbb");
        subtask.setEpicId(1);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 00:00:00", dateTimeFormatter));
        manager.add(subtask);

        ru.yandex.practicum.tasks.model.Subtask subtask2 = new ru.yandex.practicum.tasks.model.Subtask("aaa", "bbbb");
        subtask2.setEpicId(1);
        subtask2.setDuration(Duration.ofMinutes(15));
        subtask2.setStartTime(LocalDateTime.parse("01.01.2025 00:15:00", dateTimeFormatter));
        manager.add(subtask2);

        // Act
        Subtask updateSubtask = new Subtask("Updated aaa", "Updated bbbb", OptionalInt.of(3), 1, 30, LocalDateTime.parse("01.01.2025 00:07:00", dateTimeFormatter), Status.DONE);
        String subtaskJson = gson.toJson(updateSubtask);
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtasks).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Arrange
        assertEquals(406, response.statusCode(), "Неправильный статус");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic1", "epic1 descr");
        manager.add(epic);
        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("aaa", "bbbb");
        subtask.setEpicId(1);
        subtask.setStatus(Status.NEW);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.parse("01.02.2024 01:23:45", dateTimeFormatter));
        manager.add(subtask);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtasks2).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Некорректный статус");
        List<ru.yandex.practicum.tasks.model.Subtask> subtasks = manager.getSubtasksOfEpic(1);
        assertEquals(0, subtasks.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic1", "epic1 descr");
        manager.add(epic);
        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("aaa", "bbbb");
        subtask.setEpicId(1);
        subtask.setStatus(Status.NEW);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.parse("01.02.2024 01:23:45", dateTimeFormatter));
        manager.add(subtask);

        ru.yandex.practicum.tasks.model.Subtask subtask2 = new ru.yandex.practicum.tasks.model.Subtask("ccc", "dddd");
        subtask2.setEpicId(1);
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setDuration(Duration.ofMinutes(30));
        subtask2.setStartTime(LocalDateTime.parse("02.03.2025 12:34:56", dateTimeFormatter));
        manager.add(subtask2);

        ru.yandex.practicum.tasks.model.Epic epic2 = new ru.yandex.practicum.tasks.model.Epic("epic2", "epic2 descr");
        manager.add(epic2);

        ru.yandex.practicum.tasks.model.Subtask subtask3 = new ru.yandex.practicum.tasks.model.Subtask("eee", "ffff");
        subtask3.setEpicId(4);
        subtask3.setStatus(Status.DONE);
        subtask3.setDuration(Duration.ofMinutes(45));
        subtask3.setStartTime(LocalDateTime.parse("03.04.2025 23:45:59", dateTimeFormatter));
        manager.add(subtask3);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtasks).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        Type listType = new TypeToken<List<ru.yandex.practicum.tasks.model.Subtask>>(){}.getType();
        List<ru.yandex.practicum.tasks.model.Subtask> subtasks = gson.fromJson(response.body(), listType);
        assertEquals(200, response.statusCode(), "Некорректный статус");
        assertEquals(3, subtasks.size(), "Некорректный размер");

        assertEquals(2, subtasks.getFirst().getId(), "Некорректный ид подзадачи");
        assertEquals(1, subtasks.getFirst().getEpicId(), "Некорректный ид эпика подзадачи");
        assertEquals("aaa", subtasks.getFirst().getName(), "Некорректное имя подзадачи");
        assertEquals("bbbb", subtasks.getFirst().getDescription(), "Некорректное описание подзадачи");
        assertEquals(Status.NEW, subtasks.getFirst().getStatus(), "Некорректный статус подзадачи");
        assertEquals(Duration.ofMinutes(15), subtasks.getFirst().getDuration(), "Некорректная продолжительность подзадачи");
        assertEquals(LocalDateTime.parse("01.02.2024 01:23:45", dateTimeFormatter), subtasks.getFirst().getStartTime(), "Неправильное время подзадачи");

        assertEquals(3, subtasks.get(1).getId(), "Некорректный ид подзадачи");
        assertEquals(1, subtasks.get(1).getEpicId(), "Некорректный ид эпика подзадачи");
        assertEquals("ccc", subtasks.get(1).getName(), "Некорректное имя подзадачи");
        assertEquals("dddd", subtasks.get(1).getDescription(), "Некорректное описание подзадачи");
        assertEquals(Status.IN_PROGRESS, subtasks.get(1).getStatus(), "Некорректный статус подзадачи");
        assertEquals(Duration.ofMinutes(30), subtasks.get(1).getDuration(), "Некорректная продолжительность подзадачи");
        assertEquals(LocalDateTime.parse("02.03.2025 12:34:56", dateTimeFormatter), subtasks.get(1).getStartTime(), "Неправильное время подзадачи");

        assertEquals(5, subtasks.get(2).getId(), "Некорректный ид подзадачи");
        assertEquals(4, subtasks.get(2).getEpicId(), "Некорректный ид эпика подзадачи");
        assertEquals("eee", subtasks.get(2).getName(), "Некорректное имя подзадачи");
        assertEquals("ffff", subtasks.get(2).getDescription(), "Некорректное описание подзадачи");
        assertEquals(Status.DONE, subtasks.get(2).getStatus(), "Некорректный статус подзадачи");
        assertEquals(Duration.ofMinutes(45), subtasks.get(2).getDuration(), "Некорректная продолжительность подзадачи");
        assertEquals(LocalDateTime.parse("03.04.2025 23:45:59", dateTimeFormatter), subtasks.get(2).getStartTime(), "Неправильное время подзадачи");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("epic1", "epic1 descr");
        manager.add(epic);
        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("aaa", "bbbb");
        subtask.setEpicId(1);
        subtask.setStatus(Status.DONE);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStartTime(LocalDateTime.parse("01.02.2024 01:23:45", dateTimeFormatter));
        manager.add(subtask);

        ru.yandex.practicum.tasks.model.Subtask subtask2 = new ru.yandex.practicum.tasks.model.Subtask("ccc", "dddd");
        subtask2.setEpicId(1);
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask2.setDuration(Duration.ofMinutes(30));
        subtask2.setStartTime(LocalDateTime.parse("02.03.2025 12:34:56", dateTimeFormatter));
        manager.add(subtask2);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtasks2).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        ru.yandex.practicum.tasks.model.Subtask outputSubtask = gson.fromJson(response.body(), ru.yandex.practicum.tasks.model.Subtask.class);
        assertEquals(2, outputSubtask.getId(), "Некорректный ид подзадачи");
        assertEquals(1, outputSubtask.getEpicId(), "Некорректный ид эпика подзадачи");
        assertEquals("aaa", outputSubtask.getName(), "Некорректное имя подзадачи");
        assertEquals("bbbb", outputSubtask.getDescription(), "Некорректное описание подзадачи");
        assertEquals(Status.DONE, outputSubtask.getStatus(), "Некорректный статус подзадачи");
        assertEquals(Duration.ofMinutes(15), outputSubtask.getDuration(), "Некорректная продолжительность подзадачи");
        assertEquals(LocalDateTime.parse("01.02.2024 01:23:45", dateTimeFormatter), outputSubtask.getStartTime(), "Неправильное время подзадачи");
    }

    @Test
    public void testGetTaskById404() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("test1", "descr1");
        manager.add(epic);

        ru.yandex.practicum.tasks.model.Task task = new ru.yandex.practicum.tasks.model.Task("test2", "descr2");
        manager.add(task);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlSubtasks2).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Неправильный статус");
    }
}
