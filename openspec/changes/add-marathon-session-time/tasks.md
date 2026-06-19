# Tasks

- [x] Add deterministic `GameSessionTimer` tests.
- [x] Implement the timer and formatting with an injectable monotonic clock.
- [x] Add elapsed-time formatting and SidePanel layout tests.
- [x] Add `Time` below `Lines` without changing the side panel's fixed size.
- [ ] Integrate timer lifecycle with pause, overlays, restart, and game over.
- [ ] Add `Time` below `Lines` without changing the side panel's fixed size.
- [ ] Update README, Help, canonical specs, and future-mode backlog.
- [ ] Verify focused tests, full test suite, and both visual themes.
- [ ] Archive the completed OpenSpec change.

## Verification Notes
- `GameSessionTimerTest` RED: compilation failed because the timer did not exist.
- `GameSessionTimerTest` GREEN: 4 tests passed for accumulation, pause/resume,
  reset, idempotence, and monotonic display protection.
- Formatter/SidePanel RED: compilation failed because `ElapsedTimeFormatter`
  and the elapsed-time SidePanel constructor did not exist.
- Formatter/SidePanel GREEN: 8 focused tests passed, including hour formatting,
  core-stat order, and the unchanged `200 x 520` panel size.
