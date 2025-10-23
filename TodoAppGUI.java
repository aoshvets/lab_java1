package com.todolist; // Или org.example, в зависимости от вашей структуры

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Класс, реализующий графический пользовательский интерфейс (GUI)
 * для приложения To-Do List с использованием библиотеки Swing.
 */
public class TodoAppGUI extends JFrame {

    private final TodoList todoList;
    private final DefaultListModel<Task> listModel;
    private final JList<Task> taskList;

    public TodoAppGUI() {
        // --- 1. Установка современного Look and Feel (Nimbus) ---
        try {
            // Nimbus - хороший кроссплатформенный вариант
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            // Альтернатива: сделать приложение похожим на системное (Windows, macOS)
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- 2. Инициализация основной логики и данных ---
        this.todoList = new TodoList();
        this.listModel = new DefaultListModel<>();
        this.taskList = new JList<>(listModel);

        // --- 3. Настройка главного окна (JFrame) ---
        setTitle("To-Do List Manager");
        setSize(700, 500); // Немного увеличим размер
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- 4. Создание главной панели с отступами ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // --- 5. Создание и настройка компонентов ---
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Ваши задачи")); // Заголовок для списка

        // Улучшаем отображение элементов в списке с помощью нашего рендерера
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Создание кнопок с иконками (Unicode символы)
        JButton createTaskButton = createStyledButton("Создать", "\u2795"); // Плюс
        JButton editTaskButton = createStyledButton("Редактировать", "\u270E"); // Карандаш
        JButton deleteTaskButton = createStyledButton("Удалить", "\u2716"); // Крестик

        JPanel buttonPanel = new JPanel();
        // Используем BoxLayout для лучшего контроля над размером кнопок
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue()); // Растягиваем пространство
        buttonPanel.add(createTaskButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // Промежуток
        buttonPanel.add(editTaskButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // Промежуток
        buttonPanel.add(deleteTaskButton);
        buttonPanel.add(Box.createHorizontalGlue()); // Растягиваем пространство

        // Создание меню
        setJMenuBar(createMenuBar());

        // --- 6. Добавление компонентов на главную панель ---
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- 7. Добавление обработчиков событий ---
        createTaskButton.addActionListener(e -> createTask());
        editTaskButton.addActionListener(e -> editTask());
        deleteTaskButton.addActionListener(e -> deleteTask());

        // --- Изначальное обновление списка ---
        updateTaskList();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Опции");

        JMenuItem sortByDateItem = new JMenuItem("Сортировать по дате");
        sortByDateItem.addActionListener(e -> {
            todoList.sortByDueDate();
            updateTaskList();
        });

        JMenuItem searchItem = new JMenuItem("Найти задачу...");
        searchItem.addActionListener(e -> searchTask());

        fileMenu.add(sortByDateItem);
        fileMenu.add(searchItem);
        menuBar.add(fileMenu);

        return menuBar;
    }

    private JButton createStyledButton(String text, String unicodeIcon) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setText(unicodeIcon + " " + text);
        button.setFocusPainted(false); // Убираем рамку при фокусе
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Курсор-рука при наведении
        return button;
    }

    /**
     * Обновляет JList, синхронизируя его с данными из todoList.
     */
    private void updateTaskList() {
        listModel.clear();
        for (Task task : todoList.getAllTasks()) {
            listModel.addElement(task);
        }
    }

    /**
     * Обработчик для создания новой задачи.
     */
    private void createTask() {
        TaskDialog dialog = new TaskDialog(this, "Создать задачу", null);
        dialog.setVisible(true);

        if (dialog.isSucceeded()) {
            todoList.addTask(dialog.getTask());
            updateTaskList();
        }
    }

    /**
     * Обработчик для редактирования выбранной задачи.
     */
    private void editTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите задачу для редактирования.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task selectedTask = listModel.getElementAt(selectedIndex);
        TaskDialog dialog = new TaskDialog(this, "Редактировать задачу", selectedTask);
        dialog.setVisible(true);

        if (dialog.isSucceeded()) {
            Task updatedTask = dialog.getTask();
            todoList.editTask(selectedIndex, updatedTask.getTitle(), updatedTask.getDescription(), updatedTask.getDueDate(), updatedTask.getPriority());
            updateTaskList();
        }
    }

    /**
     * Обработчик для удаления выбранной задачи.
     */
    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите задачу для удаления.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить эту задачу?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            todoList.deleteTask(selectedIndex);
            updateTaskList();
        }
    }

    /**
     * Обработчик для поиска задач.
     */
    private void searchTask() {
        String keyword = JOptionPane.showInputDialog(this, "Введите ключевое слово для поиска:");
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Task> searchResults = todoList.searchByKeyword(keyword);
            listModel.clear();
            for (Task task : searchResults) {
                listModel.addElement(task);
            }
        } else if (keyword != null) {
            // Если пользователь нажал "ОК", но ничего не ввел, показываем снова весь список
            updateTaskList();
        }
    }

    /**
     * Точка входа для запуска GUI-приложения.
     */
    public static void main(String[] args) {
        // Swing-приложения должны запускаться в специальном потоке (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            TodoAppGUI app = new TodoAppGUI();
            app.setVisible(true);
        });
    }
}

/**
 * Вспомогательный класс для красивого отображения задач в JList.
 */
class TaskCellRenderer extends JPanel implements ListCellRenderer<Task> {

    private JLabel titleLabel;
    private JLabel detailsLabel;
    private JPanel priorityPanel;

    public TaskCellRenderer() {
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        detailsLabel = new JLabel();
        detailsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        detailsLabel.setForeground(Color.GRAY);

        priorityPanel = new JPanel();
        priorityPanel.setPreferredSize(new Dimension(10, 10));

        add(titleLabel, BorderLayout.CENTER);
        add(detailsLabel, BorderLayout.SOUTH);
        add(priorityPanel, BorderLayout.WEST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index, boolean isSelected, boolean cellHasFocus) {

        titleLabel.setText(task.getTitle());
        detailsLabel.setText("Срок: " + task.getDueDate() + " | " + task.getDescription());

        // Цветовое кодирование приоритета
        switch (task.getPriority()) {
            case HIGH:
                priorityPanel.setBackground(new Color(255, 102, 102)); // Красный
                break;
            case MEDIUM:
                priorityPanel.setBackground(new Color(255, 204, 102)); // Оранжевый
                break;
            case LOW:
                priorityPanel.setBackground(new Color(102, 204, 102)); // Зеленый
                break;
        }

        // Цвет фона при выделении
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}

/**
 * Вспомогательный класс для диалогового окна создания/редактирования задачи.
 */
class TaskDialog extends JDialog {
    private JTextField titleField = new JTextField(20);
    private JTextField descriptionField = new JTextField(20);
    private JTextField dueDateField = new JTextField(10);
    private JComboBox<Priority> priorityComboBox = new JComboBox<>(Priority.values());

    private Task task;
    private boolean succeeded = false;

    public TaskDialog(Frame owner, String title, Task taskToEdit) {
        super(owner, title, true);
        this.task = (taskToEdit == null) ? new Task("", "", LocalDate.now(), Priority.MEDIUM) : taskToEdit;

        setLayout(new BorderLayout(10, 10));
        JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldsPanel.add(new JLabel("Название:"));
        fieldsPanel.add(titleField);
        fieldsPanel.add(new JLabel("Описание:"));
        fieldsPanel.add(descriptionField);
        fieldsPanel.add(new JLabel("Срок (гггг-мм-дд):"));
        fieldsPanel.add(dueDateField);
        fieldsPanel.add(new JLabel("Приоритет:"));
        fieldsPanel.add(priorityComboBox);

        // Заполняем поля, если редактируем
        titleField.setText(this.task.getTitle());
        descriptionField.setText(this.task.getDescription());
        dueDateField.setText(this.task.getDueDate().toString());
        priorityComboBox.setSelectedItem(this.task.getPriority());

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        okButton.addActionListener(e -> onOK());
        cancelButton.addActionListener(e -> dispose());

        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack(); // Автоматически подбирает размер окна
        setLocationRelativeTo(owner);
    }

    private void onOK() {
        try {
            String title = titleField.getText();
            if (title.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Название не может быть пустым.", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
                return;
            }
            task.setTitle(title);
            task.setDescription(descriptionField.getText());
            task.setDueDate(LocalDate.parse(dueDateField.getText()));
            task.setPriority((Priority) priorityComboBox.getSelectedItem());
            succeeded = true;
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Неверный формат даты. Используйте гггг-мм-дд.", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Task getTask() {
        return task;
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}