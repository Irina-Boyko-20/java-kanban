package test;

import manager.InMemoryHistoryManager;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Тест задачи", "Описание задачи.");
        epic = new Epic("Тест epic", "Описание epic.");
        subtask = new Subtask("Тест подзадачи", "Описание подзадачи.", epic.getId());
    }

    @Test
    public void shouldAddTaskBelowLimit() {
        for (int i = 1; i <= 5; i++) {
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertEquals(5, history.size());
    }

    @Test
    public void shouldAddTaskExceedingLimit() {
        for (int i = 1; i <= 10; i++) {
            historyManager.add(new Task("Задача №" + i, "Описание задачи №" + i));
        }

        historyManager.add(new Task("Задача №" + 11, "Описание задачи №" + 11));

        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size());
        assertEquals("Задача №2", history.get(0).getTitle());
        assertEquals("Задача №11", history.get(9).getTitle());
    }

    @Test
    public void shouldAddEpicExceedingLimit() {
        for (int i = 1; i <= 10; i++) {
            historyManager.add(new Epic("Epic №" + i, "Описание epic №" + i));
        }

        historyManager.add(new Epic("Epic №" + 11, "Описание epic №" + 11));

        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size());
        assertEquals("Epic №2", history.get(0).getTitle());
        assertEquals("Epic №11", history.get(9).getTitle());
    }

    @Test
    public void shouldGetHistoryEmpty() {
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    public void shouldGetHistoryAfterAddingOneTask() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task.getTitle(), history.get(0).getTitle());
    }

    @Test
    public void shouldGetHistoryAfterAddingOneEpic() {
        historyManager.add(epic);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(epic.getTitle(), history.get(0).getTitle());
    }

    @Test
    public void shouldGetHistoryAfterAddingOneSubtask() {
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(subtask.getTitle(), history.get(0).getTitle());
    }
}