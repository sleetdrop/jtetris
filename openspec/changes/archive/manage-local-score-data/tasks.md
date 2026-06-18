# Tasks

- [x] Add failing tests for platform-specific score data path resolution.
- [x] Implement the score data path resolver without external dependencies.
- [x] Add failing tests for legacy migration and new-store precedence.
- [x] Implement safe legacy score migration and parent-directory creation.
- [x] Add failing tests for durable player deletion and save-failure rollback.
- [x] Implement the `ScoreManager` deletion contract.
- [x] Add failing tests for leaderboard selection and deletion interaction.
- [x] Implement leaderboard delete, confirmation, feedback, and refresh overlays.
- [x] Update `doc/overview.md`, `doc/algorithms.md`, and relevant OpenSpec documentation.
- [x] Run `./mvnw -Djava.awt.headless=true clean test` and record verification evidence.
- [x] Review the complete diff, update canonical specs, and archive this change.

## Verification Notes

- TDD RED confirmed for `ScoreDataPathsTest`: compilation failed because `ScoreDataPaths` did not exist.
- `ScoreDataPathsTest` passed: 7 tests, 0 failures, 0 errors, 0 skipped.
- TDD RED confirmed for migration tests: explicit `ScoreManager` paths and persistence seam did not exist.
- TDD RED confirmed for deletion tests: `deleteUser(String)` did not exist.
- TDD RED confirmed for malformed properties: `Properties.load()` raised `IllegalArgumentException`.
- `ScoreManagerTest` passed: 8 tests, 0 failures, 0 errors, 0 skipped.
- TDD RED confirmed for `LeaderboardContentTest`: `LeaderboardContent` did not exist.
- `LeaderboardContentTest` passed: 3 tests, 0 failures, 0 errors, 0 skipped.
- Focused leaderboard and overlay tests passed: 7 tests, 0 failures, 0 errors, 0 skipped.
- Full verification passed on 2026-06-18 with `./mvnw -Djava.awt.headless=true clean test`: 81 tests, 0 failures, 0 errors, 0 skipped.
- `git diff --check` passed before archive.
- Manual visual verification remains available through the packaged application; no automated screenshot was taken per user preference.
