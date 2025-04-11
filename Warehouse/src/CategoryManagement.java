import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CategoryManagement {
    private PrintWriter out;
    private BufferedReader in;

    public CategoryManagement(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void loadCategories(DefaultTableModel model) {
        try {
            out.println("LOAD_CATEGORIES");
            model.setRowCount(0);

            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                String[] data = response.split(",");
                model.addRow(new Object[]{data[0], data[1]});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки категорий: " + e.getMessage());
        }
    }

    public void addCategory(DefaultTableModel model) {
        String name = JOptionPane.showInputDialog(null, "Введите название категории:");
        if (name != null && !name.trim().isEmpty()) {
            try {
                out.println("ADD_CATEGORY," + name);
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadCategories(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void editCategory(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите категорию для редактирования");
            return;
        }

        String id = model.getValueAt(selectedRow, 0).toString();
        String newName = JOptionPane.showInputDialog(null, "Введите новое название категории:",
                model.getValueAt(selectedRow, 1));

        if (newName != null && !newName.trim().isEmpty()) {
            try {
                out.println("EDIT_CATEGORY," + id + "," + newName);
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadCategories(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void deleteCategory(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите категорию для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Вы уверены, что хотите удалить эту категорию?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                out.println("DELETE_CATEGORY," + model.getValueAt(selectedRow, 0));
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadCategories(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }
}