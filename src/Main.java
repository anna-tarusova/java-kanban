import ru.yandex.practicum.tasks.logic.Managers;
import ru.yandex.practicum.tasks.logic.TaskManager;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;

public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("Позвонить папе", "Позвонить с мобильного телефона в 19:00 во вторник");
        Task task2 = new Task("Испечь шарлотку", "Испечь шарлотку с яблоками с дачи");

        Epic epic1 = new Epic("Купить квартиру", "Купить квартиру в Московском районе");
        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1");
        Subtask subtask2 = new Subtask("Сабтаск 2", "Описание сабтаска 2");

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Subtask subtask3 = new Subtask("Сабтаск 1 эпика 2", "Описание сабтаска 1 эпика 2");
        TaskManager taskManager = (new Managers()).getDefault();


        taskManager.add(epic1);
        taskManager.add(epic2);
        taskManager.add(task1);
        taskManager.add(task2);

        taskManager.add(subtask1, epic1.getId());
        taskManager.add(subtask2, epic1.getId());

        taskManager.add(subtask3, epic2.getId());


        System.out.println(taskManager.getListEpics());
        System.out.println(taskManager.getSubtasksOfEpic(epic1.getId()));
        System.out.println(taskManager.getSubtasksOfEpic(epic2.getId()));


        System.out.println("epic1.getStatus() = " + epic1.getStatus());
        System.out.println(epic2.getStatus());
        taskManager.setStatus(subtask1.getId(), Status.IN_PROGRESS);
        System.out.println("epic1.getStatus() = " + epic1.getStatus());

        taskManager.setStatus(subtask1, Status.DONE);
        taskManager.setStatus(subtask2, Status.DONE);
        System.out.println("epic1.getStatus() = " + epic1.getStatus());

        taskManager.setStatus(subtask2.getId(), Status.IN_PROGRESS);
        System.out.println("epic1.getStatus() = " + epic1.getStatus());
        taskManager.remove(subtask2);
        System.out.println("epic1.getStatus() = " + epic1.getStatus());

        System.out.println("clearSubTasks");
        taskManager.clearSubTasks();
        System.out.println("epic1.getStatus() = " + epic1.getStatus());
        System.out.println(taskManager.getSubtasksOfEpic(epic1.getId()));

        taskManager.add(subtask3, epic2.getId());
        System.out.println("Сабтаски эпика 2");
        System.out.println(taskManager.getSubtasksOfEpic(epic2.getId()));

        taskManager.clearEpics();;
        System.out.println("Сабтаски после clearEpics");
        System.out.println(taskManager.getListSubtasks());


        taskManager.clearTasksOfAnyType();
    }
}
