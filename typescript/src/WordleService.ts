import { v4 as uuidv4 } from 'uuid';
import {
  GameState,
  GameOptions,
  GuessResult,
  LetterCode,
  GameNotFoundError,
  GameOverError,
} from './types';
import { DictionaryService } from './DictionaryService';

/**
 * Wordle Game Service
 *
 * Manages game state and validates guesses for Wordle games.
 */
export class WordleService {
  private games: Map<string, GameState> = new Map();
  private dictionary: DictionaryService;

  constructor(dictionary?: DictionaryService) {
    this.dictionary = dictionary ?? new DictionaryService();
  }

  /**
   * Start a new game
   */
  startGame(options: GameOptions = {}): string {
    const id = uuidv4();
    const answer = options.answer?.toUpperCase() ?? this.dictionary.getRandomWord();
    const maxGuesses = options.maxGuesses ?? 6;

    const game: GameState = {
      id,
      answer,
      maxGuesses,
      guesses: [],
      won: false,
      lost: false,
    };

    this.games.set(id, game);
    return id;
  }

  /**
   * Submit a guess for a game
   */
  async submitGuess(gameId: string, guess: string): Promise<GuessResult> {
    const game = this.games.get(gameId);

    if (!game) {
      throw new GameNotFoundError(gameId);
    }

    if (game.won || game.lost) {
      throw new GameOverError(gameId);
    }

    const normalizedGuess = guess.toUpperCase();

    // Simulate async work (e.g., external validation, logging)
    await this.simulateAsyncWork();

    const codes = this.calculateLetterCodes(normalizedGuess, game.answer);

    // Update game state (but another request might have already updated it!)
    game.guesses.push(normalizedGuess);
    
    const won = normalizedGuess === game.answer;
    const lost = !won && game.guesses.length >= game.maxGuesses;
    
    game.won = won;
    game.lost = lost;

    return {
      guess: normalizedGuess,
      codes,
      remainingGuesses: game.maxGuesses - game.guesses.length,
      won,
      lost,
    };
  }

  /**
   * Get current game state (for debugging)
   */
  getGame(gameId: string): GameState | undefined {
    return this.games.get(gameId);
  }

  /**
   * Calculate letter codes for a guess
   */
  private calculateLetterCodes(guess: string, answer: string): LetterCode[] {
    const codes: LetterCode[] = [];

    for (let i = 0; i < guess.length; i++) {
      const guessLetter = guess[i];
      const answerLetter = answer[i];

      if (guessLetter === answerLetter) {
        codes.push(LetterCode.GREEN);
      } else if (answer.includes(guessLetter)) {
        codes.push(LetterCode.YELLOW);
      } else {
        codes.push(LetterCode.GREY);
      }
    }

    return codes;
  }

  /**
   * Simulate async work (validation, logging, etc.)
   */
  private async simulateAsyncWork(): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, 10));
  }
}
