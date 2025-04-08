package tracker.controllers;

import tracker.model.*;
import java.util.ArrayList;

public interface TaskManager {
    // методы для задач
    ArrayList<Task> getAllTasks();

    void clearTasks();

    Task getTaskById(int id);

    void addTask(Task task);

    void removeTaskById(int id);

    void updateTask(Task task);

    // методы для эпиков
    ArrayList<Epic> getAllEpics();

    void clearEpics();

    Epic getEpicById(int id);

    void addEpic(Epic epic);

    void removeEpicById(int id);

    ArrayList<Subtask> getSubtasksByEpic(int id);

    void updateEpic(Epic epic);

    // методы для подзадач
    ArrayList<Subtask> getAllSubtasks();

    void clearSubtasks();

    Subtask getSubtaskById(int id);

    void addSubtask(Subtask subtask);

    void removeSubtaskById(int id);

    void updateSubtask(Subtask subtask);

    ArrayList<Task> getHistory();
}
