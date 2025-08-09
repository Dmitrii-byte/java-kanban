package tracker.controllers;

import tracker.Comparator.TaskStartTimeComparator;
import tracker.Exception.ManagerTimeOverLapException;
import tracker.model.*;
import tracker.Status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();
    private int id = 0;

    // методы для задач
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        for (Integer task : tasks.keySet())
            history.remove(task);
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        return getTaskFromMap(id, tasks);
    }

    @Override
    public void addTask(Task task) {
        if (isTaskOverLapWithAny(task))
            throw new ManagerTimeOverLapException("Задача \"" + task.getTitle() + "\" пересекается по времени с существующей задачей");

        Task newTask = new Task(generationId(), task.getTitle(), task.getDescription(), task.getDuration(), task.getStartTime(), task.getStatus());
        tasks.put(newTask.getId(), newTask);
        task.setId(newTask.getId());
    }

    @Override
    public void removeTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) return;
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (isTaskOverLapWithAny(task))
            throw new ManagerTimeOverLapException("Обновленная задача \"" + task.getTitle() + "\" пересекается по времени с существующей задачей");

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
        for (Integer epic : epics.keySet())
            history.remove(epic);
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        return getTaskFromMap(id, epics);
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
        if (epic == null) return;
        ArrayList<Integer> subIds = new ArrayList<>(epic.getSubtasksId());
        for (Integer subId : subIds) {
            subtasks.remove(subId);
            history.remove(subId);
        }
        epics.remove(id);
        history.remove(id);
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
        for (Integer subtask : subtasks.keySet())
            history.remove(subtask);
        subtasks.clear();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return getTaskFromMap(id, subtasks);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (isTaskOverLapWithAny(subtask))
            throw new ManagerTimeOverLapException("Подзадача \"" + subtask.getTitle() + "\" пересекается по времени с существующей задачей");

        Subtask newSubtask = new Subtask(generationId(), subtask.getTitle(), subtask.getDescription(), subtask.getDuration(), subtask.getStartTime(), subtask.getStatus(), subtask.getEpicId());
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
        if (subtask == null) return;
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.getSubtasksId().remove(Integer.valueOf(id));
            updateEpicStatus(epic);
        }
        subtasks.remove(id);
        history.remove(id);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isTaskOverLapWithAny(subtask))
            throw new ManagerTimeOverLapException("Обновленная подзадача \"" + subtask.getTitle() + "\" пересекается по времени с существующей задачей");

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public void clearHistory() {
        history.clearHistory();
    }

    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> tree = new TreeSet<>(new TaskStartTimeComparator());

        tasks.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(tree::add);

        subtasks.values().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .forEach(tree::add);

        return tree;
    }

    protected void updateEpicStatus(Epic epic) {
        boolean allNew = true;
        boolean allDone = true;
        LocalDateTime earliestStart = LocalDateTime.MAX;
        LocalDateTime latestEnd = LocalDateTime.MIN;
        Duration totalDuration = Duration.ZERO;

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

            LocalDateTime subtaskStart = subtask.getStartTime();
            if (subtaskStart != null) {
                if (subtaskStart.isBefore(earliestStart)) {
                    earliestStart = subtaskStart;
                }

                LocalDateTime subtaskEnd = subtask.getEndTime();
                if (subtaskEnd.isAfter(latestEnd)) {
                    latestEnd = subtaskEnd;
                }
            }

            if (subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        epic.setStartTime(earliestStart);
        epic.setDuration(totalDuration);
        epic.setEndTime(latestEnd);
    }

    // вспомогательные методы
    private int generationId() {
        return ++id;
    }

    private <T> T getTaskFromMap(int id, HashMap<Integer, T> map) {
        T object = map.get(id);
        if (object != null) {
            switch (object) {
                case Subtask subtask ->
                        history.addToHistory(new Subtask(subtask.getId(), subtask.getTitle(), subtask.getDescription(), subtask.getDuration(), subtask.getStartTime(), subtask.getStatus(), subtask.getEpicId()));
                case Epic epic ->
                        history.addToHistory(new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getSubtasksId()));
                case Task task ->
                        history.addToHistory(new Task(task.getId(), task.getTitle(), task.getDescription(), task.getDuration(), task.getStartTime(), task.getStatus()));
                default -> {
                }
            }
        }
        return object;
    }

    private boolean isTaskOverLap(Task task1, Task task2) {
        if (task1.getStartTime() == null && task2.getStartTime() == null)
            return false;


        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }

    private boolean isTaskOverLapWithAny(Task task) {
        if (task.getStartTime() == null)
            return false;

        boolean taskOverLap = tasks.values().stream()
                .filter(t -> t.getId() != task.getId())
                .filter(t -> t.getStartTime() != null)
                .anyMatch(t -> isTaskOverLap(t, task));

        if (taskOverLap) return true;

        return subtasks.values().stream()
                .filter(s -> s.getId() != task.getId())
                .filter(s -> s.getStartTime() != null)
                .anyMatch(s -> isTaskOverLap(s, task));
    }
}