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
        // TODO: Implement your solver logic here
        //
        // Suggested approach:
        // 1. If no previous guesses, return a good starting word (e.g. "CRANE")
        //
        // 2. Filter possibleWords based on previous results:
        //    - GREEN: letter must be at that position
        //    - YELLOW: letter must exist but NOT at that position
        //    - GREY: letter must not exist (unless accounted for by GREEN/YELLOW)
        //
        // 3. If only 1–2 words remain, guess one directly.
        //
        // 4. Otherwise, calculate information entropy for each candidate:
        //    - For each candidate guess, simulate the result against every possible answer
        //    - Group answers by result pattern and count partition sizes
        //    - Choose the guess that minimises expected remaining possibilities
        //
        // Advanced: letter-frequency weighting, positional frequency analysis,
        //           pre-computed first-guess entropy table.

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
