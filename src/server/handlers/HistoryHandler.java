package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import models.Task;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        System.out.printf("Обрабатывается запрос %s с методом %s", path, method);

        Collection<Task> history = manager.getHistory();
        if (history.isEmpty()) {
            writeResponse(exchange, "История пустая", 404);
            return;
        }

        List<Map<String, Object>> historyDetails = history
                .stream()
                .map(task -> {
                    Map<String, Object> taskInfo = new HashMap<>();
                    taskInfo.put("id", task.getId());
                    taskInfo.put("name", task.getTitle());
                    return taskInfo;
                })
                .collect(Collectors.toList());
        String json = gson.toJson(historyDetails);
        sendText(exchange, json);
    }
}
