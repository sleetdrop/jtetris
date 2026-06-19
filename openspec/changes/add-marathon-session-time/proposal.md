# Add Endless Marathon Session Time

## Problem
JTetris currently presents score, level, and cleared lines for an endless
score-focused game, but it does not show how long the active run has lasted.
Elapsed time is useful performance context for Marathon play even though it is
not the primary ranking metric.

## Goal
Define the current game as Endless Marathon and display active session time in
the side panel without changing gameplay rules, score ranking, or replay
determinism.

## Scope
- Identify the current mode as Endless Marathon: play continues until top-out.
- Add active elapsed time directly below Lines in the side panel.
- Exclude paused and gameplay-blocking overlay time.
- Reset time when the game restarts.
- Explain the mode and timer behavior in player-facing documentation.
- Record Sprint, Ultra, Score Attack, and Versus as future mode directions.

## Out Of Scope
- A game-mode selection screen.
- Fixed-line Marathon completion.
- Sprint, Ultra, Score Attack, or Versus implementation.
- Score-file, leaderboard, replay-format, gravity, scoring, or top-out changes.
