package tracker.Server;

import com.sun.net.httpserver.HttpExchange;
import tracker.Exception.ManagerTimeOverLapException;
import tracker.controllers.TaskManager;
import tracker.model.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void handleRequest(HttpExchange exchange, String method, String path) throws IOException {
        switch (method) {
            case "GET":
                if (path.equals("/subtasks")) {
                    handleGetAllSubtasks(exchange);
                } else if (path.matches("/subtasks/\\d+")) {
                    handleGetSubtaskById(exchange);
                } else {
                    sendNotFound(exchange);
                }
                break;
            case "POST":
                handleCreateOrUpdateSubtask(exchange);
                break;
            case "DELETE":
                if (path.matches("/subtasks/\\d+")) {
                    handleDeleteSubtask(exchange);
                } else {
                    sendNotFound(exchange);
                }
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllSubtasks());
        sendSuccess(exchange, response);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(pathParts[2]);
        Subtask subtask = taskManager.getSubtaskById(id);

        if (subtask != null) {
            sendSuccess(exchange, gson.toJson(subtask));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        Subtask subtask = readRequest(exchange, Subtask.class);
        if (subtask == null) {
            sendNotFound(exchange);
            return;
        }

        try {
            if (subtask.getId() == 0) {
                taskManager.addSubtask(subtask);
                sendCreated(exchange, "Подзадача с id " + subtask.getId() + " добавлена");
            } else {
                taskManager.updateSubtask(subtask);
                sendSuccess(exchange, "Подзадача с id " + subtask.getId() + " обновлена");
            }
        } catch (ManagerTimeOverLapException e) {
            sendHasOverlaps(exchange);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(pathParts[2]);
        taskManager.removeSubtaskById(id);
        sendSuccess(exchange, "Подзадача удалена");
    }
}