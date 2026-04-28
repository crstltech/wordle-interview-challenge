import pytest
import asyncio

from src.types import (
    LetterCode,
    GameOptions,
    GameState,
    GuessResult,
    ValidationError,
    GameNotFoundError,
    GameOverError,
)
from src.wordle_service import WordleService


class TestStartGame:
    def test_start_game_default_options(self):
        service = WordleService()
        game_id = service.start_game()
        game = service.get_game(game_id)

        assert game_id is not None
        assert game is not None
        assert game.max_guesses == 6
        assert game.guesses == []
        assert game.won is False
        assert game.lost is False

    def test_start_game_custom_options(self):
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="TESTS", max_guesses=3))
        game = service.get_game(game_id)

        assert game is not None
        assert game.answer == "TESTS"
        assert game.max_guesses == 3


class TestSubmitGuessBasic:
    async def test_submit_guess_exact_match(self):
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="REACT"))
        result = await service.submit_guess(game_id, "REACT")

        assert result.codes == [
            LetterCode.GREEN,
            LetterCode.GREEN,
            LetterCode.GREEN,
            LetterCode.GREEN,
            LetterCode.GREEN,
        ]
        assert result.won is True

    async def test_submit_guess_all_grey(self):
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="REACT"))
        result = await service.submit_guess(game_id, "WOUND")

        assert result.codes == [
            LetterCode.GREY,
            LetterCode.GREY,
            LetterCode.GREY,
            LetterCode.GREY,
            LetterCode.GREY,
        ]

    async def test_submit_guess_invalid_game_id(self):
        service = WordleService()
        with pytest.raises(GameNotFoundError):
            await service.submit_guess("invalid-id", "REACT")


class TestDuplicateLetters:
    async def test_duplicate_letters_apple_vs_paper(self):
        # PAPER = P(0) A(1) P(2) E(3) R(4)
        # APPLE = A(0) P(1) P(2) L(3) E(4)
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="PAPER"))
        result = await service.submit_guess(game_id, "APPLE")

        assert result.codes == [
            LetterCode.YELLOW,  # A - exists in PAPER
            LetterCode.YELLOW,  # P - exists but wrong position
            LetterCode.GREEN,   # P - correct position
            LetterCode.GREY,    # L - not in answer
            LetterCode.YELLOW,  # E - exists in PAPER
        ]

    async def test_duplicate_letters_creep_vs_sheep(self):
        # SHEEP = S(0) H(1) E(2) E(3) P(4)
        # CREEP = C(0) R(1) E(2) E(3) P(4)
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="SHEEP"))
        result = await service.submit_guess(game_id, "CREEP")

        assert result.codes == [
            LetterCode.GREY,    # C
            LetterCode.GREY,    # R
            LetterCode.GREEN,   # E - correct position
            LetterCode.GREEN,   # E - correct position
            LetterCode.GREEN,   # P - correct position
        ]

    async def test_duplicate_letters_excess(self):
        # CRANE = C(0) R(1) A(2) N(3) E(4) - only one A
        # ABATE = A(0) B(1) A(2) T(3) E(4) - two A's
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="CRANE"))
        result = await service.submit_guess(game_id, "ABATE")

        assert result.codes == [
            LetterCode.GREY,    # A - the only A is used by position 2
            LetterCode.GREY,    # B
            LetterCode.GREEN,   # A - correct position
            LetterCode.GREY,    # T
            LetterCode.GREEN,   # E - correct position
        ]


class TestValidation:
    async def test_validation_wrong_length(self):
        """FAILS until Bug 2 is fixed: service must validate guess length."""
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="REACT"))

        with pytest.raises(ValidationError):
            await service.submit_guess(game_id, "HI")

    async def test_validation_not_in_dictionary(self):
        """FAILS until Bug 2 is fixed: service must validate against dictionary."""
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="REACT"))

        with pytest.raises(ValidationError):
            await service.submit_guess(game_id, "XXXXX")

    async def test_validation_valid_word(self):
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="REACT"))
        result = await service.submit_guess(game_id, "APPLE")

        assert result.guess == "APPLE"


class TestConcurrency:
    async def test_concurrency_max_guesses(self):
        """
        FAILS until Bug 3 is fixed: without asyncio.Lock, concurrent submit_guess
        calls all pass the won/lost check, then all await, then all update state,
        exceeding max_guesses.
        """
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="REACT", max_guesses=2))

        tasks = [
            service.submit_guess(game_id, "APPLE"),
            service.submit_guess(game_id, "BRAVE"),
            service.submit_guess(game_id, "CRANE"),
            service.submit_guess(game_id, "DREAM"),
            service.submit_guess(game_id, "EIGHT"),
        ]

        results = await asyncio.gather(*tasks, return_exceptions=True)

        successes = [r for r in results if isinstance(r, GuessResult)]

        # Should only allow at most 2 guesses
        assert len(successes) <= 2

        game = service.get_game(game_id)
        assert game is not None
        assert len(game.guesses) <= 2

    async def test_concurrency_independent_games(self):
        service = WordleService()
        game1 = service.start_game(GameOptions(answer="REACT"))
        game2 = service.start_game(GameOptions(answer="CRANE"))

        result1, result2 = await asyncio.gather(
            service.submit_guess(game1, "APPLE"),
            service.submit_guess(game2, "BRAVE"),
        )

        assert result1.guess == "APPLE"
        assert result2.guess == "BRAVE"


class TestGameFlow:
    async def test_game_flow_remaining_guesses(self):
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="REACT", max_guesses=3))

        result = await service.submit_guess(game_id, "APPLE")
        assert result.remaining_guesses == 2

        result = await service.submit_guess(game_id, "BRAVE")
        assert result.remaining_guesses == 1

        result = await service.submit_guess(game_id, "CRANE")
        assert result.remaining_guesses == 0
        assert result.lost is True

    async def test_game_flow_game_over_after_win(self):
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="REACT"))
        await service.submit_guess(game_id, "REACT")

        with pytest.raises(GameOverError):
            await service.submit_guess(game_id, "APPLE")

    async def test_game_flow_game_over_after_loss(self):
        service = WordleService()
        game_id = service.start_game(GameOptions(answer="REACT", max_guesses=1))
        await service.submit_guess(game_id, "APPLE")

        with pytest.raises(GameOverError):
            await service.submit_guess(game_id, "BRAVE")
