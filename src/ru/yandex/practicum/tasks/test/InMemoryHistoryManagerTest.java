package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.logic.InMemoryHistoryManager;
import ru.yandex.practicum.tasks.logic.InMemoryTaskManager;
import ru.yandex.practicum.tasks.logic.TaskManager;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager inMemoryHistoryManager;
    @BeforeEach
    void setUp() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }


    @Test
    void addShouldAddTaskToHistory() {
        //Arrange
        Task task = new Task("task1", "descr1");
        Epic epic = new Epic("task2", "descr2");
        Subtask subtask = new Subtask("task3", "descr1");

        //Act
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.add(subtask);

        //Assert
        List<BaseTask> history = inMemoryHistoryManager.getHistory();
        assertEquals(3, history.size());
        BaseTask firstTask = history.get(0);
        assertEquals(task, firstTask);

        BaseTask secondTask = history.get(1);
        assertEquals(epic, secondTask);

        BaseTask thirdTask = history.get(2);
        assertEquals(subtask, thirdTask);
    }

    @Test
    void getHistoryShouldReturnHistory() {
        //Arrange
        Task task = new Task("task1", "descr1");
        Epic epic = new Epic("task2", "descr2");
        Subtask subtask = new Subtask("task3", "descr1");
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.add(subtask);

        //Act
        List<BaseTask> history = inMemoryHistoryManager.getHistory();

        //Assert
        assertEquals(3, history.size());
        BaseTask firstTask = history.get(0);
        assertEquals(task, firstTask);

        BaseTask secondTask = history.get(1);
        assertEquals(epic, secondTask);

        BaseTask thirdTask = history.get(2);
        assertEquals(subtask, thirdTask);

        //да, тела методов addShouldAddTaskToHistory, getHistoryShouldReturnHistory получились одинаковые
    }


    @Test
    void getHistoryShouldBeAbleToReturnPreviousTask() {
        //Arrange
        Task task = new Task("task1", "descr1");
        TaskManager taskManager = new InMemoryTaskManager(inMemoryHistoryManager);

        //Act
        inMemoryHistoryManager.add(task);
        taskManager.add(task);
        taskManager.setStatus(task, Status.IN_PROGRESS);
        inMemoryHistoryManager.add(task);

        //Assert
        List<BaseTask> history = inMemoryHistoryManager.getHistory();
        assertEquals(2, history.size());
        BaseTask firstTask = history.get(0);
        assertEquals(Status.NEW, firstTask.getStatus());

        BaseTask secondTask = history.get(1);
        assertEquals(Status.IN_PROGRESS, secondTask.getStatus());
    }

    @Test
    void getHistoryShouldReturnTaskWhichWasReturnedByGetTask() {
        //Arrange
        Task task = new Task("task1", "descr1");
        TaskManager taskManager = new InMemoryTaskManager(inMemoryHistoryManager);
        taskManager.add(task);

        //Act
        taskManager.getTask(task.getId());

        //Assert
        List<BaseTask> history = inMemoryHistoryManager.getHistory();
        assertEquals(1, history.size());
        BaseTask firstTask = history.get(0);
        assertEquals(task, firstTask);
    }

    @Test
    void getHistoryShouldReturnSubtasksIfGetSubtasksOfEpicCalled() {
        //Arrange
        Task task = new Task("task1", "descr1");
        Epic epic = new Epic("epic1", "descr epic");
        Subtask subtask1 = new Subtask("subtask1", "descr epic");
        Subtask subtask2 = new Subtask("subtask2", "descr epic");

        TaskManager taskManager = new InMemoryTaskManager(inMemoryHistoryManager);
        taskManager.add(task);
        taskManager.add(epic);
        taskManager.add(subtask1, epic.getId());
        taskManager.add(subtask2, epic.getId());

        //Act
        taskManager.getSubtasksOfEpic(epic.getId());

        //Assert
        List<BaseTask> history = inMemoryHistoryManager.getHistory();
        assertEquals(2, history.size());
        BaseTask firstTask = history.get(0);
        assertEquals(subtask1, firstTask);
        BaseTask secondTask = history.get(1);
        assertEquals(subtask2, secondTask);
    }
}