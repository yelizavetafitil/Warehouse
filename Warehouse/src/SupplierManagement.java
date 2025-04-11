import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SupplierManagement {
    private PrintWriter out;
    private BufferedReader in;

    public SupplierManagement(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void loadSuppliers(DefaultTableModel model) {
        try {
            model.setRowCount(0);
            out.println("LOAD_SUPPLIERS");
            out.flush();

            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                String[] data = response.split(",");
                model.addRow(new Object[]{data[0], data[1], data[2]});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки поставщиков: " + e.getMessage());
        }
    }

    public void addSupplier(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Имя:"));
        panel.add(nameField);
        panel.add(new JLabel("Контакты:"));
        panel.add(contactField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Добавить поставщика", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                out.println("ADD_SUPPLIERS," + nameField.getText() + "," +
                        contactField.getText());
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadSuppliers(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void editSupplier(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите поставщика для редактирования");
            return;
        }

        String id = model.getValueAt(selectedRow, 0).toString();
        JTextField nameField = new JTextField(model.getValueAt(selectedRow, 1).toString());
        JTextField contactField = new JTextField(model.getValueAt(selectedRow, 2).toString());

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Имя:"));
        panel.add(nameField);
        panel.add(new JLabel("Контакты:"));
        panel.add(contactField);


        int result = JOptionPane.showConfirmDialog(null, panel, "Редактировать поставщика", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                out.println("EDIT_SUPPLIERS," + id + "," + nameField.getText() + "," +
                        contactField.getText());
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadSuppliers(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void deleteSupplier(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите поставщика для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Вы уверены, что хотите удалить этого поставщика?", "Подтверждение удаления", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                out.println("DELETE_SUPPLIERS," + model.getValueAt(selectedRow, 0));
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadSuppliers(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }
}
