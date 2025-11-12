import java.util.EventObject;
import java.util.ArrayList;
import java.util.List;

interface GameEventListener {
    void onGameEvent(GameEvent event);
}

abstract class GameEvent extends EventObject {
    private final String message;

    public GameEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() { return message; }
}

class ScoreChangedEvent extends GameEvent {
    private final int newScore;

    public ScoreChangedEvent(Object source, String message, int newScore) {
        super(source, message);
        this.newScore = newScore;
    }

    public int getNewScore() { return newScore; }
}

class GameOverEvent extends GameEvent {
    private final boolean playerWon;

    public GameOverEvent(Object source, String message, boolean playerWon) {
        super(source, message);
        this.playerWon = playerWon;
    }

    public boolean isPlayerWon() { return playerWon; }
}

class ShipHitEvent extends GameEvent {
    private final int x;
    private final int y;
    private final boolean destroyed;

    public ShipHitEvent(Object source, String message, int x, int y, boolean destroyed) {
        super(source, message);
        this.x = x;
        this.y = y;
        this.destroyed = destroyed;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isDestroyed() { return destroyed; }
}

class EventManager {
    private final List<GameEventListener> listeners = new ArrayList<>();

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public void fireEvent(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.onGameEvent(event);
        }
    }
}