package ru.yandex.practicum.tasks.deserializers;

import com.google.gson.*;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseTaskDeserializer implements JsonDeserializer<BaseTask> {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public BaseTask deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        BaseTask task = switch (object.get("taskType").getAsString()) {
            case "TASK" -> new Task(null, null);
            case "SUBTASK" -> new Subtask(null, null);
            case "EPIC" -> new Epic(null, null);
            default -> throw new IllegalStateException("Неверный тип таски");
        };

        task.setName(object.get("name").getAsString());
        task.setDescription(object.get("description").getAsString());

        task.setId(object.get("id").getAsInt());
        if (task.getTaskType() == TaskType.SUBTASK) {
            ((Subtask)task).setEpicId(object.get("epicId").getAsInt());
        }

        if (task.getTaskType() != TaskType.EPIC) {
            JsonElement duration = object.get("duration");
            if (duration != null) {
                task.setDuration(Duration.ofMinutes(duration.getAsInt()));
            }

            JsonElement startTime = object.get("startTime");
            if (startTime != null) {
                task.setStartTime(LocalDateTime.parse(startTime.getAsString(), dateTimeFormatter));
            }

            JsonElement status = object.get("status");
            if (status != null) {
                task.setStatus(Enum.valueOf(Status.class, status.getAsString()));
            }
        } else if (object.has("subtasks")) {
            JsonElement subtasks = object.get("subtasks");
            JsonArray epicSubtasks = subtasks.getAsJsonArray();
            for (JsonElement element : epicSubtasks) {
                JsonObject subTaskObject = element.getAsJsonObject();

                Subtask subtask = new Subtask(null, null);
                subtask.setName(subTaskObject.get("name").getAsString());
                subtask.setDescription(subTaskObject.get("description").getAsString());
                subtask.setEpicId(task.getId());
                subtask.setId(subTaskObject.get("id").getAsInt());
                subtask.setStatus(Enum.valueOf(Status.class, subTaskObject.get("status").getAsString()));

                if (subTaskObject.has("duration")) {
                    subtask.setDuration(Duration.ofMinutes(subTaskObject.get("duration").getAsInt()));
                }

                if (subTaskObject.has("startTime")) {
                    subtask.setStartTime(LocalDateTime.parse(subTaskObject.get("startTime").getAsString(), dateTimeFormatter));
                }

                ((Epic)task).addSubtask(subtask);
            }
        }

        return task;
    }
}
