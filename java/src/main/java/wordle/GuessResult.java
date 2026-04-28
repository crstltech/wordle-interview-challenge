package wordle;

import java.util.List;

public record GuessResult(
    String guess,
    List<LetterCode> codes,
    int remainingGuesses,
    boolean won,
    boolean lost
) {}
