package ru.yandex.practicum.tasks;

import ru.yandex.practicum.tasks.exceptions.NotFound;
import ru.yandex.practicum.tasks.exceptions.WrongTaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private static int taskId = 0;
    private static Map<Integer, BaseTask> tasks = new HashMap<>();

    public static List<BaseTask> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public static void clearTasks() {
        tasks = new HashMap<>();
        taskId = 0;
    }

    public static BaseTask getTask(int id) {
        return tasks.get(id);
    }

    public static void addTask(BaseTask task) {
        taskId++;
        task.setId(taskId);
        tasks.put(taskId, task);
    }

    public static void updateTask(BaseTask task) {
        tasks.put(task.getId(), task);
    }

    public static void removeTask(int id) {
        tasks.remove(id);
    }

    public static List<Subtask> getSubtasksOfEpic(int epicId) {
        if (!tasks.containsKey(epicId)) {
            throw new NotFound("Не найден эпик с id = " + epicId);
        }
        BaseTask task = tasks.get(epicId);

        if (!task.getTaskType().equals(TaskType.EPIC)) {
            throw new WrongTaskType("Задача не является эпиком");
        }

        Epic epic = (Epic) task;
        List<Subtask> result = new ArrayList<>();

        for (int id : epic.getSubtasksIds()) {
            Subtask subtask = (Subtask) tasks.get(id);
            result.add(subtask);
        }

        return result;
    }

    public static List<Task> getListTasks() {
        List<BaseTask> allTasks = getAllTasks();
        return allTasks
                .stream()
                .filter((BaseTask t) -> t.getTaskType() == TaskType.TASK)
                .map((BaseTask bt) -> (Task)bt)
                .toList();
    }

    public static List<Epic> getListEpics() {
        List<BaseTask> allTasks = getAllTasks();
        return allTasks
                .stream()
                .filter((BaseTask t) -> t.getTaskType() == TaskType.EPIC)
                .map((BaseTask bt) -> (Epic)bt)
                .toList();
    }

    public static List<Subtask> getListSubtasks() {
        List<BaseTask> allTasks = getAllTasks();
        return allTasks
                .stream()
                .filter((BaseTask t) -> t.getTaskType() == TaskType.SUBTASK)
                .map((BaseTask bt) -> (Subtask)bt)
                .toList();
    }
}
