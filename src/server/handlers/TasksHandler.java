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

            System.out.printf("Обрабатывается запрос %s с методом %s", path, method);

            switch (method) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange);
                default -> writeResponse(exchange, "Некорректный запрос", 404);
            }

        } catch (Exception e) {
            e.printStackTrace();
            writeResponse(exchange, "Внутренняя ошибка сервера: " + e.getMessage(), 500);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            String idS = pathParts[2];
            try {
                int id = Integer.parseInt(pathParts[2]);
                String json = gson.toJson(manager.getTaskById(id));
                sendText(exchange, json);
            } catch (NumberFormatException e) {
                writeResponse(exchange,
                        String.format(
                                "Неверный формат id - %s. Попробуйте ещё раз! Нужно ввести целое число!",
                                idS
                        ),
                        400
                );
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
            String idS = pathParts[2];
            try {
                manager.deleteTask(Integer.parseInt(pathParts[2]));
                sendText(exchange, "Задача успешно удалена");
            } catch (NumberFormatException e) {
                writeResponse(exchange,
                        String.format(
                                "Неверный формат id - %s. Попробуйте ещё раз! Нужно ввести целое число!",
                                idS
                        ),
                        400
                );
            }
        } else {
            writeResponse(exchange, "Не указан id задачи", 400);
        }
    }
}
