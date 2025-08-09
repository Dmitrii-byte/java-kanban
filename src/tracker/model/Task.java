package tracker.model;

import tracker.Status.Status;
import tracker.TypeTask.TypeTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    protected Task() {
        status = Status.NEW;
    }

    public Task(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, Status status) {
        this();
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description, Duration duration, LocalDateTime startTime) {
        this(title, description);
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String title, String description, Duration duration, LocalDateTime startTime, Status status) {
        this(title, description, duration, startTime);
        this.status = status;
    }

    public Task(int id, String title, String description) {
        this(title, description);
        this.id = id;
    }

    public Task(int id, String title, String description, Status status) {
        this(title, description, status);
        this.id = id;
    }

    public Task(int id, String title, String description, Duration duration, LocalDateTime startTime) {
        this(title, description, duration, startTime);
        this.id = id;
    }

    public Task(int id, String title, String description, Duration duration, LocalDateTime startTime, Status status) {
        this(title, description, duration, startTime, status);
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TypeTask getType() {
        return TypeTask.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof Task task) {
            return id == task.id;
        }
        return false;
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
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{ "
                + "id - " + id
                + "; title - " + title
                + "; description - " + description
                + "; status - " + status
                + "; startTime - " + getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + "; duration - " + getDuration().toHours() + ":" + getDuration().toMinutesPart()
                + "; endTime=" + getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + "}";
    }
}