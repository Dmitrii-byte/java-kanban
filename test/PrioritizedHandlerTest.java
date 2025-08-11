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

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest extends BaseHttpHandlerTest {

    @Test
    void testGetPrioritizedWhenEmpty() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + PORT + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testGetPrioritizedWithTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test Task", "Description",
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1), Status.NEW);
        Task task2 = new Task("Test Task", "Description",
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(2), Status.NEW);
        Task task3 = new Task("Test Task", "Description",
                Duration.ofMinutes(30), LocalDateTime.now().minusDays(10), Status.NEW);
        Subtask subtask = new Subtask("subtask", "descrSub", Duration.ofMinutes(100), LocalDateTime.now().plusHours(10), 5);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addSubtask(subtask);

        URI url = URI.create("http://localhost:" + PORT + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);

        assertEquals(task3.getId(), tasks[0].getId(), "Первой должна быть самая ранняя задача");
        assertEquals(task1.getId(), tasks[1].getId(), "Второй должна быть задача task1");
        assertEquals(task2.getId(), tasks[2].getId(), "Третьей должна быть задача task2");
        assertEquals(subtask.getId(), tasks[3].getId(), "Четвертой должна быть подзадача subtask");
    }

    @Test
    void testPrioritizedWithWrongMethod() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + PORT + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}