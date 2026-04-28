from enum import IntEnum
from dataclasses import dataclass, field
from typing import Optional


class LetterCode(IntEnum):
    GREEN = 0   # Correct letter in correct position
    YELLOW = 1  # Letter exists but wrong position
    GREY = 2    # Letter not in answer


@dataclass
class GuessResult:
    guess: str
    codes: list[LetterCode]
    remaining_guesses: int
    won: bool
    lost: bool


@dataclass
class GameState:
    id: str
    answer: str
    max_guesses: int
    guesses: list[str] = field(default_factory=list)
    won: bool = False
    lost: bool = False


@dataclass
class GameOptions:
    answer: Optional[str] = None
    max_guesses: int = 6


class ValidationError(Exception):
    pass


class GameNotFoundError(Exception):
    def __init__(self, game_id: str):
        super().__init__(f"Game not found: {game_id}")


class GameOverError(Exception):
    def __init__(self, game_id: str):
        super().__init__(f"Game is already over: {game_id}")
