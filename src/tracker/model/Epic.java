package tracker.model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

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
    public String toString() {
        return "Epic{ "
                + "id - " + getId()
                + ";title - " + getTitle()
                + ";description - " + getDescription()
                + ";status - " + getStatus()
                + ";subtasks=" + subtasksId
                + '}';
    }
}