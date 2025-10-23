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
     * ������� ���� ����������.
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
                    System.out.println("����� �� ����������. �� ��������!");
                    return;
                default:
                    System.out.println("�������� �����. ����������, ���������� ��� ���.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- ���� To-Do List ---");
        System.out.println("1. ������� ������");
        System.out.println("2. ������������� ������");
        System.out.println("3. ������� ������");
        System.out.println("4. �������� ��� ������");
        System.out.println("5. ����������� ������ �� ����");
        System.out.println("6. ����� ������");
        System.out.println("0. �����");
        System.out.print("������� ��� �����: ");
    }

    private static int getUserChoice() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.next(); // ������� ��������� �����
            return -1;     // ������� ��������� ��������
        } finally {
            scanner.nextLine(); // ����������� ����������� ������� ����� ������
        }
    }

    private static void createTask() {
        System.out.println("\n--- �������� ����� ������ ---");
        System.out.print("������� ��������: ");
        String title = scanner.nextLine();

        System.out.print("������� ��������: ");
        String description = scanner.nextLine();

        LocalDate dueDate = getDateInput(false);
        Priority priority = getPriorityInput(false);

        Task newTask = new Task(title, description, dueDate, priority);
        todoList.addTask(newTask);
        System.out.println("������ ������� �������!");
    }

    private static void editTask() {
        viewAllTasks();
        List<Task> tasks = todoList.getAllTasks();
        if (tasks.isEmpty()) return;

        System.out.print("������� ����� ������ ��� ��������������: ");
        try {
            int taskNumber = scanner.nextInt() - 1;
            scanner.nextLine(); // ����������� ����� ������

            if (taskNumber >= 0 && taskNumber < tasks.size()) {
                System.out.println("������� ����� ������ (�������� ���� ������, ����� ��������� ������� ��������):");

                Task oldTask = tasks.get(taskNumber);

                System.out.print("������� ����� ��������: ");
                String newTitle = scanner.nextLine();
                if (newTitle.isEmpty()) newTitle = oldTask.getTitle();

                System.out.print("������� ����� ��������: ");
                String newDescription = scanner.nextLine();
                if (newDescription.isEmpty()) newDescription = oldTask.getDescription();

                LocalDate newDueDate = getDateInput(true); // ��������� ������ ����
                if (newDueDate == null) newDueDate = oldTask.getDueDate();

                Priority newPriority = getPriorityInput(true); // ��������� ������ ����
                if (newPriority == null) newPriority = oldTask.getPriority();

                todoList.editTask(taskNumber, newTitle, newDescription, newDueDate, newPriority);
                System.out.println("������ ������� ���������������!");
            } else {
                System.out.println("�������� ����� ������.");
            }
        } catch (InputMismatchException e) {
            System.out.println("�������� ����. ����������, ������� �����.");
            scanner.next(); // ������� ������
        }
    }

    private static void deleteTask() {
        viewAllTasks();
        List<Task> tasks = todoList.getAllTasks();
        if (tasks.isEmpty()) return;

        System.out.print("������� ����� ������ ��� ��������: ");
        try {
            int taskNumber = scanner.nextInt() - 1;
            scanner.nextLine(); // ����������� ����� ������

            if (todoList.deleteTask(taskNumber)) {
                System.out.println("������ ������� �������!");
            } else {
                System.out.println("�������� ����� ������.");
            }
        } catch (InputMismatchException e) {
            System.out.println("�������� ����. ����������, ������� �����.");
            scanner.next(); // ������� ������
        }
    }

    private static void viewAllTasks() {
        System.out.println("\n--- ��� ������ ---");
        List<Task> tasks = todoList.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("��� ����� ��� �����������.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println("������ #" + (i + 1));
                System.out.println(tasks.get(i));
            }
        }
    }

    private static void sortTasksByDate() {
        todoList.sortByDueDate();
        System.out.println("������ ������������� �� ���� ����������.");
        viewAllTasks();
    }

    private static void searchTasks() {
        System.out.println("\n--- ����� ����� ---");
        System.out.println("1. ����� �� ��������� �����");
        System.out.println("2. ����� �� ����������");
        System.out.print("������� ��� �����: ");
        int choice = getUserChoice();

        switch (choice) {
            case 1:
                System.out.print("������� �������� ����� ��� ������ � ��������/��������: ");
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
                System.out.println("�������� ����� ������.");
        }
    }

    private static void displaySearchResults(List<Task> results) {
        if (results.isEmpty()) {
            System.out.println("�� ������� �����, ��������������� ����� ���������.");
        } else {
            System.out.println("--- ���������� ������ ---");
            for (Task task : results) {
                System.out.println(task);
            }
        }
    }

    // --- ��������������� ������ ��� ����� ---

    private static LocalDate getDateInput(boolean allowEmpty) {
        while (true) {
            System.out.print("������� ���� ���������� (����-��-��): ");
            String dateString = scanner.nextLine();
            if (allowEmpty && dateString.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(dateString);
            } catch (DateTimeParseException e) {
                System.out.println("�������� ������ ����. ����������, ����������� ����-��-��.");
            }
        }
    }

    private static Priority getPriorityInput(boolean allowEmpty) {
        while (true) {
            System.out.print("������� ��������� (HIGH, MEDIUM, LOW): ");
            String priorityString = scanner.nextLine().toUpperCase();
            if (allowEmpty && priorityString.isEmpty()) {
                return null;
            }
            try {
                return Priority.valueOf(priorityString);
            } catch (IllegalArgumentException e) {
                System.out.println("�������� ���������. ����������, ������� HIGH, MEDIUM ��� LOW.");
            }
        }
    }
}