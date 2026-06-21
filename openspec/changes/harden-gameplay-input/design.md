# Gameplay Input Hardening Design

## Decision
Keep the current architecture: Swing key bindings update held-key state, and a short Swing timer polls repeaters before applying model movement on the event dispatch thread. This preserves single-threaded model access, deterministic repeat policy, and independence from operating-system key-repeat settings.

Replace only the unreliable timing and priority details. A larger input-controller rewrite is not justified because the current component boundaries are small, testable, and otherwise appropriate for this game.

## Root Cause
`InputRepeater.poll()` and `SoftDropRepeater.poll()` currently advance through all elapsed repeat deadlines:

```java
while (nowMs >= nextRepeatAt) {
    steps++;
    nextRepeatAt += repeatMs;
}
```

When the Swing event dispatch thread is delayed, one callback emits the full backlog. The frame then applies all returned steps synchronously, producing a visible multi-cell burst. The apparent pause before the burst is the same event-dispatch delay.

The first hardening pass removed that backlog, but a real player trace exposed a
second cause. The application default DAS was 130ms while observed taps had a
121ms median, 135ms 75th percentile, and 176ms maximum. Twenty of 48 taps
crossed 130ms and 13 emitted one repeat step. Press/release events paired exactly
and the EDT watchdog reported no stalls.

Tune the default DAS to 180ms. The immediate press step remains unchanged, so
tap latency does not increase. ARR remains 35ms so deliberate holds still move
quickly after DAS activation.

## Repeat Semantics
- A new eligible press emits one immediate step.
- Repeated press notifications for an already-held key emit no additional immediate step.
- Before DAS or the repeat interval expires, polling emits no step.
- At or after a repeat deadline, one poll emits exactly one step.
- After emitting a delayed repeat, the next deadline is `now + interval`; missed repeats are discarded.
- Releasing all horizontal keys returns the repeater to idle.
- Reset clears all held state, active direction, priority, and deadlines.

This policy favors controllability over simulated catch-up. Input repeat is a control signal, not a simulation clock: replaying stale intent after a UI stall is harmful.

## Horizontal Priority
Use an incrementing press-order sequence for genuine up-to-down transitions. When both horizontal keys are held, the key with the newer sequence wins. This remains correct even when two events share the same millisecond timestamp.

Press notifications for an already-held key do not update priority. This prevents operating-system-generated duplicate press events from stealing direction or creating extra movement.

When the active key is released while the opposite key remains held, the opposite direction becomes active and receives one immediate step, preserving the existing deterministic fallback behavior.

## Clock
`TetrisFrame` will supply elapsed milliseconds derived from `System.nanoTime()`. `nanoTime()` is monotonic for measuring durations and is unaffected by wall-clock corrections. The repeaters remain clock-agnostic and continue accepting explicit timestamps, which keeps tests deterministic.

## Soft Drop
Soft drop has the same backlog problem and shares the same event-dispatch constraints. It will adopt one-step-per-poll and deadline rebasing. Its immediate press, release, and reset behavior remain unchanged.

## Tests
`InputRepeaterTest` will cover:
- immediate press and normal DAS/ARR cadence;
- delayed polling emits one step rather than accumulated steps;
- delayed polling rebases the next ARR deadline;
- duplicate press events do not move or change priority;
- equal-timestamp opposite presses preserve actual event order;
- active-key release falls back immediately to the other held direction;
- reset clears all state.
- the application default emits no repeat at 176ms and begins repeat at 180ms.

`SoftDropRepeaterTest` will cover:
- immediate press and normal repeat cadence;
- delayed polling emits one step rather than accumulated steps;
- delayed polling rebases the next deadline;
- duplicate press, release, and reset behavior.

## Manual Verification
- Tap left and right repeatedly; each tap moves exactly one cell.
- Hold each direction; movement begins immediately, pauses for DAS, then repeats smoothly.
- Rapidly alternate left and right, including briefly holding both; the most recently pressed direction wins without bursts.
- Open and close overlays, pause/resume, and switch window focus; no held movement leaks across transitions.
- Hold soft drop while opening or closing an overlay; no accumulated downward burst occurs afterward.
