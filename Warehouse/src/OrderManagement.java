import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderManagement {
    private PrintWriter out;
    private BufferedReader in;

    public OrderManagement(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void loadOrders(DefaultTableModel model) {
        try {
            out.println("LOAD_ORDER");
            model.setRowCount(0);

            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                String[] data = response.split(",");
                model.addRow(new Object[]{data[0], data[1], data[2], data[3]});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки категорий: " + e.getMessage());
        }
    }

    public void addOrder(DefaultTableModel model) {

        JComboBox<String> suppliersCombo = new JComboBox<>();

        try {
            out.println("GET_SUPPLIERS");
            String response;
            suppliersCombo.removeAllItems();

            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                if (response.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ошибка: Пустой ответ от сервера.");
                    continue;
                }
                suppliersCombo.addItem(response.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки : " + e.getMessage());
        }

         
        JTextField dateField = new JTextField();  

         
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"В обработке", "Принят", "Отменен"});

         
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Поставщик:"));
        panel.add(suppliersCombo);
        panel.add(new JLabel("Дата заказа (yyyy-mm-dd):"));
        panel.add(dateField);
        panel.add(new JLabel("Статус:"));
        panel.add(statusCombo);

         
        int result = JOptionPane.showConfirmDialog(
                null, panel, "Добавить заказ",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String supplierId = (String) suppliersCombo.getSelectedItem();
            String orderDate = dateField.getText().trim();
            String status = (String) statusCombo.getSelectedItem();

             
            if (supplierId == null || orderDate.isEmpty() || status == null) {
                JOptionPane.showMessageDialog(null, "Пожалуйста, заполните все поля.");
                return;
            }

            if (!isValidDate(orderDate)) {
                JOptionPane.showMessageDialog(null, "Некорректный формат даты. Используйте YYYY-MM-DD.");
                return;
            }

            try {
                out.println("ADD_ORDER," + supplierId + "," + orderDate + "," + status);
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadOrders(model);  
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void editOrder(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите заказ для редактирования");
            return;
        }

         
        String orderId = model.getValueAt(selectedRow, 0).toString();
        String currentSupplierId = model.getValueAt(selectedRow, 1).toString();
        String currentOrderDate = model.getValueAt(selectedRow, 2).toString();
        String currentStatus = model.getValueAt(selectedRow, 3).toString();

         
        JComboBox<String> supplierCombo = new JComboBox<>();
        try {
            out.println("GET_SUPPLIERS");
            String response;
            supplierCombo.removeAllItems();

            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                if (response.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ошибка: Пустой ответ от сервера.");
                    continue;
                }
                supplierCombo.addItem(response.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки поставщиков: " + e.getMessage());
        }
        supplierCombo.setSelectedItem(currentSupplierId);

         
        JTextField dateField = new JTextField(currentOrderDate);  

         
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"В обработке", "Принят", "Отменен"});
        statusCombo.setSelectedItem(currentStatus);

         
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Поставщик:"));
        panel.add(supplierCombo);
        panel.add(new JLabel("Дата заказа (yyyy-mm-dd):"));
        panel.add(dateField);
        panel.add(new JLabel("Статус:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(null, panel, "Редактировать заказ", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String supplierId = (String) supplierCombo.getSelectedItem();
            String orderDate = dateField.getText().trim();
            String status = (String) statusCombo.getSelectedItem();

             
            if (supplierId == null || orderDate.isEmpty() || status == null) {
                JOptionPane.showMessageDialog(null, "Пожалуйста, заполните все поля.");
                return;
            }

            if (!isValidDate(orderDate)) {
                JOptionPane.showMessageDialog(null, "Некорректный формат даты. Используйте YYYY-MM-DD.");
                return;
            }

            try {
                out.println("EDIT_ORDER," + orderId + "," + supplierId + "," + orderDate + "," + status);
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadOrders(model);  
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }
    public void deleteOrder(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите заказ для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Вы уверены, что хотите удалить этот заказ?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                out.println("DELETE_ORDER," + model.getValueAt(selectedRow, 0));
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadOrders(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);  
        try {
            Date parsedDate = sdf.parse(date);
            return true;  
        } catch (ParseException e) {
            return false;  
        }
    }
}
