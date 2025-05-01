package tracker.controllers;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private final LinkedList<Task> linkedHistory = new LinkedList<>();

    @Override
    public void remove(int id) {
        Node<Task> node = historyMap.remove(id);
        if (node != null) {
            linkedHistory.removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(linkedHistory.getTask());
    }

    @Override
    public void clearHistory() {
        historyMap.clear();
        linkedHistory.head = null;
        linkedHistory.tail = null;
    }

    @Override
    public void addToHistory(Task task) {
        if (historyMap.containsKey(task.getId()))
            remove(task.getId());
        Node<Task> node = linkedHistory.linkLast(task);
        historyMap.put(task.getId(), node);
    }
}

