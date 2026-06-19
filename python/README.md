# Wordle API Service — Senior Engineer Challenge (Python)

**Total time: 45 minutes** (Phase 1: 15 min, Phase 2: 30 min)

## Overview

This is a two-phase challenge designed to assess debugging, system design, and prioritization skills. You're given a buggy Wordle service that needs fixing, then you'll extend it with production-ready features.

**Important:** You won't finish everything in Phase 2 — we want to see how you prioritize and communicate tradeoffs.

**Unfamiliar with Wordle?** [Play the actual game here](https://www.nytimes.com/games/wordle/index.html)

## Installation

**Install Python 3.11+** (via [pyenv](https://github.com/pyenv/pyenv), recommended):

```bash
# macOS (Homebrew)
brew install pyenv
pyenv install 3.11.0
pyenv local 3.11.0   # sets version for this directory

# Linux
curl https://pyenv.run | bash
# restart your shell, then:
pyenv install 3.11.0
pyenv local 3.11.0
```

Or download directly from [python.org](https://www.python.org/downloads/).

Verify:

```bash
python --version   # should print Python 3.11.x or later
```

## Setup

All commands should be run from the `python/` directory.

```bash
cd python
python -m venv .venv
source .venv/bin/activate        # Windows: .venv\Scripts\activate
pip install -e ".[dev]"
pytest                               # Run all tests
pytest tests/test_wordle_service.py  # Run Phase 1 tests
python demo.py                       # See the bugs in action
```

---

# PHASE 1: Debug & Fix (15 minutes)

## Your Task

Fix the bugs in `src/wordle_service.py`. Your job:

1. **Run the tests** (`pytest`) and analyze the failures
2. **Identify the root causes** (there are multiple bugs in `WordleService`)
3. **Fix the bugs** in `src/wordle_service.py`
4. **Get all WordleService tests passing**

**Note:** You'll see some WordleSolver tests are skipped — those are for Phase 2, ignore them for now.

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

# PHASE 2: Production Features (30 minutes)

**You now have a working Wordle service. Time to make it production-ready.**

Choose **ONE** track below based on your strengths. You can switch tracks, but focus on doing one thing well rather than three things poorly.

## Track A: Algorithm (Solver Implementation)

**Challenge:** Build an optimal Wordle solver

Implement a solver that can guess any word in **<= 4 attempts** using information theory:

```python
class WordleSolver:
    def get_next_guess(
        self,
        previous_guesses: list[PreviousGuess],
        possible_words: list[str] | None = None,
    ) -> str:
        """
        Given the current game state (previous guesses + results),
        return the optimal next guess that minimizes expected
        remaining possibilities.

        Your solver will be tested against 100 random words.
        Target: 95% solved in <=4 guesses, 100% in <=6 guesses
        """
        ...
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
   - Memory usage (current dict grows unbounded)
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

**Evaluation:**
- Feature completeness and correctness
- Edge case handling
- Test coverage
- Code organization

---

## Files

```
src/
├── types.py              # Shared types (don't modify)
├── dictionary_service.py # Mock dictionary (working, don't modify)
├── wordle_service.py     # BUGGY — fix this file
└── wordle_solver.py      # STUB — implement for Track A

tests/
├── test_wordle_service.py  # Tests (some failing)
└── test_wordle_solver.py   # Tests (all skipped until Phase 2)

demo.py                     # Run to see the bugs in action
```

## Dictionary API

The `DictionaryService` provides:
- `async is_valid_word(word: str) -> bool` — async validation (10-50ms simulated latency)
- `get_random_word() -> str` — returns a random 5-letter word
- `get_all_words() -> list[str]` — get all words (useful for solver)

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
- Phase 1 done in 15 minutes with all tests passing
- Clear explanation of the bugs and fixes
- Phase 2 has 1-2 polished features with tests and documentation
- Thoughtful discussion of tradeoffs and alternative approaches

---

Good luck! Remember: **communication and decision-making** matter more than finishing everything.
