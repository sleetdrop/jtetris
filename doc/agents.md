# Agents Guide

Use this project with coding agents efficiently by pointing them to key files and expectations.

## Project map
- Core model: `src/tetris/model/Board.java`, `Tetromino.java`, `TetrominoType.java`
- UI: `src/tetris/ui/TetrisFrame.java`, `GamePanel.java`, `SidePanel.java`, `ColorPalette.java`
- Scores: `src/tetris/score/ScoreManager.java`
- Docs: `doc/overview.md`, `doc/algorithms.md`

## Conventions
- Language: Java 17+ (Swing). English-only UI text.
- Build: Maven (see `pom.xml`).
- Persistence: local `~/.tetris_scores.properties`; best-per-user only.
- Hidden rows: board height 22 with top 2 hidden.

## Common tasks and entry points
- Rendering tweaks: `GamePanel` and `SidePanel`.
- Input/loop: `TetrisFrame` (key bindings, timer, menus, dialogs).
- Scoring: `ScoreManager` and `Board.clearLines()` scoring table.
- Piece behavior: `TetrominoType` rotation data, `Board` movement/validation.

## Interaction tips for agents
- Preserve key bindings and English UI text when modifying controls.
- Keep score persistence compatible: same file path and key casing unless instructed otherwise.
- If changing game speed/level, adjust timer in `TetrisFrame` alongside level math in `Board`.
- When adding features, prefer new small helpers over large rewrites; keep rendering/UI responsive (invokeLater for UI changes if async).
- Focus handling matters: keep `focusGame()` and `WHEN_IN_FOCUSED_WINDOW` bindings intact.

## Definition of done for common changes
- Builds via Maven without errors.
- Keyboard input remains responsive after UI changes.
- Score dialog still appears on game over and leaderboard works.
- Docs updated if behavior or controls change.

## Quick run/build
```bash
mvn clean package
java -jar target/tetris-1.0-SNAPSHOT.jar
```

