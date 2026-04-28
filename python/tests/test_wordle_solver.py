import pytest

from src.types import LetterCode
from src.dictionary_service import DictionaryService
from src.wordle_solver import WordleSolver, PreviousGuess


@pytest.mark.skip("Phase 2 - Track A: implement WordleSolver")
def test_solver_returns_valid_starting_word():
    dictionary = DictionaryService()
    solver = WordleSolver(dictionary)
    guess = solver.get_next_guess([])

    assert guess is not None
    assert len(guess) == 5
    assert guess in dictionary.get_all_words()


@pytest.mark.skip("Phase 2 - Track A: implement WordleSolver")
def test_solver_narrows_down_after_first_guess():
    # Simulate: Answer is CRANE, we guessed APPLE
    # A: Yellow (exists in CRANE)
    # P: Grey
    # P: Grey
    # L: Grey
    # E: Yellow (exists in CRANE)
    dictionary = DictionaryService()
    solver = WordleSolver(dictionary)

    previous_guesses = [
        PreviousGuess(
            guess="APPLE",
            result=[
                LetterCode.YELLOW,  # A
                LetterCode.GREY,    # P
                LetterCode.GREY,    # P
                LetterCode.GREY,    # L
                LetterCode.YELLOW,  # E
            ],
        )
    ]

    next_guess = solver.get_next_guess(previous_guesses)

    # Next guess should:
    # - Include A and E (yellows)
    # - Not have A in position 0
    # - Not have E in position 4
    # - Not include P or L
    assert next_guess is not None
    assert len(next_guess) == 5


@pytest.mark.skip("Phase 2 - Track A: implement WordleSolver")
def test_solver_solves_simple_case():
    dictionary = DictionaryService()
    solver = WordleSolver(dictionary)
    answer = "CRANE"
    guesses = solver.solve(answer, 6)

    assert guesses is not None
    assert len(guesses) <= 6
    assert guesses[-1] == answer


@pytest.mark.skip("Phase 2 - Track A: implement WordleSolver")
def test_solver_performance_sample():
    dictionary = DictionaryService()
    solver = WordleSolver(dictionary)

    test_words = dictionary.get_all_words()[:20]
    total_guesses = 0
    max_guesses = 0
    failures = 0

    for answer in test_words:
        guesses = solver.solve(answer, 6)

        if guesses is None:
            failures += 1
        else:
            total_guesses += len(guesses)
            max_guesses = max(max_guesses, len(guesses))

    avg_guesses = total_guesses / (len(test_words) - failures) if (len(test_words) - failures) > 0 else 0

    print(f"Sample results ({len(test_words)} words):")
    print(f"  Average guesses: {avg_guesses:.2f}")
    print(f"  Max guesses: {max_guesses}")
    print(f"  Failures: {failures}")

    assert failures == 0
    assert max_guesses <= 6
    assert avg_guesses < 5


@pytest.mark.skip("Phase 2 - Track A: implement WordleSolver")
def test_solver_handles_duplicate_letters():
    dictionary = DictionaryService()
    solver = WordleSolver(dictionary)
    answer = "SPEED"
    guesses = solver.solve(answer, 6)

    assert guesses is not None
    assert guesses[-1] == answer


@pytest.mark.skip("Phase 2 - Track A: implement WordleSolver")
def test_solver_handles_all_different_letters():
    dictionary = DictionaryService()
    solver = WordleSolver(dictionary)
    answer = "SPORT"
    guesses = solver.solve(answer, 6)

    assert guesses is not None
    assert guesses[-1] == answer


@pytest.mark.skip("Phase 2 - Track A: implement WordleSolver")
def test_solver_respects_green_constraints():
    # If we know position 0 is C, next guess should have C at position 0
    dictionary = DictionaryService()
    solver = WordleSolver(dictionary)

    previous_guesses = [
        PreviousGuess(
            guess="CRANE",
            result=[
                LetterCode.GREEN,  # C correct
                LetterCode.GREY,
                LetterCode.GREY,
                LetterCode.GREY,
                LetterCode.GREY,
            ],
        )
    ]

    next_guess = solver.get_next_guess(previous_guesses)
    assert next_guess[0] == "C"


@pytest.mark.skip("Phase 2 - Track A: implement WordleSolver")
def test_solver_respects_yellow_constraints():
    # If A is yellow at position 0, next guess should:
    # - Include A somewhere
    # - Not have A at position 0
    dictionary = DictionaryService()
    solver = WordleSolver(dictionary)

    previous_guesses = [
        PreviousGuess(
            guess="APPLE",
            result=[
                LetterCode.YELLOW,  # A wrong position
                LetterCode.GREY,
                LetterCode.GREY,
                LetterCode.GREY,
                LetterCode.GREY,
            ],
        )
    ]

    next_guess = solver.get_next_guess(previous_guesses)
    assert "A" in next_guess
    assert next_guess[0] != "A"


@pytest.mark.skip("Phase 2 - Track A: implement WordleSolver")
def test_solver_respects_grey_constraints():
    # If a letter is grey, next guess should not contain it
    dictionary = DictionaryService()
    solver = WordleSolver(dictionary)

    previous_guesses = [
        PreviousGuess(
            guess="TRAMP",
            result=[
                LetterCode.GREY,   # T not in word
                LetterCode.GREY,   # R not in word
                LetterCode.GREEN,  # A correct
                LetterCode.GREY,   # M not in word
                LetterCode.GREY,   # P not in word
            ],
        )
    ]

    next_guess = solver.get_next_guess(previous_guesses)
    assert "T" not in next_guess
    assert "R" not in next_guess
    assert next_guess[2] == "A"
    assert "M" not in next_guess
    assert "P" not in next_guess
