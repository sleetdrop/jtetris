# Tasks

- [x] Add deterministic `GameSessionTimer` tests.
- [ ] Implement the timer and formatting with an injectable monotonic clock.
- [ ] Integrate timer lifecycle with pause, overlays, restart, and game over.
- [ ] Add `Time` below `Lines` without changing the side panel's fixed size.
- [ ] Update README, Help, canonical specs, and future-mode backlog.
- [ ] Verify focused tests, full test suite, and both visual themes.
- [ ] Archive the completed OpenSpec change.

## Verification Notes
- `GameSessionTimerTest` RED: compilation failed because the timer did not exist.
- `GameSessionTimerTest` GREEN: 4 tests passed for accumulation, pause/resume,
  reset, idempotence, and monotonic display protection.
