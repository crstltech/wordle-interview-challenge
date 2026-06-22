# Wordle API Service — Senior Engineer Challenge (TypeScript)

**Total time: 45 minutes** (Phase 1: 15 min, Phase 2: 30 min)

**Unfamiliar with Wordle?** [Play the actual game here](https://www.nytimes.com/games/wordle/index.html)

## Overview

This is a two-phase challenge designed to assess debugging, system design, and prioritization skills. You're given a buggy Wordle service that needs fixing, then you'll extend it with production-ready features.

**Important:** You won't finish everything in Phase 2 — we want to see how you prioritize and communicate tradeoffs.

## Installation

**Install Node.js 20** (via [nvm](https://github.com/nvm-sh/nvm), recommended):

```bash
# macOS / Linux
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
# restart your shell, then:
nvm install 20
nvm use 20
```

Or download directly from [nodejs.org](https://nodejs.org/).

Verify:

```bash
node --version   # should print v20.x.x
npm --version
```

## Setup

> Run all commands from the `typescript/` directory.

```bash
npm install
npm test              # Run all tests
npm run test:phase1   # Run only Phase 1 tests (recommended to start)
npm run demo          # See the bugs in action
```

---

# 🔧 PHASE 1: Debug & Fix (15 minutes)

## Your Task

Fix the bugs in `src/WordleService.ts`. Your job:

1. **Run the tests** (`npm test`) and analyze the failures
2. **Identify the root causes** (there are multiple bugs in WordleService)
3. **Fix the bugs** in `src/WordleService.ts`
4. **Get all WordleService tests passing**

**Note:** You'll see some WordleSolver tests failing too - those are for Phase 2, ignore them for now.

## What We're Looking For

- Can you quickly diagnose issues from test output?
- Can you reason about correctness and edge cases from the failing tests?
- Are your fixes clean and well-justified?

## Wordle Rules Reference

Standard Wordle letter coloring:
- **Green (0)**: Correct letter in correct position
- **Yellow (1)**: Letter exists in answer but wrong position
- **Grey (2)**: Letter not in answer (or already accounted for)

---

# 🚀 PHASE 2: Production Features (30 minutes)

**You now have a working Wordle service. Time to make it production-ready.**

Choose **ONE** track below based on your strengths. You can switch tracks, but focus on doing one thing well rather than three things poorly.

## Track A: Algorithm (Solver Implementation)

**Challenge:** Build an optimal Wordle solver

Implement a solver that can guess any word in **≤ 4 attempts** using information theory:

```typescript
class WordleSolver {
  /**
   * Given the current game state (previous guesses + results),
   * return the optimal next guess that minimizes expected
   * remaining possibilities.
   *
   * Your solver will be tested against 100 random words.
   * Target: 95% solved in ≤4 guesses, 100% in ≤6 guesses
   */
  getNextGuess(
    previousGuesses: Array<{guess: string, result: LetterCode[]}>,
    wordList: string[]
  ): string;
}
```

**Evaluation:**
- Algorithm correctness (does it find valid solutions?)
- Optimization strategy (information entropy, frequency analysis, etc.)
- Performance (can it run in real-time?)
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

3. **Performance Optimizations**
   - Memory usage (current Map grows unbounded)
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

---

## Files

```
src/
├── types.ts              # Shared interfaces (don't modify)
├── DictionaryService.ts  # Mock dictionary (working, don't modify)
├── WordleService.ts      # BUGGY — fix this file
└── index.ts              # Demo script

tests/
├── WordleService.test.ts # Phase 1 tests (some failing)
└── WordleSolver.test.ts  # Phase 2 tests
```

## Dictionary API

The `DictionaryService` provides:
- `isValidWord(word: string): Promise<boolean>` — async validation (10-50ms simulated latency)
- `getRandomWord(): string` — returns a random 5-letter word
- `getAllWords(): string[]` — get all words (useful for solver)

---

## Evaluation Criteria

### Phase 1 (40%)
| Criteria | Weight |
|----------|--------|
| Identifies root causes correctly | 15% |
| Fixes the bugs correctly | 15% |
| Robust handling of edge cases | 10% |

### Phase 2 (60%)
| Criteria | Weight |
|----------|--------|
| Architectural thinking & tradeoffs | 25% |
| Implementation quality | 20% |
| Edge case handling | 10% |
| Communication & prioritization | 5% |

---

## Tips

- **Talk through your thinking** — we want to understand your approach
- **Ask clarifying questions** — requirements may be intentionally ambiguous
- **Prioritize ruthlessly** — you won't finish everything in Phase 2
- **Focus on correctness over optimization** — working code beats fast broken code
- **Pick ONE track** in Phase 2 and do it well

### What Success Looks Like
- ✅ Phase 1 done in 15 minutes with all tests passing
- ✅ Clear explanation of the bugs and fixes
- ✅ Phase 2 has 1-2 polished features with tests and documentation
- ✅ Thoughtful discussion of tradeoffs and alternative approaches

---

Good luck! Remember: **communication and decision-making** matter more than finishing everything.
