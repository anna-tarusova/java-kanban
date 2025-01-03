package ru.yandex.practicum.tasks.logic;

import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.Status;

import java.util.List;

public interface TaskManager {

    //Методы, работающие с тасками всех типов
    void clearTasksOfAnyType();

    //Методы, работающие с тасками определенного типа
    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    Epic getEpic(int epicId);

    Task getTask(int taskId);

    Subtask getSubtask(int subtaskId);

    List<Subtask> getSubtasksOfEpic(int epicId);

    List<Task> getListTasks();

    List<Epic> getListEpics();

    List<Subtask> getListSubtasks();

    void add(Task task);

    void add(Epic epic);

    void add(Subtask subtask);

    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

    void remove(Task task);

    void remove(Epic epic);

    void remove(Subtask task);

    void setStatus(int taskId, Status status);

    void setStatus(Task task, Status status);

    void setStatus(Subtask subtask, Status status);

    List<BaseTask> getHistory();

    List<BaseTask> getPrioritizedTasks();
}
