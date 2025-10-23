package com.todolist;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class TodoList {
    private final List<Task> tasks;

    public TodoList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * ��������� ����� ������ � ������.
     * @param task ������ ��� ����������.
     */
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    /**
     * ����������� ������������ ������ �� ���������� �������.
     * @return true, ���� �������������� ������ �������, ����� false.
     */
    public boolean editTask(int index, String newTitle, String newDescription, LocalDate newDueDate, Priority newPriority) {
        if (isValidIndex(index)) {
            Task taskToEdit = tasks.get(index);
            taskToEdit.setTitle(newTitle);
            taskToEdit.setDescription(newDescription);
            taskToEdit.setDueDate(newDueDate);
            taskToEdit.setPriority(newPriority);
            return true;
        }
        return false;
    }

    /**
     * ������� ������ �� ���������� �������.
     * @param index ������ ������ ��� ��������.
     * @return true, ���� �������� ������ �������, ����� false.
     */
    public boolean deleteTask(int index) {
        if (isValidIndex(index)) {
            tasks.remove(index);
            return true;
        }
        return false;
    }

    /**
     * ���������� ������ ���� �����.
     * @return ������ �����.
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks); // ���������� ����� ��� �������������� ������� ���������
    }

    /**
     * ��������� ������ �� ����� ����������.
     */
    public void sortByDueDate() {
        tasks.sort(Comparator.comparing(Task::getDueDate));
    }

    /**
     * ���� ������, ���������� ������������ �������� ����� � �������� ��� ��������.
     * @param keyword �������� ����� ��� ������.
     * @return ������ ��������� �����.
     */
    public List<Task> searchByKeyword(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(lowerCaseKeyword) ||
                        task.getDescription().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toList());
    }

    /**
     * ���� ������ � ������������ �����������.
     * @param priority ��������� ��� ������.
     * @return ������ ��������� �����.
     */
    public List<Task> searchByPriority(Priority priority) {
        return tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    /**
     * ���������, �������� �� ������ ������ ���������� ��� ������ �����.
     * @param index ������ ��� ��������.
     * @return true, ���� ������ ������������, ����� false.
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}