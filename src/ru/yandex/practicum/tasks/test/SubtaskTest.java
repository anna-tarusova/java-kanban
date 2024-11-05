package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void getTaskTypeShouldReturnSubtask() {
        //Arrange
        Subtask subtask = new Subtask("subtask", "descr");
        //Act
        TaskType type = subtask.getTaskType();
        //Assert
        assertEquals(TaskType.SUBTASK, type);
    }

    @Test
    void subtasksShouldBeEqualIfTheyHaveTheSameId() {
        //Фраза из ТЗ:
        //"проверьте, что наследники класса Task равны друг другу, если равен их id;"
        //Arrange
        Subtask subtask1 = new Subtask("subtask1", "descr1");
        Subtask subtask2 = new Subtask("subtask2", "descr2");

        subtask1.setId(1);
        subtask2.setId(1);
        //Act
        boolean result = subtask1.equals(subtask2);

        //Assert
        assertTrue(result);
    }
}