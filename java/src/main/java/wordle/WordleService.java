package wordle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Wordle Game Service
 *
 * Manages game state and validates guesses for Wordle games.
 */
public class WordleService {

    // BUG 3: HashMap is not thread-safe for concurrent access
    private final Map<String, GameState> games = new HashMap<>();
    private final DictionaryService dictionary;

    public WordleService() {
        this.dictionary = new DictionaryService();
    }

    public WordleService(DictionaryService dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Start a new game.
     */
    public String startGame(GameOptions options) {
        String id = UUID.randomUUID().toString();
        String answer = (options.answer() != null)
            ? options.answer().toUpperCase()
            : dictionary.getRandomWord();
        int maxGuesses = options.maxGuesses() > 0 ? options.maxGuesses() : 6;

        games.put(id, new GameState(id, answer, maxGuesses));
        return id;
    }

    public String startGame() {
        return startGame(GameOptions.defaults());
    }

    /**
     * Submit a guess for a game.
     */
    public GuessResult submitGuess(String gameId, String guess) {
        GameState game = games.get(gameId);

        if (game == null) {
            throw new GameNotFoundException(gameId);
        }

        if (game.isWon() || game.isLost()) {
            throw new GameOverException(gameId);
        }

        String normalizedGuess = guess.toUpperCase();

        // BUG 2: No validation — missing length check and dictionary lookup

        // Simulate async work (e.g., logging, external call) — creates a race window
        simulateAsyncWork();

        // BUG 3: State update after sleep with no lock held.
        // Another thread may have already updated game state.
        List<LetterCode> codes = calculateLetterCodes(normalizedGuess, game.getAnswer());
        game.addGuess(normalizedGuess);

        boolean won = normalizedGuess.equals(game.getAnswer());
        boolean lost = !won && game.getGuessCount() >= game.getMaxGuesses();

        game.setWon(won);
        game.setLost(lost);

        return new GuessResult(
            normalizedGuess,
            codes,
            game.getMaxGuesses() - game.getGuessCount(),
            won,
            lost
        );
    }

    /**
     * Get current game state (for debugging/inspection).
     */
    public GameState getGame(String gameId) {
        return games.get(gameId);
    }

    /**
     * Calculate letter codes for a guess against the answer.
     */
    private List<LetterCode> calculateLetterCodes(String guess, String answer) {
        List<LetterCode> codes = new ArrayList<>();

        for (int i = 0; i < guess.length(); i++) {
            char guessChar = guess.charAt(i);
            char answerChar = answer.charAt(i);

            if (guessChar == answerChar) {
                codes.add(LetterCode.GREEN);
            // BUG 1: Naive single-pass — does not track which answer letters are already
            // accounted for. Excess duplicate letters should be GREY, not YELLOW.
            } else if (answer.indexOf(guessChar) >= 0) {
                codes.add(LetterCode.YELLOW);
            } else {
                codes.add(LetterCode.GREY);
            }
        }

        return codes;
    }

    private void simulateAsyncWork() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
