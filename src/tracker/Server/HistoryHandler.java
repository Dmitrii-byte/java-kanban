package tracker.Server;

import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleRequest(HttpExchange exchange, String method, String path) throws IOException {
        if ("GET".equals(method)) {
            String response = gson.toJson(taskManager.getHistory());
            sendSuccess(exchange, response);
        } else {
            sendMethodNotAllowed(exchange);
        }
    }
}