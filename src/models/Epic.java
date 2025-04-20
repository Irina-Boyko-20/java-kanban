package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private final List<Integer> subtasks; // подзадачи

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(int subtask) {
        subtasks.add(subtask);
    }

    public List<Integer> getSubtasks() {
        return subtasks;
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
        return "Epic{" +
                "id=" + getId() + '\'' +
                ", type = '" + TaskType.EPIC + '\'' +
                ", title='" + getTitle() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subtasks=" + subtasks +
                '}';
    }
}
