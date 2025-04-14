import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ReturnManagement {
    private PrintWriter out;
    private BufferedReader in;

    public ReturnManagement(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void loadReturn(DefaultTableModel model) {
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


    public void addReturn(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField(); // Поле для ввода количества
        JTextField volumeField = new JTextField(); // Поле для ввода объема
        JComboBox<String> unitCombo = new JComboBox<>(new String[] {"шт", "уп", "литр", "поддон"});
        JComboBox<String> categoryCombo = new JComboBox<>();
        JComboBox<String> warehouseCombo = new JComboBox<>(); // Комбобокс для выбора склада

        try {
            // Загрузка категорий
            out.println("GET_CATEGORIES");
            String response;
            categoryCombo.removeAllItems(); // Очищаем комбобокс перед загрузкой категорий
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                categoryCombo.addItem(response.trim());
            }

            // Загрузка складов
            out.println("GET_WAREHOUSES");
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                warehouseCombo.addItem(response.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки: " + e.getMessage());
        }

        // Создание панели для ввода данных
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
        // Отображение диалогового окна с подтверждением
        int result = JOptionPane.showConfirmDialog(
                null, panel, "Добавить товар",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {

            try {
                // Создание транзакции на прием товаров
                out.println("CREATE_RECEPTION_TRANSACTION");
                String transactionResponse = in.readLine();
                int transactionId = Integer.parseInt(transactionResponse); // Получаем ID транзакции

                // Ввод товаров в цикле
                while (true) {
                    // Отображение диалогового окна для ввода товара
                    int productResult = JOptionPane.showConfirmDialog(
                            null, panel, "Добавить товар в транзакцию (или отмените для завершения)",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (productResult == JOptionPane.CANCEL_OPTION) {
                        break; // Выход из цикла, если пользователь отменил
                    }

                    String name = nameField.getText().trim();
                    String category = (String) categoryCombo.getSelectedItem();
                    String warehouse = (String) warehouseCombo.getSelectedItem();
                    String quantity = quantityField.getText().trim();
                    String price = priceField.getText().trim();
                    String volume = volumeField.getText().trim();
                    String unit = (String) unitCombo.getSelectedItem();

                    // Проверка на пустые значения
                    if (name.isEmpty() || quantity.isEmpty() || price.isEmpty() || volume.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Пожалуйста, заполните все поля товара.");
                        continue; // Продолжить цикл, если есть пустые поля
                    }

                    // Добавление товара в транзакцию
                    out.println("ADD_RECEPTION," + transactionId + "," + name + "," + quantity + "," + price + "," + volume + "," + unit + "," + category + "," + warehouse);
                    String productResponse = in.readLine();
                    JOptionPane.showMessageDialog(null, productResponse);

                    // Очистка полей для следующего товара
                    nameField.setText("");
                    quantityField.setText("");
                    priceField.setText("");
                    volumeField.setText("");
                }

                loadReturn(model); // Перезагрузка списка продуктов
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ошибка получения ID транзакции.");
            }
        }
    }

    public void editReturn(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите товар для редактирования.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Товар редактируется только в этой таблице, остальные изменения вносятся вручную.");

        // Поля для редактирования
        JTextField nameField = new JTextField(model.getValueAt(selectedRow, 2).toString());
        JTextField priceField = new JTextField(model.getValueAt(selectedRow, 5).toString());
        JTextField quantityField = new JTextField(model.getValueAt(selectedRow, 3).toString());
        JTextField volumeField = new JTextField(model.getValueAt(selectedRow, 6).toString()); // Добавлено поле для объема
        JComboBox<String> unitCombo = new JComboBox<>(new String[] {"шт", "уп", "литр", "поддон"});
        unitCombo.setSelectedItem(model.getValueAt(selectedRow, 4));

        JComboBox<String> categoryCombo = new JComboBox<>();
        JComboBox<String> warehouseCombo = new JComboBox<>();

        // Загрузка категорий
        try {
            out.println("GET_CATEGORIES");
            String response;
            categoryCombo.removeAllItems(); // Очищаем комбобокс перед загрузкой категорий

            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                categoryCombo.addItem(response.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки категорий: " + e.getMessage());
        }

        // Установка текущей категории
        categoryCombo.setSelectedItem(model.getValueAt(selectedRow, 7).toString()); // Установка категории

        // Загрузка складов
        try {
            out.println("GET_WAREHOUSES");
            String response;
            warehouseCombo.removeAllItems(); // Очищаем комбобокс перед загрузкой складов

            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                warehouseCombo.addItem(response.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки складов: " + e.getMessage());
        }

        // Установка текущего склада
        warehouseCombo.setSelectedItem(model.getValueAt(selectedRow, 8).toString()); // Установка склада

        // Установка выбранного склада
        String currentWarehouse = model.getValueAt(selectedRow, 8).toString(); // Предположим, что склад находится в 7 столбце
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

        // Отображение диалогового окна с подтверждением
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

            // Проверка на пустые значения
            if (name.isEmpty() || category == null || warehouse == null || quantity.isEmpty() || price.isEmpty() || volume.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Пожалуйста, заполните все поля.");
                return;
            }

            // Отправка данных на сервер
            try {
                out.println("EDIT_RECEPTION," + model.getValueAt(selectedRow, 0) +
                        ","+ model.getValueAt(selectedRow, 1) + "," +
                        name + "," + category + "," + warehouse + "," +
                        price + "," + quantity + "," + volume + "," + unit);
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadReturn(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }

    }


    public void deleteReturn(JTable table, DefaultTableModel model) {
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
                loadReturn(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }
}
