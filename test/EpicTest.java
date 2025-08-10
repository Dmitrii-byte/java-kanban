import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryTaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.Status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void testEpicStatusWithNoSubtasks() {
        Epic epic = new Epic("Epic with no subtasks", "Description", new ArrayList<>());
        taskManager.addEpic(epic);

        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика без подзадач должен быть NEW");
    }

    @Test
    public void testEpicStatusWithNewSubtasks() {
        Epic epic = new Epic("Epic with NEW subtasks", "Description", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofMinutes(30),
                LocalDateTime.now().plusHours(1), Status.NEW, epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика со всеми NEW подзадачами должен быть NEW");
    }

    @Test
    public void testEpicStatusWithDoneSubtasks() {
        Epic epic = new Epic("Epic with DONE subtasks", "Description", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofMinutes(30),
                LocalDateTime.now().plusHours(1), Status.DONE, epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика со всеми DONE подзадачами должен быть DONE");
    }

    @Test
    public void testEpicStatusWithMixedSubtasks() {
        Epic epic = new Epic("Epic with mixed subtasks", "Description", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofMinutes(30),
                LocalDateTime.now().plusHours(1), Status.DONE, epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика с подзадачами NEW и DONE должен быть IN_PROGRESS");
    }

    @Test
    public void testEpicStatusWithInProgressSubtasks() {
        Epic epic = new Epic("Epic with IN_PROGRESS subtasks", "Description", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), Status.IN_PROGRESS, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofMinutes(30),
                LocalDateTime.now().plusHours(1), Status.IN_PROGRESS, epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика со всеми IN_PROGRESS подзадачами должен быть IN_PROGRESS");
    }

    @Test
    public void testEpicTimeCalculationWithSubtasks() {
        Epic epic = new Epic("Epic with no subtasks", "Description", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", Duration.ofMinutes(30),
                LocalDateTime.of(2022, 12, 10, 10, 10), Status.IN_PROGRESS, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", Duration.ofMinutes(30),
                LocalDateTime.of(2022, 12, 10, 10, 50), Status.IN_PROGRESS, epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(taskManager.getSubtaskById(subtask1.getId()).getStartTime(), taskManager.getEpicById(epic.getId()).getStartTime(), "Время начала эпика должно совпасть с его началом его минимального сабтаска");
        assertEquals(taskManager.getSubtaskById(subtask2.getId()).getEndTime(), taskManager.getEpicById(epic.getId()).getEndTime(), "Время окончания эпика должно совпасть с окончанием его максимального сабтаска");
        assertEquals(taskManager.getSubtaskById(subtask1.getId()).getDuration().plus(taskManager.getSubtaskById(subtask2.getId()).getDuration()), taskManager.getEpicById(epic.getId()).getDuration(), "Продолжительность эпика должна равняться сумме продолжительности его сабтасков");
    }
}