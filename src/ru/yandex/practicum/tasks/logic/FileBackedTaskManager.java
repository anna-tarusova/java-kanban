package ru.yandex.practicum.tasks.logic;

import ru.yandex.practicum.tasks.exceptions.ManagerLoadException;
import ru.yandex.practicum.tasks.exceptions.ManagerSaveException;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String filename;
    public FileBackedTaskManager(String filename, HistoryManager historyManager) {
        super(historyManager);
        this.filename = filename;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.getAbsolutePath(), new InMemoryHistoryManager());
        List<String> lines = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Не удалось прочитать файл");
        }

        List<BaseTask> baseTasks = lines.stream().map(BaseTask::fromString).toList();
        //сначала восстанавливаем эпики и только потом сабтаски (так как они кладутся в эпики)
        //таски сами по себе, поэтому можно восстановить их первым или последними
        baseTasks.stream().filter(bt -> bt.getTaskType() == TaskType.TASK).forEach(bt -> {
            fileBackedTaskManager.add((Task) bt);
        });
        baseTasks.stream().filter(bt -> bt.getTaskType() == TaskType.EPIC).forEach(bt -> {
            fileBackedTaskManager.add((Epic) bt);
        });
        baseTasks.stream().filter(bt -> bt.getTaskType() == TaskType.SUBTASK).forEach(bt -> {
            Subtask st = (Subtask)bt;
            fileBackedTaskManager.add(st, st.getEpicId());
        });

        return fileBackedTaskManager;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (BaseTask task : getAllTasksOfAnyType()) {
                writer.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в файл");
        }
    }

    @Override
    public void add(Epic epic) {
        super.add(epic);
        save();
    }

    @Override
    public void add(Subtask subtask, int epicId) {
        super.add(subtask, epicId);
        save();
    }

    @Override
    public void add(Task task) {
        super.add(task);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearTasksOfAnyType() {
        super.clearTasksOfAnyType();
        save();
    }

    @Override
    public void remove(Epic epic) {
        super.remove(epic);
        save();
    }

    @Override
    public void remove(Task task) {
        super.remove(task);
        save();
    }

    @Override
    public void remove(Subtask task) {
        super.remove(task);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void setStatus(Task task, Status status) {
        super.setStatus(task, status);
        save();
    }

    @Override
    public void setStatus(int taskId, Status status) {
        super.setStatus(taskId, status);
        save();
    }

    @Override
    public void setStatus(Subtask subtask, Status status) {
        super.setStatus(subtask, status);
        save();
    }
}
