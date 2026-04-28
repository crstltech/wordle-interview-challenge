package main

import (
	"fmt"
	"strings"
	"sync"

	"wordle/internal"
)

func codeSymbol(c internal.LetterCode) string {
	switch c {
	case internal.GREEN:
		return "[G]"
	case internal.YELLOW:
		return "[Y]"
	default:
		return "[_]"
	}
}

func printResult(guess string, codes []internal.LetterCode) {
	symbols := make([]string, len(codes))
	for i, c := range codes {
		symbols[i] = codeSymbol(c)
	}
	letters := make([]string, len(guess))
	for i, ch := range guess {
		letters[i] = string(ch)
	}
	fmt.Printf("  %s   %s\n", strings.Join(letters, " "), strings.Join(symbols, ""))
}

func main() {
	dict := internal.NewDictionaryService()
	svc := internal.NewWordleService(dict)

	// -----------------------------------------------------------------------
	// Scenario 1: Duplicate letter handling (shows Bug 1 in action)
	// -----------------------------------------------------------------------
	fmt.Println("=== Scenario 1: Duplicate Letters ===")
	fmt.Println()

	fmt.Println("Game: answer=PAPER, guess=APPLE")
	fmt.Println("Expected: [Y][Y][G][_][Y]  (buggy impl gives too many YELLOWs)")
	gameID := svc.StartGame(internal.GameOptions{Answer: "PAPER"})
	result, err := svc.SubmitGuess(gameID, "APPLE")
	if err != nil {
		fmt.Printf("  ERROR: %v\n", err)
	} else {
		printResult(result.Guess, result.Codes)
	}
	fmt.Println()

	fmt.Println("Game: answer=SHEEP, guess=CREEP")
	fmt.Println("Expected: [_][_][G][G][G]  (buggy impl colours first E yellow)")
	gameID = svc.StartGame(internal.GameOptions{Answer: "SHEEP"})
	result, err = svc.SubmitGuess(gameID, "CREEP")
	if err != nil {
		fmt.Printf("  ERROR: %v\n", err)
	} else {
		printResult(result.Guess, result.Codes)
	}
	fmt.Println()

	// -----------------------------------------------------------------------
	// Scenario 2: Validation (shows Bug 2 in action)
	// -----------------------------------------------------------------------
	fmt.Println("=== Scenario 2: Validation ===")
	fmt.Println()

	gameID = svc.StartGame(internal.GameOptions{Answer: "REACT"})

	fmt.Println("Guess: 'HI' (wrong length — should return ValidationError)")
	_, err = svc.SubmitGuess(gameID, "HI")
	if err != nil {
		fmt.Printf("  Got error: %v\n", err)
	} else {
		fmt.Println("  BUG: no error returned for wrong-length guess!")
	}
	fmt.Println()

	fmt.Println("Guess: 'XXXXX' (not in dictionary — should return ValidationError)")
	_, err = svc.SubmitGuess(gameID, "XXXXX")
	if err != nil {
		fmt.Printf("  Got error: %v\n", err)
	} else {
		fmt.Println("  BUG: no error returned for non-dictionary word!")
	}
	fmt.Println()

	// -----------------------------------------------------------------------
	// Scenario 3: Concurrency race condition (shows Bug 3 in action)
	// -----------------------------------------------------------------------
	fmt.Println("=== Scenario 3: Concurrency Race Condition ===")
	fmt.Println()
	fmt.Println("5 goroutines submit guesses to a maxGuesses=2 game simultaneously.")
	fmt.Println("A correct implementation allows at most 2 successes.")
	fmt.Println()

	gameID = svc.StartGame(internal.GameOptions{Answer: "REACT", MaxGuesses: 2})

	var wg sync.WaitGroup
	var mu sync.Mutex
	successes := 0
	errors := 0

	for i := 0; i < 5; i++ {
		wg.Add(1)
		goroutineNum := i + 1
		go func() {
			defer wg.Done()
			_, err := svc.SubmitGuess(gameID, "APPLE")
			mu.Lock()
			defer mu.Unlock()
			if err == nil {
				successes++
				fmt.Printf("  Goroutine %d: SUCCESS\n", goroutineNum)
			} else {
				errors++
				fmt.Printf("  Goroutine %d: %v\n", goroutineNum, err)
			}
		}()
	}
	wg.Wait()

	game, _ := svc.GetGame(gameID)
	fmt.Println()
	fmt.Printf("  Successes: %d (expected ≤2)\n", successes)
	fmt.Printf("  Errors:    %d\n", errors)
	fmt.Printf("  game.Guesses length: %d (should be ≤2)\n", len(game.Guesses))
	if successes > 2 || len(game.Guesses) > 2 {
		fmt.Println()
		fmt.Println("  BUG CONFIRMED: race condition allowed more guesses than MaxGuesses!")
	} else {
		fmt.Println()
		fmt.Println("  (Race condition may not have fired — try running again.)")
	}
}
