/**
 * Letter result codes for Wordle
 */
export enum LetterCode {
  GREEN = 0,   // Correct letter in correct position
  YELLOW = 1,  // Letter exists in answer but wrong position
  GREY = 2,    // Letter not in answer
}

/**
 * Result of a single guess
 */
export interface GuessResult {
  guess: string;
  codes: LetterCode[];
  remainingGuesses: number;
  won: boolean;
  lost: boolean;
}

/**
 * Internal game state
 */
export interface GameState {
  id: string;
  answer: string;
  maxGuesses: number;
  guesses: string[];
  won: boolean;
  lost: boolean;
}

/**
 * Options for starting a new game
 */
export interface GameOptions {
  answer?: string;       // Override random answer (for testing)
  maxGuesses?: number;   // Default: 6
  hardMode?: boolean;    // Hard mode: must use revealed hints (Phase 2, Track C)
  dailyPuzzle?: boolean; // Daily puzzle mode (Phase 2, Track B)
}

/**
 * Error thrown when validation fails
 */
export class ValidationError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'ValidationError';
  }
}

/**
 * Error thrown when game is not found
 */
export class GameNotFoundError extends Error {
  constructor(gameId: string) {
    super(`Game not found: ${gameId}`);
    this.name = 'GameNotFoundError';
  }
}

/**
 * Error thrown when game is already over
 */
export class GameOverError extends Error {
  constructor(gameId: string) {
    super(`Game is already over: ${gameId}`);
    this.name = 'GameOverError';
  }
}
