package wordle;

public class GameOverException extends RuntimeException {
    public GameOverException(String gameId) {
        super("Game is already over: " + gameId);
    }
}
