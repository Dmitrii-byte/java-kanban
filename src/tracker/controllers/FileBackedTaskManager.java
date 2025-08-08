package tracker.controllers;

import tracker.Status.Status;
import tracker.TypeTask.TypeTask;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.Exception.ManagerSaveException;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File filename;

    public FileBackedTaskManager(File filename) {
        this.filename = filename;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager savedData = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // пропускается заголовок в файле
            String content;

            while ((content = reader.readLine()) != null) {
                Task task = savedData.fromString(content);

                switch (Objects.requireNonNull(task).getType()) {
                    case SUBTASK -> savedData.subtasks.put(task.getId(), (Subtask) task);
                    case EPIC -> savedData.epics.put(task.getId(), (Epic) task);
                    case TASK -> savedData.tasks.put(task.getId(), task);
                    default -> {
                    }
                }

            }

            for (Epic epic : savedData.epics.values()) {
                savedData.updateEpicStatus(epic);
            }

        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка загрузки данных из файла " + ex.getMessage());
        }

        return savedData;
    }

    private List<Task> getTasks() {
        List<Task> getTasks = new ArrayList<>();
        getTasks.addAll(tasks.values());
        getTasks.addAll(epics.values());
        getTasks.addAll(subtasks.values());
        return getTasks;
    }

    private String headline() {
        return "id,type,name,status,description,epic,startTime,duration\n";
    }

    private String toString(Task task) {
        TypeTask type = task.getType();
        StringBuilder result = new StringBuilder();

        result.append(task.getId()).append(",")
                .append(type).append(",")
                .append(task.getTitle()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getDescription()).append(",");

        if (task.getStartTime() != null) {
            result.append(task.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        result.append(",");

        if (task.getDuration() != null) {
            result.append(task.getDuration().toMinutes());
        }

        if (type == TypeTask.SUBTASK) {
            result.append(",").append(((Subtask) task).getEpicId()).append(",");
        }

        result.append("\n");
        return result.toString();
    }

    private Task fromString(String value) {
        String[] split = value.split(",");

        int id = Integer.parseInt(split[0]);
        TypeTask type = TypeTask.valueOf(split[1]);
        String title = split[2];
        String description = split[4];
        Status status = Status.valueOf(split[3]);

        LocalDateTime startTime = split[5].isEmpty() ? null : LocalDateTime.parse(split[5], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Duration duration = split[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(split[6]));

        switch (type) {
            case TASK -> {
                return new Task(id, title, description, duration, startTime, status);
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(split[7]);
                return new Subtask(id, title, description, duration, startTime, status, epicId);
            }
            case EPIC -> {
                Epic epic = new Epic(id, title, description, new ArrayList<>());
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                if (startTime != null && duration != null) {
                    epic.setEndTime(startTime.plus(duration));
                }
                return epic;
            }
            default -> {
                return null;
            }
        }
    }

    private void save() {
        try (Writer writer = new FileWriter(filename)) {
            writer.write(headline());

            for (Task task : getTasks()) {
                writer.write(toString(task));
            }

        } catch (IOException ex) {
            throw new ManagerSaveException("Ошибка при чтении файла " + ex.getMessage());
        }
    }
}