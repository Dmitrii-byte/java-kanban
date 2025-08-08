package tracker.controllers;

import tracker.Status.Status;
import tracker.TypeTask.TypeTask;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.Exception.ManagerSaveException;

import java.io.*;
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
        return "id,type,name,status,description,epic\n";
    }

    private String toString(Task task) {
        TypeTask type = task.getType();
        String result;

        if (type == TypeTask.SUBTASK)
            result = task.getId() + "," + type + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() + "," + subtasks.get(task.getId()).getEpicId() + "\n";
        else
            result = task.getId() + "," + type + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() + "\n";

        return result;
    }

    private Task fromString(String value) {
        String[] split = value.split(",");

        if (split[1].equals(String.valueOf(TypeTask.TASK))) {
            return new Task(Integer.parseInt(split[0]), split[2], split[4], Status.valueOf(split[3]));
        } else if (split[1].equals(String.valueOf(TypeTask.SUBTASK))) {
            Subtask subtask = new Subtask(Integer.parseInt(split[0]), split[2], split[4], Status.valueOf(split[3]), Integer.parseInt(split[5]));
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasksId().add(subtask.getId());
            }
            return subtask;
        } else if (split[1].equals(String.valueOf(TypeTask.EPIC))) {
            return new Epic(Integer.parseInt(split[0]), split[2], split[4], new ArrayList<>());
        } else
            return null;
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