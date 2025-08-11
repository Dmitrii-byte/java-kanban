import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tracker.Server.BaseHttpHandler;
import tracker.Server.HttpTaskServer;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;

import java.io.IOException;
import java.net.http.HttpClient;

public abstract class BaseHttpHandlerTest {
    protected static final int PORT = 8080;
    protected TaskManager manager = Managers.getDefault();
    protected HttpTaskServer taskServer;
    protected Gson gson;
    protected HttpClient client;

    public BaseHttpHandlerTest() {
        try {
            this.taskServer = new HttpTaskServer(manager);
            this.gson = BaseHttpHandler.getGson();
            this.client = HttpClient.newHttpClient();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize test", e);
        }
    }

    @BeforeEach
    public void setUp() {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }
}