import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.Status.Status;

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

class EpicsHandlerTest extends BaseHttpHandlerTest {
    private final Epic epic1 = new Epic("Test Epic", "Description", new ArrayList<>());
    private final Epic epic2 = new Epic("New Epic", "Description", new ArrayList<>());

    @Test
    void testGetAllEpicsWhenEmpty() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testGetAllEpicsWithEpics() throws IOException, InterruptedException {
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        URI url = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertEquals(2, epics.length, "Неверное количество эпиков в ответе");

        Map<Integer, Epic> epicMap = Arrays.stream(epics)
                .collect(Collectors.toMap(Epic::getId, Function.identity()));

        assertTrue(epicMap.containsKey(epic1.getId()), "Эпик 1 отсутствует в ответе");
        assertTrue(epicMap.containsKey(epic2.getId()), "Эпик 2 отсутствует в ответе");
    }

    @Test
    void testGetEpicById() throws IOException, InterruptedException {
        manager.addEpic(epic1);
        int epicId = epic1.getId();

        URI url = URI.create("http://localhost:" + PORT + "/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test Epic"));
        assertTrue(response.body().contains(" \"id\": " + epicId));
    }

    @Test
    void testGetEpicSubtasks() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        int epicId = epic2.getId();

        Subtask subtask = new Subtask("Subtask", "Description",
                Duration.ofMinutes(30), LocalDateTime.now(), Status.NEW, epicId);
        manager.addSubtask(subtask);

        URI url = URI.create("http://localhost:" + PORT + "/epics/" + epicId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask"));
    }

    @Test
    void testCreateEpic() throws IOException, InterruptedException {
        String epicJson = gson.toJson(epic2);

        URI url = URI.create("http://localhost:" + PORT + "/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals("New Epic", manager.getAllEpics().getFirst().getTitle());
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        int epicId = epic2.getId();

        URI url = URI.create("http://localhost:" + PORT + "/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllEpics().size());
        assertNull(manager.getEpicById(epicId));
    }
}