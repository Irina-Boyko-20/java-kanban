import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private int idCounter = 1;

    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    // создание задач, подзадач, epic
    public Task createTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        return task;
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        if (epics.containsKey(subtask.getEpicId())) {
            epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
        }
        return subtask;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    // получение id задачи, подзадачи, epic
    public Task getTaskById(int id) {
        return (Task) tasks.get(id);
    }

    public Subtask getSubtaskByID(int id) {
        return (Subtask) subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return (Epic) epics.get(id);
    }

    // обновление задач и подзадач
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpic(epic);
        }
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // удаление задач и подзадач
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask subtask = (Subtask) subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpic(epic);
            }
        }
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    // обновление статуса
    private void updateEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = (Epic) epics.get(epicId);
            List<Subtask> associatedSubtasks = getSubtasksByEpicId(epicId);

            if (associatedSubtasks.isEmpty()) {
                epic.setStatus(TaskStatus.NEW);
            } else if (associatedSubtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE)) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> result = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            for (int subtaskId : epics.get(epicId).getSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    result.add(subtask);
                }

            }
        }
        return result;
    }
}
