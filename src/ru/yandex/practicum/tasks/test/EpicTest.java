package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {

    @Test
    void getTaskTypeShouldReturnEpic() {
        //Arrange
        Epic epic = new Epic("epic", "descr");
        //Act
        TaskType type = epic.getTaskType();
        //Assert
        assertEquals(TaskType.EPIC, type);
    }

    @Test
    void epicsShouldBeEqualIfTheyHaveTheSameId() {
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


}