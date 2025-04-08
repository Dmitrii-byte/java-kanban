package tracker.controllers;

import tracker.model.*;
import tracker.Status.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();
    private int id = 0;

    // методы для задач
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        final Task savedTask = tasks.get(id);
        history.addToHistory(new Task(savedTask.getId(), savedTask.getTitle(), savedTask.getDescription(), savedTask.getStatus()));
        return tasks.get(id);
    }

    @Override
    public void addTask(Task task) {
        Task newTask = new Task(generationId(), task.getTitle(), task.getDescription(), task.getStatus());
        tasks.put(newTask.getId(), newTask);
        task.setId(newTask.getId());
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // методы для эпиков
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subIds = new ArrayList<>(epic.getSubtasksId());
            for (Integer subId : subIds) {
                removeSubtaskById(subId);
            }
        }
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        final Epic savedEpic = epics.get(id);
        Epic epic = new Epic(savedEpic.getId(), savedEpic.getTitle(), savedEpic.getDescription(),savedEpic.getSubtasksId());
        updateEpicStatus(epic);
        history.addToHistory(epic);
        return epics.get(id);
    }

    @Override
    public void addEpic(Epic epic) {
        Epic newEpic = new Epic(generationId(), epic.getTitle(), epic.getDescription(), new ArrayList<>());
        epics.put(newEpic.getId(), newEpic);
        updateEpicStatus(epic);
        epic.setId(newEpic.getId());
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            ArrayList<Integer> subIds = new ArrayList<>(epic.getSubtasksId());
            for (Integer subId : subIds) {
                subtasks.remove(subId);
            }
        }
        epics.remove(id);
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null)
            return null;
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (Integer subId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subId);
            subtasksList.add(subtask);
        }
        return subtasksList;
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        savedEpic.setTitle(epic.getTitle());
        savedEpic.setDescription(epic.getDescription());
    }

    // методы для подзадач
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask savedSub = subtasks.get(id);
        history.addToHistory(new Subtask(savedSub.getId(), savedSub.getTitle(), savedSub.getDescription(), savedSub.getStatus(), savedSub.getEpicId()));
        return subtasks.get(id);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        Subtask newSubtask = new Subtask(generationId(), subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
        subtasks.put(newSubtask.getId(), newSubtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtasksId().add(newSubtask.getId());
            updateEpicStatus(epic);
        }
        subtask.setId(newSubtask.getId());
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasksId().remove(Integer.valueOf(id));
                updateEpicStatus(epic);
            }
            subtasks.remove(id);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }

    // вспомогательные методы
    private int generationId() {
        return ++id;
    }

    private void updateEpicStatus(Epic epic) {
        boolean allNew = true;
        boolean allDone = true;
        for (Integer subId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subId);
            if (subtask != null) {
                if (subtask.getStatus() != Status.NEW) {
                    allNew = false;
                }
                if (subtask.getStatus() != Status.DONE) {
                    allDone = false;
                }
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}