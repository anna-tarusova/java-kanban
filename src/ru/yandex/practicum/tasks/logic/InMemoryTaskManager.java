package ru.yandex.practicum.tasks.logic;

import ru.yandex.practicum.tasks.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.exceptions.WrongTaskTypeException;
import ru.yandex.practicum.tasks.model.*;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int taskId = 0;
    private Map<Integer, BaseTask> tasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    //вспомогательный метод
    private void addBaseTask(BaseTask task) {
        BaseTask copyTask = getCopyTask(task);
        taskId++;
        task.setId(taskId);
        copyTask.setId(taskId);
        tasks.put(taskId, copyTask);
    }

    private void ensureTaskIsTask(BaseTask task) {
        if (!task.getTaskType().equals(TaskType.TASK)) {
            throw new WrongTaskTypeException("Задача не является таском");
        }
    }

    private void ensureTaskIsSubTask(BaseTask task) {
        if (!task.getTaskType().equals(TaskType.SUBTASK)) {
            throw new WrongTaskTypeException("Задача не является сабтаском");
        }
    }

    private void ensureTaskIsEpic(BaseTask task) {
        if (!task.getTaskType().equals(TaskType.EPIC)) {
            throw new WrongTaskTypeException("Задача не является эпиком");
        }
    }

    private List<BaseTask> getAllTasksOfAnyType() {
        return new ArrayList<>(tasks.values());
    }

    //Методы, работающие с тасками всех типов
    @Override
    public void clearTasksOfAnyType() {
        tasks = new HashMap<>();
        taskId = 0;
    }

    private BaseTask getTaskOfAnyType(int id) {
        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException("Не найден таск с id = " + id);
        }
        return tasks.get(id);
    }

    private void updateStatusOfEpic(Epic epic) {
        updateStatusOfEpic(epic.getId());
    }

    private void updateStatusOfEpic(int epicId) {
        BaseTask epic = getTaskOfAnyType(epicId);
        ensureTaskIsEpic(epic);
        List<Subtask> subtasksOfEpic = tasks
                .values()
                .stream()
                .filter(t -> t.getTaskType() == TaskType.SUBTASK && ((Subtask)t).getEpicId() == epicId)
                .map(t -> (Subtask)t)
                .toList();

        if (subtasksOfEpic.stream().allMatch(t -> t.getStatus() == Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else if (subtasksOfEpic.stream().allMatch(t -> t.getStatus() == Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void updateTaskOfAnyType(BaseTask task) {
        tasks.put(task.getId(), task);

        //если таска является сабтаской, то найти её эпик и вызывать для него updateStatus
        if (task.getTaskType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            updateStatusOfEpic(subtask.getEpicId());
        }
    }

    //Методы, работающие с тасками определенного типа
    @Override
    public void clearTasks() {
        List<Integer> toRemove = new ArrayList<>();
        tasks.keySet().forEach((Integer id) -> {
            BaseTask task = tasks.get(id);
            if (task.getTaskType() == TaskType.TASK) {
                toRemove.add(id);
            }
        });
        toRemove.forEach((Integer id) -> tasks.remove(id));
    }

    @Override
    public void clearSubTasks() {
        List<Integer> toRemove = new ArrayList<>();
        tasks.keySet().forEach((Integer id) -> {
            BaseTask task = tasks.get(id);
            if (task.getTaskType() == TaskType.SUBTASK) {
                toRemove.add(id);
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

    @Override
    public void clearEpics() {
        List<Integer> toRemove = new ArrayList<>();
        tasks.keySet().forEach((Integer id) -> {
            BaseTask task = tasks.get(id);
            if (task.getTaskType() == TaskType.EPIC) {
                toRemove.add(id);
            }
        });

        for (int i = 0; i < toRemove.size(); i++) {
            int id = toRemove.get(i);
            tasks.values()
                .stream()
                .filter(t -> t.getTaskType() == TaskType.SUBTASK && ((Subtask)t).getEpicId() == id)
                .forEach(t -> toRemove.add(t.getId()));
        }

        toRemove.forEach(id -> tasks.remove(id));
    }

    @Override
    public Epic getEpic(int epicId) {
        BaseTask task = getTaskOfAnyType(epicId);
        ensureTaskIsEpic(task);
        historyManager.add(task);
        return (Epic) task;
    }

    @Override
    public Task getTask(int taskId) {
        BaseTask task = getTaskOfAnyType(taskId);
        ensureTaskIsTask(task);
        historyManager.add(task);
        return (Task) task;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        BaseTask task = getTaskOfAnyType(subtaskId);
        ensureTaskIsSubTask(task);
        historyManager.add(task);
        return (Subtask) task;
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        BaseTask task = getTaskOfAnyType(epicId);
        ensureTaskIsEpic(task);
        Epic epic = (Epic)task;
        List<Subtask> result = new ArrayList<>();
        for (int id : getAllTasksOfAnyType()
                .stream()
                .filter(bt -> bt.getTaskType() == TaskType.SUBTASK && ((Subtask)bt).getEpicId() == epic.getId())
                .map(BaseTask::getId)
                .toList()) {
            Subtask subtask = (Subtask) tasks.get(id);
            historyManager.add(subtask);
            result.add(subtask);
        }

        return result;
    }

    @Override
    public List<Task> getListTasks() {
        List<BaseTask> allTasks = getAllTasksOfAnyType();
        return allTasks
                .stream()
                .filter((BaseTask t) -> t.getTaskType() == TaskType.TASK)
                .map((BaseTask bt) -> {
                    historyManager.add(bt);
                    return (Task)bt;
                })
                .toList();
    }

    @Override
    public List<Epic> getListEpics() {
        List<BaseTask> allTasks = getAllTasksOfAnyType();
        return allTasks
                .stream()
                .filter((BaseTask t) -> t.getTaskType() == TaskType.EPIC)
                .map((BaseTask bt) -> {
                    historyManager.add(bt);
                    return (Epic)bt;
                })
                .toList();
    }

    @Override
    public List<Subtask> getListSubtasks() {
        List<BaseTask> allTasks = getAllTasksOfAnyType();
        return allTasks
                .stream()
                .filter((BaseTask t) -> t.getTaskType() == TaskType.SUBTASK)
                .map((BaseTask bt) -> {
                    historyManager.add(bt);
                    return (Subtask)bt;
                })
                .toList();
    }

    @Override
    public void add(Task task) {
        addBaseTask(task);
    }

    @Override
    public void add(Epic epic) {
        addBaseTask(epic);
    }

    @Override
    public void add(Subtask subtask, int epicId) {
        BaseTask epic = getTaskOfAnyType(epicId);
        ensureTaskIsEpic(epic);
        subtask.setEpicId(epic.getId());
        addBaseTask(subtask);
        updateStatusOfEpic((Epic)epic);
    }

    @Override
    public void removeTask(int id) {
        BaseTask task = getTaskOfAnyType(id);
        ensureTaskIsTask(task);
        tasks.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        Subtask subtask = getSubtask(id);
        tasks.remove(id);
        updateStatusOfEpic(subtask.getEpicId());
    }

    @Override
    public void removeEpic(int id) {
        BaseTask task = getTaskOfAnyType(id);
        ensureTaskIsEpic(task);

        List<Subtask> subtasks = getSubtasksOfEpic(id);
        subtasks.forEach((Subtask subtask) -> tasks.remove(subtask.getId()));
        tasks.remove(id);
    }

    @Override
    public void remove(Task task) {
        removeTask(task.getId());
    }

    @Override
    public void remove(Epic epic) {
        removeEpic(epic.getId());
    }

    @Override
    public void remove(Subtask task) {
        removeSubTask(task.getId());
    }

    @Override
    public void setStatus(int taskId, Status status) {
        BaseTask task = getTaskOfAnyType(taskId);
        if (task.getTaskType() == TaskType.EPIC) {
            throw new WrongTaskTypeException("Нельзя менять статус эпику!");
        }
        task.setStatus(status);
        if (task.getTaskType() == TaskType.SUBTASK) {
            int epicId = ((Subtask)task).getEpicId();
            Epic epic = (Epic)tasks.get(epicId);
            List<Subtask> subtasksOfEpic = tasks
                    .values()
                    .stream()
                    .filter(t -> t.getTaskType() == TaskType.SUBTASK && ((Subtask) t).getEpicId() == epicId)
                    .map(t -> (Subtask)t)
                    .toList();

            if (subtasksOfEpic.stream().allMatch(st -> st.getStatus() == Status.NEW)) {
                epic.setStatus(Status.NEW);
            } else if (subtasksOfEpic.stream().allMatch(st -> st.getStatus() == Status.DONE)) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    @Override
    public void setStatus(Task task, Status status) {
        setStatus(task.getId(), status);
    }

    @Override
    public void setStatus(Subtask subtask, Status status) {
        setStatus(subtask.getId(), status);
    }

    @Override
    public List<BaseTask> getHistory() {
        return historyManager.getHistory();
    }

    public static BaseTask getCopyTask(BaseTask task) {
        BaseTask copyTask = null;
        if (task.getTaskType() == TaskType.TASK) {
            copyTask = new Task(task.getName(), task.getDescription());
        } else if (task.getTaskType() == TaskType.SUBTASK) {
            copyTask = new Subtask(task.getName(), task.getDescription());
            ((Subtask)copyTask).setEpicId(((Subtask)task).getEpicId());
        } else if (task.getTaskType() == TaskType.EPIC) {
            copyTask = new Epic(task.getName(), task.getDescription());
        }

        if (copyTask == null) {
            throw new WrongTaskTypeException("Неизвестный тип таски");
        }

        copyTask.setId(task.getId());
        copyTask.setStatus(task.getStatus());
        return copyTask;
    }
}
