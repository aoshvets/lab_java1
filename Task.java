package com.todolist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Представляет одну задачу в списке дел.
 * Этот класс инкапсулирует все свойства задачи,
 * такие как название, описание, срок выполнения и приоритет.
 */
public class Task {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;

    // Форматтер для единообразного отображения даты
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Task(String title, String description, LocalDate dueDate, Priority priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    // Геттеры
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

    // Сеттеры
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
        return "Название: " + title + "\n" +
                "  Описание: " + description + "\n" +
                "  Срок выполнения: " + dueDate.format(DATE_FORMATTER) + "\n" +
                "  Приоритет: " + priority + "\n";
    }
}