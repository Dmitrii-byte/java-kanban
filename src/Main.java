import tracker.Status.Status;
import tracker.controllers.*;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static tracker.controllers.FileBackedTaskManager.loadFromFile;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic with DONE subtasks", "Description", new ArrayList<>());
        taskManager.addEpic(epic);

        System.out.println(epic.getId() + ", " + epic.getStatus() + ", " + epic.getSubtasksId());
        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofMinutes(30),
                LocalDateTime.now().plusHours(1), Status.DONE, 1);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        System.out.println(epic.getId() + ", " + epic.getStatus() + ", " + epic.getSubtasksId());

       // Path path = Paths.get("resources/data.txt");
        /*FileBackedTaskManager newEr = new FileBackedTaskManager(path.toFile());
        newEr.addTask(new Task("reg", "sdcds", Duration.ofMinutes(100), LocalDateTime.of(2022, 11, 10, 10, 25),Status.IN_PROGRESS));
        newEr.addEpic(new Epic("asdasd", "asdasd", new ArrayList<>()));
        newEr.addSubtask(new Subtask("dsd", "saad", Duration.ofMinutes(100), LocalDateTime.of(2022, 12, 2, 10, 25), Status.DONE, 2));
        newEr.addTask(new Task("reg", "sdcds", Duration.ofMinutes(100), LocalDateTime.of(2022, 12, 25, 10, 25),Status.IN_PROGRESS));
        newEr.addSubtask(new Subtask("dsd", "saad", Duration.ofMinutes(100), LocalDateTime.of(2022, 9, 22, 10, 25), Status.DONE, 2));

        //FileBackedTaskManager files = loadFromFile(path.toFile());

        System.out.println("список:");
        System.out.println(files.getAllTasks());
        System.out.println(files.getAllSubtasks());
        System.out.println(files.getAllEpics());
        System.out.println("Список по дате начала");
        System.out.println(files.getPrioritizedTasks());
        */
    }
}