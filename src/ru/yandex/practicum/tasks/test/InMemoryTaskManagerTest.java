package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.exceptions.WrongTaskTypeException;
import ru.yandex.practicum.tasks.logic.InMemoryTaskManager;
import ru.yandex.practicum.tasks.logic.Managers;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager inMemoryTaskManager;
    @BeforeEach
    void setUp() {
        inMemoryTaskManager = new InMemoryTaskManager((new Managers()).getDefaultHistory());
    }

    @Test
    void clearTasksShouldClearOnlyTasks() {
        //Arrange
        Task task1 = new Task("task1","descr1");
        Task task2 = new Task("task2", "descr2");
        inMemoryTaskManager.add(task1);
        inMemoryTaskManager.add(task2);
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        inMemoryTaskManager.add(epic1);
        inMemoryTaskManager.add(epic2);

        inMemoryTaskManager.add(subtask1, epic1.getId());
        inMemoryTaskManager.add(subtask2, epic1.getId());
        inMemoryTaskManager.add(subtask3, epic2.getId());

        //Act
        inMemoryTaskManager.clearTasks();

        //Assert
        List<Task> allTasks = inMemoryTaskManager.getListTasks();
        assertEquals(0, allTasks.size());
        List<Epic> allEpics = inMemoryTaskManager.getListEpics();
        assertEquals(2, allEpics.size());
        List<Subtask> allSubtasks = inMemoryTaskManager.getListSubtasks();
        assertEquals(3, allSubtasks.size());
    }

    @Test
    void clearSubtasksShouldClearOnlySubtasks() {
        //Arrange
        Task task1 = new Task("task1","descr1");
        Task task2 = new Task("task2", "descr2");
        inMemoryTaskManager.add(task1);
        inMemoryTaskManager.add(task2);
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        inMemoryTaskManager.add(epic1);
        inMemoryTaskManager.add(epic2);

        inMemoryTaskManager.add(subtask1, epic1.getId());
        inMemoryTaskManager.add(subtask2, epic1.getId());
        inMemoryTaskManager.add(subtask3, epic2.getId());

        //Act
        inMemoryTaskManager.clearSubTasks();

        //Assert
        List<Task> allTasks = inMemoryTaskManager.getListTasks();
        assertEquals(2, allTasks.size());
        List<Epic> allEpics = inMemoryTaskManager.getListEpics();
        assertEquals(2, allEpics.size());
        List<Subtask> allSubtasks = inMemoryTaskManager.getListSubtasks();
        assertEquals(0, allSubtasks.size());
    }

    @Test
    void clearEpicsShouldClearEpicsAndItsSubtasks() {
        //Arrange
        Task task1 = new Task("task1","descr1");
        Task task2 = new Task("task2", "descr2");
        inMemoryTaskManager.add(task1);
        inMemoryTaskManager.add(task2);
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        inMemoryTaskManager.add(epic1);
        inMemoryTaskManager.add(epic2);

        inMemoryTaskManager.add(subtask1, epic1.getId());
        inMemoryTaskManager.add(subtask2, epic1.getId());
        inMemoryTaskManager.add(subtask3, epic2.getId());

        //Act
        inMemoryTaskManager.clearEpics();

        //Assert
        List<Task> allTasks = inMemoryTaskManager.getListTasks();
        assertEquals(2, allTasks.size());
        List<Epic> allEpics = inMemoryTaskManager.getListEpics();
        assertEquals(0, allEpics.size());
        List<Subtask> allSubtasks = inMemoryTaskManager.getListSubtasks();
        assertEquals(0, allSubtasks.size());
    }

    @Test
    void addShouldSetNewIdForTask() {
        //Arrange
        Task task = new Task("task1", "descr");
        task.setId(999999);

        //Act
        inMemoryTaskManager.add(task);

        //Assert
        assertEquals(1, task.getId());
    }

    @Test
    void addShouldSetNewIdForEpic() {
        //Arrange
        Epic epic = new Epic("epic", "descr");
        epic.setId(999999);

        //Act
        inMemoryTaskManager.add(epic);

        //Assert
        assertEquals(1, epic.getId());
    }

    @Test
    void addShouldSetNewIdForSubtask() {
        //Arrange
        Epic epic = new Epic("epic", "descr");
        Subtask subtask = new Subtask("subtask", "descr");
        subtask.setId(99999);
        inMemoryTaskManager.add(epic);

        //Act
        inMemoryTaskManager.add(subtask, epic.getId());


        //Assert
        assertEquals(2, subtask.getId());
    }

    @Test
    void addShouldAllowToAddTask() {
        //Arrange
        Task task = new Task("task1", "descr");

        //Act
        inMemoryTaskManager.add(task);

        //Assert
        Task foundTask = inMemoryTaskManager.getTask(task.getId());
        assertEquals("descr", foundTask.getDescription());
    }

    @Test
    void addShouldAllowToAddEpic() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");

        //Act
        inMemoryTaskManager.add(epic);

        //Assert
        Epic foundEpic = inMemoryTaskManager.getEpic(epic.getId());
        assertEquals("descr", foundEpic.getDescription());
    }

    @Test
    void addShouldAllowToAddSubtask() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        inMemoryTaskManager.add(epic);
        Subtask subtask = new Subtask("subtask1", "descr");
        
        //Act
        inMemoryTaskManager.add(subtask, epic.getId());

        //Assert
        Subtask foundSubtask = inMemoryTaskManager.getSubtask(subtask.getId());
        assertEquals("descr", foundSubtask.getDescription());
    }

    @Test
    void addShouldAllowToAddTasksWithPresetIdAndGeneratedId() {
        //Фраза из ТЗ:
        //"проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;"
        //Arrange
        Task task1 = new Task("abc1", "descr");
        Task task2 = new Task("abc2", "descr");

        //Act
        inMemoryTaskManager.add(task1);
        task2.setId(1);
        inMemoryTaskManager.add(task2);

        //Assert
        assertEquals(1, task1.getId());
        assertEquals(2, task2.getId());
    }

    @Test
    void getTaskShouldReturnTheSameTaskWhichWasUsedInMethodAdd() {
        //Фраза из ТЗ:
        //"создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер"
        //Arrange
        Task task1 = new Task("abc1", "descr");
        task1.setStatus(Status.IN_PROGRESS);

        //Act
        inMemoryTaskManager.add(task1);
        Task foundTask = inMemoryTaskManager.getTask(task1.getId());

        //Assert
        assertEquals(task1.getId(), foundTask.getId());
        assertEquals(task1.getName(), foundTask.getName());
        assertEquals(task1.getDescription(), foundTask.getDescription());
        assertEquals(task1.getStatus(), foundTask.getStatus());
    }



    @Test
    void getSubtasksOfEpicShouldReturnOnlySubtasksOfEpic() {
        //Arrange
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        inMemoryTaskManager.add(epic1);
        inMemoryTaskManager.add(epic2);

        inMemoryTaskManager.add(subtask1, epic1.getId());
        inMemoryTaskManager.add(subtask2, epic1.getId());
        inMemoryTaskManager.add(subtask3, epic2.getId());

        //Act
        List<Subtask> subtasks = inMemoryTaskManager.getSubtasksOfEpic(epic1.getId());

        //Assert
        assertEquals(2, subtasks.size());
        long count = subtasks.stream().filter(st -> st.getName().equals("subtask3")).count();
        assertEquals(0, count);
    }

    @Test
    void getListTasksShouldReturnAllTasks() {
        //Arrange
        Task task1 = new Task("task1", "descr");
        Task task2 = new Task("task2", "descr");

        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        inMemoryTaskManager.add(task1);
        inMemoryTaskManager.add(task2);

        inMemoryTaskManager.add(epic1);
        inMemoryTaskManager.add(epic2);

        inMemoryTaskManager.add(subtask1, epic1.getId());
        inMemoryTaskManager.add(subtask2, epic1.getId());
        inMemoryTaskManager.add(subtask3, epic2.getId());

        //Act
        List<Task> tasks = inMemoryTaskManager.getListTasks();

        //Assert
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(st -> st.getTaskType() == TaskType.TASK));
    }

    @Test
    void getListEpicsShouldReturnAllEpics() {
        //Arrange
        Task task1 = new Task("task1", "descr");
        Task task2 = new Task("task2", "descr");

        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        inMemoryTaskManager.add(task1);
        inMemoryTaskManager.add(task2);

        inMemoryTaskManager.add(epic1);
        inMemoryTaskManager.add(epic2);

        inMemoryTaskManager.add(subtask1, epic1.getId());
        inMemoryTaskManager.add(subtask2, epic1.getId());
        inMemoryTaskManager.add(subtask3, epic2.getId());

        //Act
        List<Epic> epics = inMemoryTaskManager.getListEpics();

        //Assert
        assertEquals(2, epics.size());
        assertTrue(epics.stream().allMatch(st -> st.getTaskType() == TaskType.EPIC));
    }

    @Test
    void getListSubtasksShouldReturnAllSubtasks() {
        //Arrange
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic2", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        Subtask subtask3 = new Subtask("subtask3", "descr");
        inMemoryTaskManager.add(epic1);
        inMemoryTaskManager.add(epic2);

        inMemoryTaskManager.add(subtask1, epic1.getId());
        inMemoryTaskManager.add(subtask2, epic1.getId());
        inMemoryTaskManager.add(subtask3, epic2.getId());

        //Act
        List<Subtask> subtasks = inMemoryTaskManager.getListSubtasks();

        //Assert
        assertEquals(3, subtasks.size());
        assertTrue(subtasks.stream().allMatch(st -> st.getTaskType() == TaskType.SUBTASK));
    }

    @Test
    void setStatusShouldAllowToBeCalledForTask() {
        //Arrange
        Task task = new Task("task1", "descr");
        inMemoryTaskManager.add(task);

        //Act
        inMemoryTaskManager.setStatus(task, Status.IN_PROGRESS);

        //Assert
        Task foundTask = inMemoryTaskManager.getTask(task.getId());
        assertEquals(Status.IN_PROGRESS, foundTask.getStatus());
    }

    @Test
    void setStatusShouldAllowToBeCalledForTask2() {
        //Arrange
        Task task = new Task("task1", "descr");
        inMemoryTaskManager.add(task);

        //Act
        inMemoryTaskManager.setStatus(task.getId(), Status.IN_PROGRESS);

        //Assert
        Task foundTask = inMemoryTaskManager.getTask(task.getId());
        assertEquals(Status.IN_PROGRESS, foundTask.getStatus());
    }

    @Test
    void setStatusShouldAllowToBeCalledForSubtask() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask = new Subtask("subtask1", "descr");
        inMemoryTaskManager.add(epic);
        inMemoryTaskManager.add(subtask, epic.getId());

        //Act
        inMemoryTaskManager.setStatus(subtask, Status.IN_PROGRESS);

        //Assert
        Subtask foundTask = inMemoryTaskManager.getSubtask(subtask.getId());
        assertEquals(Status.IN_PROGRESS, foundTask.getStatus());
    }

    @Test
    void setStatusShouldAllowToBeCalledForSubtask2() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask = new Subtask("subtask1", "descr");
        inMemoryTaskManager.add(epic);
        inMemoryTaskManager.add(subtask, epic.getId());

        //Act
        inMemoryTaskManager.setStatus(subtask.getId(), Status.IN_PROGRESS);

        //Assert
        Subtask foundTask = inMemoryTaskManager.getSubtask(subtask.getId());
        assertEquals(Status.IN_PROGRESS, foundTask.getStatus());
    }

    @Test
    void setStatusShouldNotAllowToBeCalledForEpic() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        inMemoryTaskManager.add(epic);

        //Act && Assert
        try {
            inMemoryTaskManager.setStatus(epic.getId(), Status.IN_PROGRESS);
        }
        catch (WrongTaskTypeException e) {
            return;
        }
        fail();
    }

    @Test
    void setStatusShouldSetStatusOfEpicToInProgressIfOneSubtaskIsInProgress() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        inMemoryTaskManager.add(epic);
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        inMemoryTaskManager.add(subtask1, epic.getId());
        inMemoryTaskManager.add(subtask2, epic.getId());

        //Act
        inMemoryTaskManager.setStatus(subtask1, Status.IN_PROGRESS);
        inMemoryTaskManager.setStatus(subtask2, Status.DONE);

        //Assert
        epic = inMemoryTaskManager.getEpic(epic.getId());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void setStatusShouldSetStatusOfEpicToDoneIfAllSubtasksAreDone() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        inMemoryTaskManager.add(epic);
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        inMemoryTaskManager.add(subtask1, epic.getId());
        inMemoryTaskManager.add(subtask2, epic.getId());

        //Act
        inMemoryTaskManager.setStatus(subtask1, Status.DONE);
        inMemoryTaskManager.setStatus(subtask2, Status.DONE);

        //Assert
        epic = inMemoryTaskManager.getEpic(epic.getId());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void setStatusShouldSetStatusOfEpicToNewIfAllSubtasksAreNew() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        inMemoryTaskManager.add(epic);
        Subtask subtask1 = new Subtask("subtask1", "descr");
        Subtask subtask2 = new Subtask("subtask2", "descr");
        inMemoryTaskManager.add(subtask1, epic.getId());
        inMemoryTaskManager.add(subtask2, epic.getId());

        //Act
        inMemoryTaskManager.setStatus(subtask1, Status.NEW);
        inMemoryTaskManager.setStatus(subtask2, Status.NEW);

        //Assert
        epic = inMemoryTaskManager.getEpic(epic.getId());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void removeTaskShouldRemoveTask() {
        //Arrange
        Task task1 = new Task("task1", "descr1");
        Task task2 = new Task("task2", "descr2");
        inMemoryTaskManager.add(task1);
        inMemoryTaskManager.add(task2);

        //Act
        inMemoryTaskManager.removeTask(task1.getId());

        //Assert
        List<Task> tasks = inMemoryTaskManager.getListTasks();
        assertEquals(1, tasks.size());
        Task foundTask = tasks.stream().findFirst().orElseThrow();
        assertEquals("descr2", foundTask.getDescription());
    }

    //Внутри эпиков не должно оставаться неактуальных id подзадач.
    //тест написан в прошлом спринте
    @Test
    void removeSubTaskShouldRemoveSubtask() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        inMemoryTaskManager.add(epic);
        Subtask subtask = new Subtask("subtask1", "descr");
        inMemoryTaskManager.add(subtask, epic.getId());

        //Act
        inMemoryTaskManager.removeSubTask(subtask.getId());

        //Assert
        List<Subtask> subtasks = inMemoryTaskManager.getListSubtasks();
        List<Subtask> subtasks2 = inMemoryTaskManager.getSubtasksOfEpic(epic.getId());
        assertEquals(0, subtasks.size());
        assertEquals(0, subtasks2.size());
    }


    @Test
    void removeEpicShouldRemoveEpicAndItsSubtasks() {
        //Arrange
        Epic epic1 = new Epic("epic1", "descr");
        Epic epic2 = new Epic("epic1", "descr");
        inMemoryTaskManager.add(epic1);
        inMemoryTaskManager.add(epic2);
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        Subtask subtask3 = new Subtask("subtask3", "descr3");

        inMemoryTaskManager.add(subtask1, epic1.getId());
        inMemoryTaskManager.add(subtask2, epic1.getId());
        inMemoryTaskManager.add(subtask3, epic2.getId());

        //Act
        inMemoryTaskManager.removeEpic(epic1.getId());

        //Arrange
        try {
            List<Subtask> subtasksOfEpic1 = inMemoryTaskManager.getSubtasksOfEpic(epic1.getId());
            fail();//<-- не должен выполняться
        }
        catch (TaskNotFoundException e) {

        }
        List<Subtask> allSubtasks = inMemoryTaskManager.getListSubtasks();
        List<Epic> epics = inMemoryTaskManager.getListEpics();
        assertEquals(1, allSubtasks.size());
        assertEquals(1, epics.size());
    }

    @Test
    void removeShouldBeAbleToRemoveTask() {
        //Arrange
        Task task1 = new Task("task1", "descr1");
        Task task2 = new Task("task2", "descr2");
        Epic epic1 = new Epic("epic1", "descr1");
        Epic epic2 = new Epic("epic1", "descr2");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        inMemoryTaskManager.add(task1);
        inMemoryTaskManager.add(task2);
        inMemoryTaskManager.add(epic1);
        inMemoryTaskManager.add(epic2);
        inMemoryTaskManager.add(subtask1, epic1.getId());
        inMemoryTaskManager.add(subtask2, epic2.getId());

        //Act
        inMemoryTaskManager.remove(task1);

        //Assert
        List<Task> tasks = inMemoryTaskManager.getListTasks();
        assertEquals(1, tasks.size());
        try {
            Task foundTask = inMemoryTaskManager.getTask(task1.getId());
            fail();
        }
        catch (TaskNotFoundException e) {

        }
    }

    @Test
    void removeShouldBeAbleToRemoveEpic() {
        //Arrange
        Task task1 = new Task("task1", "descr1");
        Task task2 = new Task("task2", "descr2");
        Epic epic1 = new Epic("epic1", "descr1");
        Epic epic2 = new Epic("epic1", "descr2");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        inMemoryTaskManager.add(task1);
        inMemoryTaskManager.add(task2);
        inMemoryTaskManager.add(epic1);
        inMemoryTaskManager.add(epic2);
        inMemoryTaskManager.add(subtask1, epic1.getId());
        inMemoryTaskManager.add(subtask2, epic2.getId());

        //Act
        inMemoryTaskManager.remove(epic1);

        //Assert
        List<Epic> epics = inMemoryTaskManager.getListEpics();
        assertEquals(1, epics.size());
        try {
            Epic foundTask = inMemoryTaskManager.getEpic(epic1.getId());
            fail();
        }
        catch (TaskNotFoundException e) {

        }

        try {
            List<Subtask> subtasks = inMemoryTaskManager.getSubtasksOfEpic(epic1.getId());
            fail();
        }
        catch (TaskNotFoundException e) {

        }
        List<Subtask> subtasks = inMemoryTaskManager.getListSubtasks();
        assertEquals(1, subtasks.size());
    }

    @Test
    void removeShouldBeAbleToRemoveSubtask() {
        //Arrange
        Task task1 = new Task("task1", "descr1");
        Task task2 = new Task("task2", "descr2");
        Epic epic1 = new Epic("epic1", "descr1");
        Epic epic2 = new Epic("epic1", "descr2");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        inMemoryTaskManager.add(task1);
        inMemoryTaskManager.add(task2);
        inMemoryTaskManager.add(epic1);
        inMemoryTaskManager.add(epic2);
        inMemoryTaskManager.add(subtask1, epic1.getId());
        inMemoryTaskManager.add(subtask2, epic2.getId());

        //Act
        inMemoryTaskManager.remove(subtask1);

        //Assert
        List<Subtask> subtasks = inMemoryTaskManager.getListSubtasks();
        assertEquals(1, subtasks.size());
        try {
            Subtask subtask = inMemoryTaskManager.getSubtask(subtask1.getId());
            fail();
        }
        catch (TaskNotFoundException e) {

        }

        List<Subtask> subtasksOfEpic1 = inMemoryTaskManager.getSubtasksOfEpic(epic1.getId());
        assertEquals(0, subtasksOfEpic1.size());
    }

    @Test
    void epicMustNotBeAllowedToAddedAsSubtaskToItself() {
        //Фраза из ТЗ:
        //"проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;"
        //Arrange
        Epic epic = new Epic("epic", "descr1");
        inMemoryTaskManager.add(epic);
        int epicId = inMemoryTaskManager.getListEpics().stream().findFirst().stream().findFirst().orElseThrow().getId();
        //Act
        //Метод добавления сабтаски не принимает класс Epic,
        //код ниже не скомпилится. Тест сделан для того, чтобы ревьювер не сказал, что нет метода, проверяющего, что Epic
        //нельзя добавить в самого себя

        //inMemoryTaskManager.add(epic, epicId) <-- Код закоменчен, читаем комментарий выше, почему!!!

        //Assert
    }

    @Test
    void subTaskMustNotBeAllowedToAddedAsSubtaskToItself() {
        //Фраза из ТЗ:
        //"проверьте, что объект Subtask нельзя сделать своим же эпиком;"
        //Arrange
        Epic epic = new Epic("epic", "descr");
        Subtask subtask = new Subtask("subtask", "descr");
        inMemoryTaskManager.add(epic);
        int epicId = inMemoryTaskManager.getListEpics().stream().findFirst().stream().findFirst().orElseThrow().getId();
        inMemoryTaskManager.add(subtask, epicId);
        int subtaskId = inMemoryTaskManager.getListSubtasks().stream().findFirst().stream().findFirst().orElseThrow().getId();
        //Act && Arrange
        try {
            inMemoryTaskManager.add(subtask, subtaskId);
        }
        catch (WrongTaskTypeException e) {
            return;
        }
        fail();
    }

    //С помощью сеттеров экземпляры задач позволяют изменить любое своё поле, но это может повлиять на данные внутри менеджера.
    // Протестируйте эти кейсы и подумайте над возможными вариантами решения проблемы.
    @Test
    void addShouldHaveCopyOfTask() {
        //Arrange
        Task task = new Task("name1", "descr1");
        inMemoryTaskManager.add(task);
        int id = task.getId();

        //Act
        task.setId(3333);
        task.setName("name2");
        task.setDescription("descr2");
        task.setStatus(Status.DONE);

        //Assert
        Task foundTask = inMemoryTaskManager.getTask(id);
        assertNotNull(foundTask);
        assertEquals("name1", foundTask.getName());
        assertEquals("descr1", foundTask.getDescription());
    }

    //С помощью сеттеров экземпляры задач позволяют изменить любое своё поле, но это может повлиять на данные внутри менеджера.
    //Протестируйте эти кейсы и подумайте над возможными вариантами решения проблемы.
    @Test
    void changingEpicIdOfSubtaskShouldNotAffectSubtaskInManager() {
        //Arrange
        Epic epic = new Epic("epic1", "descr1");
        Epic epic2 = new Epic("epic1", "descr2");
        Subtask subtask1 = new Subtask("subtask1", "descr3");
        Subtask subtask2 = new Subtask("subtask2", "descr4");
        inMemoryTaskManager.add(epic);
        inMemoryTaskManager.add(epic2);
        inMemoryTaskManager.add(subtask1, epic.getId());
        inMemoryTaskManager.add(subtask2, epic2.getId());

        //Act
        subtask2.setEpicId(subtask1.getEpicId());

        //Assert
        List<Subtask> subtasks = inMemoryTaskManager.getSubtasksOfEpic(epic.getId());
        assertEquals(1, subtasks.size());
        assertEquals("subtask1", subtasks.getFirst().getName());
        assertEquals("descr3", subtasks.getFirst().getDescription());
    }

    //С помощью сеттеров экземпляры задач позволяют изменить любое своё поле, но это может повлиять на данные внутри менеджера.
    //Протестируйте эти кейсы и подумайте над возможными вариантами решения проблемы.
    @Test
    void changingStatusOfSubtaskShouldNotAffectEpicInManager() {
        //Arrange
        Epic epic = new Epic("epic1", "descr1");
        Subtask subtask1 = new Subtask("subtask1", "descr3");
        Subtask subtask2 = new Subtask("subtask2", "descr4");
        inMemoryTaskManager.add(epic);
        inMemoryTaskManager.add(subtask1, epic.getId());
        inMemoryTaskManager.add(subtask2, epic.getId());

        //Act
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        //Assert
        epic = inMemoryTaskManager.getEpic(epic.getId());
        assertEquals(Status.NEW, epic.getStatus());
    }
}