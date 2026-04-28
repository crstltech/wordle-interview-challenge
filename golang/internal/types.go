package internal

import "fmt"

// LetterCode represents the result code for a single letter in a guess.
type LetterCode int

const (
	GREEN  LetterCode = 0
	YELLOW LetterCode = 1
	GREY   LetterCode = 2
)

// GuessResult holds the result of a single guess submission.
type GuessResult struct {
	Guess            string
	Codes            []LetterCode
	RemainingGuesses int
	Won              bool
	Lost             bool
}

// GameState holds the full state of a Wordle game.
type GameState struct {
	ID         string
	Answer     string
	MaxGuesses int
	Guesses    []string
	Won        bool
	Lost       bool
}

// GameOptions configures a new game.
type GameOptions struct {
	Answer     string // optional; if empty a random word is chosen
	MaxGuesses int    // 0 means use default (6)
}

// ValidationError is returned when a guess fails validation.
type ValidationError struct {
	Message string
}

func (e *ValidationError) Error() string {
	return e.Message
}

// GameNotFoundError is returned when a game ID does not exist.
type GameNotFoundError struct {
	GameID string
}

func (e *GameNotFoundError) Error() string {
	return fmt.Sprintf("Game not found: %s", e.GameID)
}

// GameOverError is returned when an attempt is made to guess on a finished game.
type GameOverError struct {
	GameID string
}

func (e *GameOverError) Error() string {
	return fmt.Sprintf("Game is already over: %s", e.GameID)
}
