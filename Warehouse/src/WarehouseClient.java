import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class WarehouseClient extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JTextArea responseArea;

    public WarehouseClient() {
        setTitle("Авторизация");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Отступы

        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Войти");
        responseArea = new JTextArea(5, 30);
        responseArea.setEditable(false);
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setBackground(Color.LIGHT_GRAY);
        responseArea.setFont(new Font("Arial", Font.PLAIN, 12));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Пароль:"), gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Занимает две колонки
        panel.add(loginButton, gbc);

        gbc.gridy = 3;
        panel.add(new JScrollPane(responseArea), gbc);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticate();
            }
        });
    }

    private void authenticate() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Socket socket = new Socket("localhost", 12345);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(email);
            out.println(password);

            String response = in.readLine();
            responseArea.setText(response);

            if (response.contains("Уровень доступа: 5")) {
                dispose();
                SwingUtilities.invokeLater(() -> new Admin(socket));
            } else if (response.contains("Уровень доступа: 4")) {
                dispose();
                SwingUtilities.invokeLater(() -> new Storekeeper(socket));
            } else if (response.contains("Уровень доступа: 3")) {
                dispose();
                SwingUtilities.invokeLater(() -> new Manager(socket));
            }else if (response.contains("Уровень доступа: 2")) {
                dispose();
                SwingUtilities.invokeLater(() -> new Worker(socket));
            }else if (response.contains("Уровень доступа: 1")) {
                dispose();
                SwingUtilities.invokeLater(() -> new Analyst(socket));
            }else {
                responseArea.setText("Недостаточно прав для доступа к панели администратора");
                socket.close(); // Закрываем сокет, если не нужен Admin
            }

        } catch (IOException e) {
            responseArea.setText("Ошибка подключения к серверу: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WarehouseClient client = new WarehouseClient();
            client.setVisible(true);
        });
    }
}