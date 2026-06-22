import { LetterCode } from './types';
import { DictionaryService } from './DictionaryService';

/**
 * Phase 2 - Track A: Wordle Solver Implementation
 *
 * Build an optimal solver that can guess any word in ≤4 attempts
 * using information theory and entropy-based strategies.
 *
 * Target Performance:
 * - 95% of words solved in ≤4 guesses
 * - 100% of words solved in ≤6 guesses
 * - Average guesses: ~3.5
 */

export interface GuessResult {
  guess: string;
  result: LetterCode[];
}

export class WordleSolver {
  private wordList: string[];
  private allWords: string[];

  constructor(dictionary: DictionaryService) {
    this.allWords = dictionary.getAllWords();
    this.wordList = [...this.allWords];
  }

  /**
   * Get the next optimal guess based on previous results
   *
   * @param previousGuesses - Array of previous guesses and their results
   * @param possibleWords - Current list of possible answers (optional, will compute if not provided)
   * @returns The next guess that maximizes information gain
   */
  getNextGuess(
    previousGuesses: GuessResult[],
    possibleWords?: string[]
  ): string {
    // TODO: Implement your solver logic here.
    // Return the next guess given the previous guesses and their results.

    throw new Error('Not implemented');
  }

  /**
   * Filter word list based on guess results
   *
   * @param words - Current list of possible words
   * @param guess - The guess that was made
   * @param result - The result codes for each letter
   * @returns Filtered list of words that match the constraints
   */
  private filterWordsByResult(
    words: string[],
    guess: string,
    result: LetterCode[]
  ): string[] {
    // TODO: Return only the words consistent with the given guess result.

    throw new Error('Not implemented');
  }

  /**
   * Calculate information entropy for a guess
   *
   * Information theory approach:
   * - For a given guess, calculate how many words would remain for each possible result pattern
   * - The "best" guess is the one that, on average, leaves the fewest possibilities
   *
   * @param guess - The candidate guess
   * @param possibleWords - Current possible answer words
   * @returns Expected information gain (lower is better)
   */
  private calculateEntropy(guess: string, possibleWords: string[]): number {
    // TODO: Score this guess against the current candidate words.

    throw new Error('Not implemented');
  }

  /**
   * Simulate the result you'd get if you guessed 'guess' and the answer was 'answer'
   *
   * @param guess - The guess word
   * @param answer - The actual answer
   * @returns Array of LetterCodes representing the result
   */
  private simulateGuess(guess: string, answer: string): LetterCode[] {
    // TODO: Implement Wordle coloring logic
    // This should match the logic in WordleService.calculateLetterCodes
    // (but make sure it's the CORRECT implementation!)

    throw new Error('Not implemented');
  }

  /**
   * Get a good starting word
   * Pre-computed or calculated based on letter frequency
   */
  private getStartingWord(): string {
    // TODO: Return a good starting word
    // Options:
    // 1. Calculate based on letter frequency in word list
    // 2. Calculate word with maximum entropy

    throw new Error('Not implemented');
  }

  /**
   * Solve a Wordle puzzle completely
   *
   * @param answer - The target word (for testing)
   * @param maxGuesses - Maximum number of guesses allowed
   * @returns Array of guesses made, or null if couldn't solve
   */
  solve(answer: string, maxGuesses: number = 6): string[] | null {
    const guesses: string[] = [];
    const results: GuessResult[] = [];

    for (let i = 0; i < maxGuesses; i++) {
      const guess = this.getNextGuess(results);
      guesses.push(guess);

      if (guess === answer) {
        return guesses; // Solved!
      }

      // Simulate the result
      const result = this.simulateGuess(guess, answer);
      results.push({ guess, result });
    }

    return null; // Failed to solve
  }

  /**
   * Test the solver against all words in dictionary
   *
   * @returns Statistics about solver performance
   */
  benchmark(): {
    totalWords: number;
    solvedIn1: number;
    solvedIn2: number;
    solvedIn3: number;
    solvedIn4: number;
    solvedIn5: number;
    solvedIn6: number;
    failed: number;
    averageGuesses: number;
  } {
    const stats = {
      totalWords: this.allWords.length,
      solvedIn1: 0,
      solvedIn2: 0,
      solvedIn3: 0,
      solvedIn4: 0,
      solvedIn5: 0,
      solvedIn6: 0,
      failed: 0,
      averageGuesses: 0,
    };

    let totalGuesses = 0;

    for (const answer of this.allWords) {
      const guesses = this.solve(answer);

      if (!guesses) {
        stats.failed++;
      } else {
        totalGuesses += guesses.length;
        switch (guesses.length) {
          case 1:
            stats.solvedIn1++;
            break;
          case 2:
            stats.solvedIn2++;
            break;
          case 3:
            stats.solvedIn3++;
            break;
          case 4:
            stats.solvedIn4++;
            break;
          case 5:
            stats.solvedIn5++;
            break;
          case 6:
            stats.solvedIn6++;
            break;
        }
      }
    }

    stats.averageGuesses = totalGuesses / (this.allWords.length - stats.failed);

    return stats;
  }
}
