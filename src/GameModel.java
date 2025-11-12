import java.util.*;
import java.awt.Point;

class GameModel {
    public static final int BOARD_SIZE = 10;
    private static final int[] SHIP_SIZES = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

    private final int[][] playerBoard;
    private final int[][] computerBoard;
    private final boolean[][] playerShots;
    private final boolean[][] computerShots;
    private int playerScore;
    private int computerScore;
    private int shipsPlaced;
    private boolean gameStarted;
    private boolean playerTurn;
    private String difficulty;
    private boolean placementMode;
    private int currentShipIndex;
    private boolean currentShipHorizontal;

    private final EventManager eventManager;
    private final Random random;
    private final ArrayList<Point> playerShipPositions;

    public GameModel() {
        playerBoard = new int[BOARD_SIZE][BOARD_SIZE];
        computerBoard = new int[BOARD_SIZE][BOARD_SIZE];
        playerShots = new boolean[BOARD_SIZE][BOARD_SIZE];
        computerShots = new boolean[BOARD_SIZE][BOARD_SIZE];
        eventManager = new EventManager();
        random = new Random();
        playerShipPositions = new ArrayList<>();
        difficulty = "Средний";
        placementMode = true;
        currentShipIndex = 0;
        currentShipHorizontal = true;
        resetGame();
    }

    public void resetGame() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            Arrays.fill(playerBoard[i], 0);
            Arrays.fill(computerBoard[i], 0);
            Arrays.fill(playerShots[i], false);
            Arrays.fill(computerShots[i], false);
        }

        playerScore = 0;
        computerScore = 0;
        shipsPlaced = 0;
        gameStarted = false;
        playerTurn = true;
        placementMode = true;
        currentShipIndex = 0;
        currentShipHorizontal = true;
        playerShipPositions.clear();

        generateComputerShips();
        // Убрали autoPlacePlayerShips() - теперь игрок расставляет сам

        eventManager.fireEvent(new ScoreChangedEvent(this, "Расставьте ваши корабли! Текущий корабль: " + getCurrentShipSize() + " клетки", playerScore));
    }

    private void generateComputerShips() {
        for (int shipSize : SHIP_SIZES) {
            boolean placed = false;
            while (!placed) {
                int x = random.nextInt(BOARD_SIZE);
                int y = random.nextInt(BOARD_SIZE);
                boolean horizontal = random.nextBoolean();

                if (canPlaceShip(computerBoard, x, y, shipSize, horizontal, true)) {
                    placeShip(computerBoard, x, y, shipSize, horizontal);
                    placed = true;
                }
            }
        }
    }

    public boolean placePlayerShip(int x, int y) {
        if (!placementMode || gameStarted) return false;

        int shipSize = SHIP_SIZES[currentShipIndex];

        if (canPlaceShip(playerBoard, x, y, shipSize, currentShipHorizontal, false)) {
            placeShip(playerBoard, x, y, shipSize, currentShipHorizontal);

            // Сохраняем позиции корабля
            for (int i = 0; i < shipSize; i++) {
                int shipX = currentShipHorizontal ? x + i : x;
                int shipY = currentShipHorizontal ? y : y + i;
                playerShipPositions.add(new Point(shipX, shipY));
            }

            shipsPlaced++;
            currentShipIndex++;

            if (currentShipIndex < SHIP_SIZES.length) {
                eventManager.fireEvent(new ScoreChangedEvent(this,
                        "Корабль размещен! Следующий: " + getCurrentShipSize() + " клетки. " +
                                "Кораблей осталось: " + (SHIP_SIZES.length - currentShipIndex), playerScore));
            } else {
                placementMode = false;
                gameStarted = true;
                eventManager.fireEvent(new ScoreChangedEvent(this,
                        "Все корабли расставлены! Игра начинается! Ваш ход.", playerScore));
            }
            return true;
        }
        return false;
    }

    public boolean canPlaceShip(int[][] board, int x, int y, int size, boolean horizontal, boolean forComputer) {
        // Проверка выхода за границы
        if (horizontal) {
            if (x + size > BOARD_SIZE) return false;
        } else {
            if (y + size > BOARD_SIZE) return false;
        }

        // Проверка занятости клеток и области вокруг
        for (int i = -1; i <= size; i++) {
            for (int j = -1; j <= 1; j++) {
                int checkX, checkY;

                if (horizontal) {
                    checkX = x + i;
                    checkY = y + j;
                } else {
                    checkX = x + j;
                    checkY = y + i;
                }

                // Проверяем только для игрока (для компьютера можно ослабить правила)
                if (checkX >= 0 && checkX < BOARD_SIZE && checkY >= 0 && checkY < BOARD_SIZE) {
                    if (board[checkX][checkY] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placeShip(int[][] board, int x, int y, int size, boolean horizontal) {
        if (horizontal) {
            for (int i = 0; i < size; i++) {
                board[x + i][y] = size;
            }
        } else {
            for (int i = 0; i < size; i++) {
                board[x][y + i] = size;
            }
        }
    }

    public void rotateCurrentShip() {
        if (placementMode) {
            currentShipHorizontal = !currentShipHorizontal;
            eventManager.fireEvent(new ScoreChangedEvent(this,
                    "Ориентация изменена. Текущий корабль: " + getCurrentShipSize() + " клетки (" +
                            (currentShipHorizontal ? "горизонтально" : "вертикально") + ")", playerScore));
        }
    }

    public int getCurrentShipSize() {
        if (currentShipIndex < SHIP_SIZES.length) {
            return SHIP_SIZES[currentShipIndex];
        }
        return 0;
    }

    public boolean playerShoot(int x, int y) {
        if (!gameStarted || !playerTurn || playerShots[x][y]) return false;

        playerShots[x][y] = true;

        if (computerBoard[x][y] > 0) {
            playerScore++;
            boolean destroyed = checkShipDestroyed(computerBoard, playerShots, x, y);

            eventManager.fireEvent(new ShipHitEvent(this,
                    "Попадание! Стреляйте снова.", x, y, destroyed));
            eventManager.fireEvent(new ScoreChangedEvent(this,
                    "Счет: " + playerScore + " - " + computerScore, playerScore));

            if (playerScore == getTotalShipCells()) {
                eventManager.fireEvent(new GameOverEvent(this,
                        "Поздравляем! Вы победили!", true));
                gameStarted = false;
            }
            return true;
        } else {
            eventManager.fireEvent(new ScoreChangedEvent(this,
                    "Промах! Ход компьютера.", playerScore));
            playerTurn = false;
            return false;
        }
    }

    public void computerShoot() {
        if (!gameStarted || playerTurn) return;

        int x, y;

        switch (difficulty) {
            case "Легкий":
                do {
                    x = random.nextInt(BOARD_SIZE);
                    y = random.nextInt(BOARD_SIZE);
                } while (computerShots[x][y]);
                break;
            case "Сложный":
                Point smartShot = findSmartShot();
                if (smartShot != null) {
                    x = smartShot.x;
                    y = smartShot.y;
                } else {
                    do {
                        x = random.nextInt(BOARD_SIZE);
                        y = random.nextInt(BOARD_SIZE);
                    } while (computerShots[x][y]);
                }
                break;
            default:
                if (random.nextDouble() < 0.7) {
                    do {
                        x = random.nextInt(BOARD_SIZE);
                        y = random.nextInt(BOARD_SIZE);
                    } while (computerShots[x][y]);
                } else {
                    Point smartShot2 = findSmartShot();
                    if (smartShot2 != null) {
                        x = smartShot2.x;
                        y = smartShot2.y;
                    } else {
                        do {
                            x = random.nextInt(BOARD_SIZE);
                            y = random.nextInt(BOARD_SIZE);
                        } while (computerShots[x][y]);
                    }
                }
                break;
        }

        computerShots[x][y] = true;

        if (playerBoard[x][y] > 0) {
            computerScore++;
            boolean destroyed = checkShipDestroyed(playerBoard, computerShots, x, y);

            eventManager.fireEvent(new ShipHitEvent(this,
                    "Компьютер попал в ваше судно!", x, y, destroyed));
            eventManager.fireEvent(new ScoreChangedEvent(this,
                    "Счет: " + playerScore + " - " + computerScore, playerScore));

            if (computerScore == getTotalShipCells()) {
                eventManager.fireEvent(new GameOverEvent(this,
                        "Компьютер победил! Попробуйте еще раз.", false));
                gameStarted = false;
            }
        } else {
            playerTurn = true;
            eventManager.fireEvent(new ScoreChangedEvent(this,
                    "Компьютер промахнулся! Ваш ход.", playerScore));
        }
    }

    private Point findSmartShot() {
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (computerShots[x][y] && playerBoard[x][y] > 0) {
                    int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};
                    for (int[] dir : directions) {
                        int newX = x + dir[0];
                        int newY = y + dir[1];
                        if (newX >= 0 && newX < BOARD_SIZE && newY >= 0 && newY < BOARD_SIZE &&
                                !computerShots[newX][newY]) {
                            return new Point(newX, newY);
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean checkShipDestroyed(int[][] board, boolean[][] shots, int x, int y) {
        int shipSize = board[x][y];
        // Проверяем все клетки корабля
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == shipSize && !shots[i][j]) {
                    return false; // Нашли неподбитую клетку корабля
                }
            }
        }
        return true; // Все клетки корабля подбиты
    }

    private int getTotalShipCells() {
        int total = 0;
        for (int size : SHIP_SIZES) {
            total += size;
        }
        return total;
    }

    public void applySettings(String difficulty,  String theme) {
        this.difficulty = difficulty;
    }

    public void autoPlacePlayerShips() {
        for (int shipSize : SHIP_SIZES) {
            boolean placed = false;
            while (!placed) {
                int x = random.nextInt(BOARD_SIZE);
                int y = random.nextInt(BOARD_SIZE);
                boolean horizontal = random.nextBoolean();

                if (canPlaceShip(playerBoard, x, y, shipSize, horizontal, false)) {
                    placeShip(playerBoard, x, y, shipSize, horizontal);
                    for (int i = 0; i < shipSize; i++) {
                        int shipX = horizontal ? x + i : x;
                        int shipY = horizontal ? y : y + i;
                        playerShipPositions.add(new Point(shipX, shipY));
                    }
                    placed = true;
                }
            }
        }
        shipsPlaced = SHIP_SIZES.length;
        placementMode = false;
        gameStarted = true;
        currentShipIndex = SHIP_SIZES.length;
    }

    // Getters
    public int[][] getPlayerBoard() { return playerBoard; }
    public int[][] getComputerBoard() { return computerBoard; }
    public boolean[][] getPlayerShots() { return playerShots; }
    public boolean[][] getComputerShots() { return computerShots; }
    public int getPlayerScore() { return playerScore; }
    public int getComputerScore() { return computerScore; }
    public boolean isGameStarted() { return gameStarted; }
    public boolean isPlayerTurn() { return playerTurn; }
    public EventManager getEventManager() { return eventManager; }
    public boolean isPlacementMode() { return placementMode; }
    public int getCurrentShipIndex() { return currentShipIndex; }
    public boolean isCurrentShipHorizontal() { return currentShipHorizontal; }
    public int getTotalShips() { return SHIP_SIZES.length; }

    public int getShipsPlaced() {
        return shipsPlaced;
    }

    public void setShipsPlaced(int shipsPlaced) {
        this.shipsPlaced = shipsPlaced;
    }
}