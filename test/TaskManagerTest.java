import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.Exception.ManagerTimeOverLapException;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.Status.Status;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    @BeforeEach
    void setUp() throws IOException {
        task = new Task("Test Task", "Test Description",
                Duration.ofMinutes(30), LocalDateTime.now());
        epic = new Epic("Test Epic", "Test Epic Description", new ArrayList<>());
        subtask = new Subtask("Test Subtask", "Test Subtask Description",
                Duration.ofMinutes(15), LocalDateTime.now().plusHours(10), 1);
    }

    @Test
    void shouldAddTask() {
        taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldUpdateTask() {
        taskManager.addTask(task);
        Task updatedTask = new Task(task.getId(), "Updated Task", "Updated Description",
                task.getDuration(), task.getStartTime(), Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        final Task savedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated Task", savedTask.getTitle(), "Название задачи не обновилось.");
        assertEquals("Updated Description", savedTask.getDescription(), "Описание задачи не обновилось.");
        assertEquals(Status.IN_PROGRESS, savedTask.getStatus(), "Статус задачи не обновился.");
    }

    @Test
    void shouldRemoveTask() {
        taskManager.addTask(task);
        taskManager.removeTaskById(task.getId());

        assertNull(taskManager.getTaskById(task.getId()), "Задача не удалена.");
        assertEquals(0, taskManager.getAllTasks().size(), "Список задач не пуст.");
    }

    @Test
    void shouldClearTasks() {
        taskManager.addTask(task);
        taskManager.clearTasks();

        assertEquals(0, taskManager.getAllTasks().size(), "Список задач не очищен.");
    }

    @Test
    void shouldAddEpic() {
        taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void shouldUpdateEpic() {
        taskManager.addEpic(epic);
        Epic updatedEpic = new Epic(epic.getId(), "Updated Epic", "Updated Description", new ArrayList<>());
        taskManager.updateEpic(updatedEpic);

        final Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals("Updated Epic", savedEpic.getTitle(), "Название эпика не обновилось.");
        assertEquals("Updated Description", savedEpic.getDescription(), "Описание эпика не обновилось.");
    }

    @Test
    void shouldRemoveEpic() {
        taskManager.addEpic(epic);
        taskManager.removeEpicById(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()), "Эпик не удален.");
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не пуст.");
    }

    @Test
    void shouldClearEpics() {
        taskManager.addEpic(epic);
        taskManager.clearEpics();

        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не очищен.");
    }

    @Test
    void shouldAddSubtask() {
        taskManager.addEpic(epic);
        subtask.setEpicId(epic.getId());
        taskManager.addSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");

        assertTrue(taskManager.getSubtasksByEpic(epic.getId()).contains(subtask),
                "Подзадача не связана с эпиком.");
    }

    @Test
    void shouldUpdateSubtask() {
        taskManager.addEpic(epic);
        subtask.setEpicId(epic.getId());
        taskManager.addSubtask(subtask);

        Subtask updatedSubtask = new Subtask(subtask.getId(), "Updated Subtask", "Updated Description",
                subtask.getDuration(), subtask.getStartTime(), Status.DONE, epic.getId());
        taskManager.updateSubtask(updatedSubtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertEquals("Updated Subtask", savedSubtask.getTitle(), "Название подзадачи не обновилось.");
        assertEquals("Updated Description", savedSubtask.getDescription(), "Описание подзадачи не обновилось.");
        assertEquals(Status.DONE, savedSubtask.getStatus(), "Статус подзадачи не обновился.");
    }

    @Test
    void shouldRemoveSubtask() {
        taskManager.addEpic(epic);
        subtask.setEpicId(epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.removeSubtaskById(subtask.getId());

        assertNull(taskManager.getSubtaskById(subtask.getId()), "Подзадача не удалена.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не пуст.");
        assertFalse(taskManager.getSubtasksByEpic(epic.getId()).contains(subtask),
                "Подзадача осталась связанной с эпиком.");
    }

    @Test
    void shouldClearSubtasks() {
        taskManager.addEpic(epic);
        subtask.setEpicId(epic.getId());
        taskManager.addSubtask(subtask);
        taskManager.clearSubtasks();

        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не очищен.");
        assertEquals(0, taskManager.getSubtasksByEpic(epic.getId()).size(),
                "Список подзадач эпика не очищен.");
    }

    @Test
    public void searchTaskById() {
        taskManager.addEpic(epic);
        taskManager.addTask(task);
        taskManager.addSubtask(subtask);

        final Epic savedEpic = taskManager.getEpicById(epic.getId());
        final Task savedTask = taskManager.getTaskById(task.getId());
        final Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());

        assertEquals(epic, savedEpic, "Получен не правильный эпик");
        assertEquals(subtask, savedSubtask, "Получена не правильная подзадача");
        assertEquals(task, savedTask, "Получена не правильная задача");
    }

    @Test
    void shouldThrowExceptionWhenTasksOverlap() {
        taskManager.addTask(task);

        Task overlappingTask = new Task("Overlapping Task", "Description",
                Duration.ofMinutes(30), task.getStartTime().plusMinutes(10));

        assertThrows(ManagerTimeOverLapException.class, () -> taskManager.addTask(overlappingTask),
                "Должно быть выброшено исключение при пересечении интервалов задач");
    }
}
