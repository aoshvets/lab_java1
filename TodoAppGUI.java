package com.todolist; // ��� org.example, � ����������� �� ����� ���������

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * �����, ����������� ����������� ���������������� ��������� (GUI)
 * ��� ���������� To-Do List � �������������� ���������� Swing.
 */
public class TodoAppGUI extends JFrame {

    private final TodoList todoList;
    private final DefaultListModel<Task> listModel;
    private final JList<Task> taskList;

    public TodoAppGUI() {
        // --- 1. ��������� ������������ Look and Feel (Nimbus) ---
        try {
            // Nimbus - ������� ������������������ �������
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            // ������������: ������� ���������� ������� �� ��������� (Windows, macOS)
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- 2. ������������� �������� ������ � ������ ---
        this.todoList = new TodoList();
        this.listModel = new DefaultListModel<>();
        this.taskList = new JList<>(listModel);

        // --- 3. ��������� �������� ���� (JFrame) ---
        setTitle("To-Do List Manager");
        setSize(700, 500); // ������� �������� ������
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- 4. �������� ������� ������ � ��������� ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // --- 5. �������� � ��������� ����������� ---
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("���� ������")); // ��������� ��� ������

        // �������� ����������� ��������� � ������ � ������� ������ ���������
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // �������� ������ � �������� (Unicode �������)
        JButton createTaskButton = createStyledButton("�������", "\u2795"); // ����
        JButton editTaskButton = createStyledButton("�������������", "\u270E"); // ��������
        JButton deleteTaskButton = createStyledButton("�������", "\u2716"); // �������

        JPanel buttonPanel = new JPanel();
        // ���������� BoxLayout ��� ������� �������� ��� �������� ������
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue()); // ����������� ������������
        buttonPanel.add(createTaskButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // ����������
        buttonPanel.add(editTaskButton);
        buttonPanel.add(Box.createHorizontalStrut(10)); // ����������
        buttonPanel.add(deleteTaskButton);
        buttonPanel.add(Box.createHorizontalGlue()); // ����������� ������������

        // �������� ����
        setJMenuBar(createMenuBar());

        // --- 6. ���������� ����������� �� ������� ������ ---
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- 7. ���������� ������������ ������� ---
        createTaskButton.addActionListener(e -> createTask());
        editTaskButton.addActionListener(e -> editTask());
        deleteTaskButton.addActionListener(e -> deleteTask());

        // --- ����������� ���������� ������ ---
        updateTaskList();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("�����");

        JMenuItem sortByDateItem = new JMenuItem("����������� �� ����");
        sortByDateItem.addActionListener(e -> {
            todoList.sortByDueDate();
            updateTaskList();
        });

        JMenuItem searchItem = new JMenuItem("����� ������...");
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
        button.setFocusPainted(false); // ������� ����� ��� ������
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // ������-���� ��� ���������
        return button;
    }

    /**
     * ��������� JList, ������������� ��� � ������� �� todoList.
     */
    private void updateTaskList() {
        listModel.clear();
        for (Task task : todoList.getAllTasks()) {
            listModel.addElement(task);
        }
    }

    /**
     * ���������� ��� �������� ����� ������.
     */
    private void createTask() {
        TaskDialog dialog = new TaskDialog(this, "������� ������", null);
        dialog.setVisible(true);

        if (dialog.isSucceeded()) {
            todoList.addTask(dialog.getTask());
            updateTaskList();
        }
    }

    /**
     * ���������� ��� �������������� ��������� ������.
     */
    private void editTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "����������, �������� ������ ��� ��������������.", "������", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task selectedTask = listModel.getElementAt(selectedIndex);
        TaskDialog dialog = new TaskDialog(this, "������������� ������", selectedTask);
        dialog.setVisible(true);

        if (dialog.isSucceeded()) {
            Task updatedTask = dialog.getTask();
            todoList.editTask(selectedIndex, updatedTask.getTitle(), updatedTask.getDescription(), updatedTask.getDueDate(), updatedTask.getPriority());
            updateTaskList();
        }
    }

    /**
     * ���������� ��� �������� ��������� ������.
     */
    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "����������, �������� ������ ��� ��������.", "������", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "�� �������, ��� ������ ������� ��� ������?", "�������������", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            todoList.deleteTask(selectedIndex);
            updateTaskList();
        }
    }

    /**
     * ���������� ��� ������ �����.
     */
    private void searchTask() {
        String keyword = JOptionPane.showInputDialog(this, "������� �������� ����� ��� ������:");
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Task> searchResults = todoList.searchByKeyword(keyword);
            listModel.clear();
            for (Task task : searchResults) {
                listModel.addElement(task);
            }
        } else if (keyword != null) {
            // ���� ������������ ����� "��", �� ������ �� ����, ���������� ����� ���� ������
            updateTaskList();
        }
    }

    /**
     * ����� ����� ��� ������� GUI-����������.
     */
    public static void main(String[] args) {
        // Swing-���������� ������ ����������� � ����������� ������ (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            TodoAppGUI app = new TodoAppGUI();
            app.setVisible(true);
        });
    }
}

/**
 * ��������������� ����� ��� ��������� ����������� ����� � JList.
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
        detailsLabel.setText("����: " + task.getDueDate() + " | " + task.getDescription());

        // �������� ����������� ����������
        switch (task.getPriority()) {
            case HIGH:
                priorityPanel.setBackground(new Color(255, 102, 102)); // �������
                break;
            case MEDIUM:
                priorityPanel.setBackground(new Color(255, 204, 102)); // ���������
                break;
            case LOW:
                priorityPanel.setBackground(new Color(102, 204, 102)); // �������
                break;
        }

        // ���� ���� ��� ���������
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
 * ��������������� ����� ��� ����������� ���� ��������/�������������� ������.
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

        fieldsPanel.add(new JLabel("��������:"));
        fieldsPanel.add(titleField);
        fieldsPanel.add(new JLabel("��������:"));
        fieldsPanel.add(descriptionField);
        fieldsPanel.add(new JLabel("���� (����-��-��):"));
        fieldsPanel.add(dueDateField);
        fieldsPanel.add(new JLabel("���������:"));
        fieldsPanel.add(priorityComboBox);

        // ��������� ����, ���� �����������
        titleField.setText(this.task.getTitle());
        descriptionField.setText(this.task.getDescription());
        dueDateField.setText(this.task.getDueDate().toString());
        priorityComboBox.setSelectedItem(this.task.getPriority());

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("������");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        okButton.addActionListener(e -> onOK());
        cancelButton.addActionListener(e -> dispose());

        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack(); // ������������� ��������� ������ ����
        setLocationRelativeTo(owner);
    }

    private void onOK() {
        try {
            String title = titleField.getText();
            if (title.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "�������� �� ����� ���� ������.", "������ ���������", JOptionPane.ERROR_MESSAGE);
                return;
            }
            task.setTitle(title);
            task.setDescription(descriptionField.getText());
            task.setDueDate(LocalDate.parse(dueDateField.getText()));
            task.setPriority((Priority) priorityComboBox.getSelectedItem());
            succeeded = true;
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "�������� ������ ����. ����������� ����-��-��.", "������ ���������", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Task getTask() {
        return task;
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}