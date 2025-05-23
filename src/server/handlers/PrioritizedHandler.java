package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import models.Task;

import java.io.IOException;
import java.util.Collection;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        System.out.printf("Обрабатывается запрос %s с методом %s", path, method);

        Collection<Task> prioritized = manager.getPrioritizedTasks();
        if (prioritized.isEmpty()) {
            writeResponse(exchange, "Лист приоритетов пуст", 404);
            return;
        }
        String json = gson.toJson(prioritized);
        sendText(exchange, json);
    }
}
