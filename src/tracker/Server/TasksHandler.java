package tracker.Server;

import com.sun.net.httpserver.HttpExchange;
import tracker.Exception.ManagerTimeOverLapException;
import tracker.controllers.TaskManager;
import tracker.model.Task;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleRequest(HttpExchange exchange, String method, String path) throws IOException {
        switch (method) {
            case "GET":
                if (path.equals("/tasks")) {
                    handleGetAllTasks(exchange);
                } else if (path.matches("/tasks/\\d+")) {
                    handleGetTaskById(exchange);
                } else {
                    sendBadRequest(exchange);
                }
                break;
            case "POST":
                handleCreateOrUpdateTask(exchange);
                break;
            case "DELETE":
                if (path.matches("/tasks/\\d+")) {
                    handleDeleteTask(exchange);
                } else {
                    sendBadRequest(exchange);
                }
                break;
            default:
                sendMethodNotAllowed(exchange);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllTasks());
        sendSuccess(exchange, response);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(pathParts[2]);
        Task task = taskManager.getTaskById(id);

        if (task != null) {
            sendSuccess(exchange, gson.toJson(task));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        Task task = readRequest(exchange, Task.class);
        if (task == null) {
            sendBadRequest(exchange);
            return;
        }

        try {
            if (task.getId() == 0) {
                taskManager.addTask(task);
                sendCreated(exchange, "Задача с id " + task.getId() + " добавлена");
            } else {
                taskManager.updateTask(task);
                sendSuccess(exchange, "Задача с id " + task.getId() + " обновлена");
            }
        } catch (ManagerTimeOverLapException e) {
            sendHasOverlaps(exchange);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        int id = Integer.parseInt(pathParts[2]);
        Task task = taskManager.getTaskById(id);
        if (task != null) {
            taskManager.removeTaskById(id);
            sendSuccess(exchange, "Задача удалена");
        } else {
            sendNotFound(exchange);
        }
    }
}