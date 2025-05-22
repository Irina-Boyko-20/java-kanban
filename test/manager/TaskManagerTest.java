package manager;

import exception.NotFoundException;
import exception.TimeConfirmException;
import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    protected Task createTask() {
        return new Task("Задача №1", "Описание задачи №1", "12:00 10.10.2024", "60");
    }

    protected Epic createEpic() {
        return new Epic("Epic №1", "Описание Epic №1");
    }

    protected Subtask createSubtask(Epic epic) {
        return new Subtask("Подзадача №1",
                "Описание подзадачи №1",
                epic.getId(),
                "15:16 22.03.2025",
                "30"
        );
    }

    @Test
    public void shouldCreateTask() {
        Task task = createTask();
        manager.createTask(task);
        Task retrievedTask = manager.getTaskById(task.getId());

        assertNotNull(retrievedTask);
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(task, retrievedTask);
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Epic retrieveEpics = manager.getEpicById(epic.getId());

        assertNotNull(retrieveEpics);
        assertEquals(Collections.EMPTY_LIST, epic.getSubtasks());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(epic, retrieveEpics);
    }

    @Test
    public void shouldCreateSubtask() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        Subtask retrieveSubtasks = manager.getSubtaskByID(subtask.getId());

        assertNotNull(retrieveSubtasks);
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(subtask, retrieveSubtasks);
    }

    @Test
    public void shouldUpdateTaskStatusToInProgress() {
        Task task = createTask();
        manager.createTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        epic.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubtaskStatusToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(subtask);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getTaskById(subtask.getId()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldChangeTheStatusEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        Subtask subtask1 = new Subtask("Тест подзадачи 2", "Описание подзадачи 2.", epic.getId(), "08:00 04.04.2025", "15");
        manager.createSubtask(subtask1);

        subtask.setStatus(TaskStatus.DONE);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);

        manager.updateSubtask(subtask);
        manager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldBeEpicStatusIfAllSubtaskStatusNew() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        Subtask subtask1 = new Subtask("Подзадача №1", "Описание подзадачи №1", epic.getId(), "16:30 30.01.2025", "30");
        manager.createSubtask(subtask);
        manager.createSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "статусы различаются");
    }

    @Test
    void shouldBeEpicStatusIfAllSubtaskStatusDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        Subtask subtask1 = new Subtask("Подзадача №1", "Описание подзадачи №1", epic.getId(), "16:30 30.01.2025", "30");
        manager.createSubtask(subtask);
        manager.createSubtask(subtask1);
        subtask.setStatus(TaskStatus.DONE);
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);
        manager.updateSubtask(subtask1);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "статусы различаются");
    }

    @Test
    void shouldBeEpicStatusIfSubtaskStatusDoneAndNew() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        Subtask subtask1 = new Subtask("Подзадача №1", "Описание подзадачи №1", epic.getId(), "16:30 30.01.2025", "30");
        manager.createSubtask(subtask);
        manager.createSubtask(subtask1);
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);
        manager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "статусы различаются");
    }

    @Test
    void shouldBeEpicStatusIfSubtaskStatusInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        Subtask subtask1 = new Subtask("Подзадача №1", "Описание подзадачи №1", epic.getId(), "16:30 30.01.2025", "30");
        manager.createSubtask(subtask);
        manager.createSubtask(subtask1);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask);
        manager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "статусы различаются");
    }

    @Test
    public void shouldDeleteTask() {
        Task task = createTask();
        manager.createTask(task);
        manager.deleteTask(task.getId());

        //assertNull(manager.getTaskById(task.getId()));
        assertThrows(NotFoundException.class, () -> {
            manager.getTaskById(task.getId());
        });
    }

    @Test
    public void testCreateAndDeleteEpicWithSubtasks() {
        manager.deleteAllTasks();
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.deleteEpic(epic.getId());

        //assertNull(manager.getEpicById(epic.getId()));
        //assertNull(manager.getSubtaskByID(subtask.getId()));
        assertThrows(NotFoundException.class, () -> {
            manager.getTaskById(epic.getId());
        });
        assertThrows(NotFoundException.class, () -> {
            manager.getTaskById(subtask.getId());
        });
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void shouldGetHistory() {
        manager.deleteAllTasks();
        Task task = createTask();
        manager.createTask(task);
        manager.getTaskById(task.getId());
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.getEpicById(epic.getId());
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);
        manager.getSubtaskByID((subtask.getId()));

        List<Task> history = manager.getHistory();
        assertEquals(3, history.size());
    }

    @Test
    void shouldBeAddEmptyHistory() {
        assertTrue(manager.getHistory().isEmpty());
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
    public void shouldManagersReturnInstances() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager);
    }

    @Test
    public void shouldManagerAddVariedTaskTypes() {
        Task task = createTask();
        manager.createTask(task);
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);

        Task task1 = new Task("Тест 1", "Описание 1", "10:11 02.02.2025", "15");
        manager.createTask(task1);
        Epic epic1 = new Epic("Тест 1", "Описание 1");
        manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Тест 1", "Описание 1", epic1.getId(), "10:11 03.02.2025", "15");
        manager.createSubtask(subtask1);

        assertNotNull(manager.getTaskById(task.getId()));
        assertNotNull(manager.getEpicById(epic.getId()));
        assertNotNull(manager.getSubtaskByID(subtask.getId()));
    }

    @Test
    public void shouldTasksNotConflictById() {
        Task task1 = new Task("Тест задачи 2", "Описание задачи 2.", "14:35 01.02.2025", "30");
        Task task = createTask();
        manager.createTask(task);
        manager.createTask(task1);

        task.setId(1);
        task1.setId(2);

        assertNotEquals(manager.getTaskById(1), manager.getTaskById(2));
    }

    @Test
    public void shouldTaskStayImmutableOnAdd() {
        Task task = createTask();
        manager.createTask(task);
        Task retrievedTask = manager.getTaskById(task.getId());

        assertEquals(task.getTitle(), retrievedTask.getTitle());
        assertEquals(task.getDescription(), retrievedTask.getDescription());
        assertEquals(task.getId(), retrievedTask.getId());
    }

    @Test
    public void shouldClearOldIds() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Subtask subtask = createSubtask(epic);
        manager.createSubtask(subtask);

        manager.deleteSubtask(subtask.getId());

        assertFalse(epic.getSubtasks().contains(subtask.getId()));
    }

    @Test
    void shouldNotOverlapWithTheTask() {
        assertThrows(TimeConfirmException.class, () -> {
            Task task1 = new Task("Задача №1", "Описание задачи №1", "15:00 15.04.2025", "60");
            Task task2 = new Task("Задача №2", "Описание задачи №2", "15:30 15.04.2025", "30");
            manager.createTask(task1);
            manager.createTask(task2);
        }, "при пересечении тасок по времени будет ошибка и task2 не создаться");
    }
}
