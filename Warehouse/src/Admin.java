import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Admin {
    private JFrame mainFrame;
    private JFrame userManagementFrame;
    private JFrame categoryManagementFrame;
    private JFrame productManagementFrame;
    private JFrame supplierManagementFrame;
    private JFrame orderManagementFrame;
    private JFrame orderitemManagementFrame;
    private JFrame transactionManagementFrame;
    private JFrame warehouseManagementFrame;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Admin(Socket existingSocket) {
        this.socket = existingSocket;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            showMainMenu();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка инициализации: " + e.getMessage());
            System.exit(1);
        }
    }

    private void showMainMenu() {
        mainFrame = new JFrame("Главное меню администратора");
        mainFrame.setSize(400, 400);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Создаем панель для кнопок
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPanel.setMaximumSize(new Dimension(400, 300)); // Ограничиваем максимальный размер панели кнопок

        String[] buttonLabels = {
                "Управление пользователями",
                "Управление категориями",
                "Управление товарами",
                "Управление поставщиками",
                "Управление заказами",
                "Управление позициями заказов",
                "Управление транзакциями",
                "Управление складами",
                "Назад"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBackground(new Color(70, 130, 180)); // Цвет кнопки
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(240, 40)); // Устанавливаем размер кнопок
            button.addActionListener(e -> handleButtonClick(label));
            buttonPanel.add(button);
        }

        // Центрируем кнопку панель в панели
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(buttonPanel);
        mainFrame.add(panel, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    private void handleButtonClick(String label) {
        switch (label) {
            case "Управление пользователями":
                showUserManagement();
                break;
            case "Управление категориями":
                showCategoryManagement();
                break;
            case "Управление товарами":
                showProductManagement();
                break;
            case "Управление поставщиками":
                showSupplierManagement();
                break;
            case "Управление заказами":
                showOrderManagement();
                break;
            case "Управление позициями заказов":
                showOrderItemManagement();
                break;
            case "Управление транзакциями":
                showTransactionManagement();
                break;
            case "Управление складами":
                showWarehouseManagement();
                break;
            case "Назад":
                mainFrame.dispose();
                new WarehouseClient().setVisible(true);
                break;
        }
    }

    // Управление пользователями
    private void showUserManagement() {
        if (mainFrame != null) mainFrame.setVisible(false);

        userManagementFrame = new JFrame("Управление пользователями");
        userManagementFrame.setSize(800, 600);
        userManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        userManagementFrame.setLocationRelativeTo(null);
        userManagementFrame.setLayout(new BorderLayout());

        DefaultTableModel userModel = new DefaultTableModel(new String[]{"ID", "Email", "Должность", "Уровень доступа"}, 0);
        JTable userTable = new JTable(userModel);
        userTable.setFillsViewportHeight(true);
        userTable.setRowHeight(25);
        userTable.setSelectionBackground(new Color(173, 216, 230)); // Цвет выделения
        userTable.setSelectionForeground(Color.BLACK);

        UserManagement userManagement = new UserManagement(out, in);
        userManagement.loadUsers(userModel);

        // Кнопки
        JButton refreshButton = createStyledButton("Обновить");
        JButton addButton = createStyledButton("Добавить");
        JButton editButton = createStyledButton("Изменить");
        JButton deleteButton = createStyledButton("Удалить");
        JButton backButton = createStyledButton("Назад");

        refreshButton.addActionListener(e -> userManagement.loadUsers(userModel));
        addButton.addActionListener(e -> userManagement.addUser(userModel));
        editButton.addActionListener(e -> userManagement.editUser(userTable, userModel));
        deleteButton.addActionListener(e -> userManagement.deleteUser(userTable, userModel));
        backButton.addActionListener(e -> {
            userManagementFrame.dispose();
            mainFrame.setVisible(true);
        });

        // Панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        // Разделитель
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(userTable), buttonPanel);
        splitPane.setDividerLocation(350);

        userManagementFrame.add(splitPane, BorderLayout.CENTER);
        userManagementFrame.setVisible(true);
    }

    // Управление категориями
    private void showCategoryManagement() {
        if (mainFrame != null) mainFrame.setVisible(false);

        categoryManagementFrame = new JFrame("Управление категориями");
        categoryManagementFrame.setSize(800, 600);
        categoryManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        categoryManagementFrame.setLocationRelativeTo(null);
        categoryManagementFrame.setLayout(new BorderLayout());

        DefaultTableModel categoryModel = new DefaultTableModel(new String[]{"ID", "Название категории"}, 0);
        JTable categoryTable = new JTable(categoryModel);
        categoryTable.setFillsViewportHeight(true);
        categoryTable.setRowHeight(25);
        categoryTable.setSelectionBackground(new Color(173, 216, 230)); // Цвет выделения
        categoryTable.setSelectionForeground(Color.BLACK);

        CategoryManagement categoryManagement = new CategoryManagement(out, in);
        categoryManagement.loadCategories(categoryModel);

        JButton refreshButton = createStyledButton("Обновить");
        JButton addButton = createStyledButton("Добавить");
        JButton editButton = createStyledButton("Изменить");
        JButton deleteButton = createStyledButton("Удалить");
        JButton backButton = createStyledButton("Назад");

        refreshButton.addActionListener(e -> categoryManagement.loadCategories(categoryModel));
        addButton.addActionListener(e -> categoryManagement.addCategory(categoryModel));
        editButton.addActionListener(e -> categoryManagement.editCategory(categoryTable, categoryModel));
        deleteButton.addActionListener(e -> categoryManagement.deleteCategory(categoryTable, categoryModel));
        backButton.addActionListener(e -> {
            categoryManagementFrame.dispose();
            mainFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(categoryTable), buttonPanel);
        splitPane.setDividerLocation(350);

        categoryManagementFrame.add(splitPane);
        categoryManagementFrame.setVisible(true);
    }

    // Управление товарами
    private void showProductManagement() {
        if (mainFrame != null) mainFrame.setVisible(false);

        productManagementFrame = new JFrame("Управление товарами");
        productManagementFrame.setSize(800, 600);
        productManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        productManagementFrame.setLocationRelativeTo(null);
        productManagementFrame.setLayout(new BorderLayout());

        DefaultTableModel productModel = new DefaultTableModel(
                new String[]{"ID", "Название", "Категория", "Количество", "Ед. изм.", "Цена", "Общий объем(м^3)", "Склад"}, 0);
        JTable productTable = new JTable(productModel);
        productTable.setFillsViewportHeight(true);
        productTable.setRowHeight(25);
        productTable.setSelectionBackground(new Color(173, 216, 230)); // Цвет выделения
        productTable.setSelectionForeground(Color.BLACK);

        ProductManagement productManagement = new ProductManagement(out, in);
        productManagement.loadProducts(productModel);

        JButton refreshButton = createStyledButton("Обновить");
        JButton addButton = createStyledButton("Добавить");
        JButton editButton = createStyledButton("Изменить");
        JButton deleteButton = createStyledButton("Удалить");
        JButton backButton = createStyledButton("Назад");

        refreshButton.addActionListener(e -> productManagement.loadProducts(productModel));
        addButton.addActionListener(e -> productManagement.addProduct(productModel));
        editButton.addActionListener(e -> productManagement.editProduct(productTable, productModel));
        deleteButton.addActionListener(e -> productManagement.deleteProduct(productTable, productModel));
        backButton.addActionListener(e -> {
            productManagementFrame.dispose();
            mainFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(productTable), buttonPanel);
        splitPane.setDividerLocation(450);

        productManagementFrame.add(splitPane);
        productManagementFrame.setVisible(true);
    }

    private void showSupplierManagement(){
        if (mainFrame != null) mainFrame.setVisible(false);

        supplierManagementFrame = new JFrame("Управление поставщиками");
        supplierManagementFrame.setSize(800, 600);
        supplierManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        supplierManagementFrame.setLocationRelativeTo(null);
        supplierManagementFrame.setLayout(new BorderLayout());

        DefaultTableModel supplierModel = new DefaultTableModel(
                new String[]{"ID", "Имя", "Контакты"}, 0);
        JTable supplierTable = new JTable(supplierModel);
        supplierTable.setFillsViewportHeight(true);
        supplierTable.setRowHeight(25);
        supplierTable.setSelectionBackground(new Color(173, 216, 230)); // Цвет выделения
        supplierTable.setSelectionForeground(Color.BLACK);

        SupplierManagement supplierManagement = new SupplierManagement(out, in);
        supplierManagement.loadSuppliers(supplierModel);

        JButton refreshButton = createStyledButton("Обновить");
        JButton addButton = createStyledButton("Добавить");
        JButton editButton = createStyledButton("Изменить");
        JButton deleteButton = createStyledButton("Удалить");
        JButton backButton = createStyledButton("Назад");

        refreshButton.addActionListener(e -> supplierManagement.loadSuppliers(supplierModel));
        addButton.addActionListener(e -> supplierManagement.addSupplier(supplierModel));
        editButton.addActionListener(e -> supplierManagement.editSupplier(supplierTable, supplierModel));
        deleteButton.addActionListener(e -> supplierManagement.deleteSupplier(supplierTable, supplierModel));
        backButton.addActionListener(e -> {
            supplierManagementFrame.dispose();
            mainFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(supplierTable), buttonPanel);
        splitPane.setDividerLocation(450);

        supplierManagementFrame.add(splitPane);
        supplierManagementFrame.setVisible(true);
    }

    private void showOrderManagement(){
        if (mainFrame != null) mainFrame.setVisible(false);

        orderManagementFrame = new JFrame("Управление заказами");
        orderManagementFrame.setSize(800, 600);
        orderManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        orderManagementFrame.setLocationRelativeTo(null);
        orderManagementFrame.setLayout(new BorderLayout());

        DefaultTableModel orderModel = new DefaultTableModel(
                new String[]{"ID", "Поставщик", "Дата", "Статус"}, 0);
        JTable orderTable = new JTable(orderModel);
        orderTable.setFillsViewportHeight(true);
        orderTable.setRowHeight(25);
        orderTable.setSelectionBackground(new Color(173, 216, 230)); // Цвет выделения
        orderTable.setSelectionForeground(Color.BLACK);

        OrderManagement orderManagement = new OrderManagement(out, in);
        orderManagement.loadOrders(orderModel);

        JButton refreshButton = createStyledButton("Обновить");
        JButton addButton = createStyledButton("Добавить");
        JButton editButton = createStyledButton("Изменить");
        JButton deleteButton = createStyledButton("Удалить");
        JButton backButton = createStyledButton("Назад");

        refreshButton.addActionListener(e -> orderManagement.loadOrders(orderModel));
        addButton.addActionListener(e -> orderManagement.addOrder(orderModel));
        editButton.addActionListener(e -> orderManagement.editOrder(orderTable, orderModel));
        deleteButton.addActionListener(e -> orderManagement.deleteOrder(orderTable, orderModel));
        backButton.addActionListener(e -> {
            orderManagementFrame.dispose();
            mainFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(orderTable), buttonPanel);
        splitPane.setDividerLocation(450);

        orderManagementFrame.add(splitPane);
        orderManagementFrame.setVisible(true);
    }

    private void showOrderItemManagement(){
        if (mainFrame != null) mainFrame.setVisible(false);

        orderitemManagementFrame = new JFrame("Управление позициями заказов");
        orderitemManagementFrame.setSize(800, 600);
        orderitemManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        orderitemManagementFrame.setLocationRelativeTo(null);
        orderitemManagementFrame.setLayout(new BorderLayout());

        DefaultTableModel orderitemModel = new DefaultTableModel(
                new String[]{"ID", "ID заказа", "Название продукта", "Количество продукта", "Дата заказа", "Статус заказ"}, 0);
        JTable orderitemTable = new JTable(orderitemModel);
        orderitemTable.setFillsViewportHeight(true);
        orderitemTable.setRowHeight(25);
        orderitemTable.setSelectionBackground(new Color(173, 216, 230)); // Цвет выделения
        orderitemTable.setSelectionForeground(Color.BLACK);

        OrderItemManagement orderItemManagement = new OrderItemManagement(out, in);
        orderItemManagement.loadOrderItems(orderitemModel);

        JButton refreshButton = createStyledButton("Обновить");
        JButton addButton = createStyledButton("Добавить");
        JButton editButton = createStyledButton("Изменить");
        JButton deleteButton = createStyledButton("Удалить");
        JButton backButton = createStyledButton("Назад");

        refreshButton.addActionListener(e -> orderItemManagement.loadOrderItems(orderitemModel));
        addButton.addActionListener(e -> orderItemManagement.addOrderItem(orderitemModel));
        editButton.addActionListener(e -> orderItemManagement.editOrderItem(orderitemTable, orderitemModel));
        deleteButton.addActionListener(e -> orderItemManagement.deleteOrderItem(orderitemTable, orderitemModel));
        backButton.addActionListener(e -> {
            orderitemManagementFrame.dispose();
            mainFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(orderitemTable), buttonPanel);
        splitPane.setDividerLocation(450);

        orderitemManagementFrame.add(splitPane);
        orderitemManagementFrame.setVisible(true);
    }

    public void showTransactionManagement() {
        if (mainFrame != null) mainFrame.setVisible(false);

        transactionManagementFrame = new JFrame("Управление транзакциями");
        transactionManagementFrame.setSize(400, 400);
        transactionManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        transactionManagementFrame.setLocationRelativeTo(null);
        transactionManagementFrame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Создаем панель для кнопок
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPanel.setMaximumSize(new Dimension(400, 300)); // Ограничиваем максимальный размер панели кнопок

        String[] buttonLabels = {
                "Прием товаров",
                "Отгрузка товаров",
                "Возврат товаров",
                "Перемещение товаров",
                "Назад"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBackground(new Color(70, 130, 180)); // Цвет кнопки
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(240, 40)); // Устанавливаем размер кнопок
            button.addActionListener(e -> handleButtonClick2(label));
            buttonPanel.add(button);
        }

        // Центрируем кнопку панель в панели
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(buttonPanel);
        transactionManagementFrame.add(panel, BorderLayout.CENTER);
        transactionManagementFrame.setVisible(true);
    }

    private void handleButtonClick2(String label) {
        switch (label) {
            case "Прием товаров":
                showReceptionManagement();
                break;
            case "Отгрузка товаров":
                showReturnManagement();
                break;
            case "Возврат товаров":

                break;
            case "Перемещение товаров":

                break;
            case "Назад":
                transactionManagementFrame.dispose();
                mainFrame.setVisible(true);
                break;
        }
    }

    private void showReceptionManagement() {
        if (mainFrame != null) mainFrame.setVisible(false);

        JFrame receptionFrame = new JFrame("Прием товаров");
        receptionFrame.setSize(800, 600);
        receptionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        receptionFrame.setLocationRelativeTo(null);
        receptionFrame.setLayout(new BorderLayout());

        DefaultTableModel receptionModel = new DefaultTableModel(
                new String[]{"ID", "ID транзакции", "Название", "Количество", "Ед. изм.", "Цена", "Общий объем(м^3)", "Категория", "Склад"}, 0);
        JTable receptionTable = new JTable(receptionModel);
        receptionTable.setFillsViewportHeight(true);
        receptionTable.setRowHeight(25);
        receptionTable.setSelectionBackground(new Color(173, 216, 230)); // Цвет выделения
        receptionTable.setSelectionForeground(Color.BLACK);

        ReceptionManagement receptionManagement = new ReceptionManagement(out, in);
        receptionManagement.loadReception(receptionModel);

        JButton refreshButton = createStyledButton("Обновить");
        JButton addButton = createStyledButton("Добавить");
        JButton editButton = createStyledButton("Изменить");
        JButton deleteButton = createStyledButton("Удалить");
        JButton backButton = createStyledButton("Назад");

        refreshButton.addActionListener(e -> receptionManagement.loadReception(receptionModel));
        addButton.addActionListener(e -> receptionManagement.addReception(receptionModel));
        editButton.addActionListener(e -> receptionManagement.editReception(receptionTable, receptionModel));
        deleteButton.addActionListener(e -> receptionManagement.deleteReception(receptionTable, receptionModel));
        backButton.addActionListener(e -> {
            receptionFrame.dispose();
            transactionManagementFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(receptionTable), buttonPanel);
        splitPane.setDividerLocation(450);

        receptionFrame.add(splitPane);
        receptionFrame.setVisible(true);
    }


    private void showReturnManagement() {
        if (mainFrame != null) mainFrame.setVisible(false);

        JFrame returnFrame = new JFrame("Прием товаров");
        returnFrame.setSize(800, 600);
        returnFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        returnFrame.setLocationRelativeTo(null);
        returnFrame.setLayout(new BorderLayout());

        DefaultTableModel returnModel = new DefaultTableModel(
                new String[]{ "ID", "ID транзакции", "Название", "Количество", "Ед. изм.", "Цена", "Общий объем(м^3)", "Категория", "Склад"}, 0);
        JTable returnTable = new JTable(returnModel);
        returnTable.setFillsViewportHeight(true);
        returnTable.setRowHeight(25);
        returnTable.setSelectionBackground(new Color(173, 216, 230)); // Цвет выделения
        returnTable.setSelectionForeground(Color.BLACK);

        ReturnManagement returnManagement = new ReturnManagement(out, in);
        returnManagement.loadReturn(returnModel);

        JButton refreshButton = createStyledButton("Обновить");
        JButton addButton = createStyledButton("Добавить");
        JButton editButton = createStyledButton("Изменить");
        JButton deleteButton = createStyledButton("Удалить");
        JButton backButton = createStyledButton("Назад");

        refreshButton.addActionListener(e -> returnManagement.loadReturn(returnModel));
        addButton.addActionListener(e -> returnManagement.addReturn(returnModel));
        editButton.addActionListener(e -> returnManagement.editReturn(returnTable, returnModel));
        deleteButton.addActionListener(e -> returnManagement.deleteReturn(returnTable, returnModel));
        backButton.addActionListener(e -> {
            returnFrame.dispose();
            transactionManagementFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(returnTable), buttonPanel);
        splitPane.setDividerLocation(450);

        returnFrame.add(splitPane);
        returnFrame.setVisible(true);
    }


    private void showWarehouseManagement(){
        if (mainFrame != null) mainFrame.setVisible(false);

        warehouseManagementFrame = new JFrame("Управление скаладми");
        warehouseManagementFrame.setSize(800, 600);
        warehouseManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        warehouseManagementFrame.setLocationRelativeTo(null);
        warehouseManagementFrame.setLayout(new BorderLayout());

        DefaultTableModel  warehouseModel = new DefaultTableModel(
                new String[]{"ID", "Имя склада", "Общий объем(м^3)"}, 0);
        JTable  warehouseTable = new JTable(warehouseModel);
        warehouseTable.setFillsViewportHeight(true);
        warehouseTable.setRowHeight(25);
        warehouseTable.setSelectionBackground(new Color(173, 216, 230)); // Цвет выделения
        warehouseTable.setSelectionForeground(Color.BLACK);

        WarehouseManagement warehouseManagement = new WarehouseManagement(out, in);
        warehouseManagement.loadWarehouses(warehouseModel);

        JButton refreshButton = createStyledButton("Обновить");
        JButton addButton = createStyledButton("Добавить");
        JButton editButton = createStyledButton("Изменить");
        JButton deleteButton = createStyledButton("Удалить");
        JButton backButton = createStyledButton("Назад");

        refreshButton.addActionListener(e -> warehouseManagement.loadWarehouses(warehouseModel));
        addButton.addActionListener(e -> warehouseManagement.addWarehouse(warehouseModel));
        editButton.addActionListener(e -> warehouseManagement.editWarehouse(warehouseTable, warehouseModel));
        deleteButton.addActionListener(e -> warehouseManagement.deleteWarehouse(warehouseTable, warehouseModel));
        backButton.addActionListener(e -> {
            warehouseManagementFrame.dispose();
            mainFrame.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(warehouseTable), buttonPanel);
        splitPane.setDividerLocation(450);

        warehouseManagementFrame.add(splitPane);
        warehouseManagementFrame.setVisible(true);
    }

    // Метод для создания стилизованных кнопок
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180)); // Цвет кнопки
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Отступы
        return button;
    }

}