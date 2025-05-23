package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.Epic;
import models.Subtask;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.Gson;

import manager.InMemoryTaskManager;
import models.Task;

public class HttpTaskServerTest {
    private static final String BASE_URL = "http://localhost:8080";
    private InMemoryTaskManager manager;
    private HttpTaskServer server;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        manager.deleteAllTasks();

        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    public void shutDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Description", "15:10 15.10.2024", "30");
        String json = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(BASE_URL + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ответ сервера должен быть 201");

        var tasks = manager.getAllTask();
        assertEquals(1, tasks.size(), "Должна быть одна задача");
        assertEquals("Test task", tasks.getFirst().getTitle(), "Имя задачи должно совпадать");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Sample", "Desc", "16:50 15.05.2025", "15");
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(BASE_URL + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String body = response.body();
        Task[] tasksFromResponse = gson.fromJson(body, Task[].class);

        assertNotNull(tasksFromResponse);
        assertEquals("Sample", tasksFromResponse[0].getTitle());
    }

    @Test
    void testGetNonExistentTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Description", "15:10 15.10.2024", "30");
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test task", "Description", "15:10 15.10.2024", "30");
        manager.createTask(task);
        int taskId = task.getId();

        assertEquals(1, manager.getAllTask().size());

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(BASE_URL + "/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ответ сервера должен быть 200");

        assertTrue(manager.getAllTask().isEmpty(), "Задача должна быть удалена");
    }

    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask", "Description", 3, "16:00 01.01.2025", "60");
        String json = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ответ сервера должен быть 201");
    }

    @Test
    void testGetSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask", "Description", 3, "16:00 01.01.2025", "60");
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void testSubtaskWithInvalidEpicId() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask", "Description", 999, "16:00 01.01.2023", "60");
        subtask.setId(999);
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/subtasks/24"))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask", "Description", 999, "16:00 01.01.2023", "60");
        manager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        assertEquals(1, manager.getAllSubtasks().size());

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(BASE_URL + "/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ответ сервера должен быть 200");

        assertTrue(manager.getAllSubtasks().isEmpty(), "Задача должна быть удалена");
    }

    @Test
    void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);
        int epicId = epic.getId();
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        // Получаем подзадачи эпика
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/epics/" + epicId + "/subtasks"))
                .GET()
                .header("Content-Type", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        assertTrue(subtasks.isEmpty());
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        // Создаем и получаем задачу для истории
        Task task = new Task("Task", "Description", "17:00 01.01.2023", "30");
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/" + task.getId()))
                .GET()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем историю
        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(1, history.size());
    }

    @Test
    void testTaskTimeOverlap() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Description", "18:00 01.01.2023", "60");
        String taskJson = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task("Task2", "Description", "18:30 01.01.2023", "30");
        taskJson = gson.toJson(task2);

        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }
}
