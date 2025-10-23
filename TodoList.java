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
     * Добавляет новую задачу в список.
     * @param task Задача для добавления.
     */
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    /**
     * Редактирует существующую задачу по указанному индексу.
     * @return true, если редактирование прошло успешно, иначе false.
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
     * Удаляет задачу по указанному индексу.
     * @param index Индекс задачи для удаления.
     * @return true, если удаление прошло успешно, иначе false.
     */
    public boolean deleteTask(int index) {
        if (isValidIndex(index)) {
            tasks.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Возвращает список всех задач.
     * @return Список задач.
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks); // Возвращаем копию для предотвращения внешних изменений
    }

    /**
     * Сортирует задачи по сроку выполнения.
     */
    public void sortByDueDate() {
        tasks.sort(Comparator.comparing(Task::getDueDate));
    }

    /**
     * Ищет задачи, содержащие определенное ключевое слово в названии или описании.
     * @param keyword Ключевое слово для поиска.
     * @return Список найденных задач.
     */
    public List<Task> searchByKeyword(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(lowerCaseKeyword) ||
                        task.getDescription().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Ищет задачи с определенным приоритетом.
     * @param priority Приоритет для поиска.
     * @return Список найденных задач.
     */
    public List<Task> searchByPriority(Priority priority) {
        return tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, является ли данный индекс допустимым для списка задач.
     * @param index Индекс для проверки.
     * @return true, если индекс действителен, иначе false.
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}