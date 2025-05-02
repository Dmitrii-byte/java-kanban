package tracker.controllers;

import tracker.model.Task;

import java.util.List;

public interface HistoryManager {
    void clearHistory();

    void addToHistory(Task task);

    void remove(int id);

    List<Task> getHistory();
}
