package ru.yandex.practicum.tasks;

import java.util.Objects;

public abstract class BaseTask {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;

    public BaseTask(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
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
        return id == baseTask.id && Objects.equals(name, baseTask.name) && Objects.equals(description, baseTask.description) && status == baseTask.status;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
