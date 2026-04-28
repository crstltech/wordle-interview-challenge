import { WordleService } from '../src/WordleService';
import { DictionaryService } from '../src/DictionaryService';
import { LetterCode, ValidationError, GameNotFoundError, GameOverError } from '../src/types';

describe('WordleService', () => {
  let service: WordleService;

  beforeEach(() => {
    service = new WordleService();
  });

  describe('startGame', () => {
    it('should create a new game with default options', () => {
      const gameId = service.startGame();
      const game = service.getGame(gameId);
      
      expect(game).toBeDefined();
      expect(game?.maxGuesses).toBe(6);
      expect(game?.guesses).toHaveLength(0);
      expect(game?.won).toBe(false);
      expect(game?.lost).toBe(false);
    });

    it('should allow custom answer and max guesses', () => {
      const gameId = service.startGame({ answer: 'TESTS', maxGuesses: 3 });
      const game = service.getGame(gameId);
      
      expect(game?.answer).toBe('TESTS');
      expect(game?.maxGuesses).toBe(3);
    });
  });

  describe('submitGuess - basic functionality', () => {
    it('should return correct codes for exact match', async () => {
      const gameId = service.startGame({ answer: 'REACT' });
      const result = await service.submitGuess(gameId, 'REACT');
      
      expect(result.codes).toEqual([
        LetterCode.GREEN,
        LetterCode.GREEN,
        LetterCode.GREEN,
        LetterCode.GREEN,
        LetterCode.GREEN,
      ]);
      expect(result.won).toBe(true);
    });

    it('should return GREY for letters not in answer', async () => {
      const gameId = service.startGame({ answer: 'REACT' });
      const result = await service.submitGuess(gameId, 'WOUND');
      
      expect(result.codes).toEqual([
        LetterCode.GREY,
        LetterCode.GREY,
        LetterCode.GREY,
        LetterCode.GREY,
        LetterCode.GREY,
      ]);
    });

    it('should throw GameNotFoundError for invalid game ID', async () => {
      await expect(service.submitGuess('invalid-id', 'REACT'))
        .rejects.toThrow(GameNotFoundError);
    });
  });

  describe('submitGuess - duplicate letters', () => {
    it('should handle APPLE vs PAPER correctly', async () => {
      // PAPER = P(0) A(1) P(2) E(3) R(4)
      // APPLE = A(0) P(1) P(2) L(3) E(4)
      
      const gameId = service.startGame({ answer: 'PAPER' });
      const result = await service.submitGuess(gameId, 'APPLE');
      
      expect(result.codes).toEqual([
        LetterCode.YELLOW,  // A - exists in PAPER
        LetterCode.YELLOW,  // P - exists but wrong position
        LetterCode.GREEN,   // P - correct position
        LetterCode.GREY,    // L - not in answer
        LetterCode.YELLOW,  // E - exists in PAPER
      ]);
    });

    it('should handle SHEEP vs CREEP correctly', async () => {
      // SHEEP = S(0) H(1) E(2) E(3) P(4)
      // CREEP = C(0) R(1) E(2) E(3) P(4)
      
      const gameId = service.startGame({ answer: 'SHEEP' });
      const result = await service.submitGuess(gameId, 'CREEP');
      
      expect(result.codes).toEqual([
        LetterCode.GREY,    // C
        LetterCode.GREY,    // R
        LetterCode.GREEN,   // E - correct position
        LetterCode.GREEN,   // E - correct position
        LetterCode.GREEN,   // P - correct position
      ]);
    });

    it('should handle excess duplicate letters as GREY', async () => {
      // CRANE = C(0) R(1) A(2) N(3) E(4) - only one A
      // ABATE = A(0) B(1) A(2) T(3) E(4) - two A's
      
      const gameId = service.startGame({ answer: 'CRANE' });
      const result = await service.submitGuess(gameId, 'ABATE');
      
      expect(result.codes).toEqual([
        LetterCode.GREY,    // A - the only A is used by position 2
        LetterCode.GREY,    // B
        LetterCode.GREEN,   // A - correct position
        LetterCode.GREY,    // T
        LetterCode.GREEN,   // E - correct position
      ]);
    });

  });

  describe('submitGuess - validation', () => {
    it('should reject guess with wrong length', async () => {
      const gameId = service.startGame({ answer: 'REACT' });
      
      await expect(service.submitGuess(gameId, 'HI'))
        .rejects.toThrow(ValidationError);
      
      await expect(service.submitGuess(gameId, 'TOOLONG'))
        .rejects.toThrow(ValidationError);
    });

    it('should reject guess not in dictionary', async () => {
      const gameId = service.startGame({ answer: 'REACT' });
      
      await expect(service.submitGuess(gameId, 'XXXXX'))
        .rejects.toThrow(ValidationError);
    });

    it('should accept valid dictionary words', async () => {
      const gameId = service.startGame({ answer: 'REACT' });

      const result = await service.submitGuess(gameId, 'APPLE');
      expect(result.guess).toBe('APPLE');
    });
  });

  describe('submitGuess - concurrency', () => {
    it('should not allow more guesses than maxGuesses with concurrent requests', async () => {
      const gameId = service.startGame({ answer: 'REACT', maxGuesses: 2 });
      
      // Submit 5 guesses concurrently
      const promises = [
        service.submitGuess(gameId, 'APPLE'),
        service.submitGuess(gameId, 'BRAVE'),
        service.submitGuess(gameId, 'CRANE'),
        service.submitGuess(gameId, 'DREAM'),
        service.submitGuess(gameId, 'EIGHT'),
      ];
      
      const results = await Promise.allSettled(promises);
      
      // Count successful submissions
      const successes = results.filter(r => r.status === 'fulfilled').length;
      
      // Should only allow 2 guesses maximum
      expect(successes).toBeLessThanOrEqual(2);
      
      // Verify game state
      const game = service.getGame(gameId);
      expect(game?.guesses.length).toBeLessThanOrEqual(2);
    });

    it('should handle concurrent requests to different games independently', async () => {
      const game1 = service.startGame({ answer: 'REACT' });
      const game2 = service.startGame({ answer: 'CRANE' });
      
      // Concurrent guesses to different games should all succeed
      const [result1, result2] = await Promise.all([
        service.submitGuess(game1, 'APPLE'),
        service.submitGuess(game2, 'BRAVE'),
      ]);
      
      expect(result1.guess).toBe('APPLE');
      expect(result2.guess).toBe('BRAVE');
    });
  });

  /**
   * ============================================================
   * GAME FLOW TESTS
   * ============================================================
   */
  describe('game flow', () => {
    it('should track remaining guesses correctly', async () => {
      const gameId = service.startGame({ answer: 'REACT', maxGuesses: 3 });
      
      let result = await service.submitGuess(gameId, 'APPLE');
      expect(result.remainingGuesses).toBe(2);
      
      result = await service.submitGuess(gameId, 'BRAVE');
      expect(result.remainingGuesses).toBe(1);
      
      result = await service.submitGuess(gameId, 'CRANE');
      expect(result.remainingGuesses).toBe(0);
      expect(result.lost).toBe(true);
    });

    it('should throw GameOverError after game is won', async () => {
      const gameId = service.startGame({ answer: 'REACT' });
      
      await service.submitGuess(gameId, 'REACT');
      
      await expect(service.submitGuess(gameId, 'APPLE'))
        .rejects.toThrow(GameOverError);
    });

    it('should throw GameOverError after game is lost', async () => {
      const gameId = service.startGame({ answer: 'REACT', maxGuesses: 1 });
      
      await service.submitGuess(gameId, 'APPLE');
      
      await expect(service.submitGuess(gameId, 'BRAVE'))
        .rejects.toThrow(GameOverError);
    });
  });
});
