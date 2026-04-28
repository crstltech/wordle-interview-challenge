package wordle;

import java.util.List;

public record GuessAttempt(String guess, List<LetterCode> result) {}
