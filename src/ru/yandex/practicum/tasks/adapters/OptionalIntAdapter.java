package ru.yandex.practicum.tasks.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.OptionalInt;

public class OptionalIntAdapter extends TypeAdapter<OptionalInt> {
    @Override
    public void write(JsonWriter jsonWriter, OptionalInt optionalInt) throws IOException {
        if (optionalInt == null || optionalInt.isEmpty()) {
            jsonWriter.value((String) null);
            return;
        }
        jsonWriter.value(optionalInt.getAsInt());
    }

    @Override
    public OptionalInt read(JsonReader jsonReader) throws IOException {
        if (!jsonReader.hasNext()) {
           return OptionalInt.empty();
        }
        int result = jsonReader.nextInt();
        return OptionalInt.of(result);
    }
}
