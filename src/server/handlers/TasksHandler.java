package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeConfirmException;
import manager.TaskManager;
import models.Task;

import java.io.IOException;
import java.io.InputStream;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private String[] pathParts;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            pathParts = path.split("/");

            System.out.println("Обрабатывается запрос " + path + " с методом " + method);

            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    writeResponse(exchange, "Некорректный запрос", 404);
            }

        } catch (Exception e) {
            e.printStackTrace();
            writeResponse(exchange, "Внутренняя ошибка сервера: " + e.getMessage(), 500);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                String json = gson.toJson(manager.getTaskById(id));
                sendText(exchange, json);
            } catch (NumberFormatException e) {
                writeResponse(exchange, "Неверный формат id. Попробуйте ещё раз!", 400);
            } catch (NotFoundException exception) {
                sendNotFound(exchange, exception.getMessage());
            }
        } else {
            String json = gson.toJson(manager.getAllTask());
            sendText(exchange, json);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);

            if (pathParts.length == 3) {
                manager.updateTask(task);
                writeResponse(exchange, "Задача успешно обновлена", 201);
            } else {
                manager.createTask(task);
                writeResponse(exchange, "Задача успешно создана", 201);
            }

        } catch (TimeConfirmException e) {
            e.printStackTrace();
            sendHasInteractions(exchange, "Пересечение по времени задачи с другой задачей");
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Неправильный запрос", 400);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            try {
                manager.deleteTask(Integer.parseInt(pathParts[2]));
                sendText(exchange, "Задача успешно удалена");
            } catch (NumberFormatException e) {
                writeResponse(exchange, "Неверный формат id. Попробуйте ещё раз!", 400);
            }
        } else {
            writeResponse(exchange, "Не указан id задачи", 400);
        }
    }
}
