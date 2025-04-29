import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Manager {
    private JFrame mainFrame;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Management management;

    public Manager(Socket existingSocket) {
        this.socket = existingSocket;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.management = new Management(out, in);
            showMainMenu();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка инициализации: " + e.getMessage());
            System.exit(1);
        }
    }

    private void showMainMenu() {
        mainFrame = new JFrame("Главное меню менеджера");
        mainFrame.setSize(400, 400);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

         
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPanel.setMaximumSize(new Dimension(400, 300));  

        String[] buttonLabels = {
                "Управление категориями",
                "Управление товарами",
                "Управление заказами",
                "Управление позициями заказов",
                "Назад"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBackground(new Color(70, 130, 180));  
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(240, 40));  
            button.addActionListener(e -> handleButtonClick(label));
            buttonPanel.add(button);
        }

         
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(buttonPanel);
        mainFrame.add(panel, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    private void handleButtonClick(String label) {
        switch (label) {
            case "Управление категориями":
                management.showCategoryManagement(mainFrame);
                break;
            case "Управление товарами":
                management.showProductManagement(mainFrame);
                break;
            case "Управление заказами":
                management.showOrderManagement(mainFrame);
                break;
            case "Управление позициями заказов":
                management.showOrderItemManagement(mainFrame);
                break;
            case "Назад":
                mainFrame.dispose();
                new WarehouseClient().setVisible(true);
                break;
        }
    }


}
