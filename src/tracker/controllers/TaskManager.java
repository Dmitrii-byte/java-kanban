package tracker.controllers;

import tracker.model.*;
import tracker.Status.Status;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int id = 0;

    // методы для задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void addTask(Task task) {
        Task newTask = new Task(generationId(), task.getTitle(), task.getDescription(), task.getStatus());
        tasks.put(newTask.getId(), newTask);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // методы для эпиков
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void clearEpics() {
        for (Epic epic : epics.values()) {
            ArrayList<Integer> subIds = new ArrayList<>(epic.getSubtasksId());
            for (Integer subId : subIds) {
                removeSubtaskById(subId);
            }
        }
        epics.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void addEpic(Epic epic) {
        Epic newEpic = new Epic(generationId(), epic.getTitle(), epic.getDescription(), new ArrayList<>());
        epics.put(newEpic.getId(), newEpic);
        updateEpicStatus(epic);
    }

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

    // обновленный метод (убран sout)
    public ArrayList<Subtask> getSubtasksByEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null)
            return null;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Integer subId : epic.getSubtasksId())
            subtasks.add(getSubtaskById(subId));
        return subtasks;
    }

    public void updateEpic(Epic epic) {
        ArrayList<Subtask> subtask =  new ArrayList<>(subtasks.values());
        for (Subtask sb : subtask) {
            if(sb.getEpicId() == epic.getId()) {
                subtasks.remove(sb.getId());
            }
        }
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    // методы для подзадач
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // обновлен метод (чистка списка задач у каждого Эпика)
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void addSubtask(Subtask subtask) {
        int newId = generationId();
        Subtask newSubtask = new Subtask(newId, subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
        subtasks.put(newId, newSubtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtasksId().add(newId);
            updateEpicStatus(epic);
        }
    }

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

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
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
                if(subtask.getStatus() != Status.NEW) {
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