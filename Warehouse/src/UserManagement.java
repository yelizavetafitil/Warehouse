import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class UserManagement {
    private PrintWriter out;
    private BufferedReader in;

    public UserManagement(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void loadUsers(DefaultTableModel model) {
        try {
            model.setRowCount(0);
            out.println("LOAD_USERS");
            out.flush();

            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                String[] data = response.split(",");
                model.addRow(new Object[]{data[0], data[1], data[2], data[3]});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки пользователей: " + e.getMessage());
        }
    }

    public void addUser(DefaultTableModel model) {
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField positionField = new JTextField();
        JComboBox<String> accessCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Пароль:"));
        panel.add(passwordField);
        panel.add(new JLabel("Должность:"));
        panel.add(positionField);
        panel.add(new JLabel("Уровень доступа:"));
        panel.add(accessCombo);

        int result = JOptionPane.showConfirmDialog(null, panel, "Добавить пользователя", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                out.println("ADD_USER," + emailField.getText() + "," +
                        new String(passwordField.getPassword()) + "," +
                        positionField.getText() + "," +
                        accessCombo.getSelectedItem());
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadUsers(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void editUser(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите пользователя для редактирования");
            return;
        }

        String id = model.getValueAt(selectedRow, 0).toString();
        JTextField emailField = new JTextField(model.getValueAt(selectedRow, 1).toString());
        JTextField positionField = new JTextField(model.getValueAt(selectedRow, 2).toString());
        JComboBox<String> accessCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
        accessCombo.setSelectedItem(model.getValueAt(selectedRow, 3).toString());

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Должность:"));
        panel.add(positionField);
        panel.add(new JLabel("Уровень доступа:"));
        panel.add(accessCombo);

        int result = JOptionPane.showConfirmDialog(null, panel, "Редактировать пользователя", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                out.println("EDIT_USER," + id + "," + emailField.getText() + "," +
                        positionField.getText() + "," + accessCombo.getSelectedItem());
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadUsers(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void deleteUser(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите пользователя для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Вы уверены, что хотите удалить этого пользователя?", "Подтверждение удаления", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                out.println("DELETE_USER," + model.getValueAt(selectedRow, 0));
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadUsers(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }
}