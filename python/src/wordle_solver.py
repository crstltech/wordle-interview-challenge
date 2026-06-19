"""
Phase 2 - Track A: Wordle Solver Implementation

Build an optimal solver that can guess any word in <=4 attempts
using information theory and entropy-based strategies.

Target Performance:
- 95% of words solved in <=4 guesses
- 100% of words solved in <=6 guesses
- Average guesses: ~3.5
"""

from dataclasses import dataclass
from typing import Optional

from .types import LetterCode
from .dictionary_service import DictionaryService


@dataclass
class PreviousGuess:
    guess: str
    result: list[LetterCode]


class WordleSolver:
    def __init__(self, dictionary: DictionaryService):
        self._all_words = dictionary.get_all_words()
        self._word_list = list(self._all_words)

    def get_next_guess(
        self,
        previous_guesses: list[PreviousGuess],
        possible_words: Optional[list[str]] = None,
    ) -> str:
        """
        Get the next guess based on the previous guesses and their results.

        TODO: Implement your solver logic here.
        """
        raise NotImplementedError("Implement your solver here")

    def _filter_words_by_result(
        self,
        words: list[str],
        guess: str,
        result: list[LetterCode],
    ) -> list[str]:
        """
        Return only the words consistent with the given guess result.

        TODO: Implement filtering logic.
        """
        ...

    def _calculate_entropy(self, guess: str, possible_words: list[str]) -> float:
        """
        Score this guess against the current candidate words.

        TODO: Implement your scoring logic.
        """
        ...

    def _simulate_guess(self, guess: str, answer: str) -> list[LetterCode]:
        """
        Simulate the result you'd get if you guessed 'guess' and the answer was 'answer'.

        TODO: Implement Wordle coloring logic.
        """
        ...

    def _get_starting_word(self) -> str:
        """
        Get a good starting word.
        Pre-computed or calculated based on letter frequency.
        """
        raise NotImplementedError("Implement your starting word selection here")

    def solve(self, answer: str, max_guesses: int = 6) -> Optional[list[str]]:
        """
        Solve a Wordle puzzle completely.

        Uses get_next_guess and _simulate_guess to solve the puzzle.

        Args:
            answer: The target word (for testing).
            max_guesses: Maximum number of guesses allowed.

        Returns:
            List of guesses made, or None if couldn't solve within max_guesses.
        """
        guesses: list[str] = []
        results: list[PreviousGuess] = []

        for _ in range(max_guesses):
            guess = self.get_next_guess(results)
            guesses.append(guess)

            if guess == answer:
                return guesses  # Solved!

            result = self._simulate_guess(guess, answer)
            results.append(PreviousGuess(guess=guess, result=result))

        return None  # Failed to solve

    def benchmark(self) -> dict:
        """
        Test the solver against all words in the dictionary.

        Returns:
            Statistics about solver performance.
        """
        stats = {
            "total_words": len(self._all_words),
            "solved_in_1": 0,
            "solved_in_2": 0,
            "solved_in_3": 0,
            "solved_in_4": 0,
            "solved_in_5": 0,
            "solved_in_6": 0,
            "failed": 0,
            "average_guesses": 0.0,
        }

        total_guesses = 0

        for answer in self._all_words:
            guesses = self.solve(answer)

            if guesses is None:
                stats["failed"] += 1
            else:
                total_guesses += len(guesses)
                key = f"solved_in_{len(guesses)}"
                if key in stats:
                    stats[key] += 1

        solved = stats["total_words"] - stats["failed"]
        stats["average_guesses"] = total_guesses / solved if solved > 0 else 0.0

        return stats
