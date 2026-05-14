# AGENTS.md

## Scope
- This file applies to the entire repository.
- If a deeper directory later adds another `AGENTS.md`, the deeper file should override this one for that subtree.

## Project Snapshot
- Name: `JTetris`
- Language/runtime: Java 17, Swing UI
- Build tool: Maven (`pom.xml`)
- Main package: `net.vetcafe.jtetris.*`
- Entry point: `net.vetcafe.jtetris.ui.TetrisFrame`

## Source Map
- Main source: `src/main/java/net/vetcafe/jtetris`
  - Model: `model/` (`Board`, `Tetromino`, `TetrominoType`, `PieceBag`, `SrsKickTable`, `TSpinDetector`)
  - UI: `ui/` (`TetrisFrame`, `GamePanel`, `SidePanel`, `InputRepeater`, `SoftDropRepeater`)
  - Score storage: `score/ScoreManager`
- Tests: `src/test/java/net/vetcafe/jtetris`
- Design docs: `doc/overview.md`, `doc/algorithms.md`, `doc/quality-gates.md`
- Spec workflow: `doc/specs/README.md`

## Build And Test
- Preferred validation command:
```bash
mvn clean test
```
- Package runnable jar:
```bash
mvn clean package
java -jar target/jtetris-1.0-SNAPSHOT.jar
```

## Editing Rules For Agents
- Keep UI text English-only unless explicitly requested.
- Preserve keyboard behavior and focus semantics in `TetrisFrame` (`focusGame()`, key bindings, repeaters).
- Keep score file compatibility at `~/.tetris_scores.properties` unless a migration is explicitly requested.
- Prefer small, localized changes over broad rewrites.
- When gameplay timing changes, validate model and UI together (gravity timer, DAS/ARR, soft-drop repeaters).

## Definition Of Done
- Code compiles and tests pass with `mvn clean test`.
- Behavior remains responsive for input and rendering.
- Docs are updated when public behavior, paths, or developer workflow changes.


