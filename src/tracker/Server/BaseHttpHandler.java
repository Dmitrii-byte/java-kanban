package tracker.Server;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import tracker.Adapters.DurationAdapter;
import tracker.Adapters.LocalDateTimeAdapter;
import tracker.controllers.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    protected final TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static Gson getGson() {
        return gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            handleRequest(exchange, method, path);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    protected abstract void handleRequest(HttpExchange exchange, String method, String path) throws IOException;

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendSuccess(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 200);
    }

    protected void sendCreated(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 201);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Запрашиваемый ресурс не найден";
        sendText(exchange, response, 404);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        String response = "Метод не поддерживается";
        sendText(exchange, response, 405);
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        String response = "Некорректный запрос";
        sendText(exchange, response, 400);
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        String response = "Задача пересекается по времени с существующими";
        sendText(exchange, response, 406);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        String response = "Внутренняя ошибка сервера";
        sendText(exchange, response, 500);
    }

    protected <T> T readRequest(HttpExchange exchange, Class<T> clas) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return gson.fromJson(body, clas);
        }
    }
}