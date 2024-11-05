package ru.yandex.practicum.tasks.logic;

import ru.yandex.practicum.tasks.model.BaseTask;

import java.util.List;

public interface HistoryManager {
    void add(BaseTask task);
    List<BaseTask> getHistory();
    void remove(int id);
}
