package internal

// PreviousGuess holds a single guess and its result codes, used as solver input.
type PreviousGuess struct {
	Guess  string
	Result []LetterCode
}

// BenchmarkStats holds the result of running the solver against the full dictionary.
type BenchmarkStats struct {
	TotalGames   int
	Wins         int
	Losses       int
	AverageGuess float64
	Distribution map[int]int // guesses taken → number of games
}

// WordleSolver is a stub solver. Candidates implement this in Phase 2, Track A.
type WordleSolver struct {
	dictionary *DictionaryService
}

// NewWordleSolver creates a new WordleSolver backed by the given dictionary.
func NewWordleSolver(dictionary *DictionaryService) *WordleSolver {
	return &WordleSolver{dictionary: dictionary}
}

// GetNextGuess returns the optimal next guess given the previous guess results.
// Phase 2, Track A: implement this method.
func (s *WordleSolver) GetNextGuess(previousGuesses []PreviousGuess) string {
	panic("not implemented")
}

// Solve plays a complete game against the given answer and returns the sequence
// of guesses made. Returns nil if the solver failed to find the answer within
// maxGuesses attempts.
// Phase 2, Track A: implement this method.
func (s *WordleSolver) Solve(answer string, maxGuesses int) []string {
	panic("not implemented")
}

// Benchmark runs the solver against every word in the dictionary and returns
// aggregate statistics.
// Phase 2, Track A: implement this method.
func (s *WordleSolver) Benchmark() BenchmarkStats {
	panic("not implemented")
}
