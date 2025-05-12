package manager;

import exception.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,description,status,epic\n");

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.write(taskDateToString(getTaskById(entry.getKey())) + "\n");
            }
            writer.write("\n"); //отделение задач
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.write(epicDateToString(getEpicById(entry.getKey())) + "\n");
            }
            writer.write("\n"); //отделение эпиков
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                writer.write(subtaskDateToString(getSubtaskByID(entry.getKey())) + "\n");
            }
            writer.write("\n"); //отделение подзадач

            if (!historyManager.getHistory().isEmpty()) { //Если история не пустая записать в файл
                writer.write(historyToString(historyManager)); //записываем
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при записи файла.");
        }
    }

    protected static Task fromString(String value) {
        String[] taskData = value.split(",");
        String id = taskData[0];
        String type = taskData[1];
        String status = taskData[2];
        String name = taskData[3];
        String description = taskData[4];
        Integer epicId = type.equals(TaskType.SUBTASK.toString()) ? Integer.valueOf(taskData[5]) : null;
        switch (type) {
            case "EPIC" -> {
                Epic epic = new Epic(name, description);
                epic.setId(Integer.parseInt(id));
                epic.setStatus(TaskStatus.valueOf(status.toUpperCase()));
                return epic;
            }
            case "SUBTASK" -> {
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setId(Integer.parseInt(id));
                subtask.setStatus(TaskStatus.valueOf(status.toUpperCase()));
                return subtask;
            }
            case "TASK" -> {
                Task task = new Task(name, description);
                task.setId(Integer.parseInt(id));
                task.setStatus(TaskStatus.valueOf(status.toUpperCase()));
                return task;
            }
            default -> {
                return null;
            }
        }
    }

    static List<Integer> historyFromString(String value) {
        final String[] ids = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String id : ids) {
            history.add(Integer.valueOf(id));
        }

        return history;
    }

    public void loadFromFile(File file) {
        if (file.length() == 0) {
            return;
        }
        FileBackedTaskManager backedTaskManager;
        String line = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader((file)))) {
            backedTaskManager = new FileBackedTaskManager(file);
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.isEmpty()) {
                    continue;
                }

                String[] arrayLine = line.split(",");

                if (arrayLine[2].equalsIgnoreCase("TASK")) {
                    Task task = fromString(line);
                    tasks.put(Objects.requireNonNull(task).getId(), task);
                } else if (arrayLine[2].equalsIgnoreCase("EPIC")) {
                    Task task = fromString(line);
                    epics.put(Objects.requireNonNull(task).getId(), (Epic) task);
                } else if (arrayLine[2].equalsIgnoreCase("SUBTASK")) {
                    Task task = fromString(line);
                    subtasks.put(Objects.requireNonNull(task).getId(), (Subtask) task);
                }
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при загрузке данных.");
        }

        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subtasks.values());

        List<Integer> listForHistory = new ArrayList<>();
        if (!line.isEmpty()) {
            listForHistory = historyFromString(line);
        }

        for (Integer historyId : listForHistory) {
            int id = historyId;

            if (epics.containsKey(id)) {
                backedTaskManager.getHistory().add(epics.get(id));
            }

            if (subtasks.containsKey(id)) {
                backedTaskManager.getHistory().add(subtasks.get(id));
            }

            if (tasks.containsKey(id)) {
                backedTaskManager.getHistory().add(subtasks.get(id));
            }
        }
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder idOfHistory = new StringBuilder();
        for (Task task : manager.getHistory()) {
            idOfHistory.append(task.getId()).append(",");
        }

        return idOfHistory.toString();
    }

    public String taskDateToString(Task task) {
        if (task.getStartTime() == null) {
            return task.getId() + ",TASK, "
                    + task.getStatus() + ", "
                    + task.getTitle() + ", "
                    + task.getDescription()
                    + ",n/a,n/a,n/a,\n";
        } else
            return task.getId() + ",TASK, "
                    + task.getStatus() + ", "
                    + task.getTitle() + ", "
                    + task.getDescription() + ", "
                    + task.getStartTime() + ", "
                    + task.getDuration() + ", "
                    + task.getEndTime() + ",\n";
    }

    public String epicDateToString(Epic epic) {
        if (!epic.getSubtaskId().isEmpty() && epic.getStartTime() != null) {
            return epic.getId() + ",EPIC, "
                    + epic.getStatus() + ", "
                    + epic.getTitle() + ", "
                    + epic.getDescription() + ", "
                    + epic.getStartTime() + ", "
                    + epic.getDuration() + ", "
                    + epic.getEndTime() + ",\n";
        } else return epic.getId() + ",EPIC, "
                + epic.getStatus() + ", "
                + epic.getTitle() + ", "
                + epic.getDescription()
                + ",n/a,n/a,n/a,\n";
    }

    public String subtaskDateToString(Subtask subtask) {
        if (subtask.getStartTime() == null) {
            return subtask.getId() + ",SUBTASK, "
                    + subtask.getStatus() + ", "
                    + subtask.getTitle() + ", "
                    + subtask.getDescription() + ", "
                    + subtask.getEpicId() + ", "
                    + ",n/a,n/a,n/a,\n";
        } else
            return subtask.getId() + ",SUBTASK, "
                    + subtask.getStatus() + ", "
                    + subtask.getTitle() + ", "
                    + subtask.getDescription() + ", "
                    + subtask.getEpicId() + ", "
                    + subtask.getStartTime() + ", "
                    + subtask.getDuration() + ", "
                    + subtask.getEndTime() + ",\n";
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        return super.getSubtaskByID(id);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> subtasks = super.getSubtasksByEpicId(epicId);
        save();
        return subtasks;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        save();
        return history;
    }
}
