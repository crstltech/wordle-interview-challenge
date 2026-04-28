import asyncio
import uuid
from typing import Optional

from .types import (
    GameState,
    GameOptions,
    GuessResult,
    LetterCode,
    GameNotFoundError,
    GameOverError,
)
from .dictionary_service import DictionaryService


class WordleService:
    """
    Wordle Game Service

    Manages game state and validates guesses for Wordle games.

    NOTE: This implementation contains intentional bugs for the interview challenge.
    """

    def __init__(self, dictionary: Optional[DictionaryService] = None):
        self._games: dict[str, GameState] = {}
        self._dictionary = dictionary or DictionaryService()

    def start_game(self, options: Optional[GameOptions] = None) -> str:
        """Start a new game and return the game ID."""
        if options is None:
            options = GameOptions()

        game_id = str(uuid.uuid4())
        answer = options.answer.upper() if options.answer else self._dictionary.get_random_word()
        max_guesses = options.max_guesses

        game = GameState(
            id=game_id,
            answer=answer,
            max_guesses=max_guesses,
        )

        self._games[game_id] = game
        return game_id

    async def submit_guess(self, game_id: str, guess: str) -> GuessResult:
        """
        Submit a guess for a game.

        BUG 2: No validation — does not check guess length or dictionary membership.
        BUG 3: No asyncio.Lock — concurrent calls on the same game can race past the
                won/lost check, all await, then all update state, exceeding max_guesses.
        """
        game = self._games.get(game_id)

        if game is None:
            raise GameNotFoundError(game_id)

        if game.won or game.lost:
            raise GameOverError(game_id)

        normalized_guess = guess.upper()

        # BUG 3: No lock here — this await point yields control, allowing other
        # coroutines to run. Without a lock, concurrent submit_guess calls on the
        # same game can ALL pass the won/lost check above, then ALL reach this await,
        # then ALL resume and update state → exceeds max_guesses.
        await self._simulate_async_work()

        codes = self._calculate_letter_codes(normalized_guess, game.answer)

        # Update game state (but another coroutine might have already updated it!)
        game.guesses.append(normalized_guess)

        won = normalized_guess == game.answer
        lost = not won and len(game.guesses) >= game.max_guesses

        game.won = won
        game.lost = lost

        return GuessResult(
            guess=normalized_guess,
            codes=codes,
            remaining_guesses=game.max_guesses - len(game.guesses),
            won=won,
            lost=lost,
        )

    def get_game(self, game_id: str) -> Optional[GameState]:
        """Get current game state (for debugging)."""
        return self._games.get(game_id)

    def _calculate_letter_codes(self, guess: str, answer: str) -> list[LetterCode]:
        """
        Calculate letter codes for a guess.

        BUG 1: This naive implementation does not correctly handle duplicate letters.
        It marks a letter YELLOW whenever it appears anywhere in the answer, even if
        all occurrences of that letter in the answer are already accounted for by
        GREEN matches. The correct algorithm requires a two-pass approach:
          Pass 1: mark exact matches GREEN, mark those answer positions as used
          Pass 2: for non-green positions, if an unused answer letter matches → YELLOW
          Otherwise: GREY
        """
        codes = []
        for i, guess_char in enumerate(guess):
            if guess[i] == answer[i]:
                codes.append(LetterCode.GREEN)
            elif guess_char in answer:
                codes.append(LetterCode.YELLOW)
            else:
                codes.append(LetterCode.GREY)
        return codes

    async def _simulate_async_work(self) -> None:
        """Simulate async work (e.g., external validation, logging)."""
        await asyncio.sleep(0.01)
