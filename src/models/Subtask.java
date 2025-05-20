package models;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId; // идентификатор epic

    public Subtask(String title, String description, int epicId, String startTime, String duration) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        if (getStartTime() == null) {
            return "Subtask {" +
                    "id= " + getId() +
                    ", type= " + TaskType.SUBTASK +
                    ", status= " + getStatus() +
                    ", title= '" + getTitle() + '\'' +
                    ", description= '" + getDescription() + '\'' +
                    ", epicId= " + epicId +
                    ", startTime= " + "n/a" +
                    ", duration= " + "n/a" +
                    ", endTime= " + "n/a" +
                    '}';
        } else {
            return "Subtask {" +
                    "id= " + getId() +
                    ", type= " + TaskType.SUBTASK +
                    ", status= " + getStatus() +
                    ", title= '" + getTitle() + '\'' +
                    ", description= '" + getDescription() + '\'' +
                    ", epicId=" + epicId +
                    ", startTime=" + getStartTime().format(formatter) +
                    ", duration=" + getDuration() +
                    ", endTime=" + getEndTime().format(formatter) +
                    '}';
        }
    }
}
