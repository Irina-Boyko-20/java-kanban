package manager;

import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Тест задачи №1", "Описание задачи №1.");
        task1.setId(1);
        task2 = new Task("Тест задачи №2", "Описание задачи №2.");
        task2.setId(2);
        epic1 = new Epic("Тест epic №1", "Описание epic №1.");
        epic1.setId(3);
        epic2 = new Epic("Тест epic №2", "Описание epic №2.");
        epic2.setId(4);
        subtask = new Subtask("Тест подзадачи", "Описание подзадачи.", epic1.getId());
    }

    @Test
    public void shouldAddTask() {
        historyManager.add(task1);
        historyManager.add(epic1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertTrue(history.contains(task1));
        assertTrue(history.contains(epic1));
    }

    @Test
    public void shouldAddDuplicateTask() {
        task1 = new Task("Тест задачи №1", "Описание задачи №1.");
        task2 = new Task("Тест задачи №1", "Обновлённое описание задачи №1.");

        task2.setId(task1.getId());
        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertTrue(history.contains(task2));
        assertFalse(history.contains(task1));
    }

    @Test
    public void shouldDeleteTask() {
        historyManager.add(task1);
        historyManager.add(epic2);

        // Удаляем задачу
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertFalse(history.contains(task1));
        assertTrue(history.contains(epic2));
    }

    @Test
    public void shouldGetHistoryEmpty() {
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    public void shouldGetHistoryAfterAddingOneTask() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task1.getTitle(), history.get(0).getTitle());
    }

    @Test
    public void shouldGetHistoryAfterAddingOneEpic() {
        historyManager.add(epic1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(epic1.getTitle(), history.get(0).getTitle());
    }

    @Test
    public void shouldGetHistoryAfterAddingOneSubtask() {
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(subtask.getTitle(), history.get(0).getTitle());
    }
}
