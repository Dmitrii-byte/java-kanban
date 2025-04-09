package tracker.controllers;

import tracker.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final int MAX_SIZE_HISTORY = 10;
    private final ArrayList<Task> history = new ArrayList<>(MAX_SIZE_HISTORY);

    @Override
    public void addToHistory(Task task) {
        if (history.size() >= MAX_SIZE_HISTORY) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
