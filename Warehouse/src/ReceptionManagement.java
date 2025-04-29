import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ReceptionManagement {
    private PrintWriter out;
    private BufferedReader in;

    public ReceptionManagement(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void loadReception(DefaultTableModel model) {
        try {
            out.println("LOAD_RECEPTION");
            model.setRowCount(0);

            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                String[] data = response.split(",");
                model.addRow(new Object[]{data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8]});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки: " + e.getMessage());
        }
    }


    public void addReception(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();  
        JTextField volumeField = new JTextField();  
        JComboBox<String> unitCombo = new JComboBox<>(new String[] {"шт", "уп", "литр", "поддон"});
        JComboBox<String> categoryCombo = new JComboBox<>();
        JComboBox<String> warehouseCombo = new JComboBox<>();  

        try {
             
            out.println("GET_CATEGORIES");
            String response;
            categoryCombo.removeAllItems();  
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                categoryCombo.addItem(response.trim());
            }

             
            out.println("GET_WAREHOUSES");
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                warehouseCombo.addItem(response.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки: " + e.getMessage());
        }

         
        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Название:"));
        panel.add(nameField);
        panel.add(new JLabel("Категория:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Склад:"));
        panel.add(warehouseCombo);
        panel.add(new JLabel("Цена:"));
        panel.add(priceField);
        panel.add(new JLabel("Количество:"));
        panel.add(quantityField);
        panel.add(new JLabel("Общий объем(м^3):"));
        panel.add(volumeField);
        panel.add(new JLabel("Единица измерения:"));
        panel.add(unitCombo);
         
        int result = JOptionPane.showConfirmDialog(
                null, panel, "Добавить товар",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {

            try {
                 
                out.println("CREATE_RECEPTION_TRANSACTION");
                String transactionResponse = in.readLine();
                int transactionId = Integer.parseInt(transactionResponse);  

                 
                while (true) {
                     
                    int productResult = JOptionPane.showConfirmDialog(
                            null, panel, "Добавить товар в транзакцию (или отмените для завершения)",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (productResult == JOptionPane.CANCEL_OPTION) {
                        break;  
                    }

                    String name = nameField.getText().trim();
                    String category = (String) categoryCombo.getSelectedItem();
                    String warehouse = (String) warehouseCombo.getSelectedItem();
                    String quantity = quantityField.getText().trim();
                    String price = priceField.getText().trim();
                    String volume = volumeField.getText().trim();
                    String unit = (String) unitCombo.getSelectedItem();

                     
                    if (name.isEmpty() || quantity.isEmpty() || price.isEmpty() || volume.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Пожалуйста, заполните все поля товара.");
                        continue;  
                    }

                     
                    out.println("ADD_RECEPTION," + transactionId + "," + name + "," + quantity + "," + price + "," + volume + "," + unit + "," + category + "," + warehouse);
                    String productResponse = in.readLine();
                    JOptionPane.showMessageDialog(null, productResponse);

                     
                    nameField.setText("");
                    quantityField.setText("");
                    priceField.setText("");
                    volumeField.setText("");
                }

                loadReception(model);  
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ошибка получения ID транзакции.");
            }
        }
    }

    public void editReception(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите товар для редактирования.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Товар редактируется только в этой таблице, остальные изменения вносятся вручную.");

         
        JTextField nameField = new JTextField(model.getValueAt(selectedRow, 2).toString());
        JTextField priceField = new JTextField(model.getValueAt(selectedRow, 5).toString());
        JTextField quantityField = new JTextField(model.getValueAt(selectedRow, 3).toString());
        JTextField volumeField = new JTextField(model.getValueAt(selectedRow, 6).toString());  
        JComboBox<String> unitCombo = new JComboBox<>(new String[] {"шт", "уп", "литр", "поддон"});
        unitCombo.setSelectedItem(model.getValueAt(selectedRow, 4));

        JComboBox<String> categoryCombo = new JComboBox<>();
        JComboBox<String> warehouseCombo = new JComboBox<>();

         
        try {
            out.println("GET_CATEGORIES");
            String response;
            categoryCombo.removeAllItems();  

            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                categoryCombo.addItem(response.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки категорий: " + e.getMessage());
        }

         
        categoryCombo.setSelectedItem(model.getValueAt(selectedRow, 7).toString());  

         
        try {
            out.println("GET_WAREHOUSES");
            String response;
            warehouseCombo.removeAllItems();  

            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                warehouseCombo.addItem(response.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки складов: " + e.getMessage());
        }

         
        warehouseCombo.setSelectedItem(model.getValueAt(selectedRow, 8).toString());  

         
        String currentWarehouse = model.getValueAt(selectedRow, 8).toString();  
        warehouseCombo.setSelectedItem(currentWarehouse);

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Название:"));
        panel.add(nameField);
        panel.add(new JLabel("Категория:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Склад:"));
        panel.add(warehouseCombo);
        panel.add(new JLabel("Цена:"));
        panel.add(priceField);
        panel.add(new JLabel("Количество:"));
        panel.add(quantityField);
        panel.add(new JLabel("Общий объем(м^3):"));
        panel.add(volumeField);
        panel.add(new JLabel("Единица измерения:"));
        panel.add(unitCombo);

         
        int result = JOptionPane.showConfirmDialog(
                null, panel, "Редактировать товар.",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            String warehouse = (String) warehouseCombo.getSelectedItem();
            String quantity = quantityField.getText().trim();
            String price = priceField.getText().trim();
            String volume = volumeField.getText().trim();
            String unit = (String) unitCombo.getSelectedItem();

             
            if (name.isEmpty() || category == null || warehouse == null || quantity.isEmpty() || price.isEmpty() || volume.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Пожалуйста, заполните все поля.");
                return;
            }

             
            try {
                out.println("EDIT_RECEPTION," + model.getValueAt(selectedRow, 0) +
                        ","+ model.getValueAt(selectedRow, 1) + "," +
                        name + "," + category + "," + warehouse + "," +
                        price + "," + quantity + "," + volume + "," + unit);
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadReception(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }

    }


    public void deleteReception(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите товар для удаления.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Вы уверены, что хотите удалить этот товар? Товар удаляется только в этой таблице, остальные изменения вносятся вручную.",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                out.println("DELETE_RECEPTION," + model.getValueAt(selectedRow, 0));
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadReception(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }
}
