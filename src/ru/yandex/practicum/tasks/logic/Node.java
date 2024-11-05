package ru.yandex.practicum.tasks.logic;

import ru.yandex.practicum.tasks.model.BaseTask;

public class Node {
    private BaseTask data;
    private Node prev;
    private Node next;

    public Node() {
    }

    public Node(BaseTask task) {
        setData(task);
    }

    public BaseTask getData() {
        return data;
    }

    public void setData(BaseTask task) {
        data = task;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
