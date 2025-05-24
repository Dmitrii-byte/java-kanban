import org.junit.jupiter.api.Test;
import tracker.model.*;
import tracker.controllers.*;
import tracker.Status.*;

import java.io.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static tracker.controllers.FileBackedTaskManager.loadFromFile;

public class FileBackedTaskManagerTest {
    public static FileBackedTaskManager fileBackedTaskManager;

    @Test
    public void loadFromEmptyFile() {
        try {
            File tmp = File.createTempFile("tmp", ".txt");
            FileBackedTaskManager emptyData = loadFromFile(tmp);

            assertEquals(0, emptyData.getAllTasks().size());
            assertEquals(0, emptyData.getAllSubtasks().size());
            assertEquals(0, emptyData.getAllEpics().size());

        } catch (IOException ex) {
            System.out.println("Ошибка загрузки файла");
        }
    }

    @Test
    public void loadFromNotEmptyFile() {
        try {
            File tmp = File.createTempFile("tmp", ".txt");
            try (FileWriter writer = new FileWriter(tmp, true)) {
                writer.write("id,type,name,status,description,epic\n");
                writer.write("1,TASK,Task1,NEW,Description task1,\n");
                writer.write("2,EPIC,Epic2,DONE,Description epic2,\n");
                writer.write("3,SUBTASK,Sub Task2,DONE,Description sub task3,2\n");
                writer.write("4,TASK,Task3,NEW,Description task3,\n");
                writer.write("5,EPIC,Epic4,DONE,Description epic4,\n");
                writer.write("6,SUBTASK,Sub Task2,DONE,Description sub task3,2\n");
            }

            assertTrue(tmp.length() > 0, "Файл должен содержать данные");

            FileBackedTaskManager savedData = loadFromFile(tmp);
            assertEquals(2, savedData.getAllTasks().size());
            assertEquals(2, savedData.getAllEpics().size());
            assertEquals(2, savedData.getAllSubtasks().size());
        } catch (IOException ex) {
            System.out.println("Ошибка загрузки файла");
        }
    }

    @Test
    public void shouldSaveDataInFile() {
        try {
            File tmp = File.createTempFile("tmp", ".txt");
            fileBackedTaskManager = new FileBackedTaskManager(tmp);
            Task task = new Task("task1", "desTask1", Status.NEW);
            Epic epic = new Epic("Epic1", "dedEpic1", new ArrayList<>());
            fileBackedTaskManager.addTask(task);
            fileBackedTaskManager.addEpic(epic);
            Subtask subtask = new Subtask("Sub4", "desSub4", Status.NEW, epic.getId());
            fileBackedTaskManager.addSubtask(subtask);

            String expected = """
                    1,TASK,task1,NEW,desTask1
                    2,EPIC,Epic1,NEW,dedEpic1
                    3,SUBTASK,Sub4,NEW,desSub4,2
                    """;

            try (BufferedReader reader = new BufferedReader(new FileReader(tmp))) {
                StringBuilder result = new StringBuilder();
                String line;
                reader.readLine();// пропускаем заголовок списка

                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }

                assertEquals(expected, result.toString());
            }
        } catch (IOException ex) {
            System.out.println("Ошибка создания файла");
        }
    }
}

