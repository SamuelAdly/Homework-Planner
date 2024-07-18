import javax.swing.*;
public class Main {
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        HomeworkOrganizerGUI gui = new HomeworkOrganizerGUI();
        gui.setVisible(true);
    });
}
}