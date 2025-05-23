package server;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import server.handlers.*;

import java.io.*;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        TasksHandler taskHandler = new TasksHandler(manager);
        SubtasksHandler subtaskHandler = new SubtasksHandler(manager);
        EpicsHandler epicHandler = new EpicsHandler(manager);
        HistoryHandler historyHandler = new HistoryHandler(manager);
        PrioritizedHandler prioritizedHandler = new PrioritizedHandler(manager);
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", taskHandler);
        httpServer.createContext("/subtasks", subtaskHandler);
        httpServer.createContext("/epics", epicHandler);
        httpServer.createContext("/history", historyHandler);
        httpServer.createContext("/prioritized", prioritizedHandler);

    }

    public void start() {
        System.out.println("Запускаем сервер на " + PORT + " порту");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        TaskManager taskManagers = Managers.getDefault();
        Epic epic1 = new Epic("Epic 1", "Shopping");
        taskManagers.createEpic(epic1);
        taskManagers.createSubtask(new Subtask("Subtask 1", "Buy milk", 1, "18:30 17.03.2025", "50"));
        taskManagers.createSubtask(new Subtask("Subtask 2", "Buy cow", 1, "18:30 18.03.2025", "50"));
        taskManagers.createSubtask(new Subtask("Subtask 3", "Buy chicken", 1, "16:10 12.12.2024", "50"));
        taskManagers.createTask(new Task("Task 1", "Buy socks", "14:30 15.01.2025", "30"));
        taskManagers.createTask(new Task("Task 2", "Buy socks", "14:30 16.01.2025", "30"));

        Epic epic2 = new Epic("Epic 2", "Household chores");
        taskManagers.createEpic(epic2);

        HttpTaskServer server = new HttpTaskServer(taskManagers);
        server.start();

        new Thread(() -> {
            try {
                Thread.sleep(600000);
                server.stop();
                System.out.println("Сервер был автоматически остановлен через 10 минут");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}