package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String title; // название
    private String description; // описание
    private int id; // идентификатор задач
    private TaskStatus status; // статус задач
    private Duration duration; // продолжительность задачи в минутах
    private LocalDateTime startTime; // дата и время начала выполнения задания

    public Task(String title, String description, String startTime, String duration) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW; // статус по-умолчанию
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        this.startTime = LocalDateTime.parse(startTime, formatter);
        this.duration = Duration.ofMinutes(Long.parseLong(duration));
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        if (getStartTime() == null) {
            return "Task {" +
                    "id= " + id +
                    ", type = " + TaskType.TASK +
                    ", status= " + status +
                    ", title= '" + title + '\'' +
                    ", description= '" + description + '\'' +
                    ", startTime= " + "n/a" +
                    ", duration= " + "n/a" +
                    ", endTime= " + "n/a" +
                    '}';
        } else {
            return "Task {" +
                    "id= " + id +
                    ", type = " + TaskType.TASK +
                    ", title= '" + title + '\'' +
                    ", status= " + status +
                    ", description= '" + description + '\'' +
                    ", startTime= " + startTime.format(formatter) +
                    ", duration= " + duration +
                    ", endTime= " + getEndTime().format(formatter) +
                    '}';
        }
    }
}
