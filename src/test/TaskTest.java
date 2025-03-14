package test;

import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;

    @BeforeEach
    public void setUp() {
        task = new Task("Тест задачи", "Описание задачи.");
    }

    @Test
    public void addTask() {
        // Проверяем, что задача создаётся с правильными значениями
        assertEquals("Тест задачи", task.getTitle());
        assertEquals("Проверка задачи и её описания.", task.getDescription());
        // Проверяем статус
        assertEquals(TaskStatus.NEW, task.getStatus(), "Статус не соответствует статусу по-умолчанию");
    }

    @Test
    public void SetIdTask() {
        // Устанавливаем идентификатор задачи
        task.setId(1);
        assertEquals(1, task.getId());
    }

    @Test
    public void updateStatusTask() {
        // Обновляем статус задачи
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }
}