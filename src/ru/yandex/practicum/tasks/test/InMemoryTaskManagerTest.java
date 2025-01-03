package ru.yandex.practicum.tasks.test;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.tasks.logic.InMemoryTaskManager;
import ru.yandex.practicum.tasks.logic.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager((new Managers()).getDefaultHistory());
    }
}