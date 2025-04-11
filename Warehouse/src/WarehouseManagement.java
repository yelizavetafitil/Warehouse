import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class WarehouseManagement {
    private PrintWriter out;
    private BufferedReader in;

    public WarehouseManagement(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void loadWarehouses(DefaultTableModel model) {
        try {
            model.setRowCount(0);
            out.println("LOAD_WAREHOUSE");
            out.flush();

            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                String[] data = response.split(",");
                model.addRow(new Object[]{data[0], data[1], data[2]});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки: " + e.getMessage());
        }
    }

    public void addWarehouse(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField volumeField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Имя склада:"));
        panel.add(nameField);
        panel.add(new JLabel("Объем:"));
        panel.add(volumeField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Добавить склад", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                out.println("ADD_WAREHOUSE," + nameField.getText() + "," +
                        volumeField.getText());
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadWarehouses(model); // Предполагается, что у вас есть метод loadWarehouses
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void editWarehouse(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите склад для редактирования");
            return;
        }

        String id = model.getValueAt(selectedRow, 0).toString();
        JTextField nameField = new JTextField(model.getValueAt(selectedRow, 1).toString());
        JTextField volumeField = new JTextField(model.getValueAt(selectedRow, 2).toString());

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Имя склада:"));
        panel.add(nameField);
        panel.add(new JLabel("Объем:"));
        panel.add(volumeField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Редактировать склад", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                out.println("EDIT_WAREHOUSE," + id + "," + nameField.getText() + "," +
                        volumeField.getText());
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadWarehouses(model); // Предполагается, что у вас есть метод loadWarehouses
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void deleteWarehouse(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите склад для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Вы уверены, что хотите удалить этот склад?", "Подтверждение удаления", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                out.println("DELETE_SUPPLIERS," + model.getValueAt(selectedRow, 0));
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadWarehouses(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }
}

