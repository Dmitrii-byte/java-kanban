package tracker.Comparator;

import tracker.model.Task;

import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task task1, Task task2) {
        if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return Integer.compare(task1.getId(), task2.getId());
        }
        int timeComparison = task1.getStartTime().compareTo(task2.getStartTime());
        return timeComparison != 0 ? timeComparison : Integer.compare(task1.getId(), task2.getId());
    }
}