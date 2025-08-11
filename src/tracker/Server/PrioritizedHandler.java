package tracker.Server;

import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void handleRequest(HttpExchange exchange, String method, String path) throws IOException {
        if ("GET".equals(method)) {
            String response = gson.toJson(taskManager.getPrioritizedTasks());
            sendSuccess(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }
}