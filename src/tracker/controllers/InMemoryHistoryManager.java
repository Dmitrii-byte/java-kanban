package tracker.controllers;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void remove(int id) {
        Node node = historyMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(getTask());
    }

    @Override
    public void clearHistory() {
        historyMap.clear();
        head = null;
        tail = null;
    }

    @Override
    public void addToHistory(Task task) {
        if (historyMap.containsKey(task.getId()))
            remove(task.getId());
        Node node = linkLast(task);
        historyMap.put(task.getId(), node);
    }

    private Node linkLast(Task value) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, value, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else
            oldTail.next = newNode;
        return newNode;
    }

    private List<Task> getTask() {
        List<Task> allTasks = new ArrayList<>();
        Node oldHead = head;
        while (oldHead != null) {
            allTasks.add(oldHead.data);
            oldHead = oldHead.next;
        }
        return allTasks;
    }

    private void removeNode(Node node) {
        if (node == null) return;
        if (node.prev != null)
            node.prev.next = node.next;
        else
            head = node.next;
        if (node.next != null)
            node.next.prev = node.prev;
        else
            tail = node.prev;
    }

    private static class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}

