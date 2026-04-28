package wordle;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Demo — shows the three bugs in action.
 *
 * Run with: mvn exec:java -Dexec.mainClass=wordle.Demo
 */
public class Demo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Wordle Service Bug Demo ===\n");

        demoLetterCodingBug();
        demoValidationBug();
        demoConcurrencyBug();
    }

    private static void demoLetterCodingBug() {
        System.out.println("--- Bug 1: Duplicate Letter Algorithm ---");
        WordleService service = new WordleService();

        // PAPER has one P (at index 2). APPLE has two P's (indices 1 and 2).
        // Correct: A=YELLOW, P=YELLOW, P=GREEN, L=GREY, E=YELLOW
        // Buggy:   A=YELLOW, P=YELLOW, P=GREEN, L=GREY, E=YELLOW  ← often fine
        // But with CRANE / ABATE:
        //   CRANE has one A (index 2). ABATE has two A's (indices 0 and 2).
        //   Correct: A=GREY, B=GREY, A=GREEN, T=GREY, E=GREEN
        //   Buggy:   A=YELLOW, B=GREY, A=GREEN, T=GREY, E=GREEN  ← wrong first A

        String gameId = service.startGame(GameOptions.withAnswer("CRANE"));
        try {
            GuessResult result = service.submitGuess(gameId, "ABATE");
            System.out.print("  ABATE vs CRANE: ");
            printCodes(result.codes());
            System.out.println("  Expected: [GREY, GREY, GREEN, GREY, GREEN]");
            System.out.println();
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void demoValidationBug() {
        System.out.println("--- Bug 2: Missing Input Validation ---");
        WordleService service = new WordleService();

        String gameId = service.startGame(GameOptions.withAnswer("REACT"));
        try {
            // Should throw ValidationException for wrong length
            GuessResult result = service.submitGuess(gameId, "HI");
            System.out.println("  Accepted 2-letter guess 'HI' — should have rejected it!");
            System.out.println("  codes: " + result.codes());
        } catch (ValidationException e) {
            System.out.println("  Correctly rejected: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  Wrong exception type: " + e.getClass().getSimpleName());
        }

        try {
            // Should throw ValidationException for non-dictionary word
            GuessResult result = service.submitGuess(gameId, "XXXXX");
            System.out.println("  Accepted non-word 'XXXXX' — should have rejected it!");
        } catch (ValidationException e) {
            System.out.println("  Correctly rejected: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  Wrong exception type: " + e.getClass().getSimpleName());
        }
        System.out.println();
    }

    private static void demoConcurrencyBug() throws Exception {
        System.out.println("--- Bug 3: Concurrency Race Condition ---");
        WordleService service = new WordleService();

        // maxGuesses=2, but 5 concurrent requests — all may succeed due to the race
        String gameId = service.startGame(GameOptions.withAnswer("REACT", 2));

        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<String> guessWords = List.of("APPLE", "BRAVE", "CRANE", "DREAM", "EIGHT");

        List<Callable<GuessResult>> tasks = guessWords.stream()
            .<Callable<GuessResult>>map(w -> () -> service.submitGuess(gameId, w))
            .toList();

        List<Future<GuessResult>> futures = executor.invokeAll(tasks);
        executor.shutdown();

        long successes = futures.stream().filter(f -> {
            try { f.get(); return true; } catch (Exception e) { return false; }
        }).count();

        GameState game = service.getGame(gameId);
        System.out.println("  maxGuesses = 2, concurrent submissions = 5");
        System.out.printf("  Successful submissions: %d (should be ≤ 2)%n", successes);
        System.out.printf("  Guesses recorded in state: %d (should be ≤ 2)%n",
            game.getGuessCount());
        System.out.println();
    }

    private static void printCodes(List<LetterCode> codes) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < codes.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(switch (codes.get(i)) {
                case GREEN  -> "GREEN";
                case YELLOW -> "YELLOW";
                case GREY   -> "GREY";
            });
        }
        sb.append("]");
        System.out.println(sb);
    }
}
