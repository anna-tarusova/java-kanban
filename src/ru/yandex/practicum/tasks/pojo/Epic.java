package ru.yandex.practicum.tasks.pojo;

import java.util.OptionalInt;

public class Epic {
    private final String name;
    private final String description;
    private OptionalInt id;

    public Epic(String name, String description, OptionalInt id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public OptionalInt getId() {
        return id;
    }
}
