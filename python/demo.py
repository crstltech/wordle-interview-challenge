"""
Demo script that exercises the WordleService so you can observe its behaviour.

Run with: python demo.py
"""

import asyncio
import sys
import os

sys.path.insert(0, os.path.dirname(__file__))

from src.wordle_service import WordleService
from src.types import LetterCode, GameOptions


def code_to_symbol(code: LetterCode) -> str:
    if code == LetterCode.GREEN:
        return "[G]"
    elif code == LetterCode.YELLOW:
        return "[Y]"
    else:
        return "[ ]"


def format_result(guess: str, codes: list[LetterCode]) -> str:
    parts = [f"{letter}:{code_to_symbol(code)}" for letter, code in zip(guess, codes)]
    return " ".join(parts)


async def demonstrate_duplicate_letters() -> None:
    print("=" * 60)
    print("Test Case: Duplicate Letter Handling")
    print("=" * 60)

    service = WordleService()

    # Test case 1: Answer is PAPER, guess is APPLE
    game_id = service.start_game(GameOptions(answer="PAPER"))
    result = await service.submit_guess(game_id, "APPLE")

    print("\nAnswer: PAPER")
    print("Guess:  APPLE")
    print("\nResult:")
    print(format_result(result.guess, result.codes))

    # Test case 2: Answer is SHEEP, guess is CREEP
    print("\n" + "-" * 40)

    game_id2 = service.start_game(GameOptions(answer="SHEEP"))
    result2 = await service.submit_guess(game_id2, "CREEP")

    print("\nAnswer: SHEEP")
    print("Guess:  CREEP")
    print("\nResult:")
    print(format_result(result2.guess, result2.codes))


async def demonstrate_validation() -> None:
    print("\n" + "=" * 60)
    print("Test Case: Input Validation")
    print("=" * 60)

    service = WordleService()
    game_id = service.start_game(GameOptions(answer="REACT"))

    print("\nAnswer: REACT (5 letters)")

    print('\nSubmitting "HI" (2 letters)...')
    try:
        result = await service.submit_guess(game_id, "HI")
        print("  WARNING: Accepted:", format_result(result.guess, result.codes))
    except Exception as e:
        print("  OK Rejected:", e)

    print('\nSubmitting "XXXXX" (not in dictionary)...')
    try:
        result = await service.submit_guess(game_id, "XXXXX")
        print("  WARNING: Accepted:", format_result(result.guess, result.codes))
    except Exception as e:
        print("  OK Rejected:", e)


async def demonstrate_concurrency() -> None:
    print("\n" + "=" * 60)
    print("Test Case: Concurrent Requests")
    print("=" * 60)

    service = WordleService()
    game_id = service.start_game(GameOptions(answer="REACT", max_guesses=2))

    print("\nAnswer: REACT")
    print("Max guesses: 2")
    print("\nSubmitting 5 guesses simultaneously via asyncio.gather...")

    tasks = [
        service.submit_guess(game_id, "APPLE"),
        service.submit_guess(game_id, "BRAVE"),
        service.submit_guess(game_id, "CRANE"),
        service.submit_guess(game_id, "DREAM"),
        service.submit_guess(game_id, "EIGHT"),
    ]

    results = await asyncio.gather(*tasks, return_exceptions=True)

    successful = [r for r in results if not isinstance(r, Exception)]
    failed = [r for r in results if isinstance(r, Exception)]

    print(f"\n  Successful: {len(successful)}")
    print(f"  Rejected: {len(failed)}")

    for i, r in enumerate(successful):
        print(f"    {i + 1}. {r.guess} - Remaining: {r.remaining_guesses}")

    game = service.get_game(game_id)
    print(f"\nFinal game state: {len(game.guesses)} guesses recorded")
    print("Max guesses: 2")


async def main() -> None:
    print("Wordle Service Demo\n")
    print("Run `pytest` to see which tests are failing.\n")

    await demonstrate_duplicate_letters()
    await demonstrate_validation()
    await demonstrate_concurrency()

    print("\n" + "=" * 60)
    print("Phase 1: Fix the failing tests")
    print("Phase 2: Choose a track and build production features")
    print("=" * 60)


if __name__ == "__main__":
    asyncio.run(main())
