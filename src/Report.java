import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Report extends JFrame {
    private JLabel salesCountLabel;
    private DefaultTableModel salesTableModel;
    private JTable salesTable;

    public Report() {
        setTitle("بەشی فرۆشتراوەکان");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize table model with reversed column order
        salesTableModel = new DefaultTableModel();
        salesTableModel.addColumn("بەرواو");
        salesTableModel.addColumn("بڕ");
        salesTableModel.addColumn("کۆی گشتی");
        salesTableModel.addColumn("ناوی کاڵا");

        // Sales table
        salesTable = new JTable(salesTableModel);
        salesTable.setRowHeight(50); // Set row height for better visibility

        // Customize table cell renderer to add horizontal separating lines
        salesTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        // Set header alignment to center
        JTableHeader header = salesTable.getTableHeader();
        header.setDefaultRenderer(new CustomHeaderRenderer(header.getDefaultRenderer()));

        JScrollPane scrollPane = new JScrollPane(salesTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel northPanel = new JPanel();
        salesCountLabel = new JLabel("ژمارەی فرۆشی 0");
        salesCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        northPanel.add(salesCountLabel);
        add(northPanel, BorderLayout.NORTH);

        loadSalesData();

        setLocationRelativeTo(null);
    }

    private void loadSalesData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("cart.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1) {
                    String date = parts[parts.length - 1];
                    for (int i = 0; i < parts.length - 1; i++) {
                        String[] itemDetails = parts[i].split(":");
                        if (itemDetails.length >= 3) {
                            String productName = itemDetails[0];
                            String totalPrice = itemDetails[1];
                            String quantity = itemDetails[2];
                            salesTableModel.addRow(new Object[]{date, totalPrice, quantity, productName});
                        }
                    }
                }
            }
            updateSalesCountLabel(salesTableModel.getRowCount());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "هەڵە لە خوێندنی فایل: " + e.getMessage(), "هەڵە", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSalesCountLabel(int count) {
        salesCountLabel.setText("ژمارەی فرۆشی " + count);
    }

    // Custom table cell renderer to add horizontal separating lines
    private static class CustomTableCellRenderer extends DefaultTableCellRenderer {
        public CustomTableCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (row < table.getRowCount() - 1) {
                ((JComponent) c).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
            } else {
                ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            }

            return c;
        }
    }

    // Custom table header renderer to align header text to center
    private static class CustomHeaderRenderer implements TableCellRenderer {
        private final TableCellRenderer defaultRenderer;

        public CustomHeaderRenderer(TableCellRenderer defaultRenderer) {
            this.defaultRenderer = defaultRenderer;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Report().setVisible(true);
        });
    }
}
