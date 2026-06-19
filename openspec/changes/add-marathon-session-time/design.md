# Endless Marathon Session Time Design

## Product Position
JTetris will describe its current ruleset as **Endless Marathon**. A run begins
with a fresh board and ends only at top-out. Score remains the primary result;
level and lines describe progression; elapsed time supplies secondary session
and endurance context.

This positioning does not claim that elapsed time determines Marathon ranking.
It keeps JTetris aligned with score-focused Marathon play while preparing the
information architecture for future modes where time becomes the objective or
limit.

## Side Panel Layout
The side panel uses the approved Layout A:

```text
Score: 0
Level: 1
Lines: 0
Time: 00:00

Combo -
B2B Ready
```

`Time` appears immediately below `Lines`. It uses the same font, weight, and
primary text color as Level and Lines. Score retains the largest type and
strongest visual priority. The existing performance-status spacing, Hold
divider, Hold preview, Next divider, and three-piece Next queue remain in their
current positions except for the minimum vertical adjustment required by the
new line.

The format is:

- Below one hour: `MM:SS`
- At or above one hour: `H:MM:SS`

The display updates through the side panel's existing 200 ms refresh cycle. No
additional Swing timer is introduced for display updates.

## Time Semantics
Elapsed time means active gameplay time, not wall-clock time since the window
opened.

The timer runs when all of these conditions are true:

- The board has not reached game over.
- The player has not paused the game.
- No gameplay-blocking stage overlay is active.

The timer pauses when:

- The player pauses with the menu or `P`.
- Help, leaderboard, exit confirmation, score entry, game-over feedback, or any
  other gameplay-blocking overlay is visible.
- The board reaches game over.

Closing an overlay resumes timing only when the game was not already manually
paused and the board is not over. Restart clears elapsed time and immediately
starts a fresh run. Theme switching and ordinary window focus changes do not
reset the timer.

## Architecture
Add a focused `GameSessionTimer` in the UI layer. It owns:

- Accumulated active nanoseconds.
- The monotonic-clock value at the most recent start.
- Whether accumulation is currently running.

Its operations are:

- `start()`: start or resume without losing accumulated time.
- `pause()`: accumulate through the pause point and stop.
- `resetAndStart()`: discard the old run and begin a new one.
- `elapsedMillis()`: return accumulated active duration, including the current
  running segment.

The implementation uses `System.nanoTime()` through an injectable `LongSupplier`.
The injected source enables deterministic tests without sleeping.

`TetrisFrame` remains the owner of session lifecycle. It creates the timer and
uses a single synchronization method to derive whether the timer should run
from pause, overlay, and game-over state. That method is called after lifecycle
transitions instead of distributing ad hoc start/pause decisions across
individual overlays.

`SidePanel` receives a read-only elapsed-time supplier. It does not own,
start, pause, or reset the timer. Formatting is isolated in a small formatter
so boundary behavior can be tested independently.

The timer does not enter `Board`. It is not included in replay actions or replay
files because wall-clock scheduling is not deterministic game state. It is also
not persisted to the score properties file in this change.

## State Flow

```text
new frame / restart
        |
        v
     running <---- overlay closes / resume
        |
        +---- manual pause --------> paused
        +---- blocking overlay ----> paused
        +---- game over -----------> stopped
```

An overlay may temporarily pause an otherwise active run. The frame's existing
manual `paused` state remains authoritative, so dismissing an overlay never
unpauses a game the player had paused before opening it.

## Testing
Automated coverage will verify:

- A new timer starts at zero and accumulates injected monotonic time.
- Pause excludes later clock movement.
- Resume continues from the previous accumulated duration.
- `resetAndStart()` clears the old duration and starts a new run.
- Repeated start/pause calls are idempotent.
- Formatting produces `00:00`, `59:59`, and `1:00:00` at the boundaries.
- SidePanel keeps its established preferred size and places Time after Lines.
- Frame lifecycle synchronization pauses for manual pause, every blocking
  overlay, and game over, then resumes only when all blockers are absent.
- Existing replay, model, input, theme, and overlay tests remain green.

Manual verification will cover both themes at the fixed window size, including
an hour-format sample or test hook to ensure the longer value does not clip.

## Documentation
README and in-app Help will state:

- The current mode is Endless Marathon.
- A run continues until top-out.
- Time measures active gameplay and excludes pauses and blocking overlays.

The project backlog will retain these future mode directions without exposing
unfinished choices to players:

- Sprint / 40 Lines: clear a fixed line target as quickly as possible.
- Ultra / Time Trial: maximize score within a fixed duration.
- Score Attack / Versus: competitive modes requiring their own match rules and
  result model.

## Failure And Compatibility Considerations
`System.nanoTime()` is monotonic and avoids wall-clock adjustments. A negative
or inconsistent injected test value will be clamped by duration calculations
so the display never moves backward.

No data migration is required. Existing saves, leaderboard entries, replay
files, command-line theme options, keyboard bindings, and game rules remain
compatible.
