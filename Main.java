package com.todolist;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static final TodoList todoList = new TodoList();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        run();
    }

    /**
     * Главный цикл приложения.
     */
    public static void run() {
        while (true) {
            printMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    createTask();
                    break;
                case 2:
                    editTask();
                    break;
                case 3:
                    deleteTask();
                    break;
                case 4:
                    viewAllTasks();
                    break;
                case 5:
                    sortTasksByDate();
                    break;
                case 6:
                    searchTasks();
                    break;
                case 0:
                    System.out.println("Выход из приложения. До свидания!");
                    return;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, попробуйте еще раз.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Меню To-Do List ---");
        System.out.println("1. Создать задачу");
        System.out.println("2. Редактировать задачу");
        System.out.println("3. Удалить задачу");
        System.out.println("4. Показать все задачи");
        System.out.println("5. Сортировать задачи по дате");
        System.out.println("6. Найти задачи");
        System.out.println("0. Выход");
        System.out.print("Введите ваш выбор: ");
    }

    private static int getUserChoice() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.next(); // Очистка неверного ввода
            return -1;     // Возврат неверного значения
        } finally {
            scanner.nextLine(); // Потребление оставшегося символа новой строки
        }
    }

    private static void createTask() {
        System.out.println("\n--- Создание новой задачи ---");
        System.out.print("Введите название: ");
        String title = scanner.nextLine();

        System.out.print("Введите описание: ");
        String description = scanner.nextLine();

        LocalDate dueDate = getDateInput(false);
        Priority priority = getPriorityInput(false);

        Task newTask = new Task(title, description, dueDate, priority);
        todoList.addTask(newTask);
        System.out.println("Задача успешно создана!");
    }

    private static void editTask() {
        viewAllTasks();
        List<Task> tasks = todoList.getAllTasks();
        if (tasks.isEmpty()) return;

        System.out.print("Введите номер задачи для редактирования: ");
        try {
            int taskNumber = scanner.nextInt() - 1;
            scanner.nextLine(); // Потребление новой строки

            if (taskNumber >= 0 && taskNumber < tasks.size()) {
                System.out.println("Введите новые данные (оставьте поле пустым, чтобы сохранить текущее значение):");

                Task oldTask = tasks.get(taskNumber);

                System.out.print("Введите новое название: ");
                String newTitle = scanner.nextLine();
                if (newTitle.isEmpty()) newTitle = oldTask.getTitle();

                System.out.print("Введите новое описание: ");
                String newDescription = scanner.nextLine();
                if (newDescription.isEmpty()) newDescription = oldTask.getDescription();

                LocalDate newDueDate = getDateInput(true); // Разрешить пустой ввод
                if (newDueDate == null) newDueDate = oldTask.getDueDate();

                Priority newPriority = getPriorityInput(true); // Разрешить пустой ввод
                if (newPriority == null) newPriority = oldTask.getPriority();

                todoList.editTask(taskNumber, newTitle, newDescription, newDueDate, newPriority);
                System.out.println("Задача успешно отредактирована!");
            } else {
                System.out.println("Неверный номер задачи.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Неверный ввод. Пожалуйста, введите число.");
            scanner.next(); // Очистка буфера
        }
    }

    private static void deleteTask() {
        viewAllTasks();
        List<Task> tasks = todoList.getAllTasks();
        if (tasks.isEmpty()) return;

        System.out.print("Введите номер задачи для удаления: ");
        try {
            int taskNumber = scanner.nextInt() - 1;
            scanner.nextLine(); // Потребление новой строки

            if (todoList.deleteTask(taskNumber)) {
                System.out.println("Задача успешно удалена!");
            } else {
                System.out.println("Неверный номер задачи.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Неверный ввод. Пожалуйста, введите число.");
            scanner.next(); // Очистка буфера
        }
    }

    private static void viewAllTasks() {
        System.out.println("\n--- Все задачи ---");
        List<Task> tasks = todoList.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("Нет задач для отображения.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println("Задача #" + (i + 1));
                System.out.println(tasks.get(i));
            }
        }
    }

    private static void sortTasksByDate() {
        todoList.sortByDueDate();
        System.out.println("Задачи отсортированы по дате выполнения.");
        viewAllTasks();
    }

    private static void searchTasks() {
        System.out.println("\n--- Поиск задач ---");
        System.out.println("1. Поиск по ключевому слову");
        System.out.println("2. Поиск по приоритету");
        System.out.print("Введите ваш выбор: ");
        int choice = getUserChoice();

        switch (choice) {
            case 1:
                System.out.print("Введите ключевое слово для поиска в названии/описании: ");
                String keyword = scanner.nextLine();
                List<Task> foundByKeyword = todoList.searchByKeyword(keyword);
                displaySearchResults(foundByKeyword);
                break;
            case 2:
                Priority priority = getPriorityInput(false);
                List<Task> foundByPriority = todoList.searchByPriority(priority);
                displaySearchResults(foundByPriority);
                break;
            default:
                System.out.println("Неверный выбор поиска.");
        }
    }

    private static void displaySearchResults(List<Task> results) {
        if (results.isEmpty()) {
            System.out.println("Не найдено задач, соответствующих вашим критериям.");
        } else {
            System.out.println("--- Результаты поиска ---");
            for (Task task : results) {
                System.out.println(task);
            }
        }
    }

    // --- Вспомогательные методы для ввода ---

    private static LocalDate getDateInput(boolean allowEmpty) {
        while (true) {
            System.out.print("Введите срок выполнения (гггг-мм-дд): ");
            String dateString = scanner.nextLine();
            if (allowEmpty && dateString.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(dateString);
            } catch (DateTimeParseException e) {
                System.out.println("Неверный формат даты. Пожалуйста, используйте гггг-мм-дд.");
            }
        }
    }

    private static Priority getPriorityInput(boolean allowEmpty) {
        while (true) {
            System.out.print("Введите приоритет (HIGH, MEDIUM, LOW): ");
            String priorityString = scanner.nextLine().toUpperCase();
            if (allowEmpty && priorityString.isEmpty()) {
                return null;
            }
            try {
                return Priority.valueOf(priorityString);
            } catch (IllegalArgumentException e) {
                System.out.println("Неверный приоритет. Пожалуйста, введите HIGH, MEDIUM или LOW.");
            }
        }
    }
}