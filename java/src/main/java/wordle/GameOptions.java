package wordle;

public record GameOptions(
    String answer,       // null for random
    int maxGuesses,      // default: 6
    boolean hardMode,    // Phase 2, Track C
    boolean dailyPuzzle  // Phase 2, Track B
) {
    public static GameOptions defaults() {
        return new GameOptions(null, 6, false, false);
    }

    public static GameOptions withAnswer(String answer) {
        return new GameOptions(answer, 6, false, false);
    }

    public static GameOptions withAnswer(String answer, int maxGuesses) {
        return new GameOptions(answer, maxGuesses, false, false);
    }
}
