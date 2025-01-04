package ru.yandex.practicum.tasks.model;

import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public abstract class BaseTask {
    protected String name;
    protected String description;
    protected int id;
    protected Status status = Status.NEW;
    protected Duration duration;
    protected LocalDateTime startTime;
    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public BaseTask(String name, String description) {
        this.name = name;
        this.description = description;

    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                id,
                getTaskType(),
                name,
                status.toString(),
                description,
                startTime == null ? "null" : dateTimeFormatter.format(startTime),
                duration == null ? "null" : duration.toMinutes());
    }


    public static BaseTask fromString(String value) {
        String[] groups = value.split(",");
        if (groups.length < 7) {
            throw new IllegalStateException("Неправильный формат данных");
        }
        if (groups[1].equals(TaskType.SUBTASK.toString()) && groups.length < 8) {
            throw new IllegalStateException("Неправильный формат данных: для subtask должно быть 8 полей");
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

        LocalDateTime startTime = null;
        try {
            if (!groups[5].equals("null")) {
                startTime = LocalDateTime.parse(groups[5], dateTimeFormatter);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalStateException(String.format("Неправильный формат данных: пятое поле должно быть датой в формате %s или \"null\"", DATE_FORMAT));
        }

        Duration duration = null;

        if (!groups[6].equals("null")) {
            int durationInMinutes;
            try {
                durationInMinutes = Integer.parseInt(groups[6]);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Неправильный формат данных: шестое поле должно быть числом");
            }

            if (durationInMinutes <= 0) {
                throw new IllegalStateException("Продолжительность должна быть больше нуля");
            }
            duration = Duration.ofMinutes(durationInMinutes);
        }

        Integer epicId = null;
        if (taskType == TaskType.SUBTASK) {
            try {
                epicId = Integer.parseInt(groups[7]);
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
        if (task.getTaskType() != TaskType.EPIC) {
            task.setStatus(status);
            task.setStartTime(startTime);
            task.setDuration(duration);
        }
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (getStartTime() == null || getDuration() == null) {
            return null;
        }
        return getStartTime().plus(getDuration());
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

    public static boolean areTimeSpansOverLapped(LocalDateTime timeSpan1StartTime, LocalDateTime timeSpan1EndTime,
                                          LocalDateTime timeSpan2StartTime, LocalDateTime timeSpan2EndTime) {
        if (timeSpan1StartTime == null || timeSpan1EndTime == null || timeSpan2StartTime == null || timeSpan2EndTime == null) {
            return false;
        }
        return (timeSpan1StartTime.isBefore(timeSpan2StartTime) && timeSpan1EndTime.isAfter(timeSpan2StartTime))
                || (timeSpan2StartTime.isBefore(timeSpan1StartTime) && timeSpan2EndTime.isAfter(timeSpan1StartTime));
    }
}
