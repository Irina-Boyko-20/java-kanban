package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import manager.TaskManager;
import models.Epic;

import java.io.IOException;
import java.io.InputStream;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private String[] pathParts;

    public EpicsHandler(TaskManager manager) {
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
        String idS = "";
        try {
            if (pathParts.length == 2) {
                String json = gson.toJson(manager.getAllEpics());
                sendText(exchange, json);
            } else if (pathParts.length == 3) {
                idS = pathParts[2];
                int id = Integer.parseInt(pathParts[2]);
                String json = gson.toJson(manager.getEpicById(id));
                sendText(exchange, json);
            } else if (pathParts.length == 4) {
                idS = pathParts[2];
                int id = Integer.parseInt(pathParts[2]);

                if (manager.getSubtasksByEpicId(id) != null) {
                    String json = gson.toJson(manager.getSubtasksByEpicId(id));
                    sendText(exchange, json);
                } else {
                    sendNotFound(exchange, String.format("Epic с идентификатором %d отсутствует. Попробуйте снова!", id));
                }
            }
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
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(body, Epic.class);
            manager.createEpic(epic);
            writeResponse(exchange, "Epic успешно создан", 201);
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Неправильный запрос", 400);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        if (pathParts.length == 3) {
            String idS = pathParts[2];
            try {
                manager.deleteEpic(Integer.parseInt(pathParts[2]));
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
            writeResponse(exchange, "Не указан id epic", 400);
        }
    }
}
