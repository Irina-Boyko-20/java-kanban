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

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private File testFile;

    protected Subtask createSubtask(int epicId) {
        return new Subtask("name1", "description1", epicId);
    }

    protected Epic createEpic() {
        return new Epic("name1", "description1");
    }

    protected Task createTask() {
        return new Task("name1", "description1");
    }

    @BeforeEach
    void setUp() {
        testFile = new File("test_task.csv");
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
    void shouldLoadEmptyFile() throws ManagerSaveException {
        manager.save();

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(testFile);
        loadedManager.loadFromFile(testFile);

        assertTrue(loadedManager.getAllTask().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadCorrectly() {
        Task task = createTask();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(epic.getId());

        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubtask(subtask);

        manager.save();

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(testFile);
        loadedManager.loadFromFile(testFile);

        assertEquals(1, loadedManager.getAllTask().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());

        assertEquals(task.getTitle(), loadedManager.getAllTask().get(0).getTitle());
        assertEquals(subtask.getTitle(), loadedManager.getAllTask().get(0).getTitle());
        assertEquals(epic.getTitle(), loadedManager.getAllEpics().get(0).getTitle());
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
