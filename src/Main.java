import tracker.controllers.*;
import tracker.model.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Epic1", "DescriptionEpic1", new ArrayList<>());
        Epic epic2 = new Epic("Epic2", "DescriptionEpic2", new ArrayList<>());
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Sub1", "DisSub1", epic2.getId());
        Subtask subtask2 = new Subtask("Sub2", "DisSub2", epic2.getId());
        Subtask subtask3 = new Subtask("Sub3", "DisSub3", epic2.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        taskManager.getSubtaskById(subtask1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();
        taskManager.getSubtaskById(subtask2.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();
        taskManager.getSubtaskById(subtask3.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();
        taskManager.getSubtaskById(subtask2.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();
        taskManager.getSubtaskById(subtask3.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();
        taskManager.getEpicById(epic1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();
        taskManager.getEpicById(epic2.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();
        taskManager.getEpicById(epic1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();
        taskManager.removeSubtaskById(3);
        System.out.println(taskManager.getHistory());
        System.out.println();
        taskManager.removeEpicById(2);
        System.out.println(taskManager.getHistory());
        System.out.println();
    }
}