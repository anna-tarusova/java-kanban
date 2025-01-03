package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.exceptions.ManagerLoadException;
import ru.yandex.practicum.tasks.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.logic.FileBackedTaskManager;
import ru.yandex.practicum.tasks.logic.Managers;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.Status;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    void setUp() {
        taskManager = new FileBackedTaskManager("file1", (new Managers()).getDefaultHistory());
    }

    @Test
    public void loadFromFile_shouldCreateEmptyFileBackedManagerFromEmptyFile() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        //Assert
        List<Task> tasks = fileBackedTaskManager.getListTasks();
        List<Epic> epics = fileBackedTaskManager.getListEpics();
        List<Subtask> subtasks = fileBackedTaskManager.getListSubtasks();

        assertEquals(0, tasks.size());
        assertEquals(0, epics.size());
        assertEquals(0, subtasks.size());
    }

    @Test
    public void loadFromFile_shouldCreateFileBackedManagerWithOneEpicWithStatusNewIfFileWithOneEpic() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("7,EPIC,epic1,DONE,descr,null,null");
        }

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        //Assert
        List<Task> tasks = fileBackedTaskManager.getListTasks();
        List<Epic> epics = fileBackedTaskManager.getListEpics();
        List<Subtask> subtasks = fileBackedTaskManager.getListSubtasks();

        assertEquals(0, tasks.size());
        assertEquals(1, epics.size());
        Epic epic = epics.getFirst();
        assertEquals(7, epic.getId());
        assertEquals("epic1", epic.getName());
        assertEquals(Status.NEW, epic.getStatus());//<-статус Эпика вычисляемый
        assertEquals("descr", epic.getDescription());
        assertEquals(0, subtasks.size());
    }

    @Test
    public void loadFromFile_shouldCreateFileBackedManagerWithOneEpicWithSubtaskIfFileWithOneEpicWithSubtask() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("6,EPIC,epic1,IN_PROGRESS,descr1,null,null\n");
            fw.write("7,SUBTASK,subtask1,IN_PROGRESS,descr2,null,null,6\n");
        }

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        //Assert
        List<Task> tasks = fileBackedTaskManager.getListTasks();
        List<Epic> epics = fileBackedTaskManager.getListEpics();
        List<Subtask> subtasks = fileBackedTaskManager.getListSubtasks();

        assertEquals(0, tasks.size());
        assertEquals(1, subtasks.size());

        assertEquals(1, epics.size());
        Epic epic = epics.getFirst();
        assertEquals(6, epic.getId());
        assertEquals("epic1", epic.getName());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        assertEquals("descr1", epic.getDescription());

        Subtask subtask = subtasks.getFirst();
        assertEquals(7, subtask.getId());
        assertEquals("subtask1", subtask.getName());
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
        assertEquals("descr2", subtask.getDescription());
    }

    @Test
    public void loadFromFile_shouldThrowExceptionIfFileWithSubtaskOfUnexistingEpic() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("6,EPIC,epic1,IN_PROGRESS,descr1,null,null\n");
            fw.write("7,SUBTASK,subtask1,IN_PROGRESS,descr2,null,null,99999\n");
        }

        //Act & Assert
        assertThrows(TaskNotFoundException.class, () -> FileBackedTaskManager.loadFromFile(file), "Не выкинут эксепшн TaskNotFoundException");
    }

    @Test
    public void loadFromFile_shouldCreateWithoutExceptionIfFileWithSubtaskWhichIsBeforeEpic() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("7,SUBTASK,subtask1,IN_PROGRESS,descr2,null,null,6\n");
            fw.write("6,EPIC,epic1,IN_PROGRESS,descr1,null,null\n");

        }

        //Act & Assert
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    public void loadFromFile_shouldThrowExceptionIfFileWithOnlySubtask() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("7,SUBTASK,subtask1,IN_PROGRESS,descr2,null,null,99999\n");
        }

        //Act & Assert
        assertThrows(TaskNotFoundException.class, () -> FileBackedTaskManager.loadFromFile(file), "Не выкинут эксепшн TaskNotFoundException");
    }

    @Test
    public void loadFromFile_shouldCreateFileBackedTaskManagerWithTaskIfFileWithTask() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("7,TASK,task1,IN_PROGRESS,descr4,null,null\n");
        }

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        //Assert
        List<Task> tasks = fileBackedTaskManager.getListTasks();
        List<Epic> epics = fileBackedTaskManager.getListEpics();
        List<Subtask> subtasks = fileBackedTaskManager.getListSubtasks();

        assertEquals(1, tasks.size());
        Task task = tasks.getFirst();
        assertEquals(7, task.getId());
        assertEquals("task1", task.getName());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals("descr4", task.getDescription());
        assertEquals(0, epics.size());
        assertEquals(0, subtasks.size());
    }

    @Test
    public void add_shouldBeAbleToSaveTaskToFile() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        fileBackedTaskManager.add(new Task("task1", "descr1"));

        //Assert
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            assertEquals("1,TASK,task1,NEW,descr1,null,null", line);
        }
    }

    @Test
    public void add_shouldBeAbleToSaveEpicToFile() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        fileBackedTaskManager.add(new Epic("epic1", "descr1"));

        //Assert
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            assertEquals("1,EPIC,epic1,NEW,descr1,null,null", line);
        }
    }

    @Test
    public void add_shouldBeAbleToSaveEpicWithSubtaskToFile() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        Epic epic = new Epic("epic1", "descr1");
        fileBackedTaskManager.add(epic);
        Subtask subtask = new Subtask("subtask1", "descr1");
        subtask.setEpicId(epic.getId());
        fileBackedTaskManager.add(subtask);


        //Assert
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            assertEquals("1,EPIC,epic1,NEW,descr1,null,null", line);
            line = br.readLine();
            assertEquals("2,SUBTASK,subtask1,NEW,descr1,null,null,1", line);
        }
    }

    @Test
    public void save_addingSeveralTasksThenSavingShouldCreateFileBackedManagerWithThisTasks() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        Epic epic1 = new Epic("epic1", "descr1");
        Epic epic2 = new Epic("epic2", "descr2");
        Epic epic3 = new Epic("epic3", "descr3");
        Subtask subtask1 = new Subtask("subtask1", "descr4");
        Subtask subtask2 = new Subtask("subtask2", "descr5");
        Subtask subtask3 = new Subtask("subtask3", "descr6");
        Task task = new Task("task1", "descr7");

        //Act
        //save вызывается внутри
        fileBackedTaskManager.add(epic1);
        fileBackedTaskManager.add(epic2);
        fileBackedTaskManager.add(epic3);
        subtask1.setEpicId(epic1.getId());
        fileBackedTaskManager.add(subtask1);
        subtask2.setEpicId(epic2.getId());
        fileBackedTaskManager.add(subtask2);
        subtask3.setEpicId(epic2.getId());
        fileBackedTaskManager.add(subtask3);
        fileBackedTaskManager.add(task);
        fileBackedTaskManager.setStatus(task.getId(), Status.IN_PROGRESS);
        fileBackedTaskManager.setStatus(subtask2.getId(), Status.IN_PROGRESS);

        FileBackedTaskManager fileBackedTaskManager2 = FileBackedTaskManager.loadFromFile(file);

        //Assert
        List<Task> tasks = fileBackedTaskManager2.getListTasks();
        assertEquals(1, tasks.size());
        Task task1 = tasks.getFirst();
        assertEquals("task1", task1.getName());
        assertEquals("descr7", task1.getDescription());
        assertEquals(Status.IN_PROGRESS, task1.getStatus());

        List<Epic> epics = fileBackedTaskManager2.getListEpics();
        assertEquals(3, epics.size());
        Epic epic1ForAssert = epics.stream().filter(e -> e.getName().equals("epic1")).toList().getFirst();
        assertEquals("descr1", epic1ForAssert.getDescription());
        assertEquals(Status.NEW, epic1ForAssert.getStatus());

        Epic epic2ForAssert = epics.stream().filter(e -> e.getName().equals("epic2")).toList().getFirst();
        assertEquals("descr2", epic2ForAssert.getDescription());
        assertEquals(Status.IN_PROGRESS, epic2ForAssert.getStatus());

        Epic epic3ForAssert = epics.stream().filter(e -> e.getName().equals("epic3")).toList().getFirst();
        assertEquals("descr3", epic3ForAssert.getDescription());
        assertEquals(Status.NEW, epic3ForAssert.getStatus());

        List<Subtask> subtasks = fileBackedTaskManager2.getListSubtasks();
        assertEquals(3, subtasks.size());
        Subtask subtask1ForAssert = subtasks.stream().filter(st -> st.getName().equals("subtask1")).toList().getFirst();
        assertEquals("descr4", subtask1ForAssert.getDescription());
        assertEquals(Status.NEW, subtask1ForAssert.getStatus());

        Subtask subtask2ForAssert = subtasks.stream().filter(st -> st.getName().equals("subtask2")).toList().getFirst();
        assertEquals("descr5", subtask2ForAssert.getDescription());
        assertEquals(Status.IN_PROGRESS, subtask2ForAssert.getStatus());

        Subtask subtask3ForAssert = subtasks.stream().filter(st -> st.getName().equals("subtask3")).toList().getFirst();
        assertEquals("descr6", subtask3ForAssert.getDescription());
        assertEquals(Status.NEW, subtask3ForAssert.getStatus());
    }

    @Test
    public void loadFromFile_afterLoadingFromFileIdShouldBeGreaterThanMaxIdInFile() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("9999,TASK,task1,IN_PROGRESS,descr2,null,null\n");
        }

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        //Assert
        fileBackedTaskManager.add(new Task("task2", "descr"));
        List<Task> tasks = fileBackedTaskManager.getListTasks();
        assertEquals(2, tasks.size());
        Task task2 = tasks.stream().filter(t -> t.getName().equals("task2")).findFirst().orElseThrow();
        assertEquals(10000, task2.getId());
    }

    @Test
    public void loadFromFile_shouldThrowExceptionIfFileContainsTwoSameIds() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("1,TASK,task1,IN_PROGRESS,descr1\n");
            fw.write("1,TASK,task2,IN_PROGRESS,descr2\n");
        }

        //Act & Assert
        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    public void loadFromFile_shouldSetEarliestStartTimeAsStartTimeOfEpicIfFileWithOneEpicWithTwoSubtasks() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("6,EPIC,epic1,IN_PROGRESS,descr1,null,null\n");
            fw.write("7,SUBTASK,subtask1,IN_PROGRESS,descr2,01.01.2025 00:00,null,6\n");
            fw.write("8,SUBTASK,subtask1,IN_PROGRESS,descr2,02.01.2025 00:00,null,6\n");
        }

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        //Assert
        Epic epic = fileBackedTaskManager.getEpic(6);
        LocalDateTime startTime = epic.getStartTime();
        assertEquals(LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter), startTime);
    }

    @Test
    public void loadFromFile_shouldSetNonNullStartTimeAsStartTimeOfEpicIfFileWithOneEpicWithTwoSubtasksWhereOneStartTimeIsNull() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("6,EPIC,epic1,IN_PROGRESS,descr1,null,null\n");
            fw.write("7,SUBTASK,subtask1,IN_PROGRESS,descr2,01.01.2025 00:00,null,6\n");
            fw.write("8,SUBTASK,subtask1,IN_PROGRESS,descr2,null,null,6\n");
        }

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        //Assert
        Epic epic = fileBackedTaskManager.getEpic(6);
        LocalDateTime startTime = epic.getStartTime();
        assertEquals(LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter), startTime);
    }

    @Test
    public void loadFromFile_shouldSetSumOfTheDurationsAsDurationOfTheEpicIfFileWithOneEpicWithTwoSubtasksWithDurations() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("6,EPIC,epic1,IN_PROGRESS,descr1,null,null\n");
            fw.write("7,SUBTASK,subtask1,IN_PROGRESS,descr2,null,7,6\n");
            fw.write("8,SUBTASK,subtask1,IN_PROGRESS,descr2,null,8,6\n");
        }

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        //Assert
        Epic epic = fileBackedTaskManager.getEpic(6);
        Duration duration = epic.getDuration();
        assertEquals(Duration.ofMinutes(15), duration);
    }

    @Test
    public void loadFromFile_shouldSetEndDateOfTheEpicIfIfFileWithOneEpicWithTwoSubtasksWithStartTimesDurations() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("6,EPIC,epic1,IN_PROGRESS,descr1,null,null\n");
            fw.write("7,SUBTASK,subtask1,IN_PROGRESS,descr2,01.01.2025 00:00,7,6\n");
            fw.write("8,SUBTASK,subtask1,IN_PROGRESS,descr2,01.01.2025 15:00,8,6\n");
        }

        //Act
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        //Assert
        Epic epic = fileBackedTaskManager.getEpic(6);
        LocalDateTime endTime = epic.getEndTime();
        assertEquals(LocalDateTime.parse("01.01.2025 15:08", dateTimeFormatter), endTime);
    }

    @Test
    public void loadFromFile_shouldThrowExceptionIfFileWithOneEpicWithTwoSubtasksWhichAreOverlapped() throws IOException {
        //Arrange
        File file = File.createTempFile("prefix", "suffix");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("6,EPIC,epic1,IN_PROGRESS,descr1,null,null\n");
            fw.write("7,SUBTASK,subtask1,IN_PROGRESS,descr2,01.01.2025 00:00,45,6\n");
            fw.write("8,SUBTASK,subtask1,IN_PROGRESS,descr2,01.01.2025 00:15,45,6\n");
        }

        //Act && Assert
        assertThrowsExactly(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }
}
