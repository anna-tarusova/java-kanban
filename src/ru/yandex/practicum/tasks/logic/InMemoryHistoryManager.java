package ru.yandex.practicum.tasks.logic;

import ru.yandex.practicum.tasks.model.BaseTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.yandex.practicum.tasks.logic.InMemoryTaskManager.getCopyTask;

public class InMemoryHistoryManager implements HistoryManager {
    private Node last = new Node();
    private final Node head = last;
    private final HashMap<Integer, Node> taskMap = new HashMap<>();

    @Override
    public void add(BaseTask task) {
        BaseTask copyTask = getCopyTask(task);
        remove(copyTask.getId());
        Node newNode = new Node(copyTask);
        taskMap.put(copyTask.getId(), newNode);
        linkLast(newNode);
    }

    @Override
    public List<BaseTask> getHistory() {
        ArrayList<BaseTask> result = new ArrayList<>();
        Node node = head.getNext();
        while (node != null) {
            result.add(node.getData());
            node = node.getNext();
        }
        return result;
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = taskMap.get(id);
        if (nodeToRemove == null) {
            return;
        }
        taskMap.remove(id);
        remove(nodeToRemove);
    }

    private void linkLast(Node node) {
        last.setNext(node);
        node.setPrev(last);
        last = node;
    }

    private void remove(Node nodeToRemove) {
        Node prev = nodeToRemove.getPrev();
        if (nodeToRemove.getNext() == null) {
            prev.setNext(null);
            last = prev;
        } else {
            Node next = nodeToRemove.getNext();
            prev.setNext(next);
            next.setPrev(prev);
        }
    }
}
