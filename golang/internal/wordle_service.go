package internal

import (
	"strings"
	"time"

	"github.com/google/uuid"
)

const defaultMaxGuesses = 6

// WordleService manages Wordle game state.
// NOTE: This implementation contains intentional bugs for the interview challenge.
type WordleService struct {
	dictionary *DictionaryService
	games      map[string]*GameState // BUG 3: plain map with no mutex — race condition
}

// NewWordleService creates a new WordleService.
// If dictionary is nil, a new DictionaryService is created.
func NewWordleService(dictionary *DictionaryService) *WordleService {
	if dictionary == nil {
		dictionary = NewDictionaryService()
	}
	return &WordleService{
		dictionary: dictionary,
		games:      make(map[string]*GameState),
	}
}

// StartGame creates a new game and returns its ID.
func (s *WordleService) StartGame(opts GameOptions) string {
	maxGuesses := opts.MaxGuesses
	if maxGuesses == 0 {
		maxGuesses = defaultMaxGuesses
	}

	answer := strings.ToUpper(opts.Answer)
	if answer == "" {
		answer = s.dictionary.GetRandomWord()
	}

	id := uuid.New().String()
	game := &GameState{
		ID:         id,
		Answer:     answer,
		MaxGuesses: maxGuesses,
		Guesses:    []string{},
		Won:        false,
		Lost:       false,
	}
	s.games[id] = game
	return id
}

// GetGame returns the game state for the given ID, or an error if not found.
func (s *WordleService) GetGame(gameID string) (*GameState, error) {
	game, ok := s.games[gameID]
	if !ok {
		return nil, &GameNotFoundError{GameID: gameID}
	}
	return game, nil
}

// SubmitGuess submits a guess for the given game and returns the result.
// BUG 2: No validation — guess length and dictionary membership are not checked.
// BUG 3: No mutex — concurrent goroutines can race past the game-over check.
func (s *WordleService) SubmitGuess(gameID string, guess string) (*GuessResult, error) {
	game, ok := s.games[gameID]
	if !ok {
		return nil, &GameNotFoundError{GameID: gameID}
	}

	// BUG 2: missing validation block — should check:
	//   len(guess) == 5  →  ValidationError
	//   s.dictionary.IsValidWord(guess)  →  ValidationError

	if game.Won || game.Lost {
		return nil, &GameOverError{GameID: gameID}
	}

	// BUG 3: sleep BEFORE state update with no mutex; concurrent goroutines all
	// pass the Won/Lost check above, then sleep here, then all update game state,
	// allowing more guesses than MaxGuesses.
	time.Sleep(10 * time.Millisecond)

	normalizedGuess := strings.ToUpper(guess)
	codes := s.calculateLetterCodes(normalizedGuess, game.Answer)

	game.Guesses = append(game.Guesses, normalizedGuess)
	won := normalizedGuess == game.Answer
	lost := !won && len(game.Guesses) >= game.MaxGuesses

	game.Won = won
	game.Lost = lost

	remaining := game.MaxGuesses - len(game.Guesses)

	return &GuessResult{
		Guess:            normalizedGuess,
		Codes:            codes,
		RemainingGuesses: remaining,
		Won:              won,
		Lost:             lost,
	}, nil
}

// calculateLetterCodes computes GREEN/YELLOW/GREY codes for each letter.
// BUG 1: This naive implementation does not track which answer letters are already
// "used up" by green matches, so duplicate letters get too many YELLOWs.
func (s *WordleService) calculateLetterCodes(guess, answer string) []LetterCode {
	codes := make([]LetterCode, len(guess))

	// BUG 1: single-pass algorithm — no accounting for greens first.
	for i, guessChar := range guess {
		if guess[i] == answer[i] {
			codes[i] = GREEN
		} else if strings.ContainsRune(answer, guessChar) {
			codes[i] = YELLOW
		} else {
			codes[i] = GREY
		}
	}

	return codes
}
