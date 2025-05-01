package tracker.controllers;

import tracker.model.Task;

public class Node<T extends Task> {
    public T data;
    public tracker.controllers.Node<T> next;
    public tracker.controllers.Node<T> prev;

    public Node(tracker.controllers.Node<T> prev, T data, tracker.controllers.Node<T> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
