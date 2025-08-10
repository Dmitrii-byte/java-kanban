import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".csv").toFile();
        taskManager = new FileBackedTaskManager(tempFile);
        super.setUp();
    }

    @Test
    public void loadFromEmptyFile() {
        FileBackedTaskManager emptyData = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(0, emptyData.getAllTasks().size());
        assertEquals(0, emptyData.getAllSubtasks().size());
        assertEquals(0, emptyData.getAllEpics().size());

    }

    @Test
    void shouldLoadFromFile() {
        // Добавляем задачи
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        subtask.setEpicId(epic.getId());
        taskManager.addSubtask(subtask);

        // Создаем новый менеджер из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что данные загрузились корректно
        assertNotNull(loadedManager.getTaskById(task.getId()), "Задача не загрузилась из файла");
        assertNotNull(loadedManager.getEpicById(epic.getId()), "Эпик не загрузился из файла");
        assertNotNull(loadedManager.getSubtaskById(subtask.getId()), "Подзадача не загрузилась из файла");

        assertEquals(task, loadedManager.getTaskById(task.getId()), "Задачи не совпадают");
        assertEquals(epic, loadedManager.getEpicById(epic.getId()), "Эпики не совпадают");
        assertEquals(subtask, loadedManager.getSubtaskById(subtask.getId()), "Подзадачи не совпадают");

        assertTrue(loadedManager.getSubtasksByEpic(epic.getId()).contains(subtask),
                "Связь эпика и подзадачи не сохранилась");
    }
}