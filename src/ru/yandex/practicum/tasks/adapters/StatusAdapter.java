package ru.yandex.practicum.tasks.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.tasks.model.enums.Status;

import java.io.IOException;

public class StatusAdapter extends TypeAdapter<Status> {
    @Override
    public void write(JsonWriter jsonWriter, Status status) throws IOException {
        if (status == null) {
            jsonWriter.value((String) null);
            return;
        }
        jsonWriter.value(status.toString());
    }

    @Override
    public Status read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            return Status.NEW;
        }
        return Enum.valueOf(Status.class, jsonReader.nextString());
    }
}
