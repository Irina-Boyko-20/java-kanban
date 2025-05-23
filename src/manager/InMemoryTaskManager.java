package manager;

import exception.NotFoundException;
import exception.TimeConfirmException;
import models.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static final Map<Integer, Task> tasks = new HashMap<>();
    protected static final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected static final Map<Integer, Epic> epics = new HashMap<>();
    private int idCounter = 1;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        prioritizedTasks = new TreeSet<>((Task task1, Task task2) -> {
            if (task1.getStartTime() != null && task2.getStartTime() != null) {
                if (task1.getStartTime().isAfter(task2.getStartTime())) {
                    return 1;
                } else if (task1.getStartTime() == (task2.getStartTime())) {
                    return -1;
                }
            } else if (task1.getStartTime() == null && task2.getStartTime() != null) {
                return 1;
            } else if (task1.getStartTime() != null && task2.getStartTime() == null) {
                return -1;
            }
            return -1;
        });
    }

    @Override
    public List<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }


    @Override
    public void createTask(Task task) {
        if (intersectionCheck(task)) {
            throw new TimeConfirmException("Пересечение по времени задачи с другой задачей");
        }
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (intersectionCheck(subtask)) {
            throw new TimeConfirmException("Пересечение по времени задачи с другой задачей");
        }

        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        if (epics.containsKey(subtask.getEpicId())) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic.getId());
            if (subtask.getStartTime() != null) {
                epicDurationUpdater(epic);
            }
        }

        prioritizedTasks.add(subtask);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException(String.format("Задача с ID %d не найдена", id));
        }

        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException(String.format("Epic с ID %d не найден", id));
        }

        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException(String.format("Подзадача с ID %d не найдена", id));
        }

        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        epicDurationUpdater(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpic(epic);
            epicDurationUpdater(epic);
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        }

        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                List<Integer> epicSubtasks = epic.getSubtasks();
                epicSubtasks.remove(Integer.valueOf(id));
                updateEpic(epic);
                epicDurationUpdater(epic);
            }
        }

        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    // обновление статуса
    private void updateEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
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

    @Override
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // проверка пересечения по времени
    private boolean intersectionCheck(Task task) {
        List<Task> tasks = getPrioritizedTasks();
        for (Task taskCheck : tasks) {
            if (taskCheck.getStartTime() != null && taskCheck.getEndTime() != null) {
                if (
                        (taskCheck.getStartTime().equals(task.getStartTime())
                                && taskCheck.getEndTime().equals(task.getEndTime()))
                                || (taskCheck.getStartTime().isBefore(task.getStartTime())
                                && (taskCheck.getEndTime().isAfter(task.getStartTime()))
                                || (taskCheck.getStartTime().isAfter(task.getStartTime()))
                                && (taskCheck.getStartTime().isBefore(task.getEndTime())))
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    // обновление времени, даты и периода выполнения Epic по его подзадачам
    private void epicDurationUpdater(Epic epic) {
        List<Subtask> subtasks = getSubtasksByEpicId(epic.getId());

        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ofDays(0));
            return;
        }

        LocalDateTime startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime endTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);

        if (startTime != null && endTime != null) {
            long duration = Duration.between(startTime, endTime).toHours();
            epic.setDuration(Duration.ofDays(duration));
        } else {
            epic.setDuration(Duration.ofDays(0));
        }
    }
}
