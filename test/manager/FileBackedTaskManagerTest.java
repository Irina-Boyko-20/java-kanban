package manager;

import exception.ManagerSaveException;
import models.Epic;
import models.Subtask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = File.createTempFile("test_task", "csv");
        manager = new FileBackedTaskManager(testFile);
    }

    @AfterEach
    void shouldDeleteAfter() {
        try {
            Files.delete(testFile.toPath());
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    @Test
    void shouldLoadEmptyFile() {
        manager.deleteAllTasks();
        manager.loadFromFile(testFile);

        assertTrue(manager.getAllTask().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadCorrectly() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic);

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        manager.save();

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(testFile);
        loadedManager.loadFromFile(testFile);

        assertEquals(1, loadedManager.getAllTask().size());
        assertEquals(task.getTitle(), loadedManager.getAllTask().getFirst().getTitle());
        assertEquals(subtask.getTitle(), loadedManager.getAllSubtasks().getFirst().getTitle());
        assertEquals(epic.getTitle(), loadedManager.getAllEpics().getFirst().getTitle());
    }

    @Test
    void shouldThrowsExceptionOnIOException() {
        File invalidFile = new File("/invalid/path/test_tasks.csv");

        FileBackedTaskManager invalidManager = new FileBackedTaskManager(invalidFile);

        assertThrows(ManagerSaveException.class, invalidManager::save);
    }

    @Test
    void shouldHistoryToString() {
        assertEquals(Collections.emptyList(), manager.getHistory());
    }
}
