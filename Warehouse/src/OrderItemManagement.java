import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class OrderItemManagement {
    private PrintWriter out;
    private BufferedReader in;

    public OrderItemManagement(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void loadOrderItems(DefaultTableModel model) {
        try {
            out.println("LOAD_ORDER_ITEM");
            model.setRowCount(0);

            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                String[] data = response.split(",");
                model.addRow(new Object[]{data[0], data[1], data[2], data[3], data[4], data[5]});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки: " + e.getMessage());
        }
    }

    public void addOrderItem(DefaultTableModel model) {
        JComboBox<String> productsCombo = new JComboBox<>();
        JComboBox<String> orderIdCombo = new JComboBox<>();
        JLabel unitLabel = new JLabel();
        Map<String, Integer> productMap = new HashMap<>();

        // Получение идентификаторов заказов
        try {
            out.println("GET_ORDER_ID");
            String response;
            orderIdCombo.removeAllItems();

            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                if (response.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ошибка: Пустой ответ от сервера.");
                    continue;
                }
                orderIdCombo.addItem(response.trim());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки идентификаторов заказов: " + e.getMessage());
        }

        // Получение списка продуктов
        try {
            out.println("GET_PRODUCTS");
            String response;
            productsCombo.removeAllItems();
            productMap.clear();

            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                if (response.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ошибка: Пустой ответ от сервера.");
                    continue;
                }

                String[] parts = response.trim().split(",");
                if (parts.length >= 3) { // Убедитесь, что есть и склад
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String warehouse = parts[2]; // Получаем информацию о складе

                    // Формируем имя продукта с указанием склада
                    String displayName = name + " (" + warehouse + ")";
                    productsCombo.addItem(displayName);
                    productMap.put(displayName, id);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки продуктов: " + e.getMessage());
        }

        // Обработчик выбора продукта
        productsCombo.addActionListener(e -> {
            String selectedProductName = (String) productsCombo.getSelectedItem();
            if (selectedProductName != null && productMap.containsKey(selectedProductName)) {
                try {
                    out.println("GET_PRODUCT_UNIT," + productMap.get(selectedProductName));
                    String response = in.readLine();
                    unitLabel.setText("Единица измерения: " + (response != null ? response : "не указана"));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка загрузки единицы измерения: " + ex.getMessage());
                }
            }
        });

        // Создаем Spinner для ввода количества
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 1000, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(quantitySpinner, "#");
        quantitySpinner.setEditor(editor);

        // Панель для ввода данных
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Идентификатор заказа:"));
        panel.add(orderIdCombo);
        panel.add(new JLabel("Продукт:"));
        panel.add(productsCombo);
        panel.add(unitLabel);
        panel.add(new JLabel("Количество:"));
        panel.add(quantitySpinner);

        int result = JOptionPane.showConfirmDialog(
                null, panel, "Добавить позицию заказа",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String orderId = (String) orderIdCombo.getSelectedItem();
            String productName = (String) productsCombo.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();

            if (orderId == null || productName == null) {
                JOptionPane.showMessageDialog(null, "Пожалуйста, выберите заказ и продукт.");
                return;
            }

            try {
                Integer productId = productMap.get(productName);
                if (productId == null) {
                    JOptionPane.showMessageDialog(null, "Ошибка: не удалось определить ID продукта.");
                    return;
                }

                out.println("ADD_ORDER_ITEM," + orderId + "," + productId + "," + quantity);
                String response = in.readLine();

                if (response != null) {
                    if (response.startsWith("ERROR")) {
                        JOptionPane.showMessageDialog(null,
                                response.substring(response.indexOf(',') + 1),
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Позиция успешно добавлена в заказ",
                                "Успешно",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadOrderItems(model);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Нет ответа от сервера",
                            "Ошибка связи",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Ошибка связи с сервером: " + e.getMessage(),
                        "Сетевая ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void editOrderItem(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите позицию заказа для редактирования");
            return;
        }

        // Получаем текущее значение
        String orderId = model.getValueAt(selectedRow, 0).toString(); // ID заказа
        String currentProduct = model.getValueAt(selectedRow, 2).toString(); // Название продукта с указанием склада
        String currentQuantity = model.getValueAt(selectedRow, 3).toString(); // Количество
        String orderDate = model.getValueAt(selectedRow, 4).toString(); // Дата заказа
        String status = model.getValueAt(selectedRow, 5).toString();

        JComboBox<String> productsCombo = new JComboBox<>();
        JLabel unitLabel = new JLabel();
        Map<String, Integer> productMap = new HashMap<>();

        // Получение списка продуктов
        try {
            out.println("GET_PRODUCTS");
            String response;
            productsCombo.removeAllItems();
            productMap.clear();

            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                if (response.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ошибка: Пустой ответ от сервера.");
                    continue;
                }

                String[] parts = response.trim().split(",");
                if (parts.length >= 3) { // Убедитесь, что есть и склад
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String warehouse = parts[2];

                    // Формируем имя продукта с указанием склада
                    String displayName = name + " (" + warehouse + ")";
                    productsCombo.addItem(displayName);
                    productMap.put(displayName, id);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки продуктов: " + e.getMessage());
        }

        // Устанавливаем текущий продукт
        productsCombo.setSelectedItem(currentProduct);

        // Обработчик выбора продукта
        productsCombo.addActionListener(e -> {
            String selectedProductName = (String) productsCombo.getSelectedItem();
            if (selectedProductName != null && productMap.containsKey(selectedProductName)) {
                try {
                    out.println("GET_PRODUCT_UNIT," + productMap.get(selectedProductName));
                    String response = in.readLine();
                    unitLabel.setText("Единица измерения: " + (response != null ? response : "не указана"));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Ошибка загрузки единицы измерения: " + ex.getMessage());
                }
            }
        });

        // Проверка и установка количества
        int quantity = 1; // Значение по умолчанию
        try {
            // Извлекаем только количество, без добавления статуса "НЕХВАТКА"
            quantity = Integer.parseInt(currentQuantity.replaceAll(" \\(НЕДОСТАТОК\\)", "").trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Некорректное количество: " + currentQuantity);
            return; // Выход из метода, если количество некорректно
        }

// Создаем JSpinner с корректным значением
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(quantity, 1, 1000, 1));

        // Создание панели для редактирования данных
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Идентификатор заказа: " + orderId));
        panel.add(new JLabel("Продукт:"));
        panel.add(productsCombo);
        panel.add(unitLabel);
        panel.add(new JLabel("Количество:"));
        panel.add(quantitySpinner);
        panel.add(new JLabel("Дата заказа: " + orderDate)); // Добавлено поле для даты
        panel.add(new JLabel("Статус: " + status));

        int result = JOptionPane.showConfirmDialog(null, panel, "Редактировать позицию заказа", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String productName = (String) productsCombo.getSelectedItem();
            int newQuantity = (Integer) quantitySpinner.getValue();

            if (productName == null) {
                JOptionPane.showMessageDialog(null, "Пожалуйста, выберите продукт.");
                return;
            }

            try {
                // Получаем ID продукта из productMap
                Integer productId = productMap.get(productName);
                if (productId == null) {
                    JOptionPane.showMessageDialog(null, "Ошибка: не удалось определить ID продукта.");
                    return;
                }

                out.println("EDIT_ORDER_ITEM," + orderId + "," + productId + "," + newQuantity);
                String response = in.readLine();

                if (response != null) {
                    if (response.startsWith("ERROR")) {
                        JOptionPane.showMessageDialog(null,
                                response.substring(response.indexOf(',') + 1),
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Позиция успешно обновлена в заказе",
                                "Успешно",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadOrderItems(model);
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Нет ответа от сервера",
                            "Ошибка связи",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Ошибка связи с сервером: " + e.getMessage(),
                        "Сетевая ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void deleteOrderItem(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите позицию для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Вы уверены, что хотите удалить эту позицию?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                out.println("DELETE_ORDER_ITEM," + model.getValueAt(selectedRow, 0));
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadOrderItems(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

}

