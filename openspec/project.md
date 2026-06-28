# JTetris Project Guide

## Purpose
JTetris is a lightweight Java 25 Swing Tetris clone used for learning, gameplay-rule iteration, and agent-assisted maintenance.

## Tech Stack
- Java 25 LTS
- Swing UI
- Maven build
- JUnit tests
- Main package: `net.vetcafe.jtetris.*`
- Entry point: `net.vetcafe.jtetris.ui.TetrisFrame`

## Repository Layout
- `src/main/java/net/vetcafe/jtetris/model`: game state, rules, replay hooks, randomizer, rotation, scoring helpers.
- `src/main/java/net/vetcafe/jtetris/ui`: Swing frame, board rendering, panels, input repeaters, theme, fonts, stage overlays.
- `src/main/java/net/vetcafe/jtetris/score`: local high-score persistence.
- `src/test/java/net/vetcafe/jtetris`: model and UI support tests.
- No custom font binaries are currently bundled; `UiFonts` falls back to Java/Swing logical fonts.
- `art`: source and generated app icon assets for macOS, Windows, and Linux packaging.
- `doc`: stable design and quality documentation.
- `doc/specs`: historical specs and handoff ledger from the pre-OpenSpec workflow.
- `openspec`: optional spec workflow for changes that need durable behavior,
  architecture, product, or process traceability.

## Commands
```bash
./mvnw -Djava.awt.headless=true clean test
./mvnw clean package
java -jar target/jtetris-1.1.0-standalone.jar
./mvnw -Djava.awt.headless=true -Pmac clean package
open target/dist/JTetris.app
./mvnw -Djava.awt.headless=true -Pwindows clean package
```

## Development Constraints
- Keep UI text English-only unless the requested feature explicitly changes language behavior.
- Preserve keyboard behavior and focus semantics in `TetrisFrame`, including `focusGame()`, key bindings, repeaters, modal/overlay input clearing, and the `C` hold action.
- Keep the theme/font pipeline intact through `UiTheme`, `UiFonts`, and `ui/ColorPalette`.
- Preserve `UiTheme.modeOverride()` behavior for `-Djtetris.theme=auto|light|dark`.
- Keep the properties-based best-score format compatible across platform data directories, including one-time migration from `~/.tetris_scores.properties`.
- Keep replay hooks aligned when model behavior changes: `Board.applyReplayAction(...)` and `Board.replayFromSeed(...)`.
- Prefer small, localized changes. Treat `pom.xml`, package moves, dependency changes, and broad naming changes as standalone changes.

## Workflow
- Default to the lightweight project workflow in `AGENTS.md` for routine
  development.
- Use OpenSpec only for changes that alter long-lived behavior contracts,
  product decisions, architecture boundaries, project workflow,
  build/release/dependency policy, or compatibility expectations.
- Do not use OpenSpec for routine bug fixes, localized refactors, tests, small
  documentation edits, or implementation details that are clear from code and
  commits.
- When OpenSpec is used, start with `openspec/AGENTS.md`, then inspect active
  changes under `openspec/changes/`.
- When OpenSpec is used, create a new change under
  `openspec/changes/<change-id>/` before implementation.
- Include `proposal.md`, `tasks.md`, and spec deltas under `specs/<capability>/spec.md`; add `design.md` for cross-cutting or risky changes.
- Before editing implementation files, restate the current goal, exact file allowlist, and out-of-scope items.
- Complete one small task at a time and keep verification evidence with the active change.
- Run `./mvnw -Djava.awt.headless=true clean test` before marking a change complete.

## Historical Notes
The previous workflow lives in `doc/specs`. Those files remain useful project memory, especially `doc/specs/context-pack.md`, but they are no longer the starting point for new work after the OpenSpec migration.

## Game Mode Direction
- Current focus: Endless Marathon, continuing until top-out with score as the primary result and active elapsed time as session context.
- Future candidate: Sprint / 40 Lines, ranked by completion time.
- Future candidate: Ultra / Time Trial, maximizing score within a fixed duration.
- Future candidates: Score Attack and Versus, each requiring a separate match and result model.
- Do not introduce a mode-selection system until the Endless Marathon rules, timing, feedback, and quality are mature.
