package internal

import (
	"strings"
	"time"

	"github.com/google/uuid"
)

const defaultMaxGuesses = 6

// WordleService manages Wordle game state.
type WordleService struct {
	dictionary *DictionaryService
	games      map[string]*GameState
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
func (s *WordleService) SubmitGuess(gameID string, guess string) (*GuessResult, error) {
	game, ok := s.games[gameID]
	if !ok {
		return nil, &GameNotFoundError{GameID: gameID}
	}

	if game.Won || game.Lost {
		return nil, &GameOverError{GameID: gameID}
	}

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
func (s *WordleService) calculateLetterCodes(guess, answer string) []LetterCode {
	codes := make([]LetterCode, len(guess))

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
