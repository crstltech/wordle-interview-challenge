package wordle;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Demo — exercises the WordleService so you can observe its behaviour.
 *
 * Run with: mvn exec:java -Dexec.mainClass=wordle.Demo
 */
public class Demo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Wordle Service Demo ===\n");

        demoLetterCoding();
        demoValidation();
        demoConcurrency();
    }

    private static void demoLetterCoding() {
        System.out.println("--- Duplicate Letters ---");
        WordleService service = new WordleService();

        String gameId = service.startGame(GameOptions.withAnswer("CRANE"));
        try {
            GuessResult result = service.submitGuess(gameId, "ABATE");
            System.out.print("  ABATE vs CRANE: ");
            printCodes(result.codes());
            System.out.println();
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private static void demoValidation() {
        System.out.println("--- Input Validation ---");
        WordleService service = new WordleService();

        String gameId = service.startGame(GameOptions.withAnswer("REACT"));
        try {
            GuessResult result = service.submitGuess(gameId, "HI");
            System.out.println("  2-letter guess 'HI' accepted, codes: " + result.codes());
        } catch (ValidationException e) {
            System.out.println("  'HI' rejected: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  'HI' raised: " + e.getClass().getSimpleName());
        }

        try {
            service.submitGuess(gameId, "XXXXX");
            System.out.println("  Non-word 'XXXXX' accepted");
        } catch (ValidationException e) {
            System.out.println("  'XXXXX' rejected: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  'XXXXX' raised: " + e.getClass().getSimpleName());
        }
        System.out.println();
    }

    private static void demoConcurrency() throws Exception {
        System.out.println("--- Concurrent Submissions ---");
        WordleService service = new WordleService();

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
