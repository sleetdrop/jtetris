# JTetris (Java Swing)

A lightweight JTetris clone for learning Java, Swing UI, and basic game loop/scoring mechanics. UI text is English-only.

## Quick start
```bash
mvn clean package
java -jar target/jtetris-1.0-SNAPSHOT.jar
```

## Project map
- Core code: `src/main/java/net/vetcafe/jtetris` (model, UI, scoring)
- Tests: `src/test/java/net/vetcafe/jtetris`
- Base package: `net.vetcafe.jtetris.*`
- Build: `pom.xml` (Java 17)
- Docs: `doc/overview.md`, `doc/algorithms.md`

## Development
```bash
mvn clean test
```

## Packaging
Build the runnable jar and copy runtime dependencies:
```bash
mvn clean package
java -jar target/jtetris-1.0-SNAPSHOT.jar
```

Build a macOS application image:
```bash
mvn -Pmac clean package
open target/dist/JTetris.app
```

App icon assets live under `art/`:
- `art/icon.svg`: deterministic source icon.
- `art/icon.icns`: macOS packaging icon.
- `art/icon.ico`: Windows packaging icon.
- `art/icons/icon-*.png`: Linux/desktop packaging sizes.

macOS app metadata is overridden from `packaging/macos/Info.plist` so the generated app bundle only advertises capabilities JTetris actually uses.

## UI theme and fonts
- JTetris now ships with a dual palette (light/dark) and chooses a default theme from system/LAF appearance.
- You can override theme selection with a JVM flag:
  - `-Djtetris.theme=auto` (default)
  - `-Djtetris.theme=light`
  - `-Djtetris.theme=dark`
- Bundled UI font: Inter (`src/main/resources/fonts`). If loading fails, Swing logical fonts are used as fallback.
- Font license text is included at `src/main/resources/fonts/OFL-Inter.txt` (SIL Open Font License 1.1).

## Docs
- [Agent Instructions](AGENTS.md)
- [OpenSpec Project Guide](openspec/project.md)
- [OpenSpec Agent Guide](openspec/AGENTS.md)
- [Overview](doc/overview.md)
- [Algorithms](doc/algorithms.md)
- [Quality Gates](doc/quality-gates.md)
- [Historical Spec Workflow](doc/specs/README.md)
- [Historical Optimization Roadmap](doc/specs/roadmap.md)
- [Contributing](CONTRIBUTING.md)
- [License](LICENSE)

## Agent workflow
- New non-trivial work starts in `openspec/changes/<change-id>/`.
- Read `AGENTS.md`, `openspec/project.md`, and `openspec/AGENTS.md` before editing.
- Historical specs under `doc/specs` remain useful context, but new feature specs should use OpenSpec.
- Validate with `mvn clean test` before finishing.

## Controls
- Move: ← / →
- Soft drop: ↓
- Rotate: ↑ or Z
- Hard drop: Space
- Pause/Resume: P
- Restart: R
- Leaderboard: L (or menu)
- Quit: Esc (or menu)

## Scores
Local best-per-user scores are stored in `~/.tetris_scores.properties`. On game over you can save a score to an existing or new username; only the best score per user is kept.
