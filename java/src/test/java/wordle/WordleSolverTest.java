package wordle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Phase 2 - Track A")
class WordleSolverTest {

    private WordleSolver solver;
    private DictionaryService dictionary;

    @BeforeEach
    void setUp() {
        dictionary = new DictionaryService();
        solver = new WordleSolver(dictionary);
    }

    @Test
    void shouldReturnValidStartingWord() {
        String guess = solver.getNextGuess(List.of(), dictionary.getAllWords());
        assertThat(guess).isNotNull().hasSize(5);
        assertThat(dictionary.getAllWords()).contains(guess);
    }

    @Test
    void shouldNarrowDownAfterFirstGuess() {
        // Answer = CRANE, guessed APPLE → A=YELLOW, P=GREY, P=GREY, L=GREY, E=YELLOW
        List<GuessAttempt> previous = List.of(
            new GuessAttempt("APPLE", List.of(
                LetterCode.YELLOW,
                LetterCode.GREY,
                LetterCode.GREY,
                LetterCode.GREY,
                LetterCode.YELLOW
            ))
        );

        String next = solver.getNextGuess(previous, dictionary.getAllWords());

        assertThat(next).isNotNull().hasSize(5);
        assertThat(next).contains("A").contains("E");
        assertThat(next.charAt(0)).isNotEqualTo('A');
        assertThat(next.charAt(4)).isNotEqualTo('E');
        assertThat(next).doesNotContain("P").doesNotContain("L");
    }

    @Test
    void shouldSolveSimpleCase() {
        List<String> guesses = solver.solve("CRANE", 6);
        assertThat(guesses).isNotNull();
        assertThat(guesses).hasSizeLessThanOrEqualTo(6);
        assertThat(guesses.get(guesses.size() - 1)).isEqualTo("CRANE");
    }

    @Test
    void shouldSolveWordWithDuplicateLetters() {
        List<String> guesses = solver.solve("SPEED", 6);
        assertThat(guesses).isNotNull();
        assertThat(guesses.get(guesses.size() - 1)).isEqualTo("SPEED");
    }

    @Test
    void shouldRespectGreenConstraints() {
        List<GuessAttempt> previous = List.of(
            new GuessAttempt("CRANE", List.of(
                LetterCode.GREEN,
                LetterCode.GREY,
                LetterCode.GREY,
                LetterCode.GREY,
                LetterCode.GREY
            ))
        );

        String next = solver.getNextGuess(previous, dictionary.getAllWords());
        assertThat(next.charAt(0)).isEqualTo('C');
    }

    @Test
    void shouldRespectYellowConstraints() {
        List<GuessAttempt> previous = List.of(
            new GuessAttempt("APPLE", List.of(
                LetterCode.YELLOW,
                LetterCode.GREY,
                LetterCode.GREY,
                LetterCode.GREY,
                LetterCode.GREY
            ))
        );

        String next = solver.getNextGuess(previous, dictionary.getAllWords());
        assertThat(next).contains("A");
        assertThat(next.charAt(0)).isNotEqualTo('A');
    }

    @Test
    void shouldRespectGreyConstraints() {
        List<GuessAttempt> previous = List.of(
            new GuessAttempt("TRAMP", List.of(
                LetterCode.GREY,
                LetterCode.GREY,
                LetterCode.GREEN,
                LetterCode.GREY,
                LetterCode.GREY
            ))
        );

        String next = solver.getNextGuess(previous, dictionary.getAllWords());
        assertThat(next.charAt(2)).isEqualTo('A');
        assertThat(next).doesNotContain("T").doesNotContain("R")
                        .doesNotContain("M").doesNotContain("P");
    }

    @Test
    void shouldSolveSampleOfWordsEfficiently() {
        List<String> testWords = dictionary.getAllWords().subList(0, 20);
        int totalGuesses = 0;
        int maxGuesses = 0;
        int failures = 0;

        for (String answer : testWords) {
            List<String> guesses = solver.solve(answer, 6);
            if (guesses == null) {
                failures++;
            } else {
                totalGuesses += guesses.size();
                maxGuesses = Math.max(maxGuesses, guesses.size());
            }
        }

        double avg = (double) totalGuesses / (testWords.size() - failures);
        System.out.printf("Sample (%d words): avg=%.2f max=%d failures=%d%n",
            testWords.size(), avg, maxGuesses, failures);

        assertThat(failures).isZero();
        assertThat(maxGuesses).isLessThanOrEqualTo(6);
        assertThat(avg).isLessThan(5.0);
    }
}
