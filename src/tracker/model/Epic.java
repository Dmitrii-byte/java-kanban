package tracker.model;

import tracker.TypeTask.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
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
    public Duration getDuration() {
        return super.getDuration();
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime();
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
                + "; startTime - " + getStartTime()
                + "; duration - " + getDuration()
                + "; endTime - " + getEndTime()
                + "; subtasks=" + subtasksId
                + '}';
    }
}