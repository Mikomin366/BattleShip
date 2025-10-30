import javax.swing.*;
import java.awt.*;

class BattleShipGame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    public BattleShipGame() {
        setTitle("Морской бой");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel startScreen = createStartScreen();
        JPanel optionScreen = createOptionScreen();
        JPanel gameScreen = createGameScreen();

        cardPanel.add(startScreen, "START");
        cardPanel.add(optionScreen, "OPTIONS");
        cardPanel.add(gameScreen, "GAME");

        add(cardPanel);
        setVisible(true);
    }

    private JPanel createStartScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel labelPanel = new JPanel();
        JLabel game_title = new JLabel("Морской бой");
        game_title.setFont(new Font("Arial", Font.PLAIN, 50));
        labelPanel.add(game_title);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton start = new JButton("Старт");
        start.setPreferredSize(new Dimension(150, 50));
        start.addActionListener(e -> cardLayout.show(cardPanel, "GAME"));

        JButton options = new JButton("Настройки");
        options.setPreferredSize(new Dimension(150, 50));
        options.addActionListener(e -> cardLayout.show(cardPanel, "OPTIONS"));

        JButton exit = new JButton("Выход");
        exit.setPreferredSize(new Dimension(150, 50));
        exit.addActionListener(e -> System.exit(0));

        buttonsPanel.add(start, gbc);
        buttonsPanel.add(options, gbc);
        buttonsPanel.add(exit, gbc);

        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createOptionScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Настройки", JLabel.CENTER); // Добавляем выравнивание по центру
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        panel.add(title, BorderLayout.NORTH);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(4, 2, 10, 10));

        settingsPanel.add(new JLabel("Уровень сложности:"));
        JComboBox<String> difficulty = new JComboBox<>(new String[]{"Легкий", "Средний", "Сложный"});
        settingsPanel.add(difficulty);

        settingsPanel.add(new JLabel("Звук:"));
        JCheckBox sound = new JCheckBox("Включен", true);
        settingsPanel.add(sound);

        settingsPanel.add(new JLabel("Музыка:"));
        JCheckBox music = new JCheckBox("Включена", true);
        settingsPanel.add(music);

        settingsPanel.add(new JLabel("Цвет схемы:"));
        JComboBox<String> theme = new JComboBox<>(new String[]{"Стандартная", "Темная", "Синяя"});
        settingsPanel.add(theme);

        JPanel buttonPanel = new JPanel();
        JButton back = new JButton("Назад");
        back.addActionListener(e -> cardLayout.show(cardPanel, "START"));

        JButton apply = new JButton("Применить");
        apply.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Настройки применены!");
        });

        buttonPanel.add(back);
        buttonPanel.add(apply);

        panel.add(settingsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createGameScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel gameLabel = new JLabel("Игра Морской бой", JLabel.CENTER);
        gameLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        panel.add(gameLabel, BorderLayout.NORTH);

        JPanel gameBoard = new JPanel();
        gameBoard.setBackground(Color.white);
        gameBoard.add(new JLabel("Игровое поле будет здесь"));
        panel.add(gameBoard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton backToMenu = new JButton("В главное меню");
        backToMenu.addActionListener(e -> cardLayout.show(cardPanel, "START"));

        bottomPanel.add(backToMenu);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
}
