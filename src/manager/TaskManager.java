package manager;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTask();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    // создание задач, epic, подзадач
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    // получение id задачи, epic, подзадачи
    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskByID(int id);

    // обновление задач, epic, подзадач
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    // удаление задач, epic, подзадач
    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteAllTasks();

    List<Subtask> getSubtasksByEpicId(int epicId);

    //история просмотров
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
