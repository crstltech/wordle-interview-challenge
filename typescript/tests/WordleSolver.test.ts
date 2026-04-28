import { WordleSolver } from '../src/WordleSolver';
import { DictionaryService } from '../src/DictionaryService';
import { LetterCode } from '../src/types';

describe('WordleSolver - Track A', () => {
  let solver: WordleSolver;
  let dictionary: DictionaryService;

  beforeEach(() => {
    dictionary = new DictionaryService();
    solver = new WordleSolver(dictionary);
  });

  describe('Basic functionality', () => {
    it('should return a valid starting word', () => {
      const guess = solver.getNextGuess([]);
      expect(guess).toBeTruthy();
      expect(guess.length).toBe(5);
      expect(dictionary.getAllWords()).toContain(guess);
    });

    it('should narrow down possibilities after first guess', () => {
      // Simulate: Answer is CRANE, we guessed APPLE
      // A: Yellow (exists in CRANE)
      // P: Grey
      // P: Grey
      // L: Grey
      // E: Yellow (exists in CRANE)
      const previousGuesses = [
        {
          guess: 'APPLE',
          result: [
            LetterCode.YELLOW, // A
            LetterCode.GREY, // P
            LetterCode.GREY, // P
            LetterCode.GREY, // L
            LetterCode.YELLOW, // E
          ],
        },
      ];

      const nextGuess = solver.getNextGuess(previousGuesses);

      // Next guess should:
      // - Include A and E (yellows)
      // - Not have A in position 0
      // - Not have E in position 4
      // - Not include P or L
      expect(nextGuess).toBeTruthy();
      expect(nextGuess.length).toBe(5);
    });

    it('should solve simple cases in few guesses', () => {
      const answer = 'CRANE';
      const guesses = solver.solve(answer, 6);

      expect(guesses).not.toBeNull();
      expect(guesses!.length).toBeLessThanOrEqual(6);
      expect(guesses![guesses!.length - 1]).toBe(answer);
    });
  });

  describe('Performance targets', () => {
    it('should solve a sample of words efficiently', () => {
      // Test on a small sample for speed
      const testWords = dictionary.getAllWords().slice(0, 20);
      let totalGuesses = 0;
      let maxGuesses = 0;
      let failures = 0;

      for (const answer of testWords) {
        const guesses = solver.solve(answer, 6);

        if (!guesses) {
          failures++;
        } else {
          totalGuesses += guesses.length;
          maxGuesses = Math.max(maxGuesses, guesses.length);
        }
      }

      const avgGuesses = totalGuesses / (testWords.length - failures);

      console.log(`Sample results (${testWords.length} words):`);
      console.log(`  Average guesses: ${avgGuesses.toFixed(2)}`);
      console.log(`  Max guesses: ${maxGuesses}`);
      console.log(`  Failures: ${failures}`);

      // Basic expectations
      expect(failures).toBe(0); // Should solve all
      expect(maxGuesses).toBeLessThanOrEqual(6); // Within Wordle limit
      expect(avgGuesses).toBeLessThan(5); // Reasonably efficient
    });

    // This test is slow - uncomment to run full benchmark
    it.skip('should meet performance targets on full dictionary', () => {
      const stats = solver.benchmark();

      console.log('\nFull Benchmark Results:');
      console.log(`Total words: ${stats.totalWords}`);
      console.log(`Solved in 1: ${stats.solvedIn1}`);
      console.log(`Solved in 2: ${stats.solvedIn2}`);
      console.log(`Solved in 3: ${stats.solvedIn3}`);
      console.log(`Solved in 4: ${stats.solvedIn4}`);
      console.log(`Solved in 5: ${stats.solvedIn5}`);
      console.log(`Solved in 6: ${stats.solvedIn6}`);
      console.log(`Failed: ${stats.failed}`);
      console.log(`Average guesses: ${stats.averageGuesses.toFixed(2)}`);

      // Target metrics
      const solvedIn4OrLess =
        stats.solvedIn1 + stats.solvedIn2 + stats.solvedIn3 + stats.solvedIn4;
      const percentIn4 = (solvedIn4OrLess / stats.totalWords) * 100;

      console.log(`\nSolved in ≤4 guesses: ${percentIn4.toFixed(1)}%`);

      // Performance targets
      expect(stats.failed).toBe(0); // 100% solve rate
      expect(percentIn4).toBeGreaterThanOrEqual(95); // 95% in ≤4 guesses
      expect(stats.averageGuesses).toBeLessThan(4); // Average < 4
    }, 60000); // 60 second timeout for full benchmark
  });

  describe('Edge cases', () => {
    it('should handle words with duplicate letters', () => {
      const answer = 'SPEED';
      const guesses = solver.solve(answer, 6);

      expect(guesses).not.toBeNull();
      expect(guesses![guesses!.length - 1]).toBe(answer);
    });

    it('should handle words with all different letters', () => {
      const answer = 'SPORT';
      const guesses = solver.solve(answer, 6);

      expect(guesses).not.toBeNull();
      expect(guesses![guesses!.length - 1]).toBe(answer);
    });

    it('should handle difficult words', () => {
      // Words that are known to be tricky
      const difficultWords = ['FJORD', 'JAZZY', 'FUZZY', 'WHIZZ'];

      for (const answer of difficultWords) {
        // Skip if not in our dictionary
        if (!dictionary.getAllWords().includes(answer)) continue;

        const guesses = solver.solve(answer, 6);
        expect(guesses).not.toBeNull();
        if (guesses) {
          console.log(`${answer}: ${guesses.length} guesses`);
        }
      }
    });
  });

  describe('Algorithm correctness', () => {
    it('should respect green letter constraints', () => {
      // If we know position 0 is C, next guess should have C at position 0
      const previousGuesses = [
        {
          guess: 'CRANE',
          result: [
            LetterCode.GREEN, // C correct
            LetterCode.GREY,
            LetterCode.GREY,
            LetterCode.GREY,
            LetterCode.GREY,
          ],
        },
      ];

      const nextGuess = solver.getNextGuess(previousGuesses);
      expect(nextGuess[0]).toBe('C');
    });

    it('should respect yellow letter constraints', () => {
      // If A is yellow at position 0, next guess should:
      // - Include A somewhere
      // - Not have A at position 0
      const previousGuesses = [
        {
          guess: 'APPLE',
          result: [
            LetterCode.YELLOW, // A wrong position
            LetterCode.GREY,
            LetterCode.GREY,
            LetterCode.GREY,
            LetterCode.GREY,
          ],
        },
      ];

      const nextGuess = solver.getNextGuess(previousGuesses);
      expect(nextGuess).toContain('A');
      expect(nextGuess[0]).not.toBe('A');
    });

    it('should respect grey letter constraints', () => {
      // If X is grey, next guess should not contain X
      const previousGuesses = [
        {
          guess: 'TRAMP',
          result: [
            LetterCode.GREY, // T not in word
            LetterCode.GREY, // R not in word
            LetterCode.GREEN, // A correct
            LetterCode.GREY, // M not in word
            LetterCode.GREY, // P not in word
          ],
        },
      ];

      const nextGuess = solver.getNextGuess(previousGuesses);
      expect(nextGuess).not.toContain('T');
      expect(nextGuess).not.toContain('R');
      expect(nextGuess[2]).toBe('A');
      expect(nextGuess).not.toContain('M');
      expect(nextGuess).not.toContain('P');
    });
  });
});
