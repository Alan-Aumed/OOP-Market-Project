import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BillingPanel extends JFrame {
    private Map<String, Float> products = new HashMap<>();
    private Map<String, Integer> cart = new HashMap<>();
    private JTable cartTable;
    private JLabel totalLabel;
    private JButton checkoutButton;
    private float total = 0;

    private DefaultTableModel cartTableModel;

    public BillingPanel() {
        setTitle("بەشی فرۆشتن");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set Right-to-Left layout
        setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        String[] columnNames = {" ", " ", "بڕ", "نرخ", "ناوی کاڵا"};
        cartTableModel = new DefaultTableModel(columnNames, 0);
        cartTable = new JTable(cartTableModel);

        // Set column "Qty" to use spinner for quantity selection
        TableColumn qtyColumn = cartTable.getColumn("بڕ");
        qtyColumn.setCellEditor(new SpinnerEditor(new JSpinner()));

     // Set column "زیادکردن" to use button for adding items using index
        TableColumn addColumn = cartTable.getColumnModel().getColumn(1); // Assuming "زیادکردن" is at index 1
        addColumn.setCellRenderer(new ButtonRenderer());
        addColumn.setCellEditor(new ButtonEditor(new JCheckBox(), "زیادکردن"));

        // Set column "لادان" to use button for removing items using index
        TableColumn removeColumn = cartTable.getColumnModel().getColumn(0); // Assuming "لادان" is at index 0
        removeColumn.setCellRenderer(new ButtonRenderer());
        removeColumn.setCellEditor(new ButtonEditor(new JCheckBox(), "لادان"));


        JScrollPane scrollPane = new JScrollPane(cartTable);
        add(scrollPane, BorderLayout.CENTER);

        totalLabel = new JLabel("کۆی گشتی: $" + total);
        add(totalLabel, BorderLayout.SOUTH);

        checkoutButton = new JButton("فرۆشتن");
        add(checkoutButton, BorderLayout.NORTH);

        loadProductsFromTxt("products.txt");

        checkoutButton.addActionListener(e -> checkout());
    }

    private void loadProductsFromTxt(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String productName = parts[1];
                    float price = Float.parseFloat(parts[2]);
                    int quantity = Integer.parseInt(parts[3]);
                    products.put(productName, price);
                    addProductToTable(productName, String.valueOf(price));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addProductToTable(String name, String price) {
        cartTableModel.addRow(new Object[]{"لادان", "زیادکردن", 0, price, name});
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            this.label = label;
            button = new JButton(label);
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                String productName = cartTableModel.getValueAt(cartTable.getSelectedRow(), 4).toString();
                if ("زیادکردن".equals(label)) {
                    int quantity = Integer.parseInt(cartTableModel.getValueAt(cartTable.getSelectedRow(), 2).toString());
                    addToCart(productName, quantity);
                } else if ("لادان".equals(label)) {
                    removeFromCart(productName);
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private JSpinner spinner;

        public SpinnerEditor(JSpinner spinner) {
            this.spinner = spinner;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                     int row, int column) {
            spinner.setValue(value);
            return spinner;
        }

        public Object getCellEditorValue() {
            return spinner.getValue();
        }
    }

    private void addToCart(String productName, int quantity) {
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "تکایە بڕی زیاتر لە 0 هەڵبژێرە بۆ زیادکردنی لە سەبەتەکە.");
            return;
        }
        float availableQuantity = products.get(productName);
        if (quantity > availableQuantity) {
            JOptionPane.showMessageDialog(this, "ئەو بڕە زۆرە لەبەردەستدا نییە.");
            return;
        }

        cart.merge(productName, quantity, Integer::sum);
        updateTotal();
    }

    private void removeFromCart(String productName) {
        if (!cart.containsKey(productName) || cart.get(productName) == 0) {
            JOptionPane.showMessageDialog(this, "هیچ برێک هیچ برێک و کاڵایەک دیاری نەکراوە");
            return;
        }
        cart.remove(productName);
        updateTotal();
        JOptionPane.showMessageDialog(this, productName + " لە سەبەتە دەرهێنرا.");
    }

    private void updateTotal() {
        total = 0;
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();
            float price = products.get(productName);
            total += price * quantity;
        }
        totalLabel.setText("کۆی گشتی: $" + total);
    }

    private void checkout() {
        try {
            FileWriter writer = new FileWriter("cart.txt", true);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = dateFormat.format(new Date());

            StringBuilder checkoutLine = new StringBuilder();

            for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                String productName = entry.getKey();
                int quantity = entry.getValue();
                float price = products.get(productName);

                checkoutLine.append(productName).append(":").append(quantity).append(":").append(price * quantity).append(",");
            }

            if (checkoutLine.length() > 0) {
                checkoutLine.setLength(checkoutLine.length() - 1);
            }

            writer.write(checkoutLine.toString() + "," + dateStr + "\n");

            writer.close();
            cart.clear();
            totalLabel.setText("کۆی گشتی :$0.00");
            JOptionPane.showMessageDialog(this, "بەسەرکەتووی فرۆشترا,کۆی گشتی $: " + total);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BillingPanel().setVisible(true));
    }
}
