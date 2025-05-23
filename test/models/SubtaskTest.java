package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {
    private Subtask subtask;

    @BeforeEach
    public void setUp() {
        subtask = new Subtask("Тест подзадачи", "Описание подзадачи", 1);
    }

    @Test
    public void addSubtask() {
        // Проверяем, что подзадача создаётся с правильными значениями
        assertEquals("Тест подзадачи", subtask.getTitle());
        assertEquals("Описание подзадачи", subtask.getDescription());
        assertEquals(1, subtask.getEpicId()); // Проверяем идентификатор эпика
        // Проверяем статус
        assertEquals(TaskStatus.NEW, subtask.getStatus(), "Статус не соответствует статусу по-умолчанию");
    }

    @Test
    public void getEpicId() {
        // Проверяем, что идентификатор эпика корректно возвращается
        assertEquals(1, subtask.getEpicId());
    }

    @Test
    public void updateStatusSubtask() {
        // Обновляем статус подзадачи
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, subtask.getStatus());
    }
}
