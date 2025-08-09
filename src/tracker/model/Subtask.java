package tracker.model;

import tracker.Status.Status;
import tracker.TypeTask.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String title, String description, Duration duration, LocalDateTime startTime, Status status, int epicId) {
        super(id, title, description, duration, startTime, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, Duration duration, LocalDateTime startTime, int epicId) {
        super(id, title, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, Duration duration, LocalDateTime startTime, int epicId) {
        super(title, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, Duration duration, LocalDateTime startTime, Status status, int epicId) {
        super(title, description, duration, startTime, status);
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
                + "; startTime - " + getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + "; duration - " + getDuration().toHours() + ":" + getDuration().toMinutesPart()
                + "; endTime=" + getEndTime()
                + "; epicId=" + epicId
                + '}';
    }
}
