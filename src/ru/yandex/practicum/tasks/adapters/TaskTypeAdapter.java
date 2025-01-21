package ru.yandex.practicum.tasks.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.io.IOException;

public class TaskTypeAdapter extends TypeAdapter<TaskType> {

    @Override
    public void write(JsonWriter jsonWriter, TaskType taskType) throws IOException {
        jsonWriter.value(taskType.toString());
    }

    @Override
    public TaskType read(JsonReader jsonReader) throws IOException {
        return Enum.valueOf(TaskType.class, jsonReader.nextString());
    }
}
