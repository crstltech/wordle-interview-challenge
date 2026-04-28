# Wordle API Service — Senior Engineer Challenge (Go)

**Total time: 45 minutes** (Phase 1: 15 min, Phase 2: 30 min)

**Unfamiliar with Wordle?** [Play the actual game here](https://www.nytimes.com/games/wordle/index.html)

## Overview

This is a two-phase challenge designed to assess debugging, system design, and prioritization skills. You're given a buggy Wordle service that needs fixing, then you'll extend it with production-ready features.

**Important:** You won't finish everything in Phase 2 — we want to see how you prioritize and communicate tradeoffs.

## Setup

```bash
cd golang
go mod download
go test ./...                             # Run all tests
go test ./internal/... -run TestWordle    # Run Phase 1 tests
go run ./cmd/demo/                        # See the bugs in action
```

> All commands should be run from the `golang/` directory.

---

# PHASE 1: Debug & Fix (15 minutes)

## Your Task

Fix the bugs in `internal/wordle_service.go`. Your job:

1. **Run the tests** (`go test ./...`) and analyse the failures
2. **Identify the root causes** (there are multiple bugs in `WordleService`)
3. **Fix the bugs** in `internal/wordle_service.go`
4. **Get all WordleService tests passing**

**Note:** You'll see some `WordleSolver` tests skipped — those are for Phase 2, ignore them for now.

## What We're Looking For

- Can you quickly diagnose issues from test output?
- Do you understand the Wordle duplicate-letter algorithm?
- Can you spot race conditions and concurrency bugs?

## Hints

- The demo script (`go run ./cmd/demo/`) shows the bugs visually
- Some bugs are in the game logic, others in validation and concurrency
- Read the test descriptions carefully — they reveal expected behaviour

## Wordle Rules Reference

Standard Wordle letter coloring:
- **Green (0)**: Correct letter in correct position
- **Yellow (1)**: Letter exists in answer but wrong position
- **Grey (2)**: Letter not in answer (or already accounted for)

For duplicate letters: Process exact matches (green) first, then wrong-position matches (yellow), tracking which answer letters are "used up."

---

# PHASE 2: Production Features (30 minutes)

**You now have a working Wordle service. Time to make it production-ready.**

Choose **ONE** track below based on your strengths. You can switch tracks, but focus on doing one thing well rather than three things poorly.

## Track A: Algorithm (Solver Implementation)

**Challenge:** Build an optimal Wordle solver.

Implement the methods in `internal/wordle_solver.go` so the solver can guess any word in **≤ 4 attempts** using information theory:

```go
// GetNextGuess returns the optimal next guess given previous guess results.
func (s *WordleSolver) GetNextGuess(previousGuesses []PreviousGuess) string

// Solve plays a complete game against answer, returning all guesses made.
// Returns nil if failed within maxGuesses.
func (s *WordleSolver) Solve(answer string, maxGuesses int) []string

// Benchmark runs the solver against every dictionary word and returns stats.
func (s *WordleSolver) Benchmark() BenchmarkStats
```

**Evaluation:**
- Algorithm correctness (does it find valid solutions?)
- Optimisation strategy (information entropy, frequency analysis, etc.)
- Performance (can it run in real-time?)
- Code clarity and explanation of approach

## Track B: Scale (System Design)

**Challenge:** Design for 1M concurrent users.

The service needs to scale. Design and document:

1. **Persistence Strategy**
   - What database? (SQL vs NoSQL vs Redis vs...)
   - Schema design
   - Indexing strategy

2. **Caching Layer**
   - What to cache? (game state, dictionary, daily words?)
   - Cache invalidation strategy
   - Distributed caching (Redis cluster?)

3. **Performance Optimisations**
   - Memory usage (current map grows unbounded)
   - Game cleanup/TTL strategy
   - Connection pooling
   - Read replicas

4. **New Features**
   - **Daily Puzzle Mode**: Same word for all users each day
   - **Statistics Tracking**: Win rate, guess distribution per user
   - **Leaderboards**: Fastest solvers today

**Deliverable:**
- Write a `DESIGN.md` with your architecture decisions
- Implement 1-2 key features (daily puzzle or stats tracking)
- Add appropriate error handling, logging, and monitoring hooks

**Evaluation:**
- Architectural thinking and tradeoff analysis
- Scalability considerations
- Production-readiness (logging, monitoring, error handling)
- Clear communication of decisions

## Track C: Features (Hard Mode + Multiplayer)

**Challenge:** Implement advanced game modes.

Add these features to the service:

1. **Hard Mode**
   - Any revealed hints (green/yellow letters) MUST be used in subsequent guesses
   - Green letters must stay in the same position
   - Yellow letters must be included but can move
   - Validate each guess follows these rules

2. **Multiplayer Race Mode**
   - Two players compete on the same word simultaneously
   - First to solve wins
   - Real-time updates (simulate with polling or webhook concept)
   - Handle edge cases (both solve on same turn, one quits mid-game)

3. **Statistics System**
   - Track per-user stats: games played, win rate, guess distribution
   - Current streak tracking
   - Best/worst performances

**Deliverable:**
- Implement at least 2 of the 3 features above
- Write comprehensive tests for your new features
- Handle edge cases gracefully

**Evaluation:**
- Feature completeness and correctness
- Edge case handling
- Test coverage
- Code organisation

---

## Files

```
golang/
├── go.mod
├── internal/
│   ├── types.go              # Shared types (don't modify)
│   ├── dictionary.go         # Mock dictionary (working, don't modify)
│   ├── wordle_service.go     # BUGGY — fix this file (Phase 1)
│   ├── wordle_solver.go      # STUB — implement this file (Phase 2, Track A)
│   ├── wordle_service_test.go
│   └── wordle_solver_test.go
└── cmd/
    └── demo/
        └── main.go           # Demo script
```

## Dictionary API

`DictionaryService` (in `internal/dictionary.go`) provides:
- `IsValidWord(word string) bool` — validation with 10–50 ms simulated latency
- `GetRandomWord() string` — returns a random 5-letter word
- `GetAllWords() []string` — get all words (useful for the solver)

---

## Evaluation Criteria

### Phase 1 (40%)
| Criteria | Weight |
|----------|--------|
| Identifies root causes correctly | 15% |
| Fixes duplicate letter algorithm | 15% |
| Handles concurrency properly | 10% |

### Phase 2 (60%)
| Criteria | Weight |
|----------|--------|
| Architectural thinking & tradeoffs | 25% |
| Implementation quality | 20% |
| Edge case handling | 10% |
| Communication & prioritisation | 5% |

---

## Tips

### General
- **Talk through your thinking** — we want to understand your approach
- **Ask clarifying questions** — requirements may be intentionally ambiguous
- **Prioritise ruthlessly** — you won't finish everything in Phase 2
- **Focus on correctness over optimisation** — working code beats fast broken code

### Phase 1
- Use the test output to guide your debugging (`go test ./... -v`)
- The demo script visualises the bugs clearly: `go run ./cmd/demo/`
- Don't over-engineer the fixes — simple solutions are fine

### Phase 2
- **Pick ONE track** and do it well
- Document your tradeoffs and decisions
- Production code needs error handling, logging, and tests
- If you have time, you can switch tracks or combine approaches

### What Success Looks Like
- Phase 1 done in 15 minutes with all tests passing
- Clear explanation of the bugs and fixes
- Phase 2 has 1-2 polished features with tests and documentation
- Thoughtful discussion of tradeoffs and alternative approaches
