import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Cedov10 extends JFrame {
    private JTabbedPane tabs;
    private JTextField loginUserField, nameField, seatField, dateField;
    private JPasswordField loginPassField;
    private JComboBox<String> busCombo, paymentCombo;
    private JTextArea viewArea;
    private String currentUser = "";
    private final int MAX_SEATS = 40;
    private final int FARE_PER_SEAT = 200;
    private Map<String, Integer> seatMap = new HashMap<>();

    public Cedov10() {
        setTitle("🚌 Система бронирования автобусов");
        setSize(750, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Установка цветовой схемы
        UIManager.put("TabbedPane.selected", new Color(52, 152, 219));
        UIManager.put("TabbedPane.background", new Color(236, 240, 241));
        UIManager.put("TabbedPane.foreground", new Color(44, 62, 80));

        tabs = new JTabbedPane();

        tabs.add("🔐 Вход", loginPanel());
        tabs.add("🎫 Бронирование", bookingPanel());
        tabs.add("📋 Просмотр билетов", viewPanel());
        tabs.add("❌ Отмена брони", cancelPanel());

        add(tabs);
        tabs.setEnabledAt(1, false);
        tabs.setEnabledAt(2, false);
        tabs.setEnabledAt(3, false);

        // Загружаем существующие бронирования
        loadExistingBookings();

        setVisible(true);
    }

    private JPanel loginPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("🚌 Вход в систему бронирования", JLabel.CENTER);
        title.setBounds(150, 40, 400, 40);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        panel.add(title);

        // Иконка автобуса (текстовая)
        JLabel busIcon = new JLabel("🚍", JLabel.CENTER);
        busIcon.setBounds(300, 90, 100, 60);
        busIcon.setFont(new Font("Arial", Font.PLAIN, 50));
        panel.add(busIcon);

        JLabel userLabel = new JLabel("Имя пользователя:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setBounds(200, 170, 150, 30);
        panel.add(userLabel);

        loginUserField = new JTextField();
        loginUserField.setBounds(350, 170, 150, 30);
        panel.add(loginUserField);

        JLabel passLabel = new JLabel("Пароль:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setBounds(200, 220, 150, 30);
        panel.add(passLabel);

        loginPassField = new JPasswordField();
        loginPassField.setBounds(350, 220, 150, 30);
        panel.add(loginPassField);

        JButton loginBtn = new JButton("🔓 Войти в систему");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBackground(new Color(46, 204, 113));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBounds(250, 280, 200, 40);
        loginBtn.addActionListener(e -> {
            String user = loginUserField.getText();
            String pass = new String(loginPassField.getPassword());
            if (user.equals("admin") && pass.equals("1234")) {
                currentUser = user;
                tabs.setEnabledAt(1, true);
                tabs.setEnabledAt(2, true);
                tabs.setEnabledAt(3, true);
                tabs.setSelectedIndex(1);
                JOptionPane.showMessageDialog(this, 
                    "✅ Добро пожаловать, " + user + "!", 
                    "Успешный вход", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Неверный логин или пароль!", 
                    "Ошибка входа", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(loginBtn);

        return panel;
    }

    private JPanel bookingPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("🎫 Бронирование билетов", JLabel.CENTER);
        title.setBounds(200, 20, 300, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(44, 62, 80));
        panel.add(title);

        JLabel nameLabel = new JLabel("👤 ФИО пассажира:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setBounds(100, 80, 150, 25);
        panel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(250, 80, 300, 25);
        panel.add(nameField);

        JLabel busLabel = new JLabel("🚍 Номер автобуса:");
        busLabel.setFont(new Font("Arial", Font.BOLD, 14));
        busLabel.setBounds(100, 120, 150, 25);
        panel.add(busLabel);

        busCombo = new JComboBox<>(new String[]{"BUS101 (Москва-СПб)", "BUS202 (СПб-Казань)", "BUS303 (Москва-Сочи)"});
        busCombo.setBounds(250, 120, 300, 25);
        busCombo.setBackground(Color.WHITE);
        panel.add(busCombo);

        JLabel seatLabel = new JLabel("💺 Количество мест:");
        seatLabel.setFont(new Font("Arial", Font.BOLD, 14));
        seatLabel.setBounds(100, 160, 150, 25);
        panel.add(seatLabel);

        seatField = new JTextField();
        seatField.setBounds(250, 160, 300, 25);
        panel.add(seatField);

        JLabel dateLabel = new JLabel("📅 Дата поездки (дд-мм-гггг):");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setBounds(100, 200, 200, 25);
        panel.add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(300, 200, 150, 25);
        panel.add(dateField);

        JLabel payLabel = new JLabel("💳 Способ оплаты:");
        payLabel.setFont(new Font("Arial", Font.BOLD, 14));
        payLabel.setBounds(100, 240, 150, 25);
        panel.add(payLabel);

        paymentCombo = new JComboBox<>(new String[]{"💵 Наличные", "📱 UPI", "💳 Банковская карта"});
        paymentCombo.setBounds(250, 240, 200, 25);
        paymentCombo.setBackground(Color.WHITE);
        panel.add(paymentCombo);

        // Информация о свободных местах
        JLabel availableLabel = new JLabel("Доступные места:");
        availableLabel.setFont(new Font("Arial", Font.BOLD, 12));
        availableLabel.setBounds(250, 280, 150, 20);
        panel.add(availableLabel);

        JLabel availableSeatsLabel = new JLabel(MAX_SEATS + " мест");
        availableSeatsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        availableSeatsLabel.setBounds(250, 300, 150, 20);
        panel.add(availableSeatsLabel);

        JButton bookBtn = new JButton("✅ Забронировать");
        bookBtn.setFont(new Font("Arial", Font.BOLD, 14));
        bookBtn.setBackground(new Color(46, 204, 113));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false);
        bookBtn.setBounds(250, 340, 200, 40);
        bookBtn.addActionListener(e -> handleBooking(availableSeatsLabel));
        panel.add(bookBtn);

        return panel;
    }

    private JPanel viewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("📋 Список забронированных билетов", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(44, 62, 80));
        panel.add(title, BorderLayout.NORTH);

        viewArea = new JTextArea();
        viewArea.setEditable(false);
        viewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panel.add(new JScrollPane(viewArea), BorderLayout.CENTER);

        JButton refresh = new JButton("🔄 Обновить список");
        refresh.setFont(new Font("Arial", Font.BOLD, 12));
        refresh.setBackground(new Color(52, 152, 219));
        refresh.setForeground(Color.WHITE);
        refresh.setFocusPainted(false);
        refresh.addActionListener(e -> loadTickets());
        panel.add(refresh, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel cancelPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("❌ Отмена бронирования", JLabel.CENTER);
        title.setBounds(200, 50, 300, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(44, 62, 80));
        panel.add(title);

        JLabel label = new JLabel("Введите номер билета для отмены:");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBounds(150, 150, 250, 25);
        panel.add(label);

        JTextField cancelField = new JTextField();
        cancelField.setBounds(150, 190, 250, 30);
        cancelField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(cancelField);

        JButton cancelBtn = new JButton("🗑️ Отменить бронь");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setBackground(new Color(231, 76, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBounds(180, 250, 200, 40);
        cancelBtn.addActionListener(e -> {
            String id = cancelField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Введите номер билета!", 
                    "Ошибка", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            File file = new File("ticket_" + id + ".txt");
            if (file.exists()) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Вы действительно хотите отменить билет " + id + "?", 
                    "Подтверждение отмены", 
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    file.delete();
                    JOptionPane.showMessageDialog(this, 
                        "✅ Билет " + id + " успешно отменен.", 
                        "Отмена выполнена", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadTickets();
                    cancelField.setText("");
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Билет с номером " + id + " не найден.", 
                    "Ошибка", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(cancelBtn);

        return panel;
    }

    private void handleBooking(JLabel availableSeatsLabel) {
        String name = nameField.getText().trim();
        String bus = (String) busCombo.getSelectedItem();
        String seatStr = seatField.getText().trim();
        String date = dateField.getText().trim();
        String payment = (String) paymentCombo.getSelectedItem();

        if (name.isEmpty() || seatStr.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "❌ Все поля обязательны для заполнения!", 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int seats;
        try {
            seats = Integer.parseInt(seatStr);
            if (seats <= 0 || seats > 5) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Количество мест должно быть от 1 до 5!", 
                    "Ошибка", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Введите корректное количество мест!", 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int booked = seatMap.getOrDefault(bus, 0);
        if (booked + seats > MAX_SEATS) {
            JOptionPane.showMessageDialog(this, 
                "❌ Недостаточно свободных мест на рейсе " + bus + 
                "\nСвободно: " + (MAX_SEATS - booked) + " мест", 
                "Ошибка бронирования", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int fare = seats * FARE_PER_SEAT;
        String ticketID = "TICK" + String.format("%04d", new Random().nextInt(9999));

        String ticket = "╔════════════════════════════════╗\n" +
                       "║     🎫 БИЛЕТ НА АВТОБУС        ║\n" +
                       "╠════════════════════════════════╣\n" +
                       "║ Номер билета: " + ticketID + "\n" +
                       "║ Пассажир: " + name + "\n" +
                       "║ Рейс: " + bus + "\n" +
                       "║ Мест: " + seats + "\n" +
                       "║ Дата: " + date + "\n" +
                       "║ Оплата: " + payment + "\n" +
                       "║ Стоимость: " + fare + " ₽\n" +
                       "╚════════════════════════════════╝";

        JOptionPane.showMessageDialog(this, ticket, "Предварительный просмотр", JOptionPane.INFORMATION_MESSAGE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ticket_" + ticketID + ".txt"))) {
            writer.write(ticket);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "❌ Ошибка при сохранении билета!", 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }

        seatMap.put(bus, booked + seats);
        availableSeatsLabel.setText("Свободно: " + (MAX_SEATS - (booked + seats)) + " мест");
        clearFields();
        loadTickets();
        
        JOptionPane.showMessageDialog(this, 
            "✅ Билет успешно забронирован!\nНомер билета: " + ticketID, 
            "Бронирование выполнено", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        nameField.setText("");
        seatField.setText("");
        dateField.setText("");
    }

    private void loadExistingBookings() {
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.startsWith("ticket_") && name.endsWith(".txt"));
        
        if (files != null) {
            for (File f : files) {
                // Здесь можно загрузить информацию о занятых местах
            }
        }
    }

    private void loadTickets() {
        viewArea.setText("");
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.startsWith("ticket_") && name.endsWith(".txt"));

        if (files != null && files.length > 0) {
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            
            for (File f : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        viewArea.append(line + "\n");
                    }
                    viewArea.append("\n" + "─".repeat(50) + "\n\n");
                } catch (IOException e) {
                    viewArea.append("❌ Ошибка чтения " + f.getName() + "\n");
                }
            }
        } else {
            viewArea.append("📭 Нет забронированных билетов.\n");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(Cedov10::new);
    }
}