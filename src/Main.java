import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        try {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}