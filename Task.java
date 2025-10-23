package com.todolist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * ������������ ���� ������ � ������ ���.
 * ���� ����� ������������� ��� �������� ������,
 * ����� ��� ��������, ��������, ���� ���������� � ���������.
 */
public class Task {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;

    // ��������� ��� �������������� ����������� ����
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Task(String title, String description, LocalDate dueDate, Priority priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    // �������
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    // �������
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "��������: " + title + "\n" +
                "  ��������: " + description + "\n" +
                "  ���� ����������: " + dueDate.format(DATE_FORMATTER) + "\n" +
                "  ���������: " + priority + "\n";
    }
}