package ru.yandex.practicum.tasks.test;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.pojo.Epic;

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


public class HttpTaskServerEpicsTests extends HttpTaskServerBaseTests {

    URI urlEpics = URI.create("http://localhost:8080/epics");
    URI urlEpics1 = URI.create("http://localhost:8080/epics/1");
    URI urlEpics1Subtasks = URI.create("http://localhost:8080/epics/1/subtasks");

    public HttpTaskServerEpicsTests() throws IOException {
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // Arrange && Act
        Epic task = new Epic("Test 2", "Testing task 2", null);

        String taskJson = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().uri(urlEpics).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode());
        List<ru.yandex.practicum.tasks.model.Epic> epics = manager.getListEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество задач");
        assertEquals("Test 2", epics.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Testing task 2", epics.getFirst().getDescription(), "Некорректное описание задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("aaa", "bbbb");
        manager.add(epic);

        // Act
        Epic updateEpic = new Epic("Updated aaa", "Updated bbbb", OptionalInt.of(1));
        String epicJson = gson.toJson(updateEpic);
        HttpRequest request = HttpRequest.newBuilder().uri(urlEpics).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Arrange
        assertEquals(201, response.statusCode());
        ru.yandex.practicum.tasks.model.Epic outputEpic = manager.getEpic(1);

        assertEquals(1, outputEpic.getId());
        assertEquals("Updated aaa", outputEpic.getName(), "Некорректное имя задачи");
        assertEquals("Updated bbbb", outputEpic.getDescription(), "Некорректное описание задачи");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("test1", "test1");
        manager.add(epic);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlEpics1).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode());
        List<ru.yandex.practicum.tasks.model.Epic> epics = manager.getListEpics();
        assertEquals(0, epics.size());
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("test1", "descr1");
        manager.add(epic);

        ru.yandex.practicum.tasks.model.Epic epic2 = new ru.yandex.practicum.tasks.model.Epic("test2", "descr2");
        manager.add(epic2);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlEpics).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        Type listType = new TypeToken<List<ru.yandex.practicum.tasks.model.Epic>>(){}.getType();
        List<ru.yandex.practicum.tasks.model.Epic> epics = gson.fromJson(response.body(), listType);
        assertEquals(200, response.statusCode());
        assertEquals(2, epics.size());
        assertEquals("test1", epics.getFirst().getName());
        assertEquals("descr1", epics.getFirst().getDescription());

        assertEquals("test2", epics.getLast().getName());
        assertEquals("descr2", epics.getLast().getDescription());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("test1", "descr1");
        manager.add(epic);

        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("subtask1", "sub descr1");
        subtask.setEpicId(1);
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 11:22:33", dateTimeFormatter));
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStatus(Status.IN_PROGRESS);
        manager.add(subtask);

        ru.yandex.practicum.tasks.model.Subtask subtask2 = new ru.yandex.practicum.tasks.model.Subtask("subtask2", "sub descr2");
        subtask2.setEpicId(1);
        subtask2.setStartTime(LocalDateTime.parse("02.01.2025 01:23:45", dateTimeFormatter));
        subtask2.setDuration(Duration.ofMinutes(10));
        manager.add(subtask2);

        ru.yandex.practicum.tasks.model.Epic epic2 = new ru.yandex.practicum.tasks.model.Epic("test2", "descr2");
        manager.add(epic2);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlEpics1).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        ru.yandex.practicum.tasks.model.Epic outputEpic = gson.fromJson(response.body(), ru.yandex.practicum.tasks.model.Epic.class);
        assertEquals(200, response.statusCode());
        assertEquals("test1", outputEpic.getName(), "Неправильное имя эпика");
        assertEquals("descr1", outputEpic.getDescription(), "Неправильное описание эпика");
        assertEquals(Status.IN_PROGRESS, outputEpic.getStatus(), "Неправильный статус эпика");
        assertEquals(LocalDateTime.parse("01.01.2025 11:22:33", dateTimeFormatter), outputEpic.getStartTime(), "Неправильное время старта эпика");
        assertEquals(Duration.ofMinutes(25), outputEpic.getDuration(), "Неправильный статус эпика");
    }

    @Test
    public void testGetTaskById404() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("test1", "descr1");
        manager.add(epic);

        ru.yandex.practicum.tasks.model.Epic epic2 = new ru.yandex.practicum.tasks.model.Epic("test2", "descr2");
        manager.add(epic2);

        manager.removeEpic(1);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlEpics1).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Неправильный статус");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("test1", "descr1");
        manager.add(epic);

        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("subtask1", "sub descr1");
        subtask.setEpicId(1);
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 11:22:33", dateTimeFormatter));
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStatus(Status.IN_PROGRESS);
        manager.add(subtask);

        ru.yandex.practicum.tasks.model.Subtask subtask2 = new ru.yandex.practicum.tasks.model.Subtask("subtask2", "sub descr2");
        subtask2.setEpicId(1);
        subtask2.setStartTime(LocalDateTime.parse("02.01.2025 01:23:45", dateTimeFormatter));
        subtask2.setDuration(Duration.ofMinutes(10));
        manager.add(subtask2);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlEpics1Subtasks).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        Type listType = new TypeToken<List<ru.yandex.practicum.tasks.model.Subtask>>(){}.getType();
        List<ru.yandex.practicum.tasks.model.Subtask> subtasks = gson.fromJson(response.body(), listType);

        assertEquals(200, response.statusCode(), "Неправильный статус");
        assertEquals(2, subtasks.size(), "Неправильное количество сабтасок");
        assertEquals("subtask1", subtasks.getFirst().getName(), "Неправильное имя первой сабтаски");
        assertEquals("sub descr1", subtasks.getFirst().getDescription(), "Неправильное описание первой сабтаски");
        assertEquals(Duration.ofMinutes(15), subtasks.getFirst().getDuration(), "Неправильная продолжительность первой сабтаски");
        assertEquals(LocalDateTime.parse("01.01.2025 11:22:33", dateTimeFormatter), subtasks.getFirst().getStartTime(), "Неправильная дата старта первой сабтаски");
        assertEquals(Status.IN_PROGRESS, subtasks.getFirst().getStatus(), "Неправильный статус первой сабтаски");

        assertEquals("subtask2", subtasks.getLast().getName(), "Неправильное имя второй сабтаски");
        assertEquals("sub descr2", subtasks.getLast().getDescription(), "Неправильное описание второй сабтаски");
        assertEquals(Duration.ofMinutes(10), subtasks.getLast().getDuration(), "Неправильная продолжительность второй сабтаски");
        assertEquals(LocalDateTime.parse("02.01.2025 01:23:45", dateTimeFormatter), subtasks.getLast().getStartTime(), "Неправильная дата старта второй сабтаски");
        assertEquals(Status.NEW, subtasks.getLast().getStatus(), "Неправильный статус второй сабтаски");
    }

    @Test
    public void testGetSubtasks404() throws IOException, InterruptedException {
        // Arrange
        ru.yandex.practicum.tasks.model.Epic epic = new ru.yandex.practicum.tasks.model.Epic("test1", "descr1");
        manager.add(epic);

        ru.yandex.practicum.tasks.model.Subtask subtask = new ru.yandex.practicum.tasks.model.Subtask("subtask1", "sub descr1");
        subtask.setEpicId(1);
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 11:22:33", dateTimeFormatter));
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setStatus(Status.IN_PROGRESS);
        manager.add(subtask);

        ru.yandex.practicum.tasks.model.Subtask subtask2 = new ru.yandex.practicum.tasks.model.Subtask("subtask2", "sub descr2");
        subtask2.setEpicId(1);
        subtask2.setStartTime(LocalDateTime.parse("02.01.2025 01:23:45", dateTimeFormatter));
        subtask2.setDuration(Duration.ofMinutes(10));
        manager.add(subtask2);

        manager.removeEpic(1);

        // Act
        HttpRequest request = HttpRequest.newBuilder().uri(urlEpics1Subtasks).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode());
    }
}
