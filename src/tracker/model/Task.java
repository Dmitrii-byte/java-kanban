package tracker.model;

import tracker.Status.Status;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;

    protected Task () {
        status = Status.NEW;
    }

    public Task (String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    public Task (String title, String description, Status status) {
        this(title, description);
        this.status = status;
    }

    public Task (int id, String title, String description) {
        this(title, description);
        this.id = id;
    }

    public Task (int id, String title, String description, Status status) {
        this(title, description, status);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof Task task) {
            return id == task.id;
        }
        return false;
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
                + "}";
    }
}