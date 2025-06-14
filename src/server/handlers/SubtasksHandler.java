package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import exception.TimeConfirmException;
import manager.TaskManager;
import models.Subtask;

import java.io.IOException;
import java.io.InputStream;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private String[] pathParts;

    public SubtasksHandler(TaskManager manager) {
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
                String json = gson.toJson(manager.getSubtaskByID(id));
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
            String json = gson.toJson(manager.getAllSubtasks());
            sendText(exchange, json);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (pathParts.length == 3) {
                manager.updateSubtask(subtask);
                writeResponse(exchange, "Подзадача успешно обновлена", 201);
            } else {
                manager.createSubtask(subtask);
                writeResponse(exchange, "Подзадача успешно создана", 201);
            }

        } catch (TimeConfirmException e) {
            e.printStackTrace();
            sendHasInteractions(exchange, "Пересечение по времени подзадачи с другой задачей");
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Неправильный запрос", 400);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            String idS = pathParts[2];
            try {
                manager.deleteSubtask(Integer.parseInt(pathParts[2]));
                sendText(exchange, "Подзадача успешно удалена");
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
