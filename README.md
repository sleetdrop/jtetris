# Tetris (Java Swing)

A lightweight Tetris clone for learning Java, Swing UI, and basic game loop/scoring mechanics. UI text is English-only.

## Quick start
```bash
mvn clean package
java -jar target/tetris-1.0-SNAPSHOT.jar
```

## Project map
- Core code: `src/tetris` (model, UI, scoring)
- Build: `pom.xml` (Java 17)
- Docs: `doc/overview.md`, `doc/algorithms.md`, `doc/agents.md`

## Docs
- [Overview](doc/overview.md): architecture summary and class diagram.
- [Algorithms](doc/algorithms.md): board mechanics, scoring, flowchart.
- [Agents guide](doc/agents.md): pointers for code-assist agents.

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

