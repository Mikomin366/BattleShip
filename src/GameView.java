import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class GameView extends JPanel implements GameEventListener {
    private GameBoardPanel playerBoardPanel;
    private GameBoardPanel computerBoardPanel;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JLabel placementLabel;
    private JTextField statusField;
    private Timer animationTimer;
    private Timer gameTimer;
    private int gameTime;
    private boolean cellHighlighted = false;
    private final java.util.List<Point> explosionPoints;
    private JButton rotateButton;
    private JButton autoPlaceButton;
    private JPanel controlPanel;

    public GameView() {
        setLayout(new BorderLayout());
        initializeUI();
        explosionPoints = new ArrayList<>();
        applyCurrentTheme();
    }

    private void initializeUI() {
        createControlPanel();
        createGameBoards();
        setupTimers();
    }

    private void createControlPanel() {
        controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Сначала создаем все компоненты
        statusLabel = new JLabel("Статус:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        statusField = new JTextField(30);
        statusField.setEditable(false);
        statusField.setFont(new Font("Arial", Font.PLAIN, 14));
        statusField.setText("Расставьте ваши корабли!");

        scoreLabel = new JLabel("Счет: 0 - 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));

        timerLabel = new JLabel("Время: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));

        placementLabel = new JLabel("Корабли: 0/10");
        placementLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Кнопка поворота корабля
        rotateButton = new JButton("Повернуть корабль");
        rotateButton.setFont(new Font("Arial", Font.PLAIN, 14));

        // Кнопка авторасстановки
        autoPlaceButton = new JButton("Авторасстановка");
        autoPlaceButton.setFont(new Font("Arial", Font.PLAIN, 14));

        // Добавляем компоненты на панель
        controlPanel.add(statusLabel);
        controlPanel.add(statusField);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(scoreLabel);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(timerLabel);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(placementLabel);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(rotateButton);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(autoPlaceButton);

        add(controlPanel, BorderLayout.NORTH);

        // Применяем тему после создания всех компонентов
        applyControlPanelTheme();
    }

    private void applyControlPanelTheme() {
        ColorTheme theme = getCurrentTheme();
        if (controlPanel != null) {
            controlPanel.setBackground(theme.backgroundColor);
        }
        if (statusLabel != null) {
            statusLabel.setForeground(theme.textColor);
        }
        if (scoreLabel != null) {
            scoreLabel.setForeground(theme.textColor);
        }
        if (timerLabel != null) {
            timerLabel.setForeground(theme.textColor);
        }
        if (placementLabel != null) {
            placementLabel.setForeground(theme.titleColor);
        }
        if (rotateButton != null) {
            rotateButton.setBackground(theme.primaryButtonColor);
            rotateButton.setForeground(Color.WHITE);
        }
        if (autoPlaceButton != null) {
            autoPlaceButton.setBackground(theme.secondaryButtonColor);
            autoPlaceButton.setForeground(Color.WHITE);
        }
    }

    private void createGameBoards() {
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        playerBoardPanel = new GameBoardPanel("Ваше поле", false);
        computerBoardPanel = new GameBoardPanel("Поле компьютера", true);

        boardsPanel.add(playerBoardPanel);
        boardsPanel.add(computerBoardPanel);

        add(boardsPanel, BorderLayout.CENTER);

        // Применяем тему к boardsPanel
        applyBoardsPanelTheme();
    }

    private void applyBoardsPanelTheme() {
        ColorTheme theme = getCurrentTheme();
        Container boardsPanel = (Container) getComponent(1); // Второй компонент - это boardsPanel
        if (boardsPanel != null) {
            boardsPanel.setBackground(theme.backgroundColor);
        }
    }

    private void setupTimers() {
        animationTimer = new Timer(500, e -> {
            cellHighlighted = !cellHighlighted;
            if (computerBoardPanel != null) {
                computerBoardPanel.repaint();
            }
        });

        gameTimer = new Timer(1000, e -> {
            gameTime++;
            updateTimer();
        });
    }

    private void updateTimer() {
        int minutes = gameTime / 60;
        int seconds = gameTime % 60;
        if (timerLabel != null) {
            timerLabel.setText(String.format("Время: %02d:%02d", minutes, seconds));
        }
    }

    public void setComputerBoardMouseListener(MouseAdapter listener) {
        if (computerBoardPanel != null) {
            computerBoardPanel.setMouseListener(listener);
        }
    }

    public void setPlayerBoardMouseListener(MouseAdapter listener) {
        if (playerBoardPanel != null) {
            playerBoardPanel.setMouseListener(listener);
        }
    }

    public void setRotateButtonListener(ActionListener listener) {
        if (rotateButton != null) {
            rotateButton.addActionListener(listener);
        }
    }

    public void setAutoPlaceButtonListener(ActionListener listener) {
        if (autoPlaceButton != null) {
            autoPlaceButton.addActionListener(listener);
        }
    }

    public void updateBoard(int[][] playerBoard, int[][] computerBoard,
                            boolean[][] playerShots, boolean[][] computerShots) {
        if (playerBoardPanel != null) {
            playerBoardPanel.updateBoard(playerBoard, computerShots);
        }
        if (computerBoardPanel != null) {
            computerBoardPanel.updateBoard(computerBoard, playerShots);
        }
    }

    public void setStatus(String status) {
        if (statusField != null) {
            statusField.setText(status);
        }
    }

    public void setScore(int playerScore, int computerScore) {
        if (scoreLabel != null) {
            scoreLabel.setText("Счет: " + playerScore + " - " + computerScore);
        }
    }

    public void setPlacementInfo(int placed, int total) {
        if (placementLabel != null) {
            placementLabel.setText("Корабли: " + placed + "/" + total);
        }
    }

    public void setHighlightedCell(Point cell) {
        if (computerBoardPanel != null) {
            computerBoardPanel.setHighlightedCell(cell);
        }
        if (playerBoardPanel != null) {
            playerBoardPanel.setHighlightedCell(cell);
        }
    }

    public void startAnimation() {
        if (animationTimer != null) {
            animationTimer.start();
        }
        if (gameTimer != null) {
            gameTimer.start();
        }
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    public void resetTimer() {
        gameTime = 0;
        updateTimer();
    }

    public void addExplosion(Point point) {
        explosionPoints.add(point);
        Timer explosionTimer = new Timer(300, e -> {
            explosionPoints.remove(point);
            if (computerBoardPanel != null) {
                computerBoardPanel.repaint();
            }
        });
        explosionTimer.setRepeats(false);
        explosionTimer.start();
    }

    public void setPlacementMode(boolean placementMode) {
        if (rotateButton != null) {
            rotateButton.setVisible(placementMode);
        }
        if (autoPlaceButton != null) {
            autoPlaceButton.setVisible(placementMode);
        }
        if (!placementMode && playerBoardPanel != null) {
            playerBoardPanel.setShowPlacementPreview(false);
        }
    }

    public void applyTheme() {
        applyCurrentTheme();
    }

    private void applyCurrentTheme() {
        ColorTheme theme = getCurrentTheme();
        setBackground(theme.backgroundColor);
        applyControlPanelTheme();
        applyBoardsPanelTheme();

        if (playerBoardPanel != null) {
            playerBoardPanel.applyTheme();
        }
        if (computerBoardPanel != null) {
            computerBoardPanel.applyTheme();
        }
        repaint();
    }

    private ColorTheme getCurrentTheme() {
        // Находим экземпляр BattleShipGame через родительские компоненты
        Container parent = getParent();
        while (parent != null && !(parent instanceof BattleShipGame)) {
            parent = parent.getParent();
        }
        if (parent != null) {
            return ((BattleShipGame) parent).getCurrentTheme();
        }
        return ColorTheme.STANDARD;
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (event instanceof ScoreChangedEvent scoreEvent) {
            setStatus(scoreEvent.getMessage());
        } else if (event instanceof GameOverEvent gameOverEvent) {
            String message = gameOverEvent.getMessage();
            JOptionPane.showMessageDialog(this, message, "Игра окончена",
                    JOptionPane.INFORMATION_MESSAGE);
            stopAnimation();
        } else if (event instanceof ShipHitEvent hitEvent) {
            setStatus(hitEvent.getMessage());
            if (hitEvent.isDestroyed()) {
                addExplosion(new Point(hitEvent.getX(), hitEvent.getY()));
            }
        }
    }

    public GameBoardPanel getComputerBoardPanel() {
        return computerBoardPanel;
    }

    public GameBoardPanel getPlayerBoardPanel() {
        return playerBoardPanel;
    }

    class GameBoardPanel extends JPanel {
        private final String title;
        private final boolean interactive;
        private int[][] board;
        private boolean[][] shots;
        private Point highlightedCell;
        private MouseAdapter mouseListener;
        private boolean showPlacementPreview;

        public GameBoardPanel(String title, boolean interactive) {
            this.title = title;
            this.interactive = interactive;
            setPreferredSize(new Dimension(400, 400));
            this.showPlacementPreview = !interactive;
            applyTheme();
        }

        public void applyTheme() {
            ColorTheme theme = getCurrentTheme();
            setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(theme.gridColor, 2),
                    title, 0, 0, new Font("Arial", Font.BOLD, 16), theme.titleColor));
            setBackground(theme.boardBackground);
            repaint();
        }

        public void updateBoard(int[][] board, boolean[][] shots) {
            this.board = board;
            this.shots = shots;
            repaint();
        }

        public void setMouseListener(MouseAdapter listener) {
            if (mouseListener != null) {
                removeMouseListener(mouseListener);
                removeMouseMotionListener(mouseListener);
            }
            mouseListener = listener;
            addMouseListener(listener);
            addMouseMotionListener(listener);
        }

        public void setHighlightedCell(Point cell) {
            highlightedCell = cell;
            repaint();
        }

        public void setShowPlacementPreview(boolean show) {
            this.showPlacementPreview = show;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            drawBoard(g2d);
        }

        private void drawBoard(Graphics2D g2d) {
            ColorTheme theme = getCurrentTheme();
            int cellSize = Math.min(getWidth(), getHeight()) / (GameModel.BOARD_SIZE + 2);
            int offsetX = (getWidth() - cellSize * GameModel.BOARD_SIZE) / 2;
            int offsetY = (getHeight() - cellSize * GameModel.BOARD_SIZE) / 2;

            // Рисование сетки
            g2d.setColor(theme.gridColor);
            g2d.setStroke(new BasicStroke(1.5f));
            for (int i = 0; i <= GameModel.BOARD_SIZE; i++) {
                g2d.drawLine(offsetX + i * cellSize, offsetY,
                        offsetX + i * cellSize, offsetY + GameModel.BOARD_SIZE * cellSize);
                g2d.drawLine(offsetX, offsetY + i * cellSize,
                        offsetX + GameModel.BOARD_SIZE * cellSize, offsetY + i * cellSize);
            }

            // Рисование координат
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.setColor(theme.gridColor);
            for (int i = 0; i < GameModel.BOARD_SIZE; i++) {
                g2d.drawString(String.valueOf((char)('А' + i)),
                        offsetX + i * cellSize + cellSize/2 - 3, offsetY - 5);
                g2d.drawString(String.valueOf(i + 1),
                        offsetX - 15, offsetY + i * cellSize + cellSize/2 + 3);
            }

            // Рисование содержимого клеток
            if (board != null && shots != null) {
                for (int x = 0; x < GameModel.BOARD_SIZE; x++) {
                    for (int y = 0; y < GameModel.BOARD_SIZE; y++) {
                        int cellX = offsetX + x * cellSize + 1;
                        int cellY = offsetY + y * cellSize + 1;
                        int drawSize = cellSize - 2;

                        // Подсветка клетки при наведении
                        if (highlightedCell != null && highlightedCell.x == x &&
                                highlightedCell.y == y && interactive) {
                            g2d.setColor(new Color(255, 255, 0, 100));
                            g2d.fillRect(cellX, cellY, drawSize, drawSize);
                        }

                        // Корабли игрока (только на своем поле)
                        if (board[x][y] > 0 && !interactive) {
                            g2d.setColor(theme.primaryButtonColor);
                            g2d.fillRect(cellX + 2, cellY + 2, drawSize - 4, drawSize - 4);

                            g2d.setColor(theme.gridColor);
                            g2d.drawRect(cellX + 2, cellY + 2, drawSize - 4, drawSize - 4);
                        }

                        // Выстрелы
                        if (shots[x][y]) {
                            if (board[x][y] > 0) {
                                g2d.setColor(Color.RED);
                                g2d.fillOval(cellX + 5, cellY + 5, drawSize - 10, drawSize - 10);
                                g2d.setColor(Color.WHITE);
                                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                                g2d.drawString("✕", cellX + cellSize/2 - 5, cellY + cellSize/2 + 5);
                            } else {
                                g2d.setColor(Color.BLUE);
                                g2d.drawOval(cellX + 5, cellY + 5, drawSize - 10, drawSize - 10);
                                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                                g2d.drawString("•", cellX + cellSize/2 - 3, cellY + cellSize/2 + 3);
                            }
                        }
                    }
                }
            }

            // Превью размещения корабля (только для поля игрока в режиме расстановки)
            if (showPlacementPreview && highlightedCell != null && !interactive) {
                GameModel model = BattleShipGame.getGameModel();
                if (model != null && model.isPlacementMode()) {
                    int shipSize = model.getCurrentShipSize();
                    boolean horizontal = model.isCurrentShipHorizontal();

                    // Проверяем можно ли разместить корабль
                    boolean canPlace = model.canPlaceShip(model.getPlayerBoard(),
                            highlightedCell.x, highlightedCell.y, shipSize, horizontal, false);

                    g2d.setColor(canPlace ? new Color(0, 255, 0, 100) : new Color(255, 0, 0, 100));

                    for (int i = 0; i < shipSize; i++) {
                        int previewX, previewY;
                        if (horizontal) {
                            previewX = highlightedCell.x + i;
                            previewY = highlightedCell.y;
                        } else {
                            previewX = highlightedCell.x;
                            previewY = highlightedCell.y + i;
                        }

                        if (previewX < GameModel.BOARD_SIZE && previewY < GameModel.BOARD_SIZE) {
                            int cellX = offsetX + previewX * cellSize + 1;
                            int cellY = offsetY + previewY * cellSize + 1;
                            g2d.fillRect(cellX, cellY, cellSize - 2, cellSize - 2);
                        }
                    }
                }
            }

            // Анимация - мигающая рамка для активного поля
            if (cellHighlighted && interactive) {
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(offsetX - 3, offsetY - 3,
                        GameModel.BOARD_SIZE * cellSize + 6,
                        GameModel.BOARD_SIZE * cellSize + 6);
            }

            // Рисование взрывов
            for (Point explosion : explosionPoints) {
                int cellX = offsetX + explosion.x * cellSize + cellSize/2;
                int cellY = offsetY + explosion.y * cellSize + cellSize/2;

                g2d.setColor(Color.ORANGE);
                for (int i = 0; i < 5; i++) {
                    int size = 10 + i * 2;
                    g2d.setColor(new Color(255, 165, 0, 200 - i * 40));
                    g2d.fillOval(cellX - size/2, cellY - size/2, size, size);
                }
            }
        }
    }
}
