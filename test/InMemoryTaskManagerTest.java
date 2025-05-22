import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.*;
import tracker.controllers.*;
import tracker.Status.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    public static TaskManager taskManager;

    @BeforeAll
    public static void CheckingTheFunctionalityOfTheUtilityClass() {
        TaskManager taskManager1 = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(taskManager1);
        assertNotNull(historyManager);
        assertTrue(taskManager1.getAllTasks().isEmpty());
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void addNewTask() {
        Task task1 = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.addTask(task1);

        final Task savedTask = taskManager.getTaskById(task1.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        Task task2 = new Task(1, "новый", "год", Status.IN_PROGRESS);
        assertEquals(task1, task2, "Задачи с одинаковым id не совпадают");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    public void addNewEpic() {
        Epic epic1 = new Epic("Epic1", "EpicDescription", new ArrayList<>());
        taskManager.addEpic(epic1);

        final Epic savedEpic = taskManager.getEpicById(epic1.getId());

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic1, savedEpic, "Эпики не равны");

        Epic epic2 = new Epic(1, "Новый Эпик", "ЭПИК", new ArrayList<>());
        assertEquals(epic1, epic2, "Эпики с одним id не совпадают");

        final ArrayList<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    public void addNewSubtask() {
        Subtask subtask1 = new Subtask("подзадача", "ее описание", 1);
        taskManager.addSubtask(subtask1);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtask1.getId());

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask1, savedSubtask, "Подзадачи не совпадают");

        Subtask subtask2 = new Subtask(1, "ЗАдача", "описание", 2);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id не совпадают");

        final ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    public void shouldBeEqualInId() {
        Epic epic = new Epic(1, "title", "description", new ArrayList<>());
        Task task = new Task(1, "Задача", "описание");
        Subtask subtask = new Subtask(1, "knock", "knock", 1);

        assertEquals(epic, task);
        assertEquals(epic, subtask);
        assertEquals(subtask, task);
    }

    @Test
    public void updateStatusEpic() {
        Epic epic = new Epic(1, "Эпик", "новый", new ArrayList<>());
        Subtask subtask1 = new Subtask("новая", "убраться", 1);
        Subtask subtask2 = new Subtask("Еще одна", "сходить в магазин", 1);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.NEW, taskManager.getEpicById(1).getStatus(), "Неверный статус у эпика");

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus(), "Неверный статус у эпика");

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        assertEquals(Status.DONE, taskManager.getEpicById(1).getStatus(), "Неверный статус у эпика");

    }

    @Test
    public void searchTaskById() {
        Epic epic = new Epic("Дз", "Решение дз", new ArrayList<>());
        taskManager.addEpic(epic);
        Task task = new Task("Задача", "Решить задачу", Status.DONE);
        Subtask subtask = new Subtask("дз1", "решить дз1", Status.DONE, epic.getId());
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
    public void tryAddNullToHistory() {
        assertNull(taskManager.getEpicById(1));
        assertNull(taskManager.getTaskById(2));
        assertNull(taskManager.getSubtaskById(3));
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    public void historyShouldKeepTheFirstTask() {
        Task task1 = new Task("Title", "Description", Status.NEW);
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        final Task savedTask = new Task(task1.getId(), task1.getTitle(), task1.getDescription(), task1.getStatus());

        task1.setTitle("newTitle");
        task1.setDescription("newDescription");
        taskManager.updateTask(task1);

        Task task2 = taskManager.getHistory().getFirst();
        assertEquals(savedTask, task2);

        taskManager.clearHistory();
        assertEquals(0, taskManager.getHistory().size());

        // то же самое для эпика
        Epic epic = new Epic("Title", "Description", new ArrayList<>());
        taskManager.addEpic(epic);
        taskManager.getEpicById(epic.getId());

        final Epic savedEpic = new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getSubtasksId());

        epic.setTitle("newTitle");
        epic.setDescription("newDescription");
        taskManager.updateEpic(epic);

        Epic epic2 = (Epic) taskManager.getHistory().getFirst();
        assertEquals(savedEpic, epic2);

        taskManager.clearHistory();
        assertEquals(0, taskManager.getHistory().size());

        // то же самое для подзадач
        Subtask subtask = new Subtask("Title", "Description", 1);
        taskManager.addSubtask(subtask);
        taskManager.getSubtaskById(subtask.getId());

        final Subtask savedSubtask = new Subtask(subtask.getId(), subtask.getTitle(), subtask.getDescription(), subtask.getEpicId());

        subtask.setTitle("newTitle");
        subtask.setDescription("newDescription");
        taskManager.updateEpic(epic);

        Subtask subtask2 = (Subtask) taskManager.getHistory().getFirst();
        assertEquals(savedSubtask, subtask2);

        taskManager.clearHistory();
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    public void shouldStoreWithoutRepetitions() {
        Task task1 = new Task("task1", "DisTask1", Status.IN_PROGRESS);
        Task task2 = new Task("task2", "DisTask2", Status.NEW);
        Task task3 = new Task("task3", "DisTask3", Status.DONE);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2, task3));
        for (Task task : tasks)
            taskManager.addTask(task);

        Epic epic1 = new Epic("Epic1", "DisEpic1", new ArrayList<>());
        Epic epic2 = new Epic("Epic2", "DisEpic2", new ArrayList<>());
        List<Epic> epics = new ArrayList<>(List.of(epic1, epic2));
        for (Epic epic : epics)
            taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("sub1", "DisSub1", epic2.getId());
        Subtask sub2 = new Subtask("sub2", "DisSub2", epic2.getId());
        List<Subtask> subs = new ArrayList<>(List.of(sub1, sub2));
        for (Subtask subtask : subs)
            taskManager.addSubtask(subtask);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(sub1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(sub1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getEpicById(epic1.getId());
        assertEquals(4, taskManager.getHistory().size());
    }

    @Test
    public void shouldRemoveSubtasksWhenEpicWasRemoved() {
        Epic epic1 = new Epic("Epic1", "DisEpic1", new ArrayList<>());
        Epic epic2 = new Epic("Epic2", "DisEpic2", new ArrayList<>());
        List<Epic> epics = new ArrayList<>(List.of(epic1, epic2));
        for (Epic epic : epics)
            taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("sub1", "DisSub1", epic2.getId());
        Subtask sub2 = new Subtask("sub2", "DisSub2", epic2.getId());
        List<Subtask> subs = new ArrayList<>(List.of(sub1, sub2));
        for (Subtask subtask : subs)
            taskManager.addSubtask(subtask);

        taskManager.getSubtaskById(sub2.getId());
        taskManager.getSubtaskById(sub1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        assertEquals(4, taskManager.getHistory().size());

        taskManager.removeEpicById(epic2.getId());

        assertEquals(1, taskManager.getHistory().size(), "Должен удалиться эпик с подзадачами");
    }
}