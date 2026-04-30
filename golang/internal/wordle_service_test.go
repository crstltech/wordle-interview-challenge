package internal_test

import (
	"sync"
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"

	"wordle/internal"
)

func newService() *internal.WordleService {
	return internal.NewWordleService(nil)
}

// ---------------------------------------------------------------------------
// StartGame tests
// ---------------------------------------------------------------------------

func TestStartGame_DefaultOptions(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{})

	assert.NotEmpty(t, gameID)

	game, err := svc.GetGame(gameID)
	require.NoError(t, err)

	assert.Equal(t, 6, game.MaxGuesses)
	assert.Len(t, game.Guesses, 0)
	assert.False(t, game.Won)
	assert.False(t, game.Lost)
}

func TestStartGame_CustomOptions(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "TESTS", MaxGuesses: 3})

	game, err := svc.GetGame(gameID)
	require.NoError(t, err)

	assert.Equal(t, "TESTS", game.Answer)
	assert.Equal(t, 3, game.MaxGuesses)
}

// ---------------------------------------------------------------------------
// SubmitGuess — basic result tests
// ---------------------------------------------------------------------------

func TestSubmitGuess_ExactMatch(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "REACT"})

	result, err := svc.SubmitGuess(gameID, "REACT")
	require.NoError(t, err)

	assert.Equal(t, []internal.LetterCode{
		internal.GREEN, internal.GREEN, internal.GREEN, internal.GREEN, internal.GREEN,
	}, result.Codes)
	assert.True(t, result.Won)
}

func TestSubmitGuess_AllGrey(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "REACT"})

	// WOUND shares no letters with REACT
	result, err := svc.SubmitGuess(gameID, "WOUND")
	require.NoError(t, err)

	assert.Equal(t, []internal.LetterCode{
		internal.GREY, internal.GREY, internal.GREY, internal.GREY, internal.GREY,
	}, result.Codes)
}

func TestSubmitGuess_InvalidGameID(t *testing.T) {
	svc := newService()
	_, err := svc.SubmitGuess("nonexistent-id", "APPLE")

	var notFound *internal.GameNotFoundError
	assert.ErrorAs(t, err, &notFound)
}

// ---------------------------------------------------------------------------
// Duplicate letter tests
// ---------------------------------------------------------------------------

// TestDuplicateLetters_AppleVsPaper: answer=PAPER, guess=APPLE
// Expected: [YELLOW, YELLOW, GREEN, GREY, YELLOW]
//
//	A(0): not at pos 0 in PAPER but A exists → YELLOW
//	P(1): not at pos 1 in PAPER, one P still available after green at pos 2 → YELLOW
//	P(2): PAPER[2] = P → GREEN
//	L(3): L not in PAPER → GREY
//	E(4): E in PAPER → YELLOW
func TestDuplicateLetters_AppleVsPaper(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "PAPER"})

	result, err := svc.SubmitGuess(gameID, "APPLE")
	require.NoError(t, err)

	assert.Equal(t, []internal.LetterCode{
		internal.YELLOW, // A — exists in PAPER but wrong position
		internal.YELLOW, // P — one P still available after green at pos 2
		internal.GREEN,  // P — correct position (PAPER[2] = P)
		internal.GREY,   // L — not in PAPER
		internal.YELLOW, // E — exists in PAPER but wrong position
	}, result.Codes)
}

// TestDuplicateLetters_CreepVsSheep: answer=SHEEP, guess=CREEP
// Expected: [GREY, GREY, GREEN, GREEN, GREEN]
func TestDuplicateLetters_CreepVsSheep(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "SHEEP"})

	result, err := svc.SubmitGuess(gameID, "CREEP")
	require.NoError(t, err)

	assert.Equal(t, []internal.LetterCode{
		internal.GREY,  // C — not in SHEEP
		internal.GREY,  // R — not in SHEEP
		internal.GREEN, // E — SHEEP[2] = E
		internal.GREEN, // E — SHEEP[3] = E
		internal.GREEN, // P — SHEEP[4] = P
	}, result.Codes)
}

// TestDuplicateLetters_ExcessDuplicates: answer=CRANE, guess=ABATE
// Expected: [GREY, GREY, GREEN, GREY, GREEN]
//
// CRANE has one A; it is matched GREEN at pos 2, so ABATE[0]='A' should be GREY.
func TestDuplicateLetters_ExcessDuplicates(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "CRANE"})

	result, err := svc.SubmitGuess(gameID, "ABATE")
	require.NoError(t, err)

	assert.Equal(t, []internal.LetterCode{
		internal.GREY,  // A — CRANE has one A; it's used by pos 4 (GREEN), so this is GREY
		internal.GREY,  // B — not in CRANE
		internal.GREEN, // A — CRANE[2] = A (correct position)
		internal.GREY,  // T — not in CRANE
		internal.GREEN, // E — CRANE[4] = E
	}, result.Codes)
}

// ---------------------------------------------------------------------------
// Validation tests (these FAIL until Bug 2 is fixed)
// ---------------------------------------------------------------------------

// TestValidation_WrongLength: a guess shorter than 5 letters should return ValidationError.
func TestValidation_WrongLength(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "REACT"})

	_, err := svc.SubmitGuess(gameID, "HI")

	var valErr *internal.ValidationError
	assert.ErrorAs(t, err, &valErr, "expected ValidationError for wrong-length guess")
}

// TestValidation_NotInDictionary: a 5-letter word not in the dictionary should return ValidationError.
func TestValidation_NotInDictionary(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "REACT"})

	_, err := svc.SubmitGuess(gameID, "XXXXX")

	var valErr *internal.ValidationError
	assert.ErrorAs(t, err, &valErr, "expected ValidationError for non-dictionary word")
}

// TestValidation_ValidWord: a valid dictionary word should succeed without errors.
func TestValidation_ValidWord(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "REACT"})

	_, err := svc.SubmitGuess(gameID, "APPLE")
	assert.NoError(t, err)
}

// ---------------------------------------------------------------------------
// Concurrency tests
// ---------------------------------------------------------------------------

// TestConcurrency_MaxGuesses: 5 goroutines submit guesses to a maxGuesses=2 game.
// At most 2 should succeed; game.Guesses length must not exceed 2.
func TestConcurrency_MaxGuesses(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "REACT", MaxGuesses: 2})

	var wg sync.WaitGroup
	var mu sync.Mutex
	successCount := 0

	for i := 0; i < 5; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			_, err := svc.SubmitGuess(gameID, "APPLE")
			if err == nil {
				mu.Lock()
				successCount++
				mu.Unlock()
			}
		}()
	}
	wg.Wait()

	game, err := svc.GetGame(gameID)
	require.NoError(t, err)

	assert.LessOrEqual(t, successCount, 2, "at most 2 guesses should succeed")
	assert.LessOrEqual(t, len(game.Guesses), 2, "game.Guesses must not exceed MaxGuesses")
}

// TestConcurrency_IndependentGames: concurrent guesses to different games both succeed.
func TestConcurrency_IndependentGames(t *testing.T) {
	svc := newService()
	gameID1 := svc.StartGame(internal.GameOptions{Answer: "APPLE"})
	gameID2 := svc.StartGame(internal.GameOptions{Answer: "BRAVE"})

	var wg sync.WaitGroup
	var err1, err2 error

	wg.Add(2)
	go func() {
		defer wg.Done()
		_, err1 = svc.SubmitGuess(gameID1, "APPLE")
	}()
	go func() {
		defer wg.Done()
		_, err2 = svc.SubmitGuess(gameID2, "BRAVE")
	}()
	wg.Wait()

	assert.NoError(t, err1, "guess on game 1 should succeed")
	assert.NoError(t, err2, "guess on game 2 should succeed")
}

// ---------------------------------------------------------------------------
// Game flow tests
// ---------------------------------------------------------------------------

// TestGameFlow_RemainingGuesses: remainingGuesses should decrement correctly.
func TestGameFlow_RemainingGuesses(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "REACT", MaxGuesses: 3})

	r1, err := svc.SubmitGuess(gameID, "APPLE")
	require.NoError(t, err)
	assert.Equal(t, 2, r1.RemainingGuesses)

	r2, err := svc.SubmitGuess(gameID, "BRAVE")
	require.NoError(t, err)
	assert.Equal(t, 1, r2.RemainingGuesses)

	r3, err := svc.SubmitGuess(gameID, "CRANE")
	require.NoError(t, err)
	assert.Equal(t, 0, r3.RemainingGuesses)
}

// TestGameFlow_GameOverAfterWin: submitting a second guess after winning returns GameOverError.
func TestGameFlow_GameOverAfterWin(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "REACT"})

	_, err := svc.SubmitGuess(gameID, "REACT")
	require.NoError(t, err)

	_, err = svc.SubmitGuess(gameID, "REACT")
	var gameOver *internal.GameOverError
	assert.ErrorAs(t, err, &gameOver)
}

// TestGameFlow_GameOverAfterLoss: submitting a guess after losing returns GameOverError.
func TestGameFlow_GameOverAfterLoss(t *testing.T) {
	svc := newService()
	gameID := svc.StartGame(internal.GameOptions{Answer: "APPLE", MaxGuesses: 1})

	_, err := svc.SubmitGuess(gameID, "BRAVE")
	require.NoError(t, err)

	_, err = svc.SubmitGuess(gameID, "APPLE")
	var gameOver *internal.GameOverError
	assert.ErrorAs(t, err, &gameOver)
}
