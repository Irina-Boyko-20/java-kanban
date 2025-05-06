import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import models.TaskStatus;

import java.io.File;

public class Main {

    public static void printAllTasks(TaskManager manager) {

        // Проверка наличия всех задач, epic и подзадач
        System.out.println("Задачи:");
        for (Task task : manager.getAllTask()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        TaskManager taskManagers = Managers.getDefault();
        File file = new File("tasks.csv");
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(file);

        Task task1 = new Task("Кружка", "Забрать термокружку в новом Виогеме");
        taskManager.createTask(task1);
        taskManagers.createTask(task1);

        Task task2 = new Task("Кошка", "Купить кошке вкусняшки");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Отпуск", "Составить план достопримечательностей");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Домбай", "Где можно покушать и погулять", epic1.getId());
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Экскурсия", "Посмотреть экскурсии рядом с Нальчиком и Пятигорском", epic1.getId());
        taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Термальные источники", "Составить список термальных источников", epic1.getId());
        taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Обучение", "За неделю прочитать теорию 5-ого спринта");
        taskManager.createEpic(epic2);

        //добавление задачи в файл
        backedTaskManager.createTask(task1);

        // Получаем задачу по ID и выводим ее на экран
        Task retrievedTask = backedTaskManager.getTaskById(task1.getId());
        System.out.println("Полученная задача: " + retrievedTask);

        // Загружаем данные из файла
        backedTaskManager.loadFromFile(file);

        // Проверяем историю задач в файле
        System.out.println("История задач: " + backedTaskManager.getHistory());

        // Проверка статуса epic, подзадачи и задачи
        System.out.printf("Статус задачи \"%s\": %s\n",
                            task2.getTitle(),
                            taskManager.getTaskById(task2.getId()).getStatus());
        System.out.printf("Статус epic \"%s\": %s\n",
                           epic2.getTitle(),
                           taskManager.getEpicById(epic2.getId()).getStatus());
        System.out.printf("Статус подзадачи \"%s\": %s\n",
                            subtask2.getTitle(),
                            taskManager.getSubtaskByID(subtask2.getId()).getStatus());

        // Обновление статуса подзадач и проверка статуса epic
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        System.out.printf("Статус epic после обновления подзадачи \"%s\": %s\n",
                            subtask2.getTitle(),
                            taskManager.getEpicById(epic1.getId()).getStatus());

        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask3);
        System.out.printf("Статус epic после обновления подзадачи \"%s\": %s\n",
                            subtask3.getTitle(),
                            taskManager.getEpicById(epic1.getId()).getStatus());

        // Обновление статуса задачи и проверка статуса
        task1.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);
        System.out.printf("Статус задачи \"%s\" после завершения: %s\n",
                            task1.getTitle(),
                            taskManager.getTaskById(task1.getId()).getStatus());

        printAllTasks(taskManager);

        // Удаление задачи и подзадачи, проверка списка подзадач epic
        taskManager.deleteSubtask(subtask1.getId());
        taskManager.deleteTask(task2.getId());
        System.out.println("Задачи после удаления: ");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        // Обновление статуса подзадач и проверка статуса epic
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        System.out.printf("Статус epic после обновления подзадачи \"%s\": %s\n",
                            subtask2.getTitle(),
                            taskManager.getEpicById(epic1.getId()).getStatus());

        taskManager.updateSubtask(subtask3);
        System.out.printf("Статус epic после обновления подзадачи \"%s\": %s\n",
                            subtask3.getTitle(),
                            taskManager.getEpicById(epic1.getId()).getStatus());

        // Обновление статуса подзадачи и проверка статуса epic
        epic1.setStatus(TaskStatus.DONE);
        taskManager.updateEpic(epic1);
        System.out.printf("Статус epic \"%s\" после завершения всех подзадач: %s\n",
                            epic1.getTitle(),
                            taskManager.getEpicById(epic1.getId()).getStatus());

        taskManager.deleteEpic(epic1.getId());

        printAllTasks(taskManager);

        // Удаление всех задач, epic и подзадач
        taskManager.deleteEpic(epic2.getId());
        taskManager.deleteAllTasks();
    }
}
