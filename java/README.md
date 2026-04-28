# Wordle API Service — Senior Engineer Challenge (Java)

**Total time: 45 minutes** (Phase 1: 15 min, Phase 2: 30 min)

**Unfamiliar with Wordle?** [Play the actual game here](https://www.nytimes.com/games/wordle/index.html)

## Overview

This is a two-phase challenge designed to assess debugging, system design, and prioritization skills. You're given a buggy Wordle service that needs fixing, then you'll extend it with production-ready features.

**Important:** You won't finish everything in Phase 2 — we want to see how you prioritize and communicate tradeoffs.

## Setup

> Run all commands from the `java/` directory. Requires Java 21+ and Maven 3.8+.

```bash
mvn test                          # Run all tests
mvn test -Dtest=WordleServiceTest # Run only Phase 1 tests (recommended to start)
mvn exec:java                     # See the bugs in action (demo)
```

---

# 🔧 PHASE 1: Debug & Fix (15 minutes)

## Your Task

Fix the bugs in `src/main/java/wordle/WordleService.java`. Your job:

1. **Run the tests** (`mvn test`) and analyse the failures
2. **Identify the root causes** (there are multiple bugs in `WordleService`)
3. **Fix the bugs** in `WordleService.java`
4. **Get all `WordleServiceTest` tests passing**

**Note:** `WordleSolverTest` is skipped (`@Disabled`) — those are for Phase 2.

## What We're Looking For

- Can you quickly diagnose issues from test output?
- Do you understand the Wordle duplicate-letter algorithm?
- Can you spot thread-safety and concurrency bugs?

## Hints

- The demo (`mvn exec:java`) shows the bugs visually
- Some bugs are in game logic, others in validation and concurrency
- Read the test descriptions carefully — they reveal expected behaviour

## Wordle Rules Reference

Standard Wordle letter colouring:
- **Green (GREEN)**: Correct letter in correct position
- **Yellow (YELLOW)**: Letter exists in answer but wrong position
- **Grey (GREY)**: Letter not in answer (or already accounted for)

For duplicate letters: process exact matches (GREEN) first, then wrong-position matches (YELLOW), tracking which answer letters are "used up."

---

# 🚀 PHASE 2: Production Features (30 minutes)

**You now have a working Wordle service. Time to make it production-ready.**

Choose **ONE** track below based on your strengths.

## Track A: Algorithm (Solver Implementation)

**Challenge:** Build an optimal Wordle solver

Implement a solver that can guess any word in **≤ 4 attempts** using information theory:

```java
public class WordleSolver {
    /**
     * Given the current game state (previous guesses + results),
     * return the optimal next guess that minimises expected
     * remaining possibilities.
     *
     * Your solver will be tested against a sample of random words.
     * Target: 95% solved in ≤4 guesses, 100% in ≤6 guesses.
     */
    public String getNextGuess(
        List<GuessAttempt> previousGuesses,
        List<String> possibleWords
    );
}
```

**Evaluation:**
- Algorithm correctness (does it find valid solutions?)
- Optimisation strategy (information entropy, frequency analysis, etc.)
- Performance (can it run in real time?)
- Code clarity and explanation of approach

## Track B: Scale (System Design)

**Challenge:** Design for 1M concurrent users

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
   - Memory usage (current `HashMap` grows unbounded)
   - Game cleanup/TTL strategy
   - Connection pooling
   - Read replicas

4. **New Features**
   - **Daily Puzzle Mode**: Same word for all users each day
   - **Statistics Tracking**: Win rate, guess distribution per user
   - **Leaderboards**: Fastest solvers today

**Deliverable:**
- Write a `DESIGN.md` with your architecture decisions
- Implement 1–2 key features (daily puzzle or stats tracking)
- Add appropriate error handling, logging, and monitoring hooks

## Track C: Features (Hard Mode + Multiplayer)

**Challenge:** Implement advanced game modes

Add these features to the service:

1. **Hard Mode**
   - Any revealed hints (green/yellow letters) MUST be used in subsequent guesses
   - Green letters must stay in same position
   - Yellow letters must be included but can move
   - Validate each guess follows these rules

2. **Multiplayer Race Mode**
   - Two players compete on the same word simultaneously
   - First to solve wins
   - Handle edge cases (both solve on same turn, one quits mid-game)

3. **Statistics System**
   - Track per-user stats: games played, win rate, guess distribution
   - Current streak tracking
   - Best/worst performances

**Deliverable:**
- Implement at least 2 of the 3 features above
- Write comprehensive tests for your new features
- Handle edge cases gracefully

---

## Files

```
src/
├── main/java/wordle/
│   ├── LetterCode.java          # Shared enum (don't modify)
│   ├── GuessResult.java         # Record type (don't modify)
│   ├── GameState.java           # Mutable game state (don't modify)
│   ├── GameOptions.java         # Options record (don't modify)
│   ├── ValidationException.java # Exception types (don't modify)
│   ├── GameNotFoundException.java
│   ├── GameOverException.java
│   ├── GuessAttempt.java        # Solver input type (don't modify)
│   ├── DictionaryService.java   # Mock dictionary — working, don't modify
│   ├── WordleService.java       # BUGGY — fix this file
│   ├── WordleSolver.java        # Phase 2 stub
│   └── Demo.java                # Shows bugs in action
└── test/java/wordle/
    ├── WordleServiceTest.java   # Phase 1 tests (some failing)
    └── WordleSolverTest.java    # Phase 2 tests (@Disabled)
```

## Dictionary API

`DictionaryService` provides:
- `isValidWord(String word): boolean` — simulates external API latency (10–50ms)
- `getRandomWord(): String` — returns a random 5-letter word
- `getAllWords(): List<String>` — all words (useful for solver)

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

- **Talk through your thinking** — we want to understand your approach
- **Ask clarifying questions** — requirements may be intentionally ambiguous
- **Prioritise ruthlessly** — you won't finish everything in Phase 2
- **Focus on correctness over optimisation** — working code beats fast broken code
- **Pick ONE track** in Phase 2 and do it well

### What Success Looks Like
- Phase 1 done in 15 minutes with all tests passing
- Clear explanation of the bugs and fixes
- Phase 2 has 1–2 polished features with tests and documentation
- Thoughtful discussion of tradeoffs and alternative approaches

---

Good luck! Remember: **communication and decision-making** matter more than finishing everything.
