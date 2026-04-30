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
        """Submit a guess for a game."""
        game = self._games.get(game_id)

        if game is None:
            raise GameNotFoundError(game_id)

        if game.won or game.lost:
            raise GameOverError(game_id)

        normalized_guess = guess.upper()

        await self._simulate_async_work()

        codes = self._calculate_letter_codes(normalized_guess, game.answer)

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
        """Calculate letter codes for a guess."""
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
