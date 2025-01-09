package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.exceptions.MethodIsForbiddenException;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Test
    void getTaskType_shouldReturnEpic() {
        //Arrange
        Epic epic = new Epic("epic", "descr");
        //Act
        TaskType type = epic.getTaskType();
        //Assert
        assertEquals(TaskType.EPIC, type);
    }

    @Test
    void equals_epicsShouldBeEqualIfTheyHaveTheSameId() {
        //Фраза из ТЗ:
        //"проверьте, что наследники класса Task равны друг другу, если равен их id;"
        //Arrange
        Epic epic1 = new Epic("epic1", "descr1");
        Epic epic2 = new Epic("epic2", "descr2");

        epic1.setId(1);
        epic2.setId(1);
        //Act
        boolean result = epic1.equals(epic2);

        //Assert
        assertTrue(result);
    }

    @Test
    void getStatus_ifAllSubtasksAreNewTheStatusOfEpicShouldBeNew() {
        //arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        Subtask subtask3 = new Subtask("subtask3", "descr3");

        subtask1.setId(1);
        subtask2.setId(2);
        subtask3.setId(3);
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        subtask3.setStatus(Status.NEW);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.addSubtask(subtask3);
        //act
        Status epicStatus = epic.getStatus();
        //assert
        assertEquals(Status.NEW, epicStatus);
    }

    @Test
    void getStatus_ifAllSubtasksAreDoneTheStatusOfEpicShouldBeNew() {
        //arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        Subtask subtask3 = new Subtask("subtask3", "descr3");

        subtask1.setId(1);
        subtask2.setId(2);
        subtask3.setId(3);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.addSubtask(subtask3);
        //act
        Status epicStatus = epic.getStatus();
        //assert
        assertEquals(Status.DONE, epicStatus);
    }

    @Test
    void getStatus_ifSomeSubtasksAreDoneAndOtherAreNewStatusOfEpicShouldBeInProgress() {
        //arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        Subtask subtask3 = new Subtask("subtask3", "descr3");

        subtask1.setId(1);
        subtask2.setId(2);
        subtask3.setId(3);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.NEW);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.addSubtask(subtask3);
        //act
        Status epicStatus = epic.getStatus();
        //assert
        assertEquals(Status.IN_PROGRESS, epicStatus);
    }

    @Test
    void getStatus_ifAllSubtasksAreInProgressStatusOfEpicShouldBeInProgress() {
        //arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        Subtask subtask3 = new Subtask("subtask3", "descr3");

        subtask1.setId(1);
        subtask2.setId(2);
        subtask3.setId(3);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask3.setStatus(Status.IN_PROGRESS);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.addSubtask(subtask3);
        //act
        Status epicStatus = epic.getStatus();
        //assert
        assertEquals(Status.IN_PROGRESS, epicStatus);
    }

    @Test
    void getStatus_ifAllSubtasksHaveDifferentStatusStatusOfEpicShouldBeInProgress() {
        //arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        Subtask subtask3 = new Subtask("subtask3", "descr3");

        subtask1.setId(1);
        subtask2.setId(2);
        subtask3.setId(3);
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask3.setStatus(Status.DONE);

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.addSubtask(subtask3);

        //Act
        Status epicStatus = epic.getStatus();

        //Assert
        assertEquals(Status.IN_PROGRESS, epicStatus);
    }

    @Test
    void getStartTime_shouldReturnTheEarliestStartTime() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        Subtask subtask3 = new Subtask("subtask3", "descr3");

        subtask1.setId(1);
        subtask2.setId(2);
        subtask3.setId(3);
        subtask1.setStartTime(LocalDateTime.parse("01.01.2025 15:40", dateTimeFormatter));
        subtask2.setStartTime(LocalDateTime.parse("01.01.2025 15:30", dateTimeFormatter));
        subtask3.setStartTime(LocalDateTime.parse("01.01.2025 15:00", dateTimeFormatter));

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.addSubtask(subtask3);

        //Act
        LocalDateTime startTime = epic.getStartTime();

        //Assert
        assertEquals(LocalDateTime.parse("01.01.2025 15:00", dateTimeFormatter), startTime);
    }

    @Test
    void getDuration_shouldReturnTheSumOfSubtasksDurations() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        Subtask subtask3 = new Subtask("subtask3", "descr3");

        subtask1.setId(1);
        subtask2.setId(2);
        subtask3.setId(3);
        subtask1.setDuration(Duration.ofMinutes(10));
        subtask2.setDuration(Duration.ofMinutes(5));
        subtask3.setDuration(Duration.ofMinutes(10));

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.addSubtask(subtask3);

        //Act
        Duration duration = epic.getDuration();

        //Assert
        assertEquals(Duration.ofMinutes(25), duration);
    }

    @Test
    void getEndTime_shouldReturnTheLatestEndTime() {
        //Arrange
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");
        Subtask subtask3 = new Subtask("subtask3", "descr3");

        subtask1.setId(1);
        subtask2.setId(2);
        subtask3.setId(3);
        subtask1.setStartTime(LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter));
        subtask2.setStartTime(LocalDateTime.parse("02.01.2025 00:00", dateTimeFormatter));
        subtask3.setStartTime(LocalDateTime.parse("03.01.2025 00:00", dateTimeFormatter));
        subtask1.setDuration(Duration.ofMinutes(10));
        subtask2.setDuration(Duration.ofMinutes(5));
        subtask3.setDuration(Duration.ofMinutes(10));

        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);
        epic.addSubtask(subtask3);

        //Act
        LocalDateTime endTime = epic.getEndTime();

        //Assert
        assertEquals(LocalDateTime.parse("03.01.2025 00:10", dateTimeFormatter), endTime);
    }

    @Test
    void setStatus_shouldThrowException() {
        //Arrange
        Epic epic = new Epic("epic", "descr");

        //Act && Assert
        assertThrowsExactly(MethodIsForbiddenException.class, () -> epic.setStatus(Status.IN_PROGRESS));
    }

    @Test
    void setStartTime_shouldThrowException() {
        //Arrange
        Epic epic = new Epic("epic", "descr");

        //Act && Assert
        assertThrowsExactly(MethodIsForbiddenException.class, () -> epic.setStartTime(LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter)));
    }

    @Test
    void setDuration_shouldThrowException() {
        //Arrange
        Epic epic = new Epic("epic", "descr");

        //Act && Assert
        assertThrowsExactly(MethodIsForbiddenException.class, () -> epic.setDuration(Duration.ZERO));
    }

    @Test
    void addSubtask_ifTasksAreOverlappedShouldThrowException() {
        //Arrange
        Epic epic = new Epic("epic", "descr");
        Subtask subtask = new Subtask("subtask1", "descr");
        subtask.setId(1);
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter));
        subtask.setDuration(Duration.ofHours(15));
        epic.addSubtask(subtask);

        Subtask subtask2 = new Subtask("subtask2", "descr");
        subtask2.setId(2);
        subtask2.setStartTime(LocalDateTime.parse("01.01.2025 10:00", dateTimeFormatter));
        subtask2.setDuration(Duration.ofHours(15));

        //Act && Assert
        assertThrowsExactly(IllegalStateException.class, () -> epic.addSubtask(subtask2));
    }

    @Test
    void addSubtask_ifTasksAreNotOverlappedShouldNotThrowException() {
        //Arrange
        Epic epic = new Epic("epic", "descr");
        Subtask subtask = new Subtask("subtask1", "descr");
        subtask.setId(1);
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter));
        subtask.setDuration(Duration.ofHours(15));
        epic.addSubtask(subtask);

        Subtask subtask2 = new Subtask("subtask2", "descr");
        subtask2.setId(2);
        subtask2.setStartTime(LocalDateTime.parse("01.01.2025 15:00", dateTimeFormatter));
        subtask2.setDuration(Duration.ofHours(15));

        //Act && Assert
        assertDoesNotThrow(() -> epic.addSubtask(subtask2));
    }
}