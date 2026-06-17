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
  - Model: `model/` (`Board`, `Tetromino`, `TetrominoType`, `PieceBag`, `ReplayAction`, `ReplayPersistence`, `SrsKickTable`, `TSpinDetector`)
  - UI: `ui/` (`TetrisFrame`, `GamePanel`, `SidePanel`, `InputRepeater`, `SoftDropRepeater`, `UiTheme`, `UiFonts`, `ColorPalette`)
  - Score storage: `score/ScoreManager`
- Tests: `src/test/java/net/vetcafe/jtetris`
- Resources: no custom font binaries are currently bundled; `UiFonts` falls back to Java/Swing logical fonts.
- Legacy spec path note: older specs may still mention `src/tetris`; map those references to the Maven layout above when implementing.
- Design docs: `doc/overview.md`, `doc/algorithms.md`, `doc/quality-gates.md`
- OpenSpec project guide: `openspec/project.md`
- OpenSpec agent guide: `openspec/AGENTS.md`
- OpenSpec workflow spec: `openspec/specs/project-workflow/spec.md`
- Historical specs and handoff ledger: `doc/specs/README.md`, `doc/specs/context-pack.md`

## Build And Test
- Preferred validation command:
```bash
./mvnw clean test
```
- Package runnable jar:
```bash
./mvnw clean package
java -jar target/jtetris-1.0-SNAPSHOT.jar
```
- macOS app-image packaging profile:
```bash
./mvnw -Pmac clean package
```

## Editing Rules For Agents
- Keep UI text English-only unless explicitly requested.
- Preserve keyboard behavior and focus semantics in `TetrisFrame` (`focusGame()`, key bindings, repeaters, modal-dialog input clearing, and the `C` hold action).
- Keep the theme/font pipeline intact (`UiTheme`, `UiFonts`, and `ui/ColorPalette`) unless the task explicitly changes the UI system.
- Preserve the theme-selection contract in `UiTheme.modeOverride()` (`-Djtetris.theme=auto|light|dark`) and keep piece-color routing through `ui/ColorPalette`.
- Keep score file compatibility at `~/.tetris_scores.properties` unless a migration is explicitly requested.
- Keep replay hooks (`Board.applyReplayAction(...)`, `Board.replayFromSeed(...)`) aligned with any model-behavior change.
- Prefer small, localized changes over broad rewrites.
- When gameplay timing changes, validate model and UI together (gravity timer, DAS/ARR, soft-drop repeaters).

## Execution Mode (Early Stage Stability)
- Default mode is `strict`: only implement the current requested feature, no opportunistic refactors.
- For non-trivial changes, create or update an OpenSpec change under `openspec/changes/<change-id>` before code changes.
- Use `doc/specs` as historical context only unless a task explicitly asks to repair or archive old workflow documents.
- Before implementation, restate current goal, explicit file allowlist, and out-of-scope items; do not edit files outside that allowlist unless re-approved.
- After finishing each approved checklist step, create one focused git commit before starting the next step.
- Treat `pom.xml`, package moves, dependency changes, and broad naming changes as standalone tasks with their own spec.
- If unrelated issues are found, record them in notes/spec backlog and continue current scope only.

## Session Handoff Protocol
- End-of-session required steps:
  1. Run `./mvnw clean test`.
  2. Update the active OpenSpec `tasks.md` checklist and verification notes.
  3. If the work depends on historical decisions, add a concise note to the active OpenSpec change instead of rewriting `doc/specs`.
  4. Leave a short resume note in the active OpenSpec change when work is incomplete.
- Start-of-session required steps:
  1. Read `AGENTS.md`.
  2. Read `openspec/project.md` and `openspec/AGENTS.md`.
  3. Read the active OpenSpec change and any relevant historical `doc/specs/context-pack.md` entry.
  4. Restate current goal/plan + file allowlist + out-of-scope items before editing.

## Definition Of Done
- Code compiles and tests pass with `./mvnw clean test`.
- Behavior remains responsive for input and rendering.
- Docs are updated when public behavior, paths, or developer workflow changes.
