package tracker.controllers;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> linkedHistory = new HashMap<>();
    private final DoublyLinkedList<Task> nsa = new DoublyLinkedList<>();

    @Override
    public void remove(int id) {
        Node<Task> node = linkedHistory.remove(id);
        if (node != null) {
            nsa.removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(nsa.getTask());
    }

    @Override
    public void clearHistory() {
        linkedHistory.clear();
        nsa.head = null;
        nsa.tail = null;
    }

    @Override
    public void addToHistory(Task task) {
        if (linkedHistory.containsKey(task.getId()))
            remove(task.getId());
        Node<Task> node = nsa.linkLast(task);
        linkedHistory.put(task.getId(), node);
    }
}

class DoublyLinkedList<T extends Task> {
    public Node<T> head;
    public Node<T> tail;

    public Node<T> linkLast(T value) {
        final Node<T> oldTail = tail;
        final Node<T> newNode = new Node<>(oldTail, value, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else
            oldTail.next = newNode;
        return newNode;
    }

    public List<Task> getTask() {
        List<Task> allTasks = new ArrayList<>();
        Node<T> oldHead = head;
        while (oldHead != null) {
            allTasks.add(oldHead.data);
            oldHead = oldHead.next;
        }
        return allTasks;
    }

    public void removeNode(Node<T> node) {
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
}

class Node<T extends Task> {
    public T data;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T data, Node<T> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
