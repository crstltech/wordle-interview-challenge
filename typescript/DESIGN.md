# Wordle Service - Production Architecture Design

**Author:** [Your Name]
**Date:** [Date]
**Track:** System Design & Scale

---

## Executive Summary

<!-- Brief overview of your design decisions and key tradeoffs -->

---

## Current State Analysis

### Current Architecture
- In-memory Map for game storage
- No persistence layer
- Single-process Node.js service
- Synchronous dictionary lookups

### Limitations
- Games lost on restart
- Memory grows unbounded
- No horizontal scaling possible
- Single point of failure

### Performance Baseline
- Current capacity: ~X concurrent games (estimate)
- Memory per game: ~Y bytes (estimate)
- Response time: ~Z ms

---

## Target Requirements

### Scale Requirements
- **Concurrent users:** 1,000,000 active games
- **Daily active users:** 10,000,000
- **Peak load:** 100,000 requests/second
- **Response time:** p99 < 100ms

### Availability Requirements
- **Uptime:** 99.9% (monthly)
- **Data durability:** 99.999%
- **Recovery time:** < 5 minutes

---

## Architecture Decisions

### 1. Database Choice

**Decision:** [PostgreSQL / MongoDB / DynamoDB / Redis / Other]

**Rationale:**
- [Why this database?]
- [What are the tradeoffs?]
- [Considered alternatives?]

**Schema Design:**
```sql
-- Your schema here
```

**Indexing Strategy:**
- [What indexes?]
- [Why?]

### 2. Caching Strategy

**Decision:** [Redis / Memcached / In-memory / CDN / Multi-tier]

**What to cache:**
- [ ] Active game state (TTL: X minutes)
- [ ] Dictionary words (never expire)
- [ ] User statistics (TTL: Y minutes)
- [ ] Daily puzzle word (TTL: 24 hours)

**Cache Invalidation:**
- [How do you keep cache consistent?]
- [What happens on cache miss?]

### 3. Application Architecture

**Deployment Model:**
- [ ] Monolith
- [ ] Microservices
- [ ] Serverless
- [ ] Hybrid

**Service Breakdown:**
```
[Diagram or description of services]
```

**API Design:**
```
POST /games
POST /games/:id/guesses
GET /games/:id
GET /daily
GET /users/:id/stats
GET /leaderboard
```

### 4. Data Flow

```
[Request flow diagram]

Example:
Client → Load Balancer → API Server → Cache (Redis) → Database (PostgreSQL)
                              ↓
                         Dictionary Service
```

### 5. Scaling Strategy

**Horizontal Scaling:**
- [How many servers at 1M users?]
- [Auto-scaling rules?]
- [Load balancing strategy?]

**Database Scaling:**
- [ ] Read replicas
- [ ] Sharding strategy
- [ ] Connection pooling

**Bottleneck Analysis:**
- [What will fail first at scale?]
- [How to address it?]

---

## Feature Implementation

### Daily Puzzle Mode

**Requirements:**
- Same word for all users each day (UTC timezone)
- Word changes at midnight UTC
- Shared leaderboard for daily puzzle

**Implementation:**
```typescript
// Your approach
```

**Database Changes:**
```sql
-- Schema updates
```

### Statistics Tracking

**Metrics to track:**
- Games played
- Games won
- Current streak
- Max streak
- Guess distribution [1, 2, 3, 4, 5, 6, X]

**Storage approach:**
- [How to store efficiently?]
- [How to query for leaderboards?]

### Leaderboards

**Types:**
- Daily fastest solvers
- All-time win rate
- Current streaks

**Implementation challenges:**
- [Real-time updates?]
- [How to handle ties?]
- [Pagination?]

---

## Production Concerns

### Monitoring & Observability

**Metrics to track:**
- [ ] Request rate
- [ ] Error rate
- [ ] Response time (p50, p95, p99)
- [ ] Database query time
- [ ] Cache hit rate
- [ ] Active games count

**Logging Strategy:**
```typescript
// What to log?
// Structured logging format?
```

### Error Handling

**Error scenarios:**
- Database down
- Cache down
- Dictionary service slow
- Invalid input
- Concurrent modification

**Graceful degradation:**
- [What works when DB is down?]
- [Read-only mode?]
- [Circuit breakers?]

### Security

**Concerns:**
- Rate limiting (per user/IP)
- Input validation
- SQL injection prevention
- DDoS protection
- Cheating prevention

### Cost Estimation

**Infrastructure costs at 1M concurrent users:**

| Component | Spec | Cost/month | Notes |
|-----------|------|------------|-------|
| Database | | $X | |
| Cache | | $X | |
| Compute | | $X | |
| Network | | $X | |
| **Total** | | **$X** | |

---

## Migration Strategy

**Phase 1:** Add persistence (dual-write to DB)
**Phase 2:** Add caching layer
**Phase 3:** Horizontal scaling
**Phase 4:** Add new features

**Rollback plan:**
- [How to rollback if issues?]

---

## Open Questions

1. [Question about requirements?]
2. [Tradeoff to discuss with team?]
3. [Performance target confirmation?]

---

## Appendix

### Alternative Approaches Considered

1. **Serverless (AWS Lambda)**
   - Pros: Auto-scaling, pay-per-use
   - Cons: Cold starts, complexity
   - Decision: Not chosen because...

2. **Graph Database**
   - Pros: Good for social features
   - Cons: Overkill for current needs
   - Decision: Not chosen because...

### References

- [Wordle game rules]
- [Similar system designs]
- [Benchmarks]
