import org.junit.jupiter.api.Test;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.Status.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest extends BaseHttpHandlerTest {

    @Test
    void testGetHistoryWhenEmpty() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testGetHistoryWithTasks() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("Test Task1", "Description1",
                Duration.ofMinutes(30), now, Status.NEW);
        Task task2 = new Task("Test Task2", "Description2",
                Duration.ofMinutes(30), now.plusHours(3), Status.NEW);
        Subtask subtask1 = new Subtask("SubTitle1", "SubDescription1", 10);
        Subtask subtask2 = new Subtask("SubTitle2", "SubDescription2", 10);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());

        URI url = URI.create("http://localhost:" + PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(4, manager.getHistory().size(), "Неверное количество задач в истории");

        Map<Integer, Task> taskMap = Arrays.stream(tasks)
                .collect(Collectors.toMap(Task::getId, Function.identity()));

        assertTrue(taskMap.containsKey(task1.getId()), "Задача 1 отсутствует в ответе");
        assertTrue(taskMap.containsKey(task2.getId()), "Задача 2 отсутствует в ответе");
        assertTrue(taskMap.containsKey(subtask1.getId()), "Подзадача 1 отсутствует в ответе");
        assertTrue(taskMap.containsKey(subtask2.getId()), "Подзадача 2 отсутствует в ответе");
    }

    @Test
    void testHistoryWithWrongMethod() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + PORT + "/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
    }
}