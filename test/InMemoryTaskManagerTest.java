import org.junit.jupiter.api.BeforeEach;
import tracker.controllers.InMemoryTaskManager;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import tracker.model.Task;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        super.setUp();
    }

    @Test
    void getTaskFromMapShouldReturnCorrectTask() {
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertEquals(task, retrievedTask, "Извлеченная задача не совпадает с оригинальной");
        assertEquals(task.getTitle(), retrievedTask.getTitle(), "Название задачи не совпадает");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Описание задачи не совпадает");
    }

    @Test
    void getTaskFromMapShouldReturnNullForNonExistentId() {
        Task retrievedTask = taskManager.getTaskById(999);
        assertNull(retrievedTask, "Для несуществующего ID должен возвращаться null");
    }

    @Test
    void getPrioritizedTasksShouldReturnTasksInCorrectOrder() {

        Task task1 = new Task("Task 2", "Description", Duration.ofMinutes(15), task.getStartTime().plusHours(1));

        taskManager.addTask(task1);
        taskManager.addTask(task);

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(3, prioritizedTasks.size(), "Неверное количество задач в TreeSet");

        Task[] expectedOrder = {task, task1, subtask};
        assertArrayEquals(expectedOrder, prioritizedTasks.toArray(), "Порядок задач не соответствует ожидаемому");
    }

    @Test
    void getPrioritizedTasksShouldNotIncludeTasksWithoutStartTime() {
        Task taskWithoutTime = new Task("Without Time", "Desc");

        taskManager.addTask(task);
        taskManager.addTask(taskWithoutTime);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(1, prioritizedTasks.size(), "Должна быть только одна задача с временем");
        assertTrue(prioritizedTasks.contains(task), "Задача с временем должна быть в списке");
    }

    @Test
    void getPrioritizedTasksShouldReturnEmptySetForEmptyManager() {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertTrue(prioritizedTasks.isEmpty(), "Для пустого менеджера должен возвращаться пустой TreeSet");
    }
}