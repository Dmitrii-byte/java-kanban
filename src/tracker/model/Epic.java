package tracker.model;

import tracker.TypeTask.TypeTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String title, String description, ArrayList<Integer> subtasksId) {
        super(title, description);
        this.subtasksId = subtasksId;
    }

    public Epic(int id, String title, String description, ArrayList<Integer> subtasksId) {
        super(id, title, description);
        this.subtasksId = subtasksId;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasks(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{ "
                + "id - " + getId()
                + "; title - " + getTitle()
                + "; description - " + getDescription()
                + "; status - " + getStatus()
                + "; startTime - " + (getStartTime() != null ? getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : " ")
                + "; duration - " + (getDuration() != null ? getDuration().toHours() + ":" + getDuration().toMinutesPart() : " ")
                + "; endTime - " + (getEndTime() != null ? getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : " ")
                + "; subtasks=" + subtasksId
                + '}';
    }
}