import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class MoveManagement {
    private PrintWriter out;
    private BufferedReader in;

    public MoveManagement(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void loadMove(DefaultTableModel model) {
        try {
            out.println("LOAD_MOVE");
            model.setRowCount(0);

            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                String[] data = response.split(",");
                model.addRow(new Object[]{data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9]});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки: " + e.getMessage());
        }
    }


    public void addMove(JTable table, DefaultTableModel model, DefaultTableModel shmodel) {

        int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(null, "Пожалуйста, выберите хотя бы один товар для перемещения.");
            return;
        }

        try{


            out.println("CREATE_MOVE_TRANSACTION");
            String transactionResponse = in.readLine();
            int transactionId = Integer.parseInt(transactionResponse);


            for (int i = selectedRows.length - 1; i >= 0; i--) {

                JPanel panel = new JPanel(new GridLayout(4, 2));
                JTextField quantityField = new JTextField();
                JTextField volumeField = new JTextField();
                JComboBox<String> warehouseCombo = new JComboBox<>();

                try {
                    String response;
                    out.println("GET_WAREHOUSES");
                    String existingWarehouse = (String) model.getValueAt(selectedRows[i], 7);
                    while ((response = in.readLine()) != null) {
                        if (response.equals("END")) break;
                        if (!response.trim().equals(existingWarehouse)) {
                            warehouseCombo.addItem(response.trim());
                        }
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Ошибка загрузки: " + e.getMessage());
                }


                panel.add(new JLabel("Название:"));
                panel.add(new JLabel(String.valueOf(model.getValueAt(selectedRows[i], 1))));
                panel.add(new JLabel("Количество:"));
                panel.add(quantityField);
                panel.add(new JLabel("Объем:"));
                panel.add(volumeField);
                panel.add(new JLabel("Новый склад:"));
                panel.add(warehouseCombo);

                int result = JOptionPane.showConfirmDialog(null, panel, "Добавить перемещение", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    int totalQuantity = Integer.parseInt(quantityField.getText());
                    double totalVolume = Double.parseDouble(volumeField.getText());

                    Object  avQuantity = model.getValueAt(selectedRows[i], 3);
                    Object  avVolume =  model.getValueAt(selectedRows[i], 6);

                    int availableQuantity =  Integer.parseInt((String) avQuantity);
                    double availableVolume = Double.parseDouble((String)avVolume);

                     
                    if (totalQuantity > availableQuantity) {
                        JOptionPane.showMessageDialog(null, "Недостаточно количества для товара: " + model.getValueAt(selectedRows[i], 1));
                        return;
                    }
                    if (totalVolume > availableVolume) {
                        JOptionPane.showMessageDialog(null, "Недостаточно объема для товара: " + model.getValueAt(selectedRows[i], 1));
                        return;
                    }
                    if (totalQuantity == 0) {
                        JOptionPane.showMessageDialog(null, "Недостаточно количества для товара: " + model.getValueAt(selectedRows[i], 1));
                        return;
                    }
                    if (totalVolume == 0) {
                        JOptionPane.showMessageDialog(null, "Недостаточно объема для товара: " + model.getValueAt(selectedRows[i], 1));
                        return;
                    }
                    String warehouse = (String) warehouseCombo.getSelectedItem();
                    out.println("ADD_MOVE," + transactionId + "," +
                            model.getValueAt(selectedRows[i], 1) + "," +
                            totalQuantity + "," +
                            model.getValueAt(selectedRows[i], 5) + "," +
                            totalVolume + "," +
                            model.getValueAt(selectedRows[i], 4) + "," +
                            model.getValueAt(selectedRows[i], 2) + "," +
                            model.getValueAt(selectedRows[i], 7)
                            + "," +
                            warehouse
                    );
                    String productResponse = in.readLine();
                    JOptionPane.showMessageDialog(null, productResponse);
                }
            }
            loadMove(shmodel);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
        }
    }

    public void editMove(JTable table, DefaultTableModel model) {
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
        JComboBox<String> warehousePastCombo = new JComboBox<>();

         
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

        try {
            out.println("GET_WAREHOUSES");
            String response;
            warehousePastCombo.removeAllItems();  

            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                warehousePastCombo.addItem(response.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки складов: " + e.getMessage());
        }

         
        warehousePastCombo.setSelectedItem(model.getValueAt(selectedRow, 9).toString());  

         
        String currentWarehousePast = model.getValueAt(selectedRow, 9).toString();  
        warehousePastCombo.setSelectedItem(currentWarehousePast);

        JPanel panel = new JPanel(new GridLayout(8, 2));
        panel.add(new JLabel("Название:"));
        panel.add(nameField);
        panel.add(new JLabel("Категория:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Склад:"));
        panel.add(warehouseCombo);
        panel.add(new JLabel("Прошлый склад:"));
        panel.add(warehousePastCombo);
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
            String warehouse_past = (String) warehousePastCombo.getSelectedItem();
            String quantity = quantityField.getText().trim();
            String price = priceField.getText().trim();
            String volume = volumeField.getText().trim();
            String unit = (String) unitCombo.getSelectedItem();

             
            if (name.isEmpty() || category == null || warehouse == null || quantity.isEmpty() || price.isEmpty() || volume.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Пожалуйста, заполните все поля.");
                return;
            }

             
            try {
                out.println("EDIT_MOVE," + model.getValueAt(selectedRow, 0) +
                        ","+ model.getValueAt(selectedRow, 1) + "," +
                        name + "," + category + "," + warehouse + "," +
                        price + "," + quantity + "," + volume + "," + unit+ "," + warehouse_past);
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadMove(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }

    }


    public void deleteMove(JTable table, DefaultTableModel model) {
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
                out.println("DELETE_MOVE," + model.getValueAt(selectedRow, 0));
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadMove(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }
}
