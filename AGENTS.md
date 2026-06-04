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
- Resources: `src/main/resources/fonts` (bundled Inter regular/semibold fonts + license text)
- Legacy spec path note: older specs may still mention `src/tetris`; map those references to the Maven layout above when implementing.
- Design docs: `doc/overview.md`, `doc/algorithms.md`, `doc/quality-gates.md`
- Spec workflow: `doc/specs/README.md`
- Copilot usage: `doc/copilot-agent-usage.md`
- Copilot instructions: `.github/copilot-instructions.md`
- Preferred prompts: `.github/prompts/session-start.prompt.md`, `.github/prompts/feature-task.prompt.md`, `.github/prompts/session-handoff.prompt.md`

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
- macOS app-image packaging profile:
```bash
mvn -Pmac clean package
```

## Editing Rules For Agents
- Keep UI text English-only unless explicitly requested.
- Preserve keyboard behavior and focus semantics in `TetrisFrame` (`focusGame()`, key bindings, repeaters, modal-dialog input clearing, and the `C` hold action).
- Keep the theme/font pipeline intact (`UiTheme`, `UiFonts`, and the bundled Inter fonts under `src/main/resources/fonts`) unless the task explicitly changes the UI system.
- Preserve the theme-selection contract in `UiTheme.modeOverride()` (`-Djtetris.theme=auto|light|dark`) and keep piece-color routing through `ui/ColorPalette`.
- Keep score file compatibility at `~/.tetris_scores.properties` unless a migration is explicitly requested.
- Keep replay hooks (`Board.applyReplayAction(...)`, `Board.replayFromSeed(...)`) aligned with any model-behavior change.
- Prefer small, localized changes over broad rewrites.
- When gameplay timing changes, validate model and UI together (gravity timer, DAS/ARR, soft-drop repeaters).

## Execution Mode (Early Stage Stability)
- Default mode is `strict`: only implement the current requested feature, no opportunistic refactors.
- For non-trivial changes, update or create a spec in `doc/specs` before code changes.
- Before implementation, restate current goal, explicit file allowlist, and out-of-scope items; do not edit files outside that allowlist unless re-approved.
- After finishing each approved checklist step, create one focused git commit before starting the next step.
- Treat `pom.xml`, package moves, dependency changes, and broad naming changes as standalone tasks with their own spec.
- If unrelated issues are found, record them in notes/spec backlog and continue current scope only.

## Session Handoff Protocol
- End-of-session required steps:
  1. Run `mvn clean test`.
  2. Update current spec checklist and `Verification`.
  3. Append one entry to `doc/specs/context-pack.md` (append-only).
  4. Save a handoff draft from `doc/specs/_session-handoff-template.md` in the active spec or work notes.
- Start-of-session required steps:
  1. Read `AGENTS.md`.
  2. Read the active spec file and `doc/specs/context-pack.md` latest entry.
  3. Restate current goal/plan + file allowlist + out-of-scope items before editing.

## Definition Of Done
- Code compiles and tests pass with `mvn clean test`.
- Behavior remains responsive for input and rendering.
- Docs are updated when public behavior, paths, or developer workflow changes.


