import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.Status.Status;
import tracker.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest extends BaseHttpHandlerTest {

    private final Epic epic = new Epic("title", "description", new ArrayList<>());

    @Test
    void testGetAllSubtasksWhenEmpty() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testGetAllSubtasksWithSubtasks() throws IOException, InterruptedException {
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Test Subtask", "Description",
                Duration.ofMinutes(30), LocalDateTime.now(), Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Test Subtask2", "Description2",
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1), Status.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        URI url = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(2, subtasks.length, "Неверное количество задач в ответе");

        Map<Integer, Task> taskMap = Arrays.stream(subtasks)
                .collect(Collectors.toMap(Subtask::getId, Function.identity()));

        assertTrue(taskMap.containsKey(subtask1.getId()), "Подзадача 1 отсутствует в ответе");
        assertTrue(taskMap.containsKey(subtask2.getId()), "Подзадача 2 отсутствует в ответе");
    }

    @Test
    void testCreateSubtask() throws IOException, InterruptedException {
        manager.addEpic(epic);

        Subtask subtask = new Subtask("New Subtask", "Description",
                Duration.ofMinutes(30), LocalDateTime.now(), Status.NEW, epic.getId());
        String subtaskJson = gson.toJson(subtask);

        URI url = URI.create("http://localhost:" + PORT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubtasks().size());
        assertEquals("New Subtask", manager.getAllSubtasks().getFirst().getTitle());
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        manager.addEpic(epic);
        Subtask subtask = new Subtask("To Delete", "Description",
                Duration.ofMinutes(30), LocalDateTime.now(), Status.NEW, epic.getId());
        manager.addSubtask(subtask);
        int subtaskId = subtask.getId();

        URI url = URI.create("http://localhost:" + PORT + "/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllSubtasks().size());
        assertNull(manager.getSubtaskById(subtaskId));
    }
}