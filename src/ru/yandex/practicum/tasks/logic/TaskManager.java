package ru.yandex.practicum.tasks.logic;

import ru.yandex.practicum.tasks.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.exceptions.WrongTaskTypeException;
import ru.yandex.practicum.tasks.model.*;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private static int taskId = 0;
    private static Map<Integer, BaseTask> tasks = new HashMap<>();

    //вспомогательный метод
    private static void addBaseTask(BaseTask task) {
        taskId++;
        task.setId(taskId);
        tasks.put(taskId, task);
    }

    private static void ensureTaskIsTask(BaseTask task) {
        if (!task.getTaskType().equals(TaskType.TASK)) {
            throw new WrongTaskTypeException("Задача не является таском");
        }
    }

    private static void EnsureTaskIsSubTask(BaseTask task) {
        if (!task.getTaskType().equals(TaskType.SUBTASK)) {
            throw new WrongTaskTypeException("Задача не является сабтаском");
        }
    }

    private static void ensureTaskIsEpic(BaseTask task) {
        if (!task.getTaskType().equals(TaskType.EPIC)) {
            throw new WrongTaskTypeException("Задача не является эпиком");
        }
    }

    private static void updateStatusOfEpic(int epicId) {
        Epic epic = getEpic(epicId);
        updateStatusOfEpic(epic);
    }

    private static void updateStatusOfEpic(Epic epic) {
        List<Subtask> subtasks = TaskManager.getSubtasksOfEpic(epic.getId());
        if (subtasks.isEmpty() || subtasks
                .stream()
                .map(BaseTask::getStatus)
                .allMatch((Status s) -> s == Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else if (subtasks
                .stream()
                .map(BaseTask::getStatus)
                .allMatch((Status s) -> s == Status.DONE)) {
            epic.setStatus(Status.DONE);
        }
        else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    //Методы, работающие с тасками всех типов
    public static void clearTasksOfAnyType() {
        tasks = new HashMap<>();
        taskId = 0;
    }

    public static List<BaseTask> getAllTasksOfAnyType() {
        return new ArrayList<>(tasks.values());
    }


    public static BaseTask getTaskOfAnyType(int id) {
        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException("Не найден таск с id = " + id);
        }
        return tasks.get(id);
    }

    public static void updateTaskOfAnyType(BaseTask task) {
        tasks.put(task.getId(), task);

        //если таска является сабтаской, то найти её эпик и вызывать для него updateStatus
        if (task.getTaskType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            updateStatusOfEpic(subtask.getEpicId());
        }
    }

    //Методы, работающие с тасками определенного типа
    public static void clearTasks() {
        tasks.keySet().forEach((Integer id) -> {
            BaseTask task = tasks.get(id);
            if (task.getTaskType() == TaskType.TASK) {
                tasks.remove(id);
            }
        });
    }

    public static void clearSubTasks() {
        List<Integer> toRemove = new ArrayList<>();
        tasks.keySet().forEach((Integer id) -> {
            BaseTask task = tasks.get(id);
            if (task.getTaskType() == TaskType.SUBTASK) {
                Subtask subtask = (Subtask)task;
                toRemove.add(id);
                Epic epic = getEpic(subtask.getEpicId());
                epic.clearAllSubtaskIds();
            }
        });
        toRemove.forEach((Integer id) -> tasks.remove(id));

        tasks.keySet().forEach((Integer id) -> {
            BaseTask task = tasks.get(id);
            if (task.getTaskType() == TaskType.EPIC) {
                updateStatusOfEpic((Epic) task);
            }
        });
    }

    public static void clearEpics() {
        List<Integer> toRemove = new ArrayList<>();
        tasks.keySet().forEach((Integer id) -> {
            BaseTask task = tasks.get(id);
            if (task.getTaskType() == TaskType.TASK || task.getTaskType() == TaskType.SUBTASK) {
                toRemove.add(id);
            }
        });
        toRemove.forEach((Integer id) -> tasks.remove(id));
    }

    public static Epic getEpic(int epicId) {
        BaseTask task = getTaskOfAnyType(epicId);
        ensureTaskIsEpic(task);
        return (Epic) task;
    }

    public static Task getTask(int taskId) {
        BaseTask task = getTaskOfAnyType(taskId);
        ensureTaskIsTask(task);
        return (Task) task;
    }

    public static Subtask getSubtask(int subtaskId) {
        BaseTask task = getTaskOfAnyType(subtaskId);
        EnsureTaskIsSubTask(task);
        return (Subtask) task;
    }

    public static List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = getEpic(epicId);
        List<Subtask> result = new ArrayList<>();
        for (int id : epic.getSubtasksIds()) {
            Subtask subtask = (Subtask) tasks.get(id);
            result.add(subtask);
        }

        return result;
    }

    public static List<Task> getListTasks() {
        List<BaseTask> allTasks = getAllTasksOfAnyType();
        return allTasks
                .stream()
                .filter((BaseTask t) -> t.getTaskType() == TaskType.TASK)
                .map((BaseTask bt) -> (Task)bt)
                .toList();
    }

    public static List<Epic> getListEpics() {
        List<BaseTask> allTasks = getAllTasksOfAnyType();
        return allTasks
                .stream()
                .filter((BaseTask t) -> t.getTaskType() == TaskType.EPIC)
                .map((BaseTask bt) -> (Epic)bt)
                .toList();
    }

    public static List<Subtask> getListSubtasks() {
        List<BaseTask> allTasks = getAllTasksOfAnyType();
        return allTasks
                .stream()
                .filter((BaseTask t) -> t.getTaskType() == TaskType.SUBTASK)
                .map((BaseTask bt) -> (Subtask)bt)
                .toList();
    }

    public static void add(Task task) {
        addBaseTask(task);
    }

    public static void add(Epic epic) {
        epic.clearAllSubtaskIds();
        addBaseTask(epic);
    }

    public static void add(Subtask subtask, int epicId) {
        Epic epic = getEpic(epicId);
        subtask.setEpicId(epic.getId());
        addBaseTask(subtask);
        epic.addSubtask(subtask);
        updateStatusOfEpic(epic);
    }

    public static void removeTask(int id) {
        BaseTask task = getTaskOfAnyType(id);
        ensureTaskIsTask(task);
        tasks.remove(id);
    }

    public static void removeSubTask(int id) {
        Subtask subtask = getSubtask(id);
        tasks.remove(id);
        int epicId = subtask.getEpicId();
        Epic epic = getEpic(epicId);
        epic.removeSubtaskById(id);
        updateStatusOfEpic(subtask.getEpicId());
    }

    public static void removeEpic(int id) {
        BaseTask task = getTaskOfAnyType(id);
        ensureTaskIsEpic(task);
        tasks.remove(id);

        List<Subtask> subtasks = getSubtasksOfEpic(id);
        subtasks.forEach((Subtask subtask) -> {
            tasks.remove(subtask.getId());
        });
    }

    public static void remove(Task task) {
        removeTask(task.getId());
    }

    public static void remove(Epic epic) {
        removeEpic(epic.getId());
    }

    public static void remove(Subtask task) {
        removeSubTask(task.getId());
    }

    public static void setStatusOfTask(int taskId, Status status) {
        Task task = getTask(taskId);
        task.setStatus(status);
    }

    public static void setStatusOfSubtask(int subtaskId, Status status) {
        Subtask subtask = getSubtask(subtaskId);
        subtask.setStatus(status);
        updateStatusOfEpic(subtask.getEpicId());
    }

    public static void setStatus(Task task, Status status) {
        setStatusOfTask(task.getId(), status);
    }

    public static void setStatus(Subtask subtask, Status status) {
        setStatusOfSubtask(subtask.getId(), status);
    }
}
