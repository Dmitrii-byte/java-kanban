import tracker.Status.Status;
import tracker.controllers.TaskManager;
import tracker.model.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Задачи
        taskManager.addTask(new Task("Задача1", "Описание задачи1"));
        taskManager.addTask(new Task("Задача3", "Описание задачи2", Status.DONE));

        // Эпики
        taskManager.addEpic(new Epic("Эпик1", "Описание эпик1", new ArrayList<>()));
        taskManager.addEpic(new Epic("Эпик2", "Описание эпик2", new ArrayList<>()));

        // Подзадачи
        int epicId1 = taskManager.getAllEpics().get(1).getId();
        taskManager.addSubtask(new Subtask("Подзадача1", "Описание подзадача1", Status.DONE, epicId1));
        taskManager.addSubtask(new Subtask("Подзадача2", "Описание подзадача2", Status.DONE, epicId1));

        // Вывод задач
        ArrayList<Task> tasks = taskManager.getAllTasks();
        ArrayList<Epic> epics = taskManager.getAllEpics();
        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();

        System.out.println("Вывод задач:");
        System.out.println(tasks);
        System.out.println();
        System.out.println("Вывод эпиков:");
        System.out.println(epics);
        System.out.println();
        System.out.println("Вывод подзадач:");
        System.out.println(subtasks);
        System.out.println();

        // получение по id
        System.out.println("Получение по id:");
        System.out.println(taskManager.getEpicById(3));
        System.out.println(taskManager.getTaskById(2));
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println();

        // получение задач определенного эпика
        System.out.println("получение задач эпика");
        System.out.println(taskManager.getSubtasksByEpic(4));
        System.out.println();

        // обновление задач
        System.out.println("Обновление задач");
        taskManager.updateTask(new Task(2, "здесь кто нибудь есть?", "я здесь", Status.IN_PROGRESS));
        taskManager.updateEpic(new Epic(3, "го го го", "может быть", new ArrayList<>()));
        taskManager.updateSubtask(new Subtask(5, "тутут", "уууу", Status.IN_PROGRESS, 4));
        epics = taskManager.getAllEpics();
        tasks = taskManager.getAllTasks();
        subtasks = taskManager.getAllSubtasks();
        System.out.println(epics);
        System.out.println(tasks);
        System.out.println(subtasks);
        System.out.println();

        // удаление по id
        System.out.println("Удаление по id");
        taskManager.removeTaskById(1);
       // taskManager.removeEpicById(3);
        taskManager.removeSubtaskById(6);
        tasks = taskManager.getAllTasks();
        subtasks = taskManager.getAllSubtasks();
        epics = taskManager.getAllEpics();
        System.out.println(tasks);
        System.out.println(subtasks);
        System.out.println(epics);
        System.out.println();

        System.out.println("Чистка");
        taskManager.clearTasks();
        taskManager.clearEpics();

        // после чистки
        tasks = taskManager.getAllTasks();
        //epics = taskManager.getAllEpics();
        subtasks = taskManager.getAllSubtasks();

        System.out.println(tasks);
        System.out.println(epics);
        System.out.println(subtasks);
    }
}