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
        Path path = Paths.get("resources/data.txt");
        FileBackedTaskManager newEr = new FileBackedTaskManager(path.toFile());
        newEr.addTask(new Task("reg", "sdcds", Duration.ofMinutes(100), LocalDateTime.of(2022, 12, 22, 10, 25),Status.IN_PROGRESS));
        newEr.addEpic(new Epic("asdasd", "asdasd", new ArrayList<>()));
        newEr.addSubtask(new Subtask("dsd", "saad", Duration.ofMinutes(100), LocalDateTime.of(2022, 12, 22, 10, 25), Status.DONE, 2));
        FileBackedTaskManager files = loadFromFile(path.toFile());

        System.out.println(files.getAllTasks());
        System.out.println(files.getAllSubtasks());
        System.out.println(files.getAllEpics());
    }
}