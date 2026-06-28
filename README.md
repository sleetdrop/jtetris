# JTetris (Java Swing)

A lightweight JTetris clone for learning Java, Swing UI, and basic game loop/scoring mechanics. UI text is English-only.

## Features
- Java 17 Swing desktop UI with light/dark theme support.
- Endless Marathon play that continues until top-out, with active session time shown alongside score, level, and lines.
- Guideline-style Tetris mechanics including 7-bag randomization, hold piece, ghost piece, SRS rotation kicks, level-based gravity, fixed lock delay, five-piece Next preview, drop scoring, combo/B2B scoring, and T-spin detection.
- Swing-native in-game Help for controls and modern Tetris scoring concepts.
- Local best-score storage per user.
- Replay-oriented model hooks and regression tests for core gameplay behavior.
- Runnable jar packaging and macOS app-image packaging.

## Screenshots
| Light theme | Dark theme |
| --- | --- |
| ![JTetris light theme](doc/images/jtetris-light.png) | ![JTetris dark theme](doc/images/jtetris-dark.png) |

## Requirements
- JDK 17 or newer
- Maven is optional if you use the included Maven Wrapper

## Quick start
```bash
./mvnw clean package
java -jar target/jtetris-1.0.0-standalone.jar
```

## Project map
- Core code: `src/main/java/net/vetcafe/jtetris` (model, UI, scoring)
- Tests: `src/test/java/net/vetcafe/jtetris`
- Base package: `net.vetcafe.jtetris.*`
- Build: `pom.xml` (Java 17)
- Docs: `doc/overview.md`, `doc/algorithms.md`

## Development
```bash
./mvnw -Djava.awt.headless=true clean test
```

## Logging and diagnostics

Normal play logs only `ERROR` events. The default rolling log directory is:

- macOS: `~/Library/Application Support/net.vetcafe.jtetris/logs`
- Linux: `${XDG_DATA_HOME:-~/.local/share}/net.vetcafe.jtetris/logs`
- Windows: `%LOCALAPPDATA%\net.vetcafe.jtetris\logs`

Enable general diagnostics:

```bash
java -Djtetris.debug=true \
  -jar target/jtetris-1.0.0-standalone.jar
```

Enable detailed gameplay-input tracing:

```bash
java -Djtetris.debug=true \
  -Djtetris.log.input.level=TRACE \
  -jar target/jtetris-1.0.0-standalone.jar
```

Use a custom absolute directory:

```bash
java -Djtetris.debug=true \
  -Djtetris.log.input.level=TRACE \
  -Djtetris.log.dir=/absolute/path/to/logs \
  -jar target/jtetris-1.0.0-standalone.jar
```

The default rolling policy uses 10 MB files, 7 days of history, and a 100 MB
total cap. Override these with `jtetris.log.maxFileSize`,
`jtetris.log.maxHistory`, and `jtetris.log.totalSizeCap`. A complete external
Logback configuration can be selected with `-Dlogback.configurationFile=...`.

Input diagnostics contain key names, monotonic timing, movement results, piece
coordinates, and thread information. They do not contain usernames, arbitrary
text input, screenshots, or score-file contents.

## Game mode
JTetris currently focuses on **Endless Marathon**. A run continues until
top-out, with Score as the primary result and Level, Lines, and Time as session
context. Time measures active gameplay and excludes manual pauses and blocking
in-window prompts. Natural gravity speeds up as Level increases.

## Packaging
Build the standalone runnable jar:
```bash
./mvnw clean package
java -jar target/jtetris-1.0.0-standalone.jar
```

Build a macOS application image:
```bash
./mvnw -Pmac clean package
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
- The UI uses Java/Swing logical fonts through `UiFonts`, with hooks left in place for future bundled fonts.

## Docs
- [Agent Instructions](AGENTS.md)
- [OpenSpec Project Guide](openspec/project.md)
- [OpenSpec Agent Guide](openspec/AGENTS.md)
- [Overview](doc/overview.md)
- [Algorithms](doc/algorithms.md)
- [Tetris Guideline Notes](doc/tetris-guideline-notes.md)
- [Quality Gates](doc/quality-gates.md)
- [Historical Spec Workflow](doc/specs/README.md)
- [Historical Optimization Roadmap](doc/specs/roadmap.md)
- [Contributing](CONTRIBUTING.md)
- [License](LICENSE)
- [Notices](NOTICE.md)
- [Security](SECURITY.md)
- [Code of Conduct](CODE_OF_CONDUCT.md)

## Third-party notices
Runtime and test dependency notices are tracked in [NOTICE.md](NOTICE.md).

## Agent workflow
- New non-trivial work starts in `openspec/changes/<change-id>/`.
- Read `AGENTS.md`, `openspec/project.md`, and `openspec/AGENTS.md` before editing.
- Historical specs under `doc/specs` remain useful context, but new feature specs should use OpenSpec.
- Validate with `./mvnw clean test` before finishing.
- For headless verification, use `./mvnw -Djava.awt.headless=true clean test`.

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
Local best-per-user scores use the platform's application data directory:

- macOS: `~/Library/Application Support/net.vetcafe.jtetris/scores.properties`
- Linux: `${XDG_DATA_HOME:-~/.local/share}/net.vetcafe.jtetris/scores.properties`
- Windows: `%LOCALAPPDATA%\net.vetcafe.jtetris\scores.properties`

On game over you can save a score to an existing or new username; only the best score per user is kept. Select a leaderboard row and use `Delete` to remove that player's local score after confirmation.

Existing `~/.tetris_scores.properties` data is migrated when the new store does not exist. The legacy file is removed only after the new file is written successfully.
