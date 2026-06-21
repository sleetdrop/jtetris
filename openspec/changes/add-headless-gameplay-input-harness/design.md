# Headless Gameplay Input Harness Design

## Decision
Extract core gameplay input orchestration into a package-private `GameplayInputController`. Both `TetrisFrame` and headless tests call the same controller methods. The controller owns the horizontal and soft-drop repeaters, consumes an injected monotonic clock, and applies operations to an injected `Board`.

This is preferable to an RPC or CLI layer at the current project stage. It tests the production behavior boundary directly without adding process management, protocol compatibility, ports, authentication, or a second command-dispatch implementation.

## Responsibilities

### `GameplayInputController`
The controller owns:
- `Board`;
- `InputRepeater`;
- `SoftDropRepeater`;
- a `LongSupplier` returning monotonic milliseconds.

It exposes operations equivalent to:

```java
boolean pressLeft();
boolean releaseLeft();
boolean pressRight();
boolean releaseRight();
boolean pressSoftDrop();
void releaseSoftDrop();
boolean poll();
boolean rotateClockwise();
boolean rotateCounterclockwise();
boolean hardDrop();
boolean hold();
void reset();
```

Boolean results mean that visible board state changed and Swing should repaint. `poll()` applies at most one horizontal and one soft-drop step using one clock sample. `reset()` clears held input state only; board reset remains an explicit `TetrisFrame` responsibility.

Horizontal release methods return a boolean because releasing the active direction
can immediately switch to and move in the opposite direction when that key remains
held.

### `TetrisFrame`
The frame retains:
- Swing key bindings and timers;
- `isGameplayInputEnabled()` checks;
- pause, overlay, focus, and session-timer behavior;
- repaint and score/game-over presentation;
- board construction and reset.

Eligible gameplay methods delegate to the controller and repaint when its result is true. This keeps GUI policy out of the headless controller and avoids expanding first-phase scope.

## Clock
Production construction uses a monotonic millisecond supplier derived from `System.nanoTime()`. Tests use a small mutable fake clock:

```java
final class FakeClock {
    private long nowMs;

    long nowMs() {
        return nowMs;
    }

    void advance(long millis) {
        nowMs += millis;
    }
}
```

No sleeping, Swing timer, or wall-clock waiting is permitted in controller tests.

## Scenario Style
Tests use ordinary Java method calls rather than a text parser. A scenario reads as an executable sequence:

```java
assertTrue(controller.pressLeft());
clock.advance(50);
assertFalse(controller.poll());
controller.releaseLeft();
assertEquals(startX - 1, board.getCurrent().getX());
```

This is the first-phase scenario API. A textual DSL would add parsing and error-reporting work without improving current coverage. If external automation becomes necessary later, these controller methods are the stable boundary from which a CLI or RPC adapter can be built.

## First-Phase Coverage

### Horizontal input
- tap left/right moves exactly one column;
- held direction observes DAS then ARR;
- delayed polling never bursts multiple columns;
- latest genuinely pressed direction wins while both are held;
- releasing the active direction falls back to the other held direction;
- reset clears held state.

### Soft drop
- press moves immediately once;
- held soft drop repeats after its interval;
- delayed polling never bursts multiple rows;
- release and reset stop repeats.

### Discrete operations
- clockwise and counterclockwise rotation update the active piece when valid;
- hard drop locks/promotes a piece;
- hold follows one-hold-per-piece behavior;
- invalid movement or rotation returns unchanged state without a repaint signal.

### Mixed scenarios
- horizontal hold plus soft drop in the same poll;
- direction change followed by rotate or hard drop;
- hold followed by movement of the promoted piece;
- seeded board runs produce reproducible assertions.

## Replay Boundary
The controller will initially call the same public `Board` operations currently used by `TetrisFrame`. It will not change replay recording semantics as part of this extraction. Aligning live UI actions with persisted replay capture is a separate behavior decision and requires its own change.

## Error Handling
- Constructor dependencies are non-null.
- Timing constants remain positive and are supplied from existing frame constants.
- Controller methods are deterministic and do not catch model exceptions.
- Tests assert both return values and resulting board state so false-positive repaint signals are visible.

## Verification Strategy
- Controller integration tests run headlessly and use a real `Board`.
- Existing repeater and model tests remain lower-level regression gates.
- A small `TetrisFrame` wiring test may inspect action delegation only if it can run headlessly without constructing a native window; otherwise it is deferred.
- Full verification uses `./mvnw -Djava.awt.headless=true clean test`.
- Player testing remains responsible for subjective DAS/ARR feel and visual responsiveness.

## Future Extensions
Only add these when repeated bugs justify the cost:
- optional JSONL tracing around controller inputs and board-state summaries;
- pause/overlay/focus policy tests through a separate session-state coordinator;
- a CLI or RPC adapter over controller methods for external process automation.
