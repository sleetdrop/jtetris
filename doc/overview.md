# Tetris Project Overview

## Purpose
A concise Swing-based Tetris for learning Java, Swing UI, and basic game loop/algorithm patterns.

## Architecture
- **Model**: `tetris.model.*`
  - `Board`: game state (grid, active piece, next piece, scoring, level). Handles gravity (`tick`), movement, rotation, line clearing, and spawning. Hidden top rows keep spawn safe.
  - `Tetromino` & `TetrominoType`: piece data and rotations.
- **UI**: `tetris.ui.*`
  - `TetrisFrame`: main window, timer-driven loop, key bindings, menu, pause/restart/leaderboard, score prompt on game over.
  - `GamePanel`: renders board and active piece; focuses itself on show; modern dark palette.
  - `SidePanel`: stats, next preview, controls cheat-sheet.
- **Scores**: `tetris.score.ScoreManager`: per-user local high scores stored in `~/.tetris_scores.properties` (best-only per user).

## Game Loop & Timing
- Swing `Timer` in `TetrisFrame` ticks every ~700 ms (speeds up by level). On each tick: if not paused and not game over, call `Board.tick()` (gravity); repaint board.
- Movement/rotation/drop invoked via key bindings and applied to the model; rendering pulls from model snapshot.

## Scoring & Levels
- Line clear scores (per classic Tetris style): 1/2/3/4 lines = 100/300/500/800 * level.
- `linesCleared` total drives `level = 1 + linesCleared / 10` (speeds fall via timer delay tuning in future).
- High score: best-per-user persists locally. On game over, prompt to pick existing or add new user; store if higher.

## Controls (UI is English-only)
- Move: ← / →
- Soft drop: ↓
- Rotate: ↑ or Z
- Hard drop: Space
- Pause/Resume: P
- Restart: R
- Leaderboard: L (also via menu)
- Quit: Esc (also via menu)

## UI Layout & Styling
- `GamePanel` center; `SidePanel` on the right with Stats (Score/Level/Lines), Next piece, Controls list.
- Modern colors: deep charcoal background with teal/amber/lavender/mint/red/indigo/coral pieces.

## Data & Persistence
- Scores stored at `~/.tetris_scores.properties`. Keys are lowercase usernames; values are best scores. File is best-effort read/write; corrupt file is ignored and treated as empty.

## Notes for learners
- Input is handled via key bindings on the root pane (not raw key listeners).
- Gravity, locking, and line clearing live entirely in `Board`; UI remains thin.
- Hidden top rows (HEIGHT 22 with 2 hidden) keep spawn/rotation valid without immediate loss.

