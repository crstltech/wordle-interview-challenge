package wordle;

import java.util.List;

/**
 * Phase 2 — Track A: Wordle Solver Implementation
 *
 * Build an optimal solver that can guess any word in ≤4 attempts
 * using information theory and entropy-based strategies.
 *
 * Target Performance:
 * - 95% of words solved in ≤4 guesses
 * - 100% of words solved in ≤6 guesses
 * - Average guesses: ~3.5
 */
public class WordleSolver {

    private final List<String> allWords;

    public WordleSolver(DictionaryService dictionary) {
        this.allWords = dictionary.getAllWords();
    }

    /**
     * Get the next optimal guess based on previous results.
     *
     * @param previousGuesses previous guesses and their letter codes
     * @param possibleWords   current list of candidate answers (pass allWords on first call)
     * @return the next guess that maximises information gain
     */
    public String getNextGuess(List<GuessAttempt> previousGuesses, List<String> possibleWords) {
        // TODO: Implement your solver logic here.
        // Return the next guess given the previous guesses and the current candidates.

        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Solve a Wordle puzzle completely (for benchmarking/testing).
     *
     * @param answer    the target word
     * @param maxGuesses maximum allowed guesses
     * @return list of guesses made, or null if failed
     */
    public List<String> solve(String answer, int maxGuesses) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
