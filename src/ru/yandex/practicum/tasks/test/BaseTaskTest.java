package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Task;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseTaskTest {

    @Test
    void tasksShouldBeEqualIfTheyHaveTheSameId() {
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
}