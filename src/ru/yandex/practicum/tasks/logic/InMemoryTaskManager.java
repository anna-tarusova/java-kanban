package ru.yandex.practicum.tasks.logic;

import ru.yandex.practicum.tasks.exceptions.TaskAddException;
import ru.yandex.practicum.tasks.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.exceptions.WrongTaskTypeException;
import ru.yandex.practicum.tasks.model.*;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int taskId = 1;
    private final Map<Integer, BaseTask> tasks = new HashMap<>();
    private final HistoryManager historyManager;
    private final Comparator<BaseTask> taskComparator = Comparator.comparing(BaseTask::getStartTime);
    private final TreeSet<BaseTask> sortedTasksByStartTime = new TreeSet<>(taskComparator);

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    //вспомогательный метод
    private BaseTask addBaseTask(BaseTask task) {
        //Копирование нужно из-за ТЗ 6 спринта
        //Там написано: "С помощью сеттеров экземпляры задач позволяют изменить любое своё поле,
        // но это может повлиять на данные внутри менеджера.
        // Протестируйте эти кейсы и подумайте над возможными вариантами решения проблемы."
        // Написаны даже тесты про это.
        BaseTask copyTask = getCopyTask(task);
        long overlappedCount = sortedTasksByStartTime.stream()
                .filter(s -> BaseTask.areTimeSpansOverLapped(s.getStartTime(), s.getEndTime(), copyTask.getStartTime(), copyTask.getEndTime())).count();
        if (overlappedCount > 0) {
            throw new TaskAddException("Есть пересечение с уже существующими тасками");
        }
        int taskId = getNextId();
        task.setId(taskId);
        copyTask.setId(taskId);
        tasks.put(taskId, copyTask);
        if (copyTask.getStartTime() != null) {
            sortedTasksByStartTime.add(copyTask);
        }
        return copyTask;
    }

    private int getNextId() {
        return taskId++;
    }

    protected void setStartNextId(int taskId) {
        this.taskId = taskId;
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

    protected List<BaseTask> getAllTasksOfAnyType() {
        return new ArrayList<>(tasks.values());
    }

    //Вспомогательный метод. Айдишник берется из самой таски, а не из метода getNextId()
    //Метод нужен при восстановлении тасок из какого-то источника
    protected void put(BaseTask task) {
        if (task.getTaskType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            if (!tasks.containsKey(subtask.getEpicId())) {
                throw new TaskNotFoundException(String.format("Не существует эпика с id = %d", subtask.getEpicId()));
            }
            BaseTask potentialEpic = tasks.get(subtask.getEpicId());
            if (potentialEpic.getTaskType() != TaskType.EPIC) {
                throw new IllegalStateException("Сабтаска может добавляться только в эпик");
            }
            Epic epic = (Epic)potentialEpic;
            epic.addSubtask(subtask);
        }
        if (tasks.containsKey(task.getId())) {
            throw new IllegalStateException(String.format("Таска с id = %d уже была ранее добавлена", task.getId()));
        }
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            sortedTasksByStartTime.add(task);
        }
    }

    //Методы, работающие с тасками всех типов
    @Override
    public void clearTasksOfAnyType() {
        tasks.clear();
        sortedTasksByStartTime.clear();
        setStartNextId(1);
    }

    private BaseTask getTaskOfAnyType(int id) {
        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException("Не найден таск с id = " + id);
        }
        return tasks.get(id);
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
        toRemove.forEach((Integer id) -> {
            BaseTask task = tasks.get(id);
            tasks.remove(id);
            sortedTasksByStartTime.remove(task);
        });
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
        toRemove.forEach((Integer id) -> {
            BaseTask task = tasks.get(id);
            tasks.remove(id);
            sortedTasksByStartTime.remove(task);
        });

        tasks.values()
                .stream()
                .filter(t -> t.getTaskType().equals(TaskType.EPIC))
                .forEach(t -> ((Epic)t).calculateAll());
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

        toRemove.forEach(id -> {
            BaseTask task = tasks.get(id);
            sortedTasksByStartTime.remove(task);
            tasks.remove(id);
        });
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
    public void add(Subtask subtask) {
        BaseTask potentialEpic = getTaskOfAnyType(subtask.getEpicId());
        ensureTaskIsEpic(potentialEpic);
        BaseTask copyTask = addBaseTask(subtask);
        Epic epic = (Epic)potentialEpic;
        epic.addSubtask((Subtask) copyTask);
    }

    @Override
    public void removeTask(int id) {
        BaseTask task = getTaskOfAnyType(id);
        ensureTaskIsTask(task);
        tasks.remove(id);
        sortedTasksByStartTime.remove(task);
    }

    @Override
    public void removeSubTask(int id) {
        Subtask subtask = getSubtask(id);
        tasks.remove(id);
        Epic epic = getEpic(subtask.getEpicId());
        epic.removeSubtask(id);
        sortedTasksByStartTime.remove(subtask);
    }

    @Override
    public void removeEpic(int id) {
        BaseTask task = getTaskOfAnyType(id);
        ensureTaskIsEpic(task);

        List<Subtask> subtasks = getSubtasksOfEpic(id);
        subtasks.forEach((Subtask subtask) -> tasks.remove(subtask.getId()));
        tasks.remove(id);
        sortedTasksByStartTime.remove(task);
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
            Epic epic = getEpic(((Subtask)task).getEpicId());
            epic.calculateStatus();
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

    @Override
    public List<BaseTask> getPrioritizedTasks() {
        return sortedTasksByStartTime.stream().toList();
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
        if (copyTask.getTaskType() != TaskType.EPIC) {
            copyTask.setStatus(task.getStatus());
            copyTask.setStartTime(task.getStartTime());
            copyTask.setDuration(task.getDuration());
        }
        return copyTask;
    }
}
