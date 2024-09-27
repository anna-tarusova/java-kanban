package ru.yandex.practicum;

import ru.yandex.practicum.tasks.*;

public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("Позвонить папе", "Позвонить с мобильного телефона в 19:00 во вторник", Status.NEW);
        Task task2 = new Task("Испечь шарлотку", "Испечь шарлотку с яблоками с дачи", Status.IN_PROGRESS);

        Epic epic1 = new Epic("Купить квартиру", "Купить квартиру в Московском районе", Status.NEW);
        epic1.addSubTask(new Subtask("Сабтаск 1", "Описание сабтаска 1", Status.IN_PROGRESS));
        epic1.addSubTask(new Subtask("Сабтаск 2", "Описание сабтаска 2", Status.NEW));

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", Status.IN_PROGRESS);
        epic2.addSubTask(new Subtask("Сабтаск 1 эпика 2", "Описание сабтаска 1 эпика 2", Status.DONE));

        TaskManager.addTask(epic1);
        TaskManager.addTask(epic2);
        TaskManager.addTask(task1);
        TaskManager.addTask(task2);

        System.out.println(TaskManager.getAllTasks());
        System.out.println(TaskManager.getListEpics());
        System.out.println(TaskManager.getSubtasksOfEpic(epic1.getId()));
        System.out.println(TaskManager.getSubtasksOfEpic(epic2.getId()));

        task1.setStatus(Status.DONE);
        task2.setStatus(Status.IN_PROGRESS);
        System.out.println(task1.getStatus());
        System.out.println(task2.getStatus());
        System.out.println(epic1.getStatus());
        System.out.println(epic2.getStatus());

        TaskManager.clearTasks();
        System.out.println(TaskManager.getAllTasks());
    }
}
