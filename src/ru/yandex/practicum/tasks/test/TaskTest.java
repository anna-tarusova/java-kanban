package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void getTaskType_shouldReturnTask() {
        //Arrange
        Task task = new Task("subtask", "descr");
        //Act
        TaskType type = task.getTaskType();
        //Assert
        assertEquals(TaskType.TASK, type);
    }

    @Test
    void equals_tasksShouldBeEqualIfTheyHaveTheSameId() {
        //Фраза из ТЗ:
        //"проверьте, что наследники класса Task равны друг другу, если равен их id;"
        //Arrange
        Task task1 = new Task("task1", "descr1");
        Task task2 = new Task("task2", "descr2");

        task1.setId(1);
        task2.setId(1);
        //Act
        boolean result = task1.equals(task2);

        //Assert
        assertTrue(result);
    }
}