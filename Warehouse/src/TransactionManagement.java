import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


public class TransactionManagement {
    private PrintWriter out;
    private BufferedReader in;

    public TransactionManagement(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void loadTransactions(DefaultTableModel model) {
        try {
            out.println("LOAD_TRANSACTIONS");
            model.setRowCount(0);

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


    public void addTransaction(DefaultTableModel model) {
        // Поле для ввода даты транзакции
        JTextField dateField = new JTextField(); // Можно заменить на JDatePicker для выбора даты

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Прием товаров", "Возврат товаров", "Отгрузка товаров", "Перемещение товаров"});

        // Создание панели для ввода данных
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Дата транзакции (yyyy-mm-dd hh:mm:ss):"));
        panel.add(dateField);
        panel.add(new JLabel("Тип транзакции:"));
        panel.add(typeCombo);


        // Отображение диалогового окна с подтверждением
        int result = JOptionPane.showConfirmDialog(
                null, panel, "Добавить транзакцию",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String transactionDate = dateField.getText().trim();
            String transactionType = (String) typeCombo.getSelectedItem();

            if (!isValidDate(transactionDate)) {
                JOptionPane.showMessageDialog(null, "Некорректный формат даты. Используйте YYYY-MM-DD HH:MM:SS.");
                return;
            }

            // Проверка на пустые значения
            if (transactionDate.isEmpty() || transactionType == null) {
                JOptionPane.showMessageDialog(null, "Пожалуйста, заполните все поля.");
                return;
            }

            if (!isValidDate(transactionDate)) {
                JOptionPane.showMessageDialog(null, "Некорректный формат даты. Используйте YYYY-MM-DD HH:MM:SS.");
                return;
            }

            try {
                out.println("ADD_TRANSACTIONS," + transactionDate + "," + transactionType);
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadTransactions(model); // Перезагрузка списка транзакций
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void editTransaction(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Выберите транзакцию для редактирования");
            return;
        }

        // Получаем текущее значение
        String transactionId = model.getValueAt(selectedRow, 0).toString();
        String currentTransactionDate = model.getValueAt(selectedRow, 1).toString();
        String currentTransactionType = model.getValueAt(selectedRow, 2).toString();

        // Поле для ввода даты транзакции
        JTextField dateField = new JTextField(currentTransactionDate); // Установка текущей даты

        // Выпадающий список для типа транзакции
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Прием товаров", "Возврат товаров", "Отгрузка товаров", "Перемещение товаров"});
        typeCombo.setSelectedItem(currentTransactionType);

        // Создание панели для редактирования данных
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Дата транзакции (yyyy-mm-dd hh:mm:ss):"));
        panel.add(dateField);
        panel.add(new JLabel("Тип транзакции:"));
        panel.add(typeCombo);



        int result = JOptionPane.showConfirmDialog(null, panel, "Редактировать транзакцию", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String transactionDate = dateField.getText().trim();
            String transactionType = (String) typeCombo.getSelectedItem();

            if (!isValidDate(transactionDate)) {
                JOptionPane.showMessageDialog(null, "Некорректный формат даты. Используйте YYYY-MM-DD HH:MM:SS.");
                return;
            }

            // Проверка на пустые значения
            if (transactionDate.isEmpty() || transactionType == null) {
                JOptionPane.showMessageDialog(null, "Пожалуйста, заполните все поля.");
                return;
            }

            if (!isValidDate(transactionDate)) {
                JOptionPane.showMessageDialog(null, "Некорректный формат даты. Используйте YYYY-MM-DD HH:MM:SS.");
                return;
            }

            try {
                out.println("EDIT_TRANSACTIONS," + transactionId + "," + transactionDate + "," + transactionType);
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadTransactions(model); // Перезагрузка списка транзакций
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    public void deleteTransaction(JTable table, DefaultTableModel model) {
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
                out.println("DELETE_TRANSACTIONS," + model.getValueAt(selectedRow, 0));
                String response = in.readLine();
                JOptionPane.showMessageDialog(null, response);
                loadTransactions(model);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ошибка: " + e.getMessage());
            }
        }
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false); // Установка строгого формата
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            return false; // Неверный формат
        }
        return true; // Корректный формат
    }

    public void generateReport(JTable transactionsTable){

        int selectedRow = transactionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(null, "Пожалуйста, выберите транзакцию.");
            return;
        }

        String transactionId = transactionsTable.getValueAt(selectedRow, 0).toString();
        String transactionType = transactionsTable.getValueAt(selectedRow, 2).toString();

        // Запрос данных у пользователя
        JTextField senderField = new JTextField();
        JTextField recipientField = new JTextField();



        JPanel reportPanel = new JPanel(new GridLayout(3, 2));
        reportPanel.add(new JLabel("ID транзакции:"));
        reportPanel.add(new JLabel(transactionId));
        reportPanel.add(new JLabel("Грузоотправитель:"));
        reportPanel.add(senderField);
        reportPanel.add(new JLabel("Грузополучатель:"));
        reportPanel.add(recipientField);

        int result = JOptionPane.showConfirmDialog(null, reportPanel,
                "Генерация накладной", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String sender = senderField.getText().trim();
            String recipient = recipientField.getText().trim();

            if (sender.isEmpty() || recipient.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Пожалуйста, заполните все поля.");
                return;
            }

            List<Item> items = fetchTransactionItems(Integer.parseInt(transactionId), transactionType);

            generatePDF(Integer.parseInt(transactionId), transactionType, sender, recipient, items);
        }
    }

    private List<Item> fetchTransactionItems(int transactionId, String transactionType) {
        List<Item> items = new ArrayList<>();

        // Логика получения данных из базы данных (пример)
        try {
            out.println("GET_TRANSACTION_ITEMS," + transactionId + "," + transactionType);
            String response;
            while ((response = in.readLine()) != null) {
                if (response.equals("END")) break;
                String[] parts = response.split(",");
                if (parts.length < 7) {
                    JOptionPane.showMessageDialog(null, "Ошибка данных", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    continue; // Пропускаем итерацию цикла
                }
                Item item = new Item(parts[0], Integer.parseInt(parts[1]), parts[2],
                        Double.parseDouble(parts[3]), Double.parseDouble(parts[4]),
                        parts[5], parts[6]);
                items.add(item);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка получения данных: " + e.getMessage());
        }

        return items;
    }

    private void generatePDF(int transactionId, String transactionType, String sender, String recipient, List<Item> items) {
        Document document = new Document(PageSize.A4, 40, 40, 50, 30);
        try {
            // Настройка шрифтов
            BaseFont bf = BaseFont.createFont("src/resources/arialmt.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(bf, 22, Font.BOLD, new BaseColor(15, 28, 63));
            Font headerFont = new Font(bf, 12, Font.BOLD, BaseColor.WHITE);
            Font labelFont = new Font(bf, 12, Font.BOLD, new BaseColor(51, 51, 51));
            Font valueFont = new Font(bf, 12, Font.NORMAL, BaseColor.BLACK);
            Font tableFont = new Font(bf, 10, Font.NORMAL, BaseColor.DARK_GRAY);

            PdfWriter.getInstance(document, new FileOutputStream("накладная_" + transactionId + ".pdf"));
            document.open();

            // Добавление логотип

            // Заголовок документа
            Paragraph title = new Paragraph("ТОВАРНАЯ НАКЛАДНАЯ", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Блок основной информации
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 3});

            addInfoCell(infoTable, "Номер накладной:", labelFont);
            addInfoCell(infoTable, String.valueOf(transactionId), valueFont);
            addInfoCell(infoTable, "Дата составления:", labelFont);
            addInfoCell(infoTable, new SimpleDateFormat("dd.MM.yyyy").format(new Date()), valueFont);
            addInfoCell(infoTable, "Тип операции:", labelFont);
            addInfoCell(infoTable, transactionType.toUpperCase(), valueFont);

            document.add(infoTable);
            document.add(Chunk.NEWLINE);

            // Блок грузоотправитель/грузополучатель
            PdfPTable partiesTable = new PdfPTable(2);
            partiesTable.setWidthPercentage(100);
            partiesTable.setWidths(new float[]{1, 1});

            addPartyCell(partiesTable, "Грузоотправитель:", sender, labelFont, valueFont);
            addPartyCell(partiesTable, "Грузополучатель:", recipient, labelFont, valueFont);

            document.add(partiesTable);
            document.add(Chunk.NEWLINE);

            // Таблица с товарами
            PdfPTable mainTable = new PdfPTable(7);
            mainTable.setWidthPercentage(100);
            mainTable.setWidths(new float[]{3, 1, 2, 1.5f, 1.5f, 2, 2});

            // Заголовки таблицы
            String[] headers = {"Наименование", "Кол-во", "Ед.изм", "Цена", "Объем", "Категория", "Склад"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(header, headerFont));
                cell.setBackgroundColor(new BaseColor(15, 28, 63));
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);
            }

            // Данные товаров
            DecimalFormat df = new DecimalFormat("#,##0.00");
            for (Item item : items) {
                addTableRow(mainTable, item.getName(), tableFont, Element.ALIGN_LEFT);
                addTableRow(mainTable, String.valueOf(item.getQuantity()), tableFont, Element.ALIGN_CENTER);
                addTableRow(mainTable, item.getUnit(), tableFont, Element.ALIGN_CENTER);
                addTableRow(mainTable, df.format(item.getPrice()), tableFont, Element.ALIGN_RIGHT);
                addTableRow(mainTable, df.format(item.getVolume()), tableFont, Element.ALIGN_RIGHT);
                addTableRow(mainTable, item.getCategory(), tableFont, Element.ALIGN_LEFT);
                addTableRow(mainTable, item.getWarehouse(), tableFont, Element.ALIGN_LEFT);
            }

            document.add(mainTable);
            document.add(Chunk.NEWLINE);

            // Подпись
            Paragraph sign = new Paragraph("Отпуск разрешил: ___________________________ / __________________ /", valueFont);
            sign.setAlignment(Element.ALIGN_RIGHT);
            document.add(sign);

            document.close();
            JOptionPane.showMessageDialog(null, "Накладная успешно сгенерирована!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка генерации PDF: " + e.getMessage());
        }
    }

    // Вспомогательные методы
    private void addInfoCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addPartyCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);

        Phrase p = new Phrase();
        p.add(new Chunk(label + "\n", labelFont));
        p.add(new Chunk(value, valueFont));

        cell.addElement(p);
        table.addCell(cell);
    }

    private void addTableRow(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

}

