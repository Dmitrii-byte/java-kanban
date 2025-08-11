import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.Adapters.DurationAdapter;
import tracker.Adapters.LocalDateTimeAdapter;
import tracker.Server.HttpTaskServer;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Task;
import tracker.Status.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TasksHandlerTest {
    private static final int PORT = 8080;
    private HttpTaskServer taskServer;
    private TaskManager manager;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = Managers.getDefault();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
    }

    @Test
    void testGetAllTasksWhenEmpty() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testGetAllTasksWithTasks() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task("Test1", "Description 1", Duration.ofMinutes(30), now, Status.NEW);
        Task task2 = new Task("Test2", "Description 2", Duration.ofMinutes(45), now.plusHours(2), Status.IN_PROGRESS);
        Task task3 = new Task("Test3", "Description 3", Duration.ofMinutes(60), now.plusHours(4), Status.DONE);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        URI url = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);

        assertEquals(3, tasks.length, "Неверное количество задач в ответе");

        Map<Integer, Task> taskMap = Arrays.stream(tasks)
                .collect(Collectors.toMap(Task::getId, Function.identity()));

        assertTrue(taskMap.containsKey(task1.getId()), "Задача 1 отсутствует в ответе");
        assertTrue(taskMap.containsKey(task2.getId()), "Задача 2 отсутствует в ответе");
        assertTrue(taskMap.containsKey(task3.getId()), "Задача 3 отсутствует в ответе");
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), Status.NEW);
        manager.addTask(task);
        int taskId = task.getId();

        URI url = URI.create("http://localhost:" + PORT + "/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test"));
        assertTrue(response.body().contains(" \"id\": " + taskId));
    }

    @Test
    void testGetTaskByIdNotFound() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + PORT + "/tasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testCreateTask() throws IOException, InterruptedException {
        Task task = new Task("New Task", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), Status.NEW);
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());
        assertEquals("New Task", manager.getAllTasks().getFirst().getTitle());
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Original", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), Status.NEW);
        manager.addTask(task);
        int taskId = task.getId();

        Task updatedTask = new Task(taskId, "Updated", "New Description",
                Duration.ofMinutes(45), LocalDateTime.now().plusHours(1), Status.IN_PROGRESS);
        String taskJson = gson.toJson(updatedTask);

        URI url = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());
        assertEquals("Updated", manager.getTaskById(taskId).getTitle());
        assertEquals(Status.IN_PROGRESS, manager.getTaskById(taskId).getStatus());
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("To Delete", "Description", Duration.ofMinutes(30),
                LocalDateTime.now(), Status.NEW);
        manager.addTask(task);
        int taskId = task.getId();

        URI url = URI.create("http://localhost:" + PORT + "/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllTasks().size());
        assertNull(manager.getTaskById(taskId));
    }

    @Test
    void testDeleteTaskNotFound() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + PORT + "/tasks/999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void testCreateTaskWithTimeOverlap() throws IOException, InterruptedException {
        LocalDateTime time = LocalDateTime.now();
        Task task1 = new Task("First", "Description", Duration.ofMinutes(30),
                time, Status.NEW);
        manager.addTask(task1);

        Task task2 = new Task("Overlap", "Description", Duration.ofMinutes(30),
                time.plusMinutes(15), Status.NEW);
        String taskJson = gson.toJson(task2);

        URI url = URI.create("http://localhost:" + PORT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
        assertEquals(1, manager.getAllTasks().size());
    }
}