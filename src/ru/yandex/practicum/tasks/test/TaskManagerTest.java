package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.exceptions.WrongTaskTypeException;
import ru.yandex.practicum.tasks.logic.TaskManager;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;

    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Test
    void clearTasks_shouldClearOnlyTasks() {
        //Arrange
        Task task1 = new Task("task1","descr1");
        Task task2 = new Task("task2", "descr2");
        taskManager.add(task1);
        taskManager.add(task2);
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        taskManager.add(epic1);
        taskManager.add(epic2);

        subtask1.setEpicId(epic1.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic1.getId());
        taskManager.add(subtask2);
        subtask3.setEpicId(epic2.getId());
        taskManager.add(subtask3);

        //Act
        taskManager.clearTasks();

        //Assert
        List<Task> allTasks = taskManager.getListTasks();
        assertEquals(0, allTasks.size());
        List<Epic> allEpics = taskManager.getListEpics();
        assertEquals(2, allEpics.size());
        List<Subtask> allSubtasks = taskManager.getListSubtasks();
        assertEquals(3, allSubtasks.size());
    }

    @Test
    void clearSubtasks_shouldClearOnlySubtasks() {
        //Arrange
        Task task1 = new Task("task1","descr1");
        Task task2 = new Task("task2", "descr2");
        taskManager.add(task1);
        taskManager.add(task2);
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        taskManager.add(epic1);
        taskManager.add(epic2);

        subtask1.setEpicId(epic1.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic1.getId());
        taskManager.add(subtask2);
        subtask3.setEpicId(epic2.getId());
        taskManager.add(subtask3);

        //Act
        taskManager.clearSubTasks();

        //Assert
        List<Task> allTasks = taskManager.getListTasks();
        assertEquals(2, allTasks.size());
        List<Epic> allEpics = taskManager.getListEpics();
        assertEquals(2, allEpics.size());
        List<Subtask> allSubtasks = taskManager.getListSubtasks();
        assertEquals(0, allSubtasks.size());
    }

    @Test
    void clearEpics_shouldClearEpicsAndItsSubtasks() {
        //Arrange
        Task task1 = new Task("task1","descr1");
        Task task2 = new Task("task2", "descr2");
        taskManager.add(task1);
        taskManager.add(task2);
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        taskManager.add(epic1);
        taskManager.add(epic2);

        subtask1.setEpicId(epic1.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic1.getId());
        taskManager.add(subtask2);
        subtask3.setEpicId(epic2.getId());
        taskManager.add(subtask3);

        //Act
        taskManager.clearEpics();

        //Assert
        List<Task> allTasks = taskManager.getListTasks();
        assertEquals(2, allTasks.size());
        List<Epic> allEpics = taskManager.getListEpics();
        assertEquals(0, allEpics.size());
        List<Subtask> allSubtasks = taskManager.getListSubtasks();
        assertEquals(0, allSubtasks.size());
    }

    @Test
    void add_shouldAllowToAddTask() {
        //Arrange
        Task task = new Task("task1", "descr");

        //Act
        taskManager.add(task);

        //Assert
        Task foundTask = taskManager.getTask(task.getId());
        assertEquals("descr", foundTask.getDescription());
    }

    @Test
    void add_shouldAllowToAddEpic() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");

        //Act
        taskManager.add(epic);

        //Assert
        Epic foundEpic = taskManager.getEpic(epic.getId());
        assertEquals("descr", foundEpic.getDescription());
    }

    @Test
    void add_shouldAllowToAddSubtask() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        taskManager.add(epic);
        Subtask subtask = new Subtask("subtask1", "descr");

        //Act
        subtask.setEpicId(epic.getId());
        taskManager.add(subtask);

        //Assert
        Subtask foundSubtask = taskManager.getSubtask(subtask.getId());
        assertEquals("descr", foundSubtask.getDescription());
    }

    @Test
    void add_shouldAllowToAddTasksWithPresetIdAndGeneratedId() {
        //Фраза из ТЗ:
        //"проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;"
        //Arrange
        Task task1 = new Task("abc1", "descr1");
        Task task2 = new Task("abc2", "descr2");

        //Act
        taskManager.add(task1);
        task2.setId(1);
        taskManager.add(task2);

        //Assert
        List<Task> tasks = taskManager.getListTasks();
        assertEquals(2, tasks.size());
        Task task = tasks.stream().filter(t -> t.getName().equals("abc1")).findFirst().orElseThrow();
        assertEquals(1, task.getId());
        assertEquals("abc1", task.getName());
        assertEquals("descr1", task.getDescription());
        task = tasks.stream().filter(t -> t.getName().equals("abc2")).findFirst().orElseThrow();
        assertEquals(2, task.getId());
        assertEquals("abc2", task.getName());
        assertEquals("descr2", task.getDescription());
    }

    @Test
    void getTask_shouldReturnTheSameTaskWhichWasUsedInMethodAdd() {
        //Фраза из ТЗ:
        //"создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер"
        //Arrange
        Task task1 = new Task("abc1", "descr");
        task1.setStatus(Status.IN_PROGRESS);

        //Act
        taskManager.add(task1);
        Task foundTask = taskManager.getTask(task1.getId());

        //Assert
        assertEquals(task1.getId(), foundTask.getId());
        assertEquals(task1.getName(), foundTask.getName());
        assertEquals(task1.getDescription(), foundTask.getDescription());
        assertEquals(task1.getStatus(), foundTask.getStatus());
    }



    @Test
    void getSubtasksOfEpic_shouldReturnOnlySubtasksOfEpic() {
        //Arrange
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        taskManager.add(epic1);
        taskManager.add(epic2);

        subtask1.setEpicId(epic1.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic1.getId());
        taskManager.add(subtask2);
        subtask3.setEpicId(epic2.getId());
        taskManager.add(subtask3);

        //Act
        List<Subtask> subtasks = taskManager.getSubtasksOfEpic(epic1.getId());

        //Assert
        assertEquals(2, subtasks.size());
        long count = subtasks.stream().filter(st -> st.getName().equals("subtask3")).count();
        assertEquals(0, count);
    }

    @Test
    void getListTasks_shouldReturnAllTasks() {
        //Arrange
        Task task1 = new Task("task1", "descr");
        Task task2 = new Task("task2", "descr");

        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        taskManager.add(task1);
        taskManager.add(task2);

        taskManager.add(epic1);
        taskManager.add(epic2);

        subtask1.setEpicId(epic1.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic1.getId());
        taskManager.add(subtask2);
        subtask3.setEpicId(epic2.getId());
        taskManager.add(subtask3);

        //Act
        List<Task> tasks = taskManager.getListTasks();

        //Assert
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(st -> st.getTaskType() == TaskType.TASK));
    }

    @Test
    void getListEpics_shouldReturnAllEpics() {
        //Arrange
        Task task1 = new Task("task1", "descr");
        Task task2 = new Task("task2", "descr");

        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        taskManager.add(task1);
        taskManager.add(task2);

        taskManager.add(epic1);
        taskManager.add(epic2);

        subtask1.setEpicId(epic1.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic1.getId());
        taskManager.add(subtask2);
        subtask3.setEpicId(epic2.getId());
        taskManager.add(subtask3);

        //Act
        List<Epic> epics = taskManager.getListEpics();

        //Assert
        assertEquals(2, epics.size());
        assertTrue(epics.stream().allMatch(st -> st.getTaskType() == TaskType.EPIC));
    }

    @Test
    void getListSubtasks_shouldReturnAllSubtasks() {
        //Arrange
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        taskManager.add(epic1);
        taskManager.add(epic2);

        subtask1.setEpicId(epic1.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic1.getId());
        taskManager.add(subtask2);
        subtask3.setEpicId(epic2.getId());
        taskManager.add(subtask3);

        //Act
        List<Subtask> subtasks = taskManager.getListSubtasks();

        //Assert
        assertEquals(3, subtasks.size());
        assertTrue(subtasks.stream().allMatch(st -> st.getTaskType() == TaskType.SUBTASK));
    }

    @Test
    void setStatus_shouldAllowToBeCalledForTask() {
        //Arrange
        Task task = new Task("task1", "descr");
        taskManager.add(task);

        //Act
        taskManager.setStatus(task, Status.IN_PROGRESS);

        //Assert
        Task foundTask = taskManager.getTask(task.getId());
        assertEquals(Status.IN_PROGRESS, foundTask.getStatus());
    }

    @Test
    void setStatus_shouldAllowToBeCalledForTask2() {
        //Arrange
        Task task = new Task("task1", "descr");
        taskManager.add(task);

        //Act
        taskManager.setStatus(task.getId(), Status.IN_PROGRESS);

        //Assert
        Task foundTask = taskManager.getTask(task.getId());
        assertEquals(Status.IN_PROGRESS, foundTask.getStatus());
    }

    @Test
    void setStatus_shouldAllowToBeCalledForSubtask() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask = new Subtask("subtask1", "descr");
        taskManager.add(epic);
        subtask.setEpicId(epic.getId());
        taskManager.add(subtask);

        //Act
        taskManager.setStatus(subtask, Status.IN_PROGRESS);

        //Assert
        Subtask foundTask = taskManager.getSubtask(subtask.getId());
        assertEquals(Status.IN_PROGRESS, foundTask.getStatus());
    }

    @Test
    void setStatus_shouldAllowToBeCalledForSubtask2() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask = new Subtask("subtask1", "descr");
        taskManager.add(epic);
        subtask.setEpicId(epic.getId());
        taskManager.add(subtask);

        //Act
        taskManager.setStatus(subtask.getId(), Status.IN_PROGRESS);

        //Assert
        Subtask foundTask = taskManager.getSubtask(subtask.getId());
        assertEquals(Status.IN_PROGRESS, foundTask.getStatus());
    }

    @Test
    void setStatus_shouldNotAllowToBeCalledForEpic() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        taskManager.add(epic);

        //Act && Assert
        try {
            taskManager.setStatus(epic.getId(), Status.IN_PROGRESS);
        } catch (WrongTaskTypeException e) {
            return;
        }
        fail();
    }

    @Test
    void setStatus_shouldSetStatusOfEpicToInProgressIfOneSubtaskIsInProgress() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        taskManager.add(epic);
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        subtask1.setEpicId(epic.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic.getId());
        taskManager.add(subtask2);

        //Act
        taskManager.setStatus(subtask1, Status.IN_PROGRESS);
        taskManager.setStatus(subtask2, Status.DONE);

        //Assert
        epic = taskManager.getEpic(epic.getId());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void setStatus_shouldSetStatusOfEpicToDoneIfAllSubtasksAreDone() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        taskManager.add(epic);
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        subtask1.setEpicId(epic.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic.getId());
        taskManager.add(subtask2);

        //Act
        taskManager.setStatus(subtask1, Status.DONE);
        taskManager.setStatus(subtask2, Status.DONE);

        //Assert
        epic = taskManager.getEpic(epic.getId());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void setStatus_shouldSetStatusOfEpicToNewIfAllSubtasksAreNew() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        taskManager.add(epic);
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        subtask1.setEpicId(epic.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic.getId());
        taskManager.add(subtask2);

        //Act
        taskManager.setStatus(subtask1, Status.NEW);
        taskManager.setStatus(subtask2, Status.NEW);

        //Assert
        epic = taskManager.getEpic(epic.getId());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void removeTask_shouldRemoveTask() {
        //Arrange
        Task task1 = new Task("task1", "descr1");
        Task task2 = new Task("task2", "descr2");
        taskManager.add(task1);
        taskManager.add(task2);

        //Act
        taskManager.removeTask(task1.getId());

        //Assert
        List<Task> tasks = taskManager.getListTasks();
        assertEquals(1, tasks.size());
        Task foundTask = tasks.stream().findFirst().orElseThrow();
        assertEquals("descr2", foundTask.getDescription());
    }

    //Внутри эпиков не должно оставаться неактуальных id подзадач.
    @Test
    void removeSubTask_shouldRemoveSubtask() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        taskManager.add(epic);
        Subtask subtask = new Subtask("subtask1", "descr");
        subtask.setEpicId(epic.getId());
        taskManager.add(subtask);

        //Act
        taskManager.removeSubTask(subtask.getId());

        //Assert
        List<Subtask> subtasks = taskManager.getListSubtasks();
        List<Subtask> subtasks2 = taskManager.getSubtasksOfEpic(epic.getId());
        assertEquals(0, subtasks.size());
        assertEquals(0, subtasks2.size());
    }


    @Test
    void removeEpic_shouldRemoveEpicAndItsSubtasks() {
        //Arrange
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic1", "descr");
        taskManager.add(epic1);
        taskManager.add(epic2);
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        Subtask subtask3 = new Subtask("subtask3", "descr3");

        subtask1.setEpicId(epic1.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic1.getId());
        taskManager.add(subtask2);
        subtask3.setEpicId(epic2.getId());
        taskManager.add(subtask3);

        //Act
        taskManager.removeEpic(epic1.getId());

        //Arrange
        assertThrowsExactly(TaskNotFoundException.class, () -> taskManager.getSubtasksOfEpic(epic1.getId()));
        List<Subtask> allSubtasks = taskManager.getListSubtasks();
        List<Epic> epics = taskManager.getListEpics();
        assertEquals(1, allSubtasks.size());
        assertEquals(1, epics.size());
    }

    @Test
    void remove_shouldBeAbleToRemoveTask() {
        //Arrange
        Task task1 = new Task("task1", "descr1");
        Task task2 = new Task("task2", "descr2");
        Epic epic1 = new Epic("epic1", "descr1");
        Epic epic2 = new Epic("epic1", "descr2");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        taskManager.add(task1);
        taskManager.add(task2);
        taskManager.add(epic1);
        taskManager.add(epic2);
        subtask1.setEpicId(epic1.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic2.getId());
        taskManager.add(subtask2);

        //Act
        taskManager.remove(task1);

        //Assert
        List<Task> tasks = taskManager.getListTasks();
        assertEquals(1, tasks.size());
        assertThrowsExactly(TaskNotFoundException.class, () -> taskManager.getTask(task1.getId()));
    }

    @Test
    void remove_shouldBeAbleToRemoveEpic() {
        //Arrange
        Task task1 = new Task("task1", "descr1");
        Task task2 = new Task("task2", "descr2");
        Epic epic1 = new Epic("epic1", "descr1");
        Epic epic2 = new Epic("epic1", "descr2");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        taskManager.add(task1);
        taskManager.add(task2);
        taskManager.add(epic1);
        taskManager.add(epic2);
        subtask1.setEpicId(epic1.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic2.getId());
        taskManager.add(subtask2);

        //Act
        taskManager.remove(epic1);

        //Assert
        List<Epic> epics = taskManager.getListEpics();
        assertEquals(1, epics.size());
        assertThrowsExactly(TaskNotFoundException.class, () -> taskManager.getEpic(epic1.getId()));
        assertThrowsExactly(TaskNotFoundException.class, () -> taskManager.getSubtasksOfEpic(epic1.getId()));
        List<Subtask> subtasks = taskManager.getListSubtasks();
        assertEquals(1, subtasks.size());
    }

    @Test
    void remove_shouldBeAbleToRemoveSubtask() {
        //Arrange
        Task task1 = new Task("task1", "descr1");
        Task task2 = new Task("task2", "descr2");
        Epic epic1 = new Epic("epic1", "descr1");
        Epic epic2 = new Epic("epic1", "descr2");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        taskManager.add(task1);
        taskManager.add(task2);
        taskManager.add(epic1);
        taskManager.add(epic2);
        subtask1.setEpicId(epic1.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic2.getId());
        taskManager.add(subtask2);

        //Act
        taskManager.remove(subtask1);

        //Assert
        List<Subtask> subtasks = taskManager.getListSubtasks();
        assertEquals(1, subtasks.size());
        assertThrowsExactly(TaskNotFoundException.class, () -> taskManager.getSubtask(subtask1.getId()));

        List<Subtask> subtasksOfEpic1 = taskManager.getSubtasksOfEpic(epic1.getId());
        assertEquals(0, subtasksOfEpic1.size());
    }

    @Test
    void add_epicMustNotBeAllowedToAddedAsSubtaskToItself() {
        //Фраза из ТЗ:
        //"проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;"
        //Arrange
        Epic epic = new Epic("epic", "descr1");
        taskManager.add(epic);
        int epicId = taskManager.getListEpics().stream().findFirst().stream().findFirst().orElseThrow().getId();
        //Act
        //Метод добавления сабтаски не принимает класс Epic,
        //код ниже не скомпилится. Тест сделан для того, чтобы ревьювер не сказал, что нет метода, проверяющего, что Epic
        //нельзя добавить в самого себя

        //inMemoryTaskManager.add(epic, epicId) <-- Код закоменчен, читаем комментарий выше, почему!!!

        //Assert
    }

    @Test
    void add_subTaskMustNotBeAllowedToAddedAsSubtaskToItself() {
        //Фраза из ТЗ:
        //"проверьте, что объект Subtask нельзя сделать своим же эпиком;"
        //Arrange
        Epic epic = new Epic("epic", "descr");
        Subtask subtask = new Subtask("subtask", "descr");
        taskManager.add(epic);
        int epicId = taskManager.getListEpics().stream().findFirst().stream().findFirst().orElseThrow().getId();
        subtask.setEpicId(epicId);
        taskManager.add(subtask);
        subtask.setEpicId(subtask.getId());
        //Act && Arrange
        assertThrowsExactly(WrongTaskTypeException.class, () -> taskManager.add(subtask));
    }

    //С помощью сеттеров экземпляры задач позволяют изменить любое своё поле, но это может повлиять на данные внутри менеджера.
    // Протестируйте эти кейсы и подумайте над возможными вариантами решения проблемы.
    @Test
    void add_shouldCreateCopyOfTask() {
        //Arrange
        Task task = new Task("name1", "descr1");
        taskManager.add(task);
        int id = task.getId();

        //Act
        task.setId(3333);
        task.setName("name2");
        task.setDescription("descr2");
        task.setStatus(Status.DONE);

        //Assert
        Task foundTask = taskManager.getTask(id);
        assertNotNull(foundTask);
        assertEquals("name1", foundTask.getName());
        assertEquals("descr1", foundTask.getDescription());
    }

    //С помощью сеттеров экземпляры задач позволяют изменить любое своё поле, но это может повлиять на данные внутри менеджера.
    //Протестируйте эти кейсы и подумайте над возможными вариантами решения проблемы.
    @Test
    void setEpicId_changingEpicIdOfSubtaskShouldNotAffectSubtaskInManager() {
        //Arrange
        Epic epic = new Epic("epic1", "descr1");
        Epic epic2 = new Epic("epic1", "descr2");
        Subtask subtask1 = new Subtask("subtask1", "descr3");
        Subtask subtask2 = new Subtask("subtask2", "descr4");
        taskManager.add(epic);
        taskManager.add(epic2);
        subtask1.setEpicId(epic.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic2.getId());
        taskManager.add(subtask2);

        //Act
        subtask2.setEpicId(subtask1.getEpicId());

        //Assert
        List<Subtask> subtasks = taskManager.getSubtasksOfEpic(epic.getId());
        assertEquals(1, subtasks.size());
        assertEquals("subtask1", subtasks.getFirst().getName());
        assertEquals("descr3", subtasks.getFirst().getDescription());
    }

    //С помощью сеттеров экземпляры задач позволяют изменить любое своё поле, но это может повлиять на данные внутри менеджера.
    //Протестируйте эти кейсы и подумайте над возможными вариантами решения проблемы.
    @Test
    void setStatus_changingStatusOfSubtaskShouldNotAffectEpicInManager() {
        //Arrange
        Epic epic = new Epic("epic1", "descr1");
        Subtask subtask1 = new Subtask("subtask1", "descr3");
        Subtask subtask2 = new Subtask("subtask2", "descr4");
        taskManager.add(epic);
        subtask1.setEpicId(epic.getId());
        taskManager.add(subtask1);
        subtask2.setEpicId(epic.getId());
        taskManager.add(subtask2);

        //Act
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        //Assert
        epic = taskManager.getEpic(epic.getId());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void getPrioritizedTasks_shouldReturnTasksSortedByStartTime() {
        //Arrange
        Task task = new Task("task1", "descr");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter));
        taskManager.add(task);

        Epic epic = new Epic("epic1", "descr2");
        taskManager.add(epic);
        Subtask subtask = new Subtask("subtask1", "descr3");
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 10:00", dateTimeFormatter));
        subtask.setEpicId(epic.getId());
        taskManager.add(subtask);

        Epic epic2 = new Epic("epic2", "descr2");
        taskManager.add(epic2);
        Subtask subtask2 = new Subtask("subtask2", "descr3");
        subtask2.setStartTime(LocalDateTime.parse("01.01.2025 13:00", dateTimeFormatter));
        subtask2.setEpicId(epic2.getId());
        taskManager.add(subtask2);

        //Act
        List<BaseTask> tasks = taskManager.getPrioritizedTasks();

        //Assert
        assertEquals(LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter), tasks.getFirst().getStartTime());
        assertEquals(LocalDateTime.parse("01.01.2025 10:00", dateTimeFormatter), tasks.get(1).getStartTime());
        assertEquals(LocalDateTime.parse("01.01.2025 13:00", dateTimeFormatter), tasks.getLast().getStartTime());
    }

    @Test
    void getPrioritizedTasks_shouldIgnoreTasksWithNullStartTime() {
        //Arrange
        Task task = new Task("task1", "descr");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter));
        taskManager.add(task);

        Epic epic = new Epic("epic1", "descr2");
        taskManager.add(epic);
        Subtask subtask = new Subtask("subtask1", "descr3");
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 10:00", dateTimeFormatter));
        subtask.setEpicId(epic.getId());
        taskManager.add(subtask);

        Epic epic2 = new Epic("epic2", "descr2");
        taskManager.add(epic2);
        Subtask subtask2 = new Subtask("subtask2", "descr3");
        subtask2.setStartTime(null);
        subtask2.setEpicId(epic2.getId());
        taskManager.add(subtask2);

        //Act
        List<BaseTask> tasks = taskManager.getPrioritizedTasks();

        //Assert
        assertEquals(2, tasks.size());
        assertEquals(LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter), tasks.getFirst().getStartTime());
        assertEquals(LocalDateTime.parse("01.01.2025 10:00", dateTimeFormatter), tasks.getLast().getStartTime());
    }

    @Test
    void getHistory_shouldReturnHistory() {
        //!!! Методы тестирования HistoryManager.getHistory находятся в InMemoryHistoryManagerTest

        //Arrange
        Task task = new Task("task1", "descr");
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask = new Subtask("subtask1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        taskManager.add(task);
        taskManager.add(epic);
        subtask.setEpicId(epic.getId());
        taskManager.add(subtask);
        taskManager.add(epic2);
        subtask2.setEpicId(epic2.getId());
        taskManager.add(subtask2);

        //Act
        taskManager.getSubtask(subtask2.getId());
        taskManager.getTask(task.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getEpic(epic.getId());
        List<BaseTask> history = taskManager.getHistory();

        //Assert
        assertEquals(4, history.size());
        assertEquals("subtask2", history.getFirst().getName());
        assertEquals("task1", history.get(1).getName());
        assertEquals("epic2", history.get(2).getName());
        assertEquals("epic1", history.get(3).getName());
    }
}
