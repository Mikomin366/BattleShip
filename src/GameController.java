import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class GameController {
    private final GameModel model;
    private final GameView view;
    private Timer computerTurnTimer;
    private boolean listenersInitialized = false;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    public void initialize() {
        if (!listenersInitialized) {
            setupEventListeners();
            listenersInitialized = true;
        }
        model.getEventManager().addListener(view);
        updateView();
        view.setPlacementMode(model.isPlacementMode());
    }

    private void setupEventListeners() {
        // Обработчики мыши для поля компьютера (выстрелы)
        view.setComputerBoardMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleComputerBoardClick(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleComputerBoardHover(e);
            }
        });

        // Обработчики мыши для поля игрока (расстановка кораблей)
        view.setPlayerBoardMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handlePlayerBoardClick(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handlePlayerBoardHover(e);
            }
        });

        // Обработчик кнопки поворота
        view.setRotateButtonListener(e -> {
            model.rotateCurrentShip();
            view.getPlayerBoardPanel().repaint();
        });

        // Обработчик кнопки авторасстановки
        view.setAutoPlaceButtonListener(e -> {
            int result = JOptionPane.showConfirmDialog(view,
                    "Автоматически расставить все корабли? Текущая расстановка будет потеряна.",
                    "Авторасстановка",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                model.resetGame();
                model.autoPlacePlayerShips();
                updateView();
                view.setPlacementMode(false);
                view.startAnimation();
            }
        });

        // Таймер для хода компьютера
        computerTurnTimer = new Timer(1500, e -> {
            if (!model.isPlayerTurn() && model.isGameStarted()) {
                model.computerShoot();
                updateView();

                if (model.isPlayerTurn()) {
                    view.setStatus("Ваш ход! Стреляйте по полю компьютера.");
                    view.startAnimation();
                }
            }
        });
    }

    private void handleComputerBoardClick(MouseEvent e) {
        if (!model.isGameStarted() || !model.isPlayerTurn()) return;

        Point cell = getCellFromCoordinates(e.getPoint(),
                view.getComputerBoardPanel().getWidth(),
                view.getComputerBoardPanel().getHeight());

        if (cell != null) {
            boolean hit = model.playerShoot(cell.x, cell.y);
            updateView();

            if (hit) {
                view.setStatus("Попадание! Стреляйте снова.");
                view.addExplosion(cell);
            } else {
                view.setStatus("Промах! Ход компьютера.");
                computerTurnTimer.start();
                view.stopAnimation();
            }
        }
    }

    private void handlePlayerBoardClick(MouseEvent e) {
        if (model.isGameStarted()) return;

        Point cell = getCellFromCoordinates(e.getPoint(),
                view.getPlayerBoardPanel().getWidth(),
                view.getPlayerBoardPanel().getHeight());

        if (cell != null) {
            boolean placed = model.placePlayerShip(cell.x, cell.y);
            updateView();

            if (placed && !model.isPlacementMode()) {
                // Все корабли расставлены, начинаем игру
                view.setPlacementMode(false);
                view.startAnimation();
            }
        }
    }

    private void handleComputerBoardHover(MouseEvent e) {
        if (!model.isGameStarted() || !model.isPlayerTurn()) return;

        Point cell = getCellFromCoordinates(e.getPoint(),
                view.getComputerBoardPanel().getWidth(),
                view.getComputerBoardPanel().getHeight());
        view.setHighlightedCell(cell);
    }

    private void handlePlayerBoardHover(MouseEvent e) {
        Point cell = getCellFromCoordinates(e.getPoint(),
                view.getPlayerBoardPanel().getWidth(),
                view.getPlayerBoardPanel().getHeight());
        view.setHighlightedCell(cell);
    }

    private Point getCellFromCoordinates(Point point, int width, int height) {
        int cellSize = Math.min(width, height) / (GameModel.BOARD_SIZE + 2);
        int offsetX = (width - cellSize * GameModel.BOARD_SIZE) / 2;
        int offsetY = (height - cellSize * GameModel.BOARD_SIZE) / 2;

        int x = (point.x - offsetX) / cellSize;
        int y = (point.y - offsetY) / cellSize;

        if (x >= 0 && x < GameModel.BOARD_SIZE && y >= 0 && y < GameModel.BOARD_SIZE) {
            return new Point(x, y);
        }
        return null;
    }

    public void applySettings(String difficulty, String selectedTheme) {
        model.applySettings(difficulty, selectedTheme);
        view.applyTheme();
    }

    public void resetGame() {
        model.resetGame();
        if (computerTurnTimer != null) {
            computerTurnTimer.stop();
        }
        view.stopAnimation();
        view.resetTimer();
        updateView();
        view.setPlacementMode(model.isPlacementMode());
    }

    private void updateView() {
        view.updateBoard(
                model.getPlayerBoard(),
                model.getComputerBoard(),
                model.getPlayerShots(),
                model.getComputerShots()
        );
        view.setScore(model.getPlayerScore(), model.getComputerScore());
        view.setPlacementInfo(model.getCurrentShipIndex(), model.getTotalShips());
    }

    public boolean isPlayerTurn() {
        return model.isPlayerTurn();
    }

    public GameView.GameBoardPanel getComputerBoardPanel() {
        return view.getComputerBoardPanel();
    }
}