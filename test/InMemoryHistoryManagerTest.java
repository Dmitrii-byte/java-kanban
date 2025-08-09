import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.HistoryManager;
import tracker.controllers.InMemoryHistoryManager;
import tracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task(1, "Task 1", "Description 1");
        task2 = new Task(2, "Task 2", "Description 2");
        task3 = new Task(3, "Task 3", "Description 3");
    }

    @Test
    void shouldAddTasksToHistory() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Неверное количество задач в истории");
        assertEquals(task1, history.get(0), "Первая задача не совпадает");
        assertEquals(task2, history.get(1), "Вторая задача не совпадает");
        assertEquals(task3, history.get(2), "Третья задача не совпадает");
    }

    @Test
    void shouldHandleEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void shouldNotDuplicateTasksInHistory() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);

        historyManager.addToHistory(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Дубликат не удален из истории");
        assertEquals(task2, history.get(0), "Порядок задач нарушен после удаления дубликата");
        assertEquals(task1, history.get(1), "Дубликат должен быть в конце истории");
    }

    @Test
    void shouldRemoveTaskFromBeginningOfHistory() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверное количество задач после удаления");
        assertEquals(task2, history.get(0), "Первая задача после удаления не совпадает");
        assertEquals(task3, history.get(1), "Вторая задача после удаления не совпадает");
    }

    @Test
    void shouldRemoveTaskFromMiddleOfHistory() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверное количество задач после удаления");
        assertEquals(task1, history.get(0), "Первая задача после удаления не совпадает");
        assertEquals(task3, history.get(1), "Вторая задача после удаления не совпадает");
    }

    @Test
    void shouldRemoveTaskFromEndOfHistory() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверное количество задач после удаления");
        assertEquals(task1, history.get(0), "Первая задача после удаления не совпадает");
        assertEquals(task2, history.get(1), "Вторая задача после удаления не совпадает");
    }

    @Test
    void shouldClearHistory() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);

        historyManager.clearHistory();

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не очищена");
    }

    @Test
    void shouldNotFailWhenRemovingNonExistentTask() {
        historyManager.addToHistory(task1);

        historyManager.remove(999);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не должна измениться");
        assertEquals(task1, history.getFirst(), "Оставшаяся задача не совпадает");
    }

    @Test
    void shouldMaintainOrderAfterMultipleOperations() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);
        historyManager.remove(task2.getId());

        historyManager.addToHistory(task1);

        historyManager.addToHistory(new Task(4, "Task 4", "Description 4"));

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Неверное количество задач после операций");
        assertEquals(task3, history.get(0), "Первая задача не совпадает");
        assertEquals(task1, history.get(1), "Вторая задача не совпадает");
        assertEquals(4, history.get(2).getId(), "Третья задача не совпадает");
    }
}