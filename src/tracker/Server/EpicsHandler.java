package tracker.Server;

import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.TaskManager;
import tracker.model.Epic;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void handleRequest(HttpExchange exchange, String method, String path) throws IOException {
        switch (method) {
            case "GET":
                if (path.equals("/epics")) {
                    handleGetAllEpics(exchange);
                } else if (path.matches("/epics/\\d+")) {
                    handleGetEpicById(exchange);
                } else if (path.matches("/epics/\\d+/subtasks")) {
                    handleGetEpicSubtasks(exchange);
                } else {
                    sendNotFound(exchange);
                }
                break;
            case "POST":
                handleCreateEpic(exchange);
                break;
            case "DELETE":
                if (path.matches("/epics/\\d+")) {
                    handleDeleteEpic(exchange);
                } else {
                    sendNotFound(exchange);
                }
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllEpics());
        sendSuccess(exchange, response);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(pathParts[2]);
        Epic epic = taskManager.getEpicById(id);

        if (epic != null) {
            sendSuccess(exchange, gson.toJson(epic));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(pathParts[2]);
        Epic epic = taskManager.getEpicById(id);

        if (epic != null) {
            String response = gson.toJson(taskManager.getSubtasksByEpic(id));
            sendSuccess(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        Epic epic = readRequest(exchange, Epic.class);
        if (epic == null) {
            sendNotFound(exchange);
            return;
        }

        taskManager.addEpic(epic);
        sendCreated(exchange, "Эпик с id " + epic.getId() + " добавлен");
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(pathParts[2]);
        taskManager.removeEpicById(id);
        sendSuccess(exchange, "Эпик удален");
    }
}