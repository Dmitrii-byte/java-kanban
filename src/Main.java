import tracker.controllers.*;

import java.io.File;
import java.nio.file.Paths;

import static tracker.controllers.FileBackedTaskManager.loadFromFile;

public class Main {
    public static void main(String[] args) {
        File file1 = Paths.get("data.txt").toFile();
        File file2 = Paths.get("output.txt").toFile();
        FileBackedTaskManager files = loadFromFile(file2);

        System.out.println(files.getAllTasks());
        System.out.println(files.getAllSubtasks());
        System.out.println(files.getAllEpics());
    }
}