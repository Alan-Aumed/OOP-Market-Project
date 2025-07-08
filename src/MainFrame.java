import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("سوپەرمارکێتی فامیلی");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JButton cashierButton = createImageButton("Image/Cashier.jpg", "Cashier");
        JButton customerButton = createImageButton("Image/Customer.jpg", "Customer");

        cashierButton.addActionListener(e -> openCashierFrame());
        customerButton.addActionListener(e -> openCustomerFrame());

        buttonPanel.add(cashierButton, gbc);
        gbc.gridy++;
        buttonPanel.add(customerButton, gbc);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createImageButton(String imagePath, String buttonText) {
        JButton button = new JButton();
        button.setText(buttonText);
        ImageIcon icon = createImageIcon(imagePath);
        if (icon != null) {
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        }
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        return button;
    }

    private ImageIcon createImageIcon(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                BufferedImage img = ImageIO.read(file);
                return new ImageIcon(img);
            } catch (IOException e) {
                System.err.println("Unable to read the image file: " + file.getAbsolutePath());
                e.printStackTrace();
                return null;
            }
        } else {
            System.err.println("File not found: " + file.getAbsolutePath());
            return null;
        }
    }

    private void openCashierFrame() {
        JFrame cashierFrame = new JFrame("سیستەمی کاشێر");
        cashierFrame.setSize(300, 200);
        cashierFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cashierFrame.setLayout(new GridLayout(4, 1));
        cashierFrame.add(createButton("زیادکردنی کاڵا", e -> new AddProductPanel().setVisible(true)));
        cashierFrame.add(createButton("لابردنی کاڵا", e -> new RemoveProductPanel().setVisible(true)));
        cashierFrame.add(createButton("کۆگا", e -> new ProductsPanel().setVisible(true)));
        cashierFrame.add(createButton("فرۆشتراوەکان", e -> new Report().setVisible(true)));
        cashierFrame.setLocationRelativeTo(this);
        cashierFrame.setVisible(true);
    }

    private void openCustomerFrame() {
        JFrame customerFrame = new JFrame("سیستەمی کڕیار");
        customerFrame.setSize(300, 100);
        customerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        customerFrame.setLocationRelativeTo(this);
        customerFrame.setVisible(true);
        customerFrame.add(createButton("سەبەتە", e -> new BillingPanel().setVisible(true)));
        customerFrame.setVisible(true);
    }

    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.addActionListener(action);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}