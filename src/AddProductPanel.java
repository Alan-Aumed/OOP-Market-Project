import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class AddProductPanel extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextArea descriptionArea;

    public AddProductPanel() {
        setTitle("زیادکردنی کاڵا");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("باڕکۆد:"), gbc);

        idField = new JTextField(generateUniqueID());
        idField.setEditable(true);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(idField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("ناوی بڕاند:"), gbc);

        nameField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("نرخ:"), gbc);

        priceField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(priceField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("بڕی بەردەست:"), gbc);

        quantityField = new JTextField(5);
        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(quantityField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("ناوی کاڵا:"), gbc);

        descriptionArea = new JTextArea(2,2);
        descriptionArea.setLineWrap(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(descriptionScrollPane, gbc);

        panel.add(inputPanel, BorderLayout.CENTER);

        JButton addButton = new JButton("زیادکردنی کاڵا");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
        panel.add(addButton, BorderLayout.SOUTH);

        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private void addProduct() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String priceStr = priceField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            showMessage("تکایە هەموو خانەکان پڕ بکەوە");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            saveProductToFile(id, name, price, quantity, description);

            nameField.setText("");
            priceField.setText("");
            quantityField.setText("");
            descriptionArea.setText("");

            idField.setText(generateUniqueID());

            showMessage("کاڵایەکە بە سەرکەوتووی زیاد کرا");
        } catch (NumberFormatException ex) {
            showMessage("تکایە نرخ و بڕی گونجاو دیاری بکە!");
        }
    }

    private void saveProductToFile(String id, String name, double price, int quantity, String description) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("products.txt", true))) {
            writer.println(id + "," + name + "," + price + "," + quantity + "," + description);
        } catch (IOException e) {
            showMessage("هەڵە لە زیادکردنی کاڵا ڕووی دا");
        }
    }

    private String generateUniqueID() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddProductPanel addProductPanel = new AddProductPanel();
            addProductPanel.setVisible(true);
        });
    }
}
