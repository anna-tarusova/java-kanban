package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.logic.HistoryManager;
import ru.yandex.practicum.tasks.logic.Managers;
import ru.yandex.practicum.tasks.logic.TaskManager;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {
    private Managers managers;

    @BeforeEach()
    void setUp() {
        managers = new Managers();
    }

    @Test
    void getDefaultShouldReturnTaskManagerWhichIsReadyToWork() {
        //Фраза из ТЗ
        //"убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;"

        //Получаем объект интерфейса TaskManager и вызываем некоторые его методы

        //Arrange
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("task1", "descr1");
        Task task2 = new Task("task2", "descr2");


        //Act
        taskManager.add(task1);
        taskManager.add(task2);

        //Assert
        List<Task> tasks = taskManager.getListTasks();
        assertEquals(2, tasks.size());
        Task task1WhichIsGotFromTaskManager = tasks.stream().filter(t -> t.getName().equals("task1")).findFirst().orElseThrow();
        Task task2WhichIsGotFromTaskManager = tasks.stream().filter(t -> t.getName().equals("task2")).findFirst().orElseThrow();
        assertEquals("descr1", task1WhichIsGotFromTaskManager.getDescription());
        assertEquals("descr2", task2WhichIsGotFromTaskManager.getDescription());
    }

    @Test
    void getDefaultHistory() {
        //Фраза из ТЗ
        //"убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;"

        //Получаем объект интерфейса HistoryManager и вызываем некоторые его методы
        //Arrange
        HistoryManager historyManager = managers.getDefaultHistory();
        Task task = new Task("task1", "descr task");
        task.setId(1);
        Epic epic = new Epic("epic1", "descr epic");
        epic.setId(2);
        Subtask subtask = new Subtask("subtask1", "descr subtask");
        subtask.setId(3);

        //Act
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        List<BaseTask> history = historyManager.getHistory();

        //Assert
        assertEquals(3, history.size());
        assertEquals(task, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subtask, history.get(2));
    }
}