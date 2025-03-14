package test;

import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    public Task createTask() {
        return new Task("Тест задачи", "Описание задачи.");
    }

    public Epic createEpic() {
        return new Epic("Тест epic", "Описание epic.");
    }

    public Subtask createSubtask(Epic epic) {
        return new Subtask("Тест подзадачи", "Описание подзадачи.", epic.getId());
    }

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }


    @Test
    public void shouldCreateTask() {
        Task task = createTask();
        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertNotNull(retrievedTask);
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(task, retrievedTask);
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Epic retrieveEpics = taskManager.getEpicById(epic.getId());

        assertNotNull(retrieveEpics);
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(epic, retrieveEpics);
    }

    @Test
    public void shouldCreateSubtask() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        Subtask retrieveSubtasks = taskManager.getSubtaskByID(subtask.getId());

        assertNotNull(retrieveSubtasks);
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(subtask, retrieveSubtasks);
    }

    @Test
    public void shouldUpdateTaskStatusToInProgress() {
        Task task = createTask();
        taskManager.createTask(task);

        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);

        epic.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInProgress() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(subtask);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTaskById(subtask.getId()).getStatus());
    }

    @Test
    public void shouldDeleteTask() {
        Task task = createTask();
        taskManager.createTask(task);
        taskManager.deleteTask(task.getId());

        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    public void testCreateAndDeleteEpicWithSubtasks() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        taskManager.deleteEpic(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()));
        assertNull(taskManager.getSubtaskByID(subtask.getId()));
    }

    @Test
    public void shouldChangeTheStatusEpic() {
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        Subtask subtask1 = new Subtask("Тест подзадачи 2", "Описание подзадачи 2.", epic.getId());
        taskManager.createSubtask(subtask);

        subtask.setStatus(TaskStatus.DONE);
        subtask1.setStatus(TaskStatus.DONE);

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask1);

        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask1);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void shouldGetHistory() {
        Task task = createTask();
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);
        taskManager.getSubtaskByID((subtask.getId()));

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
    }

    @Test
    public void shouldTasksEqualityById() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 1", "Описание задачи 1");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи не совпадают.");
    }

    @Test
    public void shouldSubtasksEqualityById() {
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 1);
        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи 1", 1);

        subtask1.setId(1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Подзадачи не совпадают.");
    }

    @Test
    public void shouldManagersReturnInitializedInstances() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @Test
    public void shouldInMemoryTaskManagerAddsDifferentTypesOfTasks() {
        Task task = createTask();
        taskManager.createTask(task);
        Epic epic = createEpic();
        taskManager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        taskManager.createSubtask(subtask);

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        assertNotNull(taskManager.getTaskById(task.getId()));
        assertNotNull(taskManager.getEpicById(epic.getId()));
        assertNotNull(taskManager.getSubtaskByID(subtask.getId()));
    }

    @Test
    public void shouldTasksWithSameIdDoNotConflict() {
        Task task1 = new Task("Тест задачи 2", "Описание задачи 2.");
        Task task = createTask();
        taskManager.createTask(task);
        taskManager.createTask(task1);

        task.setId(1);
        task1.setId(2);

        assertNotEquals(taskManager.getTaskById(1), taskManager.getTaskById(2));
    }

    @Test
    public void shouldTaskImmutabilityWhenAdding() {
        Task task = createTask();
        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertEquals(task.getTitle(), retrievedTask.getTitle());
        assertEquals(task.getDescription(), retrievedTask.getDescription());
        assertEquals(task.getId(), retrievedTask.getId());
    }

}