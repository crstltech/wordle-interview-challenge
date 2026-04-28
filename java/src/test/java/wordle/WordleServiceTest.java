package wordle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WordleServiceTest {

    private WordleService service;

    @BeforeEach
    void setUp() {
        service = new WordleService();
    }

    // =========================================================
    // startGame
    // =========================================================

    @Nested
    class StartGame {

        @Test
        void shouldCreateGameWithDefaultOptions() {
            String gameId = service.startGame();
            GameState game = service.getGame(gameId);

            assertThat(game).isNotNull();
            assertThat(game.getMaxGuesses()).isEqualTo(6);
            assertThat(game.getGuesses()).isEmpty();
            assertThat(game.isWon()).isFalse();
            assertThat(game.isLost()).isFalse();
        }

        @Test
        void shouldAllowCustomAnswerAndMaxGuesses() {
            String gameId = service.startGame(GameOptions.withAnswer("TESTS", 3));
            GameState game = service.getGame(gameId);

            assertThat(game.getAnswer()).isEqualTo("TESTS");
            assertThat(game.getMaxGuesses()).isEqualTo(3);
        }
    }

    // =========================================================
    // submitGuess — basic
    // =========================================================

    @Nested
    class SubmitGuessBasic {

        @Test
        void shouldReturnAllGreenForExactMatch() {
            String gameId = service.startGame(GameOptions.withAnswer("REACT"));
            GuessResult result = service.submitGuess(gameId, "REACT");

            assertThat(result.codes()).containsExactly(
                LetterCode.GREEN, LetterCode.GREEN, LetterCode.GREEN,
                LetterCode.GREEN, LetterCode.GREEN
            );
            assertThat(result.won()).isTrue();
        }

        @Test
        void shouldReturnAllGreyForNoMatchingLetters() {
            String gameId = service.startGame(GameOptions.withAnswer("REACT"));
            GuessResult result = service.submitGuess(gameId, "WOUND");

            assertThat(result.codes()).containsExactly(
                LetterCode.GREY, LetterCode.GREY, LetterCode.GREY,
                LetterCode.GREY, LetterCode.GREY
            );
        }

        @Test
        void shouldThrowGameNotFoundForInvalidId() {
            assertThatThrownBy(() -> service.submitGuess("invalid-id", "REACT"))
                .isInstanceOf(GameNotFoundException.class);
        }
    }

    // =========================================================
    // submitGuess — duplicate letters
    // =========================================================

    @Nested
    class SubmitGuessDuplicateLetters {

        @Test
        void shouldHandleAppleVsPaperCorrectly() {
            // PAPER: P(0) A(1) P(2) E(3) R(4)
            // APPLE: A(0) P(1) P(2) L(3) E(4)
            String gameId = service.startGame(GameOptions.withAnswer("PAPER"));
            GuessResult result = service.submitGuess(gameId, "APPLE");

            assertThat(result.codes()).containsExactly(
                LetterCode.YELLOW,  // A — exists in PAPER
                LetterCode.YELLOW,  // P — exists but wrong position
                LetterCode.GREEN,   // P — correct position
                LetterCode.GREY,    // L — not in PAPER
                LetterCode.YELLOW   // E — exists in PAPER
            );
        }

        @Test
        void shouldHandleSheepVsCreepCorrectly() {
            // SHEEP: S(0) H(1) E(2) E(3) P(4)
            // CREEP: C(0) R(1) E(2) E(3) P(4)
            String gameId = service.startGame(GameOptions.withAnswer("SHEEP"));
            GuessResult result = service.submitGuess(gameId, "CREEP");

            assertThat(result.codes()).containsExactly(
                LetterCode.GREY,   // C
                LetterCode.GREY,   // R
                LetterCode.GREEN,  // E — correct position
                LetterCode.GREEN,  // E — correct position
                LetterCode.GREEN   // P — correct position
            );
        }

        @Test
        void shouldMarkExcessDuplicatesAsGrey() {
            // CRANE: C(0) R(1) A(2) N(3) E(4) — only one A
            // ABATE: A(0) B(1) A(2) T(3) E(4) — two A's; first A should be GREY
            String gameId = service.startGame(GameOptions.withAnswer("CRANE"));
            GuessResult result = service.submitGuess(gameId, "ABATE");

            assertThat(result.codes()).containsExactly(
                LetterCode.GREY,   // A — the only A is consumed by position 2
                LetterCode.GREY,   // B
                LetterCode.GREEN,  // A — correct position
                LetterCode.GREY,   // T
                LetterCode.GREEN   // E — correct position
            );
        }
    }

    // =========================================================
    // submitGuess — validation
    // =========================================================

    @Nested
    class SubmitGuessValidation {

        @Test
        void shouldRejectGuessThatIsTooShort() {
            String gameId = service.startGame(GameOptions.withAnswer("REACT"));
            assertThatThrownBy(() -> service.submitGuess(gameId, "HI"))
                .isInstanceOf(ValidationException.class);
        }

        @Test
        void shouldRejectGuessThatIsTooLong() {
            String gameId = service.startGame(GameOptions.withAnswer("REACT"));
            assertThatThrownBy(() -> service.submitGuess(gameId, "TOOLONG"))
                .isInstanceOf(ValidationException.class);
        }

        @Test
        void shouldRejectGuessNotInDictionary() {
            String gameId = service.startGame(GameOptions.withAnswer("REACT"));
            assertThatThrownBy(() -> service.submitGuess(gameId, "XXXXX"))
                .isInstanceOf(ValidationException.class);
        }

        @Test
        void shouldAcceptValidDictionaryWord() {
            String gameId = service.startGame(GameOptions.withAnswer("REACT"));
            GuessResult result = service.submitGuess(gameId, "APPLE");
            assertThat(result.guess()).isEqualTo("APPLE");
        }
    }

    // =========================================================
    // submitGuess — concurrency
    // =========================================================

    @Nested
    class SubmitGuessConcurrency {

        @Test
        void shouldNotAllowMoreGuessesWithConcurrentRequests() throws Exception {
            String gameId = service.startGame(GameOptions.withAnswer("REACT", 2));

            ExecutorService executor = Executors.newFixedThreadPool(5);
            List<Callable<GuessResult>> tasks = List.of(
                () -> service.submitGuess(gameId, "APPLE"),
                () -> service.submitGuess(gameId, "BRAVE"),
                () -> service.submitGuess(gameId, "CRANE"),
                () -> service.submitGuess(gameId, "DREAM"),
                () -> service.submitGuess(gameId, "EIGHT")
            );

            List<Future<GuessResult>> futures = executor.invokeAll(tasks);
            executor.shutdown();

            long successes = futures.stream().filter(f -> {
                try { f.get(); return true; } catch (Exception e) { return false; }
            }).count();

            assertThat(successes).isLessThanOrEqualTo(2);
            assertThat(service.getGame(gameId).getGuessCount()).isLessThanOrEqualTo(2);
        }

        @Test
        void shouldHandleConcurrentRequestsToDifferentGamesIndependently() throws Exception {
            String game1 = service.startGame(GameOptions.withAnswer("REACT"));
            String game2 = service.startGame(GameOptions.withAnswer("CRANE"));

            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<GuessResult> f1 = executor.submit(() -> service.submitGuess(game1, "APPLE"));
            Future<GuessResult> f2 = executor.submit(() -> service.submitGuess(game2, "BRAVE"));
            executor.shutdown();

            assertThat(f1.get().guess()).isEqualTo("APPLE");
            assertThat(f2.get().guess()).isEqualTo("BRAVE");
        }
    }

    // =========================================================
    // Game flow
    // =========================================================

    @Nested
    class GameFlow {

        @Test
        void shouldTrackRemainingGuessesCorrectly() {
            String gameId = service.startGame(GameOptions.withAnswer("REACT", 3));

            GuessResult r1 = service.submitGuess(gameId, "APPLE");
            assertThat(r1.remainingGuesses()).isEqualTo(2);

            GuessResult r2 = service.submitGuess(gameId, "BRAVE");
            assertThat(r2.remainingGuesses()).isEqualTo(1);

            GuessResult r3 = service.submitGuess(gameId, "CRANE");
            assertThat(r3.remainingGuesses()).isEqualTo(0);
            assertThat(r3.lost()).isTrue();
        }

        @Test
        void shouldThrowGameOverAfterWin() {
            String gameId = service.startGame(GameOptions.withAnswer("REACT"));
            service.submitGuess(gameId, "REACT");

            assertThatThrownBy(() -> service.submitGuess(gameId, "APPLE"))
                .isInstanceOf(GameOverException.class);
        }

        @Test
        void shouldThrowGameOverAfterLoss() {
            String gameId = service.startGame(GameOptions.withAnswer("REACT", 1));
            service.submitGuess(gameId, "APPLE");

            assertThatThrownBy(() -> service.submitGuess(gameId, "BRAVE"))
                .isInstanceOf(GameOverException.class);
        }
    }
}
