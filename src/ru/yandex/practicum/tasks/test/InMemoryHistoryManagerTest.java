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
    void add_shouldAddTaskToHistory() {
        //Arrange
        Task task = new Task("task1", "descr1");
        task.setId(1);
        Epic epic = new Epic("task2", "descr2");
        epic.setId(2);
        Subtask subtask = new Subtask("task3", "descr1");
        subtask.setId(3);

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
    void getHistory_shouldReturnHistory() {
        //Arrange
        Task task = new Task("task1", "descr1");
        task.setId(1);
        Epic epic = new Epic("task2", "descr2");
        epic.setId(2);
        Subtask subtask = new Subtask("task3", "descr1");
        subtask.setId(3);
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


    //Проверка на дублирование
    @Test
    void getHistory_shouldReturnLastVersionOfTask() {
        //Arrange
        Task task = new Task("task1", "descr1");
        TaskManager taskManager = new InMemoryTaskManager(inMemoryHistoryManager);

        //Act
        taskManager.add(task);//присваиваем таске id
        inMemoryHistoryManager.add(task);
        taskManager.setStatus(task, Status.IN_PROGRESS);
        task = taskManager.getTask(task.getId());
        inMemoryHistoryManager.add(task);

        //Assert
        List<BaseTask> history = inMemoryHistoryManager.getHistory();
        assertEquals(1, history.size());
        BaseTask taskInHistory = history.getFirst();
        assertEquals(Status.IN_PROGRESS, taskInHistory.getStatus());
    }

    @Test
    void getHistory_shouldReturnTaskWhichWasReturnedByGetTask() {
        //Arrange
        Task task = new Task("task1", "descr1");
        TaskManager taskManager = new InMemoryTaskManager(inMemoryHistoryManager);
        taskManager.add(task);

        //Act
        taskManager.getTask(task.getId());

        //Assert
        List<BaseTask> history = inMemoryHistoryManager.getHistory();
        assertEquals(1, history.size());
        BaseTask firstTask = history.getFirst();
        assertEquals(task, firstTask);
    }

    @Test
    void getHistory_shouldReturnSubtasksIfGetSubtasksOfEpicCalled() {
        //Arrange
        Task task = new Task("task1", "descr1");
        Epic epic = new Epic("epic1", "descr epic");
        Subtask subtask1 = new Subtask("subtask1", "descr epic");
        Subtask subtask2 = new Subtask("subtask2", "descr epic");

        TaskManager taskManager = new InMemoryTaskManager(inMemoryHistoryManager);
        taskManager.add(task);
        taskManager.add(epic);
        subtask1.setEpicId(epic.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic.getId());
        taskManager.add(subtask2);

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

    @Test
    void getHistory_shouldReturnOnlyLastTask() {
        //Arrange
        Task task = new Task("task1", "descr1");
        Task task2 = new Task("task2", "descr2");

        TaskManager taskManager = new InMemoryTaskManager(inMemoryHistoryManager);
        taskManager.add(task);
        taskManager.add(task2);

        //Act
        taskManager.getTask(task.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task.getId());

        //Assert
        List<BaseTask> history = inMemoryHistoryManager.getHistory();
        assertEquals(2, history.size());
        BaseTask firstTaskInHistory = history.get(0);
        assertEquals(task2, firstTaskInHistory);
        BaseTask secondTaskInHistory = history.get(1);
        assertEquals(task, secondTaskInHistory);
    }

    @Test
    void getHistory_fromStartShouldReturnEmptyHistory() {
        //Act
        List<BaseTask> history = inMemoryHistoryManager.getHistory();

        //Arrange
        assertEquals(0, history.size());
    }

    @Test
    void remove_shouldDeleteTaskFromHistory() {
        //Arrange
        Task task = new Task("task1", "descr1");
        task.setId(1);
        Epic epic = new Epic("task2", "descr2");
        epic.setId(2);
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic);

        //Act
        inMemoryHistoryManager.remove(epic.getId());

        //Assert
        List<BaseTask> history = inMemoryHistoryManager.getHistory();
        assertEquals(1, history.size());
        BaseTask firstTaskInHistory = history.getFirst();
        assertEquals(task, firstTaskInHistory);
    }

    //удаляем из начала списка
    @Test
    void remove_shouldBeAbleToDeleteTheFirstTaskFromHistory() {
        //Arrange
        Task task = new Task("task1", "descr1");
        task.setId(1);
        Epic epic1 = new Epic("epic1", "descr2");
        epic1.setId(2);
        Epic epic2 = new Epic("epic2", "descr2");
        epic2.setId(3);
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic1);
        inMemoryHistoryManager.add(epic2);

        //Act
        inMemoryHistoryManager.remove(task.getId());

        //Assert
        List<BaseTask> history = inMemoryHistoryManager.getHistory();
        assertEquals(2, history.size());
        BaseTask firstTaskInHistory = history.getFirst();
        assertEquals(epic1, firstTaskInHistory);
        BaseTask lastTaskInHistory = history.getLast();
        assertEquals(epic2, lastTaskInHistory);
    }

    //удаляем из конца списка
    @Test
    void remove_shouldBeAbleToDeleteTheLastTaskFromHistory() {
        //Arrange
        Task task = new Task("task1", "descr1");
        task.setId(1);
        Epic epic1 = new Epic("epic1", "descr2");
        epic1.setId(2);
        Epic epic2 = new Epic("epic2", "descr2");
        epic2.setId(3);
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic1);
        inMemoryHistoryManager.add(epic2);

        //Act
        inMemoryHistoryManager.remove(epic2.getId());

        //Assert
        List<BaseTask> history = inMemoryHistoryManager.getHistory();
        assertEquals(2, history.size());
        BaseTask firstTaskInHistory = history.getFirst();
        assertEquals(task, firstTaskInHistory);
        BaseTask lastTaskInHistory = history.getLast();
        assertEquals(epic1, lastTaskInHistory);
    }

    //удаляем из середины списка
    @Test
    void remove_shouldBeAbleToDeleteTaskFromTheMiddleOfHistory() {
        //Arrange
        Task task = new Task("task1", "descr1");
        task.setId(1);
        Epic epic1 = new Epic("epic1", "descr2");
        epic1.setId(2);
        Epic epic2 = new Epic("epic2", "descr2");
        epic2.setId(3);
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic1);
        inMemoryHistoryManager.add(epic2);

        //Act
        inMemoryHistoryManager.remove(epic1.getId());

        //Assert
        List<BaseTask> history = inMemoryHistoryManager.getHistory();
        assertEquals(2, history.size());
        BaseTask firstTaskInHistory = history.getFirst();
        assertEquals(task, firstTaskInHistory);
        BaseTask lastTaskInHistory = history.getLast();
        assertEquals(epic2, lastTaskInHistory);
    }
}