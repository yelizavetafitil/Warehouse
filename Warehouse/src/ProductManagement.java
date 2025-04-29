import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProductManagement {
    private PrintWriter out;
    private BufferedReader in;

    public ProductManagement(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void loadProducts(DefaultTableModel model) {
        try {
            out.println("LOAD_PRODUCTS");
            model.setRowCount(0);

            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                String[] data = response.split(",");
                model.addRow(new Object[]{data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7]});  
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки товаров: " + e.getMessage());
        }
    }


    public void addProduct(DefaultTableModel model) {
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
                out.println("ADD_PRODUCT," + name + "," + category + "," + warehouse + "," + quantity + "," + price + "," + volume + "," + unit);
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadProducts(model);  
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void editProduct(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите товар для редактирования");
            return;
        }

         
        JTextField nameField = new JTextField(model.getValueAt(selectedRow, 1).toString());
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

         
        categoryCombo.setSelectedItem(model.getValueAt(selectedRow, 2).toString());  

         
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

         
        warehouseCombo.setSelectedItem(model.getValueAt(selectedRow, 7).toString());  

         
        String currentWarehouse = model.getValueAt(selectedRow, 7).toString();  
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
                null, panel, "Редактировать товар",
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
                out.println("EDIT_PRODUCT," + model.getValueAt(selectedRow, 0) + "," +
                        name + "," + category + "," + warehouse + "," +
                        price + "," + quantity + "," + volume + "," + unit);
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadProducts(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }


    public void deleteProduct(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите товар для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Вы уверены, что хотите удалить этот товар?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                out.println("DELETE_PRODUCT," + model.getValueAt(selectedRow, 0));
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadProducts(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }
}