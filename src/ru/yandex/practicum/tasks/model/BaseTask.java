package ru.yandex.practicum.tasks.model;

import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

public abstract class BaseTask {
    protected String name;
    protected String description;
    protected int id;
    protected Status status = Status.NEW;

    public BaseTask(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", id, getTaskType(), name, status.toString(), description);
    }


    public static BaseTask fromString(String value) {
        String[] groups = value.split(",");
        if (groups.length < 5) {
            throw new IllegalStateException("Неправильный формат данных");
        }
        if (groups[1].equals(TaskType.SUBTASK.toString()) && groups.length < 6) {
            throw new IllegalStateException("Неправильный формат данных: для subtask должно быть 6 полей");
        }

        int id;

        try {
            id = Integer.parseInt(groups[0]);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Неправильный формат данных: первое поле должно быть целым числом!");
        }
        String typeTaskStr = groups[1];
        TaskType taskType = switch (typeTaskStr) {
            case "TASK" -> TaskType.TASK;
            case "SUBTASK" -> TaskType.SUBTASK;
            case "EPIC" -> TaskType.EPIC;
            default ->
                    throw new IllegalStateException("Неправильный формат данных: второе поле должно быть EPIC, SUBTASK или TASK");
        };

        String name = groups[2];
        String statusStr = groups[3];
        Status status = switch (statusStr) {
            case "NEW" -> Status.NEW;
            case "IN_PROGRESS" -> Status.IN_PROGRESS;
            case "DONE" -> Status.DONE;
            default ->
                    throw new IllegalStateException("Неправильный формат данных: четвертое поле должно быть NEW, IN_PROGRESS или DONE");
        };

        String description = groups[4];
        Integer epicId = null;
        if (taskType == TaskType.SUBTASK) {
            try {
                epicId = Integer.parseInt(groups[5]);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Неправильный формат данных: первое поле должно быть целым числом!");
            }
        }

        BaseTask task;
        if (taskType == TaskType.TASK) {
            task = new Task(name, description);
        } else if (taskType == TaskType.EPIC) {
            task = new Epic(name, description);
        } else {
            Subtask subtask = new Subtask(name, description);
            subtask.setEpicId(epicId);
            task = subtask;
        }

        task.setId(id);
        task.setStatus(status);
        return task;
    }

    public abstract TaskType getTaskType();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseTask baseTask = (BaseTask) o;
        return id == baseTask.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
