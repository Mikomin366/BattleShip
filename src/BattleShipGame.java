import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class BattleShipGame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final GameController gameController;
    private static GameModel gameModel;
    private final GameView gameView;

    private ColorTheme currentTheme = ColorTheme.STANDARD;
    private String currentDifficulty = "Средний";

    // Переменные для хранения экранов
    private JPanel startScreen;
    private JPanel optionScreen;
    private JPanel gameScreen;

    public BattleShipGame() {
        setTitle("Морской бой");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Инициализация игровых компонентов
        gameModel = new GameModel();
        gameView = new GameView();
        gameController = new GameController(gameModel, gameView);

        createScreens();

        cardPanel.add(startScreen, "START");
        cardPanel.add(optionScreen, "OPTIONS");
        cardPanel.add(gameScreen, "GAME");

        add(cardPanel);
        applyTheme(currentTheme);
        setVisible(true);
    }

    private void createScreens() {
        startScreen = createStartScreen();
        optionScreen = createOptionScreen();
        gameScreen = createGameScreen();
    }

    public static GameModel getGameModel() {
        return gameModel;
    }

    private JPanel createStartScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(currentTheme.backgroundColor);

        JPanel labelPanel = new JPanel();
        labelPanel.setBackground(currentTheme.backgroundColor);
        JLabel game_title = new JLabel("Морской бой");
        game_title.setFont(new Font("Arial", Font.BOLD, 60));
        game_title.setForeground(currentTheme.titleColor);
        labelPanel.add(game_title);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        buttonsPanel.setBackground(currentTheme.backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton start = createStyledButton("Старт", currentTheme.primaryButtonColor);
        start.addActionListener(e -> {
            gameController.resetGame();
            cardLayout.show(cardPanel, "GAME");
        });

        JButton options = createStyledButton("Настройки", currentTheme.secondaryButtonColor);
        options.addActionListener(e -> cardLayout.show(cardPanel, "OPTIONS"));

        JButton exit = createStyledButton("Выход", currentTheme.exitButtonColor);
        exit.addActionListener(e -> System.exit(0));

        buttonsPanel.add(start, gbc);
        buttonsPanel.add(options, gbc);
        buttonsPanel.add(exit, gbc);

        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 60));
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private JPanel createOptionScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(currentTheme.backgroundColor);

        JLabel title = new JLabel("Настройки", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(currentTheme.titleColor);
        panel.add(title, BorderLayout.NORTH);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(4, 2, 15, 15));
        settingsPanel.setBackground(currentTheme.backgroundColor);
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        settingsPanel.add(createStyledLabel("Уровень сложности:"));
        JComboBox<String> difficulty = new JComboBox<>(new String[]{"Легкий", "Средний", "Сложный"});
        difficulty.setSelectedItem(currentDifficulty);
        difficulty.setFont(new Font("Arial", Font.PLAIN, 16));
        difficulty.setBackground(Color.WHITE);
        settingsPanel.add(difficulty);

        settingsPanel.add(createStyledLabel("Цвет схемы:"));
        JComboBox<String> theme = new JComboBox<>(new String[]{"Стандартная", "Темная"});
        theme.setSelectedItem(currentTheme.displayName);
        theme.setFont(new Font("Arial", Font.PLAIN, 16));
        theme.setBackground(Color.WHITE);
        settingsPanel.add(theme);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(currentTheme.backgroundColor);
        JButton back = createStyledButton("Назад", currentTheme.secondaryButtonColor);
        back.addActionListener(e -> cardLayout.show(cardPanel, "START"));

        JButton apply = createStyledButton("Применить", currentTheme.primaryButtonColor);
        apply.addActionListener(e -> {
            String selectedDifficulty = (String) difficulty.getSelectedItem();
            String selectedTheme = (String) theme.getSelectedItem();

            applySettings(selectedDifficulty, selectedTheme);

            JOptionPane.showMessageDialog(this,
                    "Настройки применены!\n" +
                            "Сложность: " + selectedDifficulty + "\n" +
                            "Тема: " + selectedTheme);
        });

        buttonPanel.add(back);
        buttonPanel.add(apply);

        panel.add(settingsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(currentTheme.textColor);
        return label;
    }

    private JPanel createGameScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(currentTheme.backgroundColor);

        // Добавляем игровую панель
        gameController.initialize();
        panel.add(gameView, BorderLayout.CENTER);

        // Панель управления в игре
        JPanel gameControlPanel = new JPanel(new FlowLayout());
        gameControlPanel.setBackground(currentTheme.backgroundColor);

        JButton backToMenu = createStyledButton("В главное меню", currentTheme.secondaryButtonColor);
        backToMenu.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Вернуться в главное меню? Текущая игра будет потеряна.",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                cardLayout.show(cardPanel, "START");
            }
        });

        JButton restartGame = createStyledButton("Новая игра", currentTheme.primaryButtonColor);
        restartGame.addActionListener(e -> gameController.resetGame());

        gameControlPanel.add(backToMenu);
        gameControlPanel.add(restartGame);

        panel.add(gameControlPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void applySettings(String difficulty, String theme) {
        this.currentDifficulty = difficulty;

        ColorTheme newTheme = ColorTheme.fromDisplayName(theme);
        if (newTheme != null && !newTheme.equals(currentTheme)) {
            currentTheme = newTheme;
            applyTheme(currentTheme);
            updateScreensTheme();
        }

        gameController.applySettings(difficulty, theme);
    }

    private void applyTheme(ColorTheme theme) {
        SwingUtilities.invokeLater(() -> {
            getContentPane().setBackground(theme.backgroundColor);

            UIManager.put("Panel.background", theme.backgroundColor);
            UIManager.put("OptionPane.background", theme.backgroundColor);
            UIManager.put("OptionPane.messageForeground", theme.textColor);
            UIManager.put("Button.background", theme.primaryButtonColor);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Label.foreground", theme.textColor);
            UIManager.put("CheckBox.background", theme.backgroundColor);
            UIManager.put("CheckBox.foreground", theme.textColor);
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("ComboBox.foreground", Color.BLACK);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.foreground", Color.BLACK);

            SwingUtilities.updateComponentTreeUI(this);
        });
    }

    private void updateScreensTheme() {
        // Обновляем тему существующих экранов вместо их пересоздания
        updatePanelTheme(startScreen);
        updatePanelTheme(optionScreen);
        updatePanelTheme(gameScreen);

        // Обновляем GameView
        if (gameView != null) {
            gameView.applyTheme();
        }

        cardPanel.revalidate();
        cardPanel.repaint();
    }

    private void updatePanelTheme(JPanel panel) {
        if (panel == null) return;

        panel.setBackground(currentTheme.backgroundColor);

        // Рекурсивно обновляем все компоненты панели
        Component[] components = panel.getComponents();
        for (Component component : components) {
            updateComponentTheme(component);
        }
    }

    private void updateComponentTheme(Component component) {
        if (component instanceof JPanel) {
            updatePanelTheme((JPanel) component);
        } else if (component instanceof JLabel label) {
            // Не меняем цвет заголовков игры
            if (!label.getText().equals("Морской бой") && !label.getText().equals("Настройки")) {
                label.setForeground(currentTheme.textColor);
            } else {
                label.setForeground(currentTheme.titleColor);
            }
        } else if (component instanceof JButton button) {
            String text = button.getText();
            // Определяем цвет кнопки по ее тексту
            switch (text) {
                case "Старт", "Применить", "Новая игра" -> button.setBackground(currentTheme.primaryButtonColor);
                case "Настройки", "Назад", "В главное меню" -> button.setBackground(currentTheme.secondaryButtonColor);
                case "Выход" -> button.setBackground(currentTheme.exitButtonColor);
            }
            button.setForeground(Color.WHITE);
        }
    }

    public ColorTheme getCurrentTheme() {
        return currentTheme;
    }

    public String getCurrentDifficulty() {
        return currentDifficulty;
    }
}