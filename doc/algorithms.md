# Algorithms & Mechanics

## Board representation
- Grid: `TetrominoType[HEIGHT][WIDTH]`, with HEIGHT=22 (top 2 rows hidden for spawn).
- Active piece: `Tetromino current`; upcoming pieces: an ordered three-entry `ArrayDeque<TetrominoType>`.
- Piece generation uses `PieceBag` (7-bag randomizer) to keep distribution fair.

## Next queue
- `Board` owns exactly three upcoming tetromino types and exposes them as an immutable snapshot through `getNextQueue()`.
- Normal spawn removes the queue head, makes it active at spawn orientation, and appends one new type from `PieceBag`.
- `getNext()` remains a compatibility accessor for the queue head; new consumers should use `getNextQueue()` when complete preview state matters.
- Seeded replay verification compares the complete queue so future-piece state is reproducible.

## Movement & rotation
- Movement uses `Board.move(dx, dy)`; validates against bounds and occupied cells.
- Rotation via `Board.rotateCW/CCW()`; uses `TetrominoType.cells(rotation)` with 4 precomputed rotations.
- Rotation uses SRS kick retries (separate I-piece table and JLSTZ table).
- Rotation succeeds on the first valid kick candidate; fails when all candidates are invalid.

## Gravity & locking
- `tick()` tries to move current down; if blocked, enters lock-delay countdown and locks only after the delay expires while still grounded.
- `hardDrop()` repeatedly moves down until blocked, then locks.
- Successful move/rotation resets lock-delay countdown.

## Hold piece
- `Board.hold()` supports one hold per active piece lifecycle.
- First hold stores current, promotes the upcoming queue head, and refills the queue; subsequent hold swaps with the stored piece without changing the queue.
- Hold is reset only after lock/spawn, preserving the per-turn hold guard.

## T-Spin detection (baseline)
- `Board` computes T-Spin on lock using `TSpinDetector` and stores result in `wasLastLockTSpin()`.
- Baseline predicate: piece type is `T`, last successful action was rotation, and at least 3 pivot corners are occupied by wall/stack.
- This step only records detection state; scoring rules are unchanged.

## Ghost piece
- `Board.getGhost()` computes a non-mutating projection by copying current and descending until the next step collides.
- Ghost is render-only: it never changes board state and is hidden when game is over.
- Hard-drop lock destination is equivalent to ghost landing position.

## Line clearing
- Scan from bottom up; when a full row is found, shift all above rows down and insert empty row at top; recheck same y after shift.

## Scoring & levels
- Base line-clear score (non-T-Spin): 100/300/500/800 * level for 1/2/3/4 lines.
- Base T-Spin line-clear score: 800/1200/1600 * level for 1/2/3 lines.
- Combo bonus: consecutive clears add `50 * comboStreak * level` from streak 1 onward.
- Back-to-back (B2B): consecutive difficult clears (Tetris or T-Spin clear) apply 1.5x base score.
- `linesCleared` accumulates; `level = 1 + linesCleared / 10`.
- Current timer delay is fixed in code (700 ms); future work: derive delay from level.

## Game over
- Triggered if spawn position is invalid or a lock writes outside bounds.
- On game over: prompt to record score (optional) and ask to start new game.

## Persistence
- `ScoreManager` stores best-per-user in `~/.tetris_scores.properties`.
- Keys are lowercase usernames; original casing is remembered for display.
- Reads are tolerant of corrupt files (ignored).

## Rendering
- `GamePanel`: renders grid, locked blocks, ghost projection, and active piece; antialiased; modern dark palette.
- `SidePanel`: structured hierarchy with core stats, player-facing scoring feedback, combo/B2B status, Hold, and three vertically ordered Next previews.
- `HelpContent`: authoritative Swing-native controls reference plus Hold/Next/Ghost concepts and advanced scoring terms.

## Input
- Swing key bindings on root pane (`WHEN_IN_FOCUSED_WINDOW`): move, rotate (CW/CCW), hard drop, hold (`C`), pause, restart, leaderboard, help, quit.
- Horizontal input (`←` / `→`) uses a deterministic DAS/ARR repeater (`InputRepeater`) instead of OS key-repeat cadence.
- Soft drop (`↓`) uses a deterministic repeat policy (`SoftDropRepeater`) with immediate first step and fixed repeat interval.
- Modal UI states (score dialogs/leaderboard/new-game prompt) block gameplay input via a guard and clear held repeaters on enter/exit.
- Window focus listener nudges focus back to game panel.

## Mechanics flow
```mermaid
flowchart TD
    A([Timer tick]) --> B{Paused or Game Over?}
    B -->|Yes| C[Skip tick]
    B -->|No| D[Board tick]
    D --> E{Can move down?}
    E -->|Yes| F[Advance piece down]
    E -->|No| G[Lock piece]
    G --> H[Clear full lines]
    H --> I[Spawn next piece]
    I --> J{Spawn valid?}
    J -->|No| K[Game Over]
    J -->|Yes| A
```

## Known simplifications
- Input repeat timing is deterministic for horizontal movement and soft drop.
- Timer speed does not yet scale with level.

## Regression gates
- `BoardRegressionGateTest` enforces core model invariants: boundary/occupied-cell collisions, blocked rotation failure, line-clear state consistency, and blocked-spawn top-out.
- Existing focused tests (`SrsRotationTest`, `LockDelayTest`, `ScoringRulesTest`, etc.) remain the baseline safety net for mechanics evolution.

## Seeded replay hooks
- `ReplayAction` defines deterministic model-level action tokens (`LEFT`, `RIGHT`, `SOFT_DROP`, `ROTATE_*`, `HARD_DROP`, `HOLD`, `TICK`).
- `Board.applyReplayAction(...)` executes and records actions into an ordered replay stream.
- `Board.replayFromSeed(seed, actions)` rebuilds board state from the same seed and action stream for reproducible debugging.

## Replay persistence format (`M7.3`)
- `ReplayPersistence.save(path, seed, actions)` writes UTF-8 text with three lines:
  - `JTETRIS_REPLAY_V1`
  - `seed=<long>`
  - `actions=<comma-separated ReplayAction names>`
- `ReplayPersistence.load(path)` validates each required line and fails fast on malformed payloads.
- Loaded payload (`LoadedReplay`) can be passed directly into `Board.replayFromSeed(...)` for deterministic reconstruction.
- UI export/import wiring is intentionally deferred; when added, UI should only call this model utility and keep action execution semantics in `Board`.

## Extension ideas
- Level-based fall speed.
- Perfect Clear detection and scoring feedback.
- Soft drop and hard drop scoring.
- T-Spin Mini distinction.
- Sound effects.
- UI scaling for high-DPI and resizable layout.
