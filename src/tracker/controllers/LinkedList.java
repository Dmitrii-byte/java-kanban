package tracker.controllers;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class LinkedList<T extends Task> {
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
