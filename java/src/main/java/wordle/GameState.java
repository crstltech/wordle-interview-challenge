package wordle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameState {
    private final String id;
    private final String answer;
    private final int maxGuesses;
    private final List<String> guesses;
    private boolean won;
    private boolean lost;

    public GameState(String id, String answer, int maxGuesses) {
        this.id = id;
        this.answer = answer;
        this.maxGuesses = maxGuesses;
        this.guesses = Collections.synchronizedList(new ArrayList<>());
        this.won = false;
        this.lost = false;
    }

    public String getId() { return id; }
    public String getAnswer() { return answer; }
    public int getMaxGuesses() { return maxGuesses; }
    public List<String> getGuesses() { return guesses; }
    public boolean isWon() { return won; }
    public boolean isLost() { return lost; }
    public void setWon(boolean won) { this.won = won; }
    public void setLost(boolean lost) { this.lost = lost; }

    public void addGuess(String guess) {
        guesses.add(guess);
    }

    public int getGuessCount() {
        return guesses.size();
    }
}
