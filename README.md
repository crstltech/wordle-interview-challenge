# Wordle API Service — Senior Engineer Challenge

A two-phase debugging and design challenge built around a Wordle service. Choose your language.

**Unfamiliar with Wordle?** [Play the actual game here](https://www.nytimes.com/games/wordle/index.html)

---

## Choose Your Language

| Language | Folder | Requirements | Run Tests |
|----------|--------|--------------|-----------|
| [TypeScript](./typescript/) | `typescript/` | Node.js ≥ 20 | `npm test` |
| [Go](./golang/) | `golang/` | Go ≥ 1.21 | `go test ./...` |
| [Python](./python/) | `python/` | Python ≥ 3.11 | `pytest` |
| [Java](./java/) | `java/` | Java ≥ 21, Maven ≥ 3.8 | `mvn test` |

Each folder contains the same challenge with language-idiomatic code. See the folder README for setup and test commands.

---

## The Challenge

**Total time: 45 minutes** (Phase 1: 15 min, Phase 2: 30 min)

### Phase 1 — Debug & Fix (15 min)

Fix the bugs in the Wordle service. Run the tests, diagnose the failures, fix the bugs.

### Phase 2 — Production Features (30 min)

Pick **one** track and do it well:

- **Track A — Algorithm:** Build an optimal solver (≤4 guesses average)
- **Track B — Scale:** Design for 1M concurrent users — write a `DESIGN.md`
- **Track C — Features:** Implement hard mode + multiplayer race mode

You won't finish everything. We want to see how you prioritize and communicate tradeoffs.

---

## Evaluation

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

## Wordle Rules Reference

- **Green**: Correct letter, correct position
- **Yellow**: Letter exists in answer but wrong position
- **Grey**: Letter not in answer (or already accounted for)
