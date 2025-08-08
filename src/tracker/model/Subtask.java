package tracker.model;

import tracker.Status.Status;
import tracker.TypeTask.TypeTask;

import java.time.Duration;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String title, String description, Duration duration, Status status, int epicId) {
        super(id, title, description, duration, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, Duration duration, int epicId) {
        super(id, title, description, duration);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, Duration duration, int epicId) {
        super(title, description, duration);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, Duration duration, Status status, int epicId) {
        super(title, description, duration, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TypeTask getType() {
        return TypeTask.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{ "
                + "id - " + getId()
                + "; title - " + getTitle()
                + "; description - " + getDescription()
                + "; status - " + getStatus()
                + "; epicId=" + epicId
                + '}';
    }
}
