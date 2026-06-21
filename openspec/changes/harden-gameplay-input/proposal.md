# Harden Gameplay Input

## Summary
Harden JTetris horizontal and soft-drop repeat handling so delayed Swing event processing cannot produce burst movement, and tune horizontal DAS so normal taps do not cross into auto-repeat.

## Motivation
Horizontal movement can occasionally feel unresponsive and then move two or three columns at once. The repeaters currently accumulate every missed repeat interval and emit the backlog in one Swing timer callback. Because input, rendering, overlays, and other UI work share the event dispatch thread, ordinary UI delays can therefore become visible movement bursts.

The input review also found two boundary weaknesses: simultaneous-timestamp direction presses do not reliably preserve the actual latest press, and the repeat schedule uses wall-clock time even though elapsed input timing requires a monotonic clock.

## Scope
- Preserve the existing event-driven input architecture and current keyboard bindings.
- Limit each horizontal and soft-drop poll to one emitted step.
- Rebase repeat timing after a delayed poll instead of replaying missed intervals.
- Track horizontal press priority explicitly rather than deriving it from millisecond timestamps.
- Use a monotonic elapsed-time source for input scheduling.
- Add deterministic regression coverage for delayed polling, repeated press events, rapid direction changes, equal-timestamp presses, release fallback, and reset behavior.
- Update concise input algorithm documentation.

## Out Of Scope
- No changes to `Board`, replay data, scoring, gravity, lock delay, rotation, or hold rules.
- No changes to ARR, soft-drop interval, or input polling constants.
- No configurable handling settings in this change.
- No configurable handling menu, instant ARR, input buffering, IRS, or IHS.
- No dependency, package, or broad `TetrisFrame` refactor.

## File Allowlist
- `openspec/changes/harden-gameplay-input/**`
- `src/main/java/net/vetcafe/jtetris/ui/InputRepeater.java`
- `src/main/java/net/vetcafe/jtetris/ui/SoftDropRepeater.java`
- `src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java`
- `src/test/java/net/vetcafe/jtetris/ui/InputRepeaterTest.java`
- `src/test/java/net/vetcafe/jtetris/ui/TetrisFrameInputTimingTest.java`
- `src/test/java/net/vetcafe/jtetris/ui/SoftDropRepeaterTest.java`
- `doc/algorithms.md`
- `doc/overview.md`
