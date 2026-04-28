package internal_test

import (
	"testing"

	"wordle/internal"
)

// All solver tests are Phase 2 (Track A) and are skipped until the candidate
// implements the WordleSolver methods.

func newSolver() *internal.WordleSolver {
	dict := internal.NewDictionaryService()
	return internal.NewWordleSolver(dict)
}

// TestSolver_GetNextGuess_EmptyHistory: with no previous guesses the solver
// should return a valid 5-letter word.
func TestSolver_GetNextGuess_EmptyHistory(t *testing.T) {
	t.Skip("Phase 2 - Track A")

	solver := newSolver()
	guess := solver.GetNextGuess(nil)
	if len(guess) != 5 {
		t.Errorf("expected 5-letter word, got %q (len %d)", guess, len(guess))
	}
}

// TestSolver_GetNextGuess_WithHistory: after a grey result on CRANE the solver
// should not re-use C, R, A, N, or E in fixed positions.
func TestSolver_GetNextGuess_WithHistory(t *testing.T) {
	t.Skip("Phase 2 - Track A")

	solver := newSolver()
	history := []internal.PreviousGuess{
		{
			Guess:  "CRANE",
			Result: []internal.LetterCode{internal.GREY, internal.GREY, internal.GREY, internal.GREY, internal.GREY},
		},
	}
	guess := solver.GetNextGuess(history)
	if len(guess) != 5 {
		t.Errorf("expected 5-letter word, got %q", guess)
	}
}

// TestSolver_Solve_KnownWord: the solver should find APPLE within 6 guesses.
func TestSolver_Solve_KnownWord(t *testing.T) {
	t.Skip("Phase 2 - Track A")

	solver := newSolver()
	guesses := solver.Solve("APPLE", 6)
	if guesses == nil {
		t.Fatal("solver failed to find APPLE within 6 guesses")
	}
	if len(guesses) > 6 {
		t.Errorf("solver used %d guesses, expected ≤6", len(guesses))
	}
	if guesses[len(guesses)-1] != "APPLE" {
		t.Errorf("final guess should be APPLE, got %q", guesses[len(guesses)-1])
	}
}

// TestSolver_Solve_MaxGuessesRespected: Solve must not exceed maxGuesses and
// should return nil on failure rather than panic.
func TestSolver_Solve_MaxGuessesRespected(t *testing.T) {
	t.Skip("Phase 2 - Track A")

	solver := newSolver()
	guesses := solver.Solve("ZEBRA", 1) // nearly impossible in 1 guess
	if guesses != nil && len(guesses) > 1 {
		t.Errorf("solver exceeded maxGuesses: got %d guesses", len(guesses))
	}
}

// TestSolver_Benchmark_Runs: Benchmark should return non-zero TotalGames.
func TestSolver_Benchmark_Runs(t *testing.T) {
	t.Skip("Phase 2 - Track A")

	solver := newSolver()
	stats := solver.Benchmark()
	if stats.TotalGames == 0 {
		t.Error("expected TotalGames > 0")
	}
}

// TestSolver_Benchmark_WinRate: a good solver should win at least 95% of games.
func TestSolver_Benchmark_WinRate(t *testing.T) {
	t.Skip("Phase 2 - Track A")

	solver := newSolver()
	stats := solver.Benchmark()
	winRate := float64(stats.Wins) / float64(stats.TotalGames)
	if winRate < 0.95 {
		t.Errorf("win rate %.2f%% is below the 95%% target", winRate*100)
	}
}
