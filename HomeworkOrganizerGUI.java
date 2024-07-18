import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class HomeworkOrganizerGUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField;
    private JTextField dueDateField;
    private List<Homework> homeworkList;

    public HomeworkOrganizerGUI() {
        setTitle("Homework Organizer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a panel to hold the table and input fields
        JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel);

        // Create the table with four columns
        model = new DefaultTableModel();
        model.addColumn("Name");
        model.addColumn("Due Date");
        model.addColumn("Days Left");
        model.addColumn("Delete");
        table = new JTable(model) {
            // Disable editing for all cells
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create a panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));

        // Name field
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);

        // Due Date field
        JLabel dueDateLabel = new JLabel("Due Date (MM/DD/YYYY):");
        dueDateField = new JTextField();
        inputPanel.add(dueDateLabel);
        inputPanel.add(dueDateField);

        // Add button to add homework
        JButton addButton = new JButton("Add Homework");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addHomework();
            }
        });
        inputPanel.add(addButton);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Initialize homework list
        homeworkList = new ArrayList<>();

        // Load data from file
        loadDataFromFile();

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    deleteRow();
                }
            }
        });

        // Add a listener to handle delete button clicks
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.getColumnModel().getColumnIndex("Delete");
                int row = table.rowAtPoint(e.getPoint());
                if (column >= 0 && row >= 0 && table.columnAtPoint(e.getPoint()) == column) {
                    deleteRow();
                }
            }
        });
    }

    private void addHomework() {
        String name = nameField.getText();
        String dueDateString = dueDateField.getText();

        // Convert due date string to Date object
        Date dueDate = null;
        try {
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM.dd.yyyy");

            // Try parsing with the first format
            try {
                dueDate = dateFormat1.parse(dueDateString);
            } catch (ParseException e1) {
                // If parsing with the first format fails, try the second format
                dueDate = dateFormat2.parse(dueDateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Invalid due date format. Please use MM/DD/YYYY or MM.DD.YYYY.");
            return;
        }

        // Calculate the number of days left
        long daysLeft = calculateDaysLeft(dueDate);

        // Add homework to list
        Homework homework = new Homework(name, dueDate, daysLeft);
        homeworkList.add(homework);

        // Add homework to table
        model.addRow(new Object[]{name, dueDate, daysLeft, "Delete"});

        // Sort table by due date
        sortTableByDueDate();

        // Clear input fields
        nameField.setText("");
        dueDateField.setText("");

        // Save data to file
        saveDataToFile();
    }

    private void deleteRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
            homeworkList.remove(selectedRow); // Remove corresponding homework from list
            saveDataToFile(); // Save data to file after deletion
        }
    }

    private long calculateDaysLeft(Date dueDate) {
        Date currentDate = new Date();
        long diffInMillies = dueDate.getTime() - currentDate.getTime();
        return diffInMillies / (1000 * 60 * 60 * 24);
    }

    private void sortTableByDueDate() {
        // Get the number of rows in the table
        int rowCount = model.getRowCount();

        // Create a list to hold the Homework objects
        List<Homework> sortedHomeworkList = new ArrayList<>();

        // Populate the list with Homework objects from the table
        for (int i = 0; i < rowCount; i++) {
            String name = (String) model.getValueAt(i, 0);
            Date dueDate = (Date) model.getValueAt(i, 1);
            long daysLeft = (long) model.getValueAt(i, 2);
            Homework homework = new Homework(name, dueDate, daysLeft);
            sortedHomeworkList.add(homework);
        }

        // Sort the list by due date using a Comparator
        Collections.sort(sortedHomeworkList, Comparator.comparing(Homework::getDueDate));

        // Clear the table
        model.setRowCount(0);

        // Add sorted Homework objects back to the table
        for (Homework homework : sortedHomeworkList) {
            model.addRow(new Object[]{homework.getName(), homework.getDueDate(), homework.getDaysLeft(), "Delete"});
        }
    }

    private void saveDataToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("homework.txt"))) {
            for (Homework homework : homeworkList) {
                writer.println(homework.getName() + "," + homework.getDueDate().getTime());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromFile() {
        File file = new File("homework.txt");
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    String name = parts[0];
                    long timeMillis = Long.parseLong(parts[1]);
                    Date dueDate = new Date(timeMillis);
                    long daysLeft = calculateDaysLeft(dueDate);
                    Homework homework = new Homework(name, dueDate, daysLeft);
                    homeworkList.add(homework);
                    model.addRow(new Object[]{name, dueDate, daysLeft, "Delete"});
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}