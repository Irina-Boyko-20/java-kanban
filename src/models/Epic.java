package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private final List<Integer> subtasks; // подзадачи
    private final List<Integer> subtaskId; // идентификаторы подзадач
    private LocalDateTime endTime; // дата и время окончания выполнения задачи

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
        this.subtaskId = new ArrayList<>();
    }

    public void addSubtask(int subtask) {
        subtasks.add(subtask);
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public List<Integer> getSubtaskId() {
        return subtaskId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        if (getStartTime() == null) {
            return "Epic {" +
                    "id= " + getId() +
                    ", type= " + TaskType.EPIC +
                    ", status= " + getStatus() +
                    ", title= '" + getTitle() + '\'' +
                    ", description= '" + getDescription() + '\'' +
                    ", subtasks= " + subtasks +
                    ", startTime= " + "n/a" +
                    ", duration= " + "n/a" +
                    ", endTime= " + "n/a" +
                    '}';
        } else {
            return "Epic {" +
                    "id= " + getId() +
                    ", type= " + TaskType.EPIC +
                    ", status= " + getStatus() +
                    ", title= '" + getTitle() + '\'' +
                    ", description= '" + getDescription() + '\'' +
                    ", subtasks= " + subtasks +
                    ", startTime= " + getStartTime().format(formatter) +
                    ", duration= " + getDuration() +
                    ", endTime= " + getEndTime().format(formatter) +
                    '}';
        }
    }
}
