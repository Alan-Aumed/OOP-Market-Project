import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.*;

public class ProductsPanel extends JFrame {
    private JLabel productCountLabel;
    private DefaultTableModel tableModel;
    private JTable productsTable;

    public ProductsPanel() {
        setTitle("بەشی نیشاندانی کاڵایەکان");
        setSize(800, 600); // Set window size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize table model with reversed column order
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ژمارە"); // Add columns in reversed order
        tableModel.addColumn("نرخ");
        tableModel.addColumn("ناو");
        tableModel.addColumn("ناوی بڕاند");
        tableModel.addColumn("باڕکۆد");

        // Products table
        productsTable = new JTable(tableModel);
        productsTable.setRowHeight(50); // Set row height for better visibility

        // Customize table rendering to add horizontal separating lines
        productsTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        // Set header alignment to center
        JTableHeader header = productsTable.getTableHeader();
        header.setDefaultRenderer(new CustomHeaderRenderer(header.getDefaultRenderer()));

        // Scroll pane for products table
        JScrollPane scrollPane = new JScrollPane(productsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Product count label
        productCountLabel = new JLabel("ژمارەی کاڵایەکان: 0");
        productCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(productCountLabel, BorderLayout.NORTH);

        // Load products data into the table
        loadProductsFromFile();

        // Center the window on the screen
        setLocationRelativeTo(null);
    }

    private void loadProductsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("products.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) { // Ensure correct format
                    String id = data[0];
                    String name = data[1];
                    String price = data[2];
                    String quantity = data[3];
                    String description = data[4];

                    // Add product data to the table model (in reversed order)
                    tableModel.addRow(new String[]{quantity, price, description, name, id});
                }
            }
            updateProductCountLabel(tableModel.getRowCount()); // Update product count label
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "هەڵە ڕوویدا لە کاتی هێنانی داتایەکان لە فایل.");
        }
    }

    private void updateProductCountLabel(int count) {
        productCountLabel.setText("ژمارەی کاڵایەکان: " + count);
    }

    // Custom table cell renderer to add horizontal separating lines
    private static class CustomTableCellRenderer extends DefaultTableCellRenderer {
        public CustomTableCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER); // Align text to the center
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Set bottom border for all cells except the last row
            if (row < table.getRowCount() - 1) {
                ((JComponent) c).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
            } else {
                ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // No border for last row
            }

            return c;
        }
    }

    // Custom table header renderer to align header text to the center
    private static class CustomHeaderRenderer implements TableCellRenderer {
        private final TableCellRenderer defaultRenderer;

        public CustomHeaderRenderer(TableCellRenderer defaultRenderer) {
            this.defaultRenderer = defaultRenderer;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER); // Align header text to the center
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set the look and feel to Nimbus (modern and nicer appearance)
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ProductsPanel productsPanel = new ProductsPanel();
            productsPanel.setVisible(true);
        });
    }
}