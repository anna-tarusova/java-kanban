package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class BaseTaskTest {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Test
    void equals_tasksShouldBeEqualIfTheyHaveTheSameId() {
        //Фраза из ТЗ:
        //"проверьте, что экземпляры класса Task равны друг другу, если равен их id;"
        //Arrange
        //BaseTask - абстрактный класс, его экземпляр напрямую не создать
        BaseTask task1 = new Task("task1", "descr");
        BaseTask task2 = new Task("task2", "descr1");

        task1.setId(1);
        task2.setId(1);
        //Act
        boolean result = task1.equals(task2);

        //Assert
        assertTrue(result);
    }

    @Test
    void fromString_shouldCreateTaskIfStringContainsTask() {
        //Arrange
        String str = "7,TASK,task1,IN_PROGRESS,descr7,null,null";

        //Act
        BaseTask task = BaseTask.fromString(str);

        //Assert
        assertEquals(TaskType.TASK, task.getTaskType());
        assertEquals("task1", task.getName());
        assertEquals(7, task.getId());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals("descr7", task.getDescription());
        assertNull(task.getStartTime());
        assertNull(task.getDuration());
    }

    @Test
    void fromString_shouldCreateEpicWithStatusNewIfStringContainsEpic() {
        //Arrange
        String str = "7,EPIC,epic1,IN_PROGRESS,descr7,null,null";

        //Act
        BaseTask task = BaseTask.fromString(str);

        //Assert
        assertEquals(TaskType.EPIC, task.getTaskType());
        assertEquals("epic1", task.getName());
        assertEquals(7, task.getId());
        assertEquals(Status.NEW, task.getStatus());//<--у эпика статус вычисляемый, поэтому New
        assertEquals("descr7", task.getDescription());
        assertNull(task.getStartTime());
        assertNull(task.getDuration());
    }

    @Test
    void fromString_shouldThrowExceptionIfStringContainsSubtasksButThereIsNoEpicId() {
        //Arrange
        String str = "7,SUBTASK,task1,IN_PROGRESS,descr7,null,null";

        //Act && Assert
        assertThrowsExactly(IllegalStateException.class, () -> BaseTask.fromString(str));
    }

    @Test
    void fromString_shouldCreateSubtaskIfStringContainsSubtask() {
        //Arrange
        String str = "7,SUBTASK,subtask1,IN_PROGRESS,descr7,null,null,6";

        //Act
        BaseTask task = BaseTask.fromString(str);

        //Assert
        assertEquals(TaskType.SUBTASK, task.getTaskType());
        assertEquals("subtask1", task.getName());
        assertEquals(7, task.getId());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals("descr7", task.getDescription());
        assertNull(task.getStartTime());
        assertNull(task.getDuration());
        assertEquals(6, ((Subtask)task).getEpicId());
    }

    @Test
    void fromString_shouldSetStartTimeIfIsNotNull() {
        //Arrange
        String str = "7,SUBTASK,subtask1,IN_PROGRESS,descr7,01.01.2025 00:10,null,6";

        //Act
        BaseTask task = BaseTask.fromString(str);

        //Assert
        assertEquals(LocalDateTime.parse("01.01.2025 00:10", dateTimeFormatter), task.getStartTime());
    }

    @Test
    void fromString_shouldSetDurationIfIsNotNull() {
        //Arrange
        String str = "7,SUBTASK,subtask1,IN_PROGRESS,descr7,null,7,6";

        //Act
        BaseTask task = BaseTask.fromString(str);

        //Assert
        assertEquals(Duration.ofMinutes(7), task.getDuration());
    }

    @Test
    void getEndTime_shouldReturnSumOfStartTimeAndDuration() {
        //Arrange
        Task task = new Task("subtask", "descr");
        task.setStartTime(LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter));
        task.setDuration(Duration.ofMinutes(15));

        //Act
        LocalDateTime endTime = task.getEndTime();

        //Assert
        assertEquals(LocalDateTime.parse("01.01.2025 00:15", dateTimeFormatter), endTime);
    }

    @Test
    void areTimeSpansOverLapped_ifTheEarliestTimeSpanEndsAfterTheLatestTimeSpanShouldReturnTrue() {
        //Arrange
        LocalDateTime startTime1 = LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter);
        LocalDateTime endTime1 = LocalDateTime.parse("01.01.2025 12:00", dateTimeFormatter);
        LocalDateTime startTime2 = LocalDateTime.parse("01.01.2025 11:00", dateTimeFormatter);
        LocalDateTime endTime2 = LocalDateTime.parse("01.01.2025 13:00", dateTimeFormatter);
        //Act
        boolean isOverlapped = BaseTask.areTimeSpansOverLapped(startTime1, endTime1, startTime2, endTime2);

        //Assert
        assertTrue(isOverlapped);
    }

    @Test
    void areTimeSpansOverLapped_ifTheEarliestTimeSpanEndsBeforeTheLatestTimeSpanShouldReturnFalse() {
        //Arrange
        LocalDateTime startTime1 = LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter);
        LocalDateTime endTime1 = LocalDateTime.parse("01.01.2025 11:00", dateTimeFormatter);
        LocalDateTime startTime2 = LocalDateTime.parse("01.01.2025 12:00", dateTimeFormatter);
        LocalDateTime endTime2 = LocalDateTime.parse("01.01.2025 13:00", dateTimeFormatter);
        //Act
        boolean isOverlapped = BaseTask.areTimeSpansOverLapped(startTime1, endTime1, startTime2, endTime2);

        //Assert
        assertFalse(isOverlapped);
    }

    @Test
    void areTimeSpansOverLapped_ifStartTime1IsBeforeEndTime2AndStartTime2IsBeforeStartTime1ShouldReturnTrue() {
        //Arrange
        LocalDateTime startTime1 = LocalDateTime.parse("01.01.2025 12:00", dateTimeFormatter);
        LocalDateTime endTime1 = LocalDateTime.parse("01.01.2025 23:00", dateTimeFormatter);
        LocalDateTime startTime2 = LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter);
        LocalDateTime endTime2 = LocalDateTime.parse("01.01.2025 13:00", dateTimeFormatter);
        //Act
        boolean isOverlapped = BaseTask.areTimeSpansOverLapped(startTime1, endTime1, startTime2, endTime2);

        //Assert
        assertTrue(isOverlapped);
    }

    @Test
    void areTimeSpansOverLapped_ifStartTime1IsAfterEndTime2ShouldReturnFalse() {
        //Arrange
        LocalDateTime startTime1 = LocalDateTime.parse("01.01.2025 13:00", dateTimeFormatter);
        LocalDateTime endTime1 = LocalDateTime.parse("01.01.2025 23:00", dateTimeFormatter);
        LocalDateTime startTime2 = LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter);
        LocalDateTime endTime2 = LocalDateTime.parse("01.01.2025 12:00", dateTimeFormatter);
        //Act
        boolean isOverlapped = BaseTask.areTimeSpansOverLapped(startTime1, endTime1, startTime2, endTime2);

        //Assert
        assertFalse(isOverlapped);
    }

    @Test
    void areTimeSpansOverLapped_ifTheFirstTimeSpanInsideTheSecondShouldReturnTrue() {
        //Arrange
        LocalDateTime startTime1 = LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter);
        LocalDateTime endTime1 = LocalDateTime.parse("01.01.2025 23:00", dateTimeFormatter);
        LocalDateTime startTime2 = LocalDateTime.parse("01.01.2025 10:00", dateTimeFormatter);
        LocalDateTime endTime2 = LocalDateTime.parse("01.01.2025 15:00", dateTimeFormatter);
        //Act
        boolean isOverlapped = BaseTask.areTimeSpansOverLapped(startTime1, endTime1, startTime2, endTime2);

        //Assert
        assertTrue(isOverlapped);
    }

    @Test
    void areTimeSpansOverLapped_ifTheSecondTimeSpanInsideTheFirstShouldReturnTrue() {
        //Arrange
        LocalDateTime startTime1 = LocalDateTime.parse("01.01.2025 10:00", dateTimeFormatter);
        LocalDateTime endTime1 = LocalDateTime.parse("01.01.2025 15:00", dateTimeFormatter);
        LocalDateTime startTime2 = LocalDateTime.parse("01.01.2025 00:00", dateTimeFormatter);
        LocalDateTime endTime2 = LocalDateTime.parse("01.01.2025 23:00", dateTimeFormatter);
        //Act
        boolean isOverlapped = BaseTask.areTimeSpansOverLapped(startTime1, endTime1, startTime2, endTime2);

        //Assert
        assertTrue(isOverlapped);
    }

    @Test
    void toString_shouldSerializeStartTimeAsNullIfStartTimeIsNull() {
        //Arrange
        Task task = new Task("task1", "descr1");
        task.setStartTime(null);
        task.setStatus(Status.IN_PROGRESS);
        task.setId(5);
        task.setDuration(Duration.ofMinutes(15));

        //Act
        String str = task.toString();

        //Assert
        assertEquals("5,TASK,task1,IN_PROGRESS,descr1,null,15", str);
    }

    @Test
    void toString_shouldSerializeDurationAsNullIfDurationIsNull() {
        //Arrange
        Task task = new Task("task1", "descr1");
        task.setStartTime(LocalDateTime.parse("01.01.2025 12:34", dateTimeFormatter));
        task.setStatus(Status.IN_PROGRESS);
        task.setId(5);
        task.setDuration(null);

        //Act
        String str = task.toString();

        //Assert
        assertEquals("5,TASK,task1,IN_PROGRESS,descr1,01.01.2025 12:34,null", str);
    }

    @Test
    void toString_shouldSerializeEpicIdForSubtasks() {
        //Arrange
        Subtask subtask = new Subtask("subtask1", "descr1");
        subtask.setStartTime(LocalDateTime.parse("01.01.2025 12:34", dateTimeFormatter));
        subtask.setStatus(Status.IN_PROGRESS);
        subtask.setId(5);
        subtask.setDuration(Duration.ofMinutes(15));
        subtask.setEpicId(9999);

        //Act
        String str = subtask.toString();

        //Assert
        assertEquals("5,SUBTASK,subtask1,IN_PROGRESS,descr1,01.01.2025 12:34,15,9999", str);
    }
}