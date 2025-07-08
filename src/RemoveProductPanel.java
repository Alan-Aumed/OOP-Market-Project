import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveProductPanel extends JFrame {
    private JPanel contentPane;
    private List<String> lines = new ArrayList<>(); // Hold all lines from the file

    public RemoveProductPanel() {
        setTitle("لابردنی کاڵا");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new GridLayout(10, 10, 10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        loadProductsFromFile();

        JScrollPane scrollPane = new JScrollPane(contentPane,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void loadProductsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line); // Save line into the list
                String[] data = line.split(",");
                if (data.length == 5) {
                    String id = data[0];
                    String name = data[1];
                    JPanel productPanel = createProductPanel(id, name);
                    contentPane.add(productPanel);
                }
            }
            if (contentPane.getComponentCount() == 0) {
                JOptionPane.showMessageDialog(this, "هیچ کاڵایەک نەدۆزراوە");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "هەڵە ڕوویدا لە کاتی هێنانی داتایەکان لە فایل.");
        }
    }

    private JPanel createProductPanel(String id, String name) {
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        nameLabel.setPreferredSize(new Dimension(200, 50));
        nameLabel.setMaximumSize(nameLabel.getPreferredSize());
        productPanel.add(nameLabel, BorderLayout.CENTER);

        JButton removeButton = new JButton("سڕینەوە");
        removeButton.setBackground(Color.RED);
        removeButton.setForeground(Color.WHITE);
        removeButton.setOpaque(true);
        removeButton.setBorderPainted(false);
        removeButton.setFocusPainted(false);
        removeButton.setPreferredSize(new Dimension(100, 50));
        removeButton.setMaximumSize(removeButton.getPreferredSize());
        removeButton.addActionListener(e -> removeProduct(id, productPanel));
        productPanel.add(removeButton, BorderLayout.EAST);

        return productPanel;
    }

    private void removeProduct(String id, JPanel productPanel) {
        contentPane.remove(productPanel);
        contentPane.revalidate();
        contentPane.repaint();
        lines = lines.stream().filter(line -> !line.startsWith(id + ",")).collect(Collectors.toList());
        try {
            Files.write(Paths.get("products.txt"), lines);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to update file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RemoveProductPanel removeProductPanel = new RemoveProductPanel();
            removeProductPanel.setVisible(true);
        });
    }
}
