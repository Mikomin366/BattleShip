import java.util.EventObject;
import java.util.*;

interface GameEventListener{
    void onGameEvent(GameEvent event);
}
abstract class GameEvent extends EventObject {
    private String message;

    public GameEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() { return message; }
}

class ScoreChangedEvent extends GameEvent {
    private int newScore;

    public ScoreChangedEvent(Object source, String message, int newScore){
        super(source, message);
        this.newScore = newScore;
    }

    public int getNewScore() { return newScore;}
}

class ShipHitEvent extends GameEvent {
    private int x, y;
    private boolean destroyed;

    public ShipHitEvent(Object source, String message, int x, int y, boolean destroyed){
        super(source, message);
        this.x = x;
        this.y = y;
        this.destroyed = destroyed;
    }

    public int getX() {return x;}
    public int getY() {return y;}
    public boolean getDestroyed() {return destroyed;}
}

class GameOverEvent extends GameEvent {
    private boolean gameOver;

    public GameOverEvent(Object source, String message, boolean gameOver) {
        super(source, message);
        this.gameOver = gameOver;
    }

    public boolean getGameOver() {return gameOver;}
}

class CustomEvents {
    private List<GameEventListener> listeners = new ArrayList<>();

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    public void fireEvent(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.onGameEvent(event);
        }
    }
}
