package test;

import models.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;

    @BeforeEach
    public void setUp() {
        epic = new Epic("Тест Epic", "Тестируем epic.");
    }

    @Test
    public void AddEpic() {
        // Проверяем, что список пустой
        assertEquals(0, epic.getSubtasks().size(), "Список не пустой");

        epic.addSubtask(1);
        epic.addSubtask(2);
        epic.addSubtask(3);

        // Проверяем, что подзадачи добавились
        List<Integer> subtasks = epic.getSubtasks();
        assertEquals(3, subtasks.size());
        assertEquals(List.of(1, 2, 3), subtasks);
    }

    @Test
    public void GetSubtasksReturnsList() {
        epic.addSubtask(1);
        epic.addSubtask(2);

        // Проверяем, что метод getSubtasks возвращает список
        List<Integer> subtasks = epic.getSubtasks();
        assertEquals(2, subtasks.size());
    }

}