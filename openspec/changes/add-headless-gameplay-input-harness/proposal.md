# Add Headless Gameplay Input Harness

## Summary
Introduce a small gameplay input controller that is shared by Swing and headless tests, allowing deterministic end-to-end verification of player operations without relying on screenshots, native keyboard automation, or a visible window.

## Motivation
Real-time Swing gameplay cannot be validated reliably through screenshot-driven computer control. Unit tests already cover `Board` and the individual repeaters, but they do not prove that press/release events, repeat timing, and game actions are wired together correctly.

JTetris needs a practical middle layer: production input handling that can be driven directly in tests with a fake clock and a seeded board. This provides broad behavioral coverage at low maintenance cost while leaving subjective game feel to player testing.

## Scope
- Add a `GameplayInputController` in the UI layer.
- Move core gameplay input orchestration from `TetrisFrame` into the controller.
- Support horizontal press/release, soft-drop press/release, polling, rotate clockwise/counterclockwise, hard drop, hold, and reset.
- Inject a monotonic millisecond clock so tests can advance time deterministically.
- Drive a real seeded `Board` in headless integration tests.
- Assert board coordinates, rotation, hold state, piece promotion/locking, and repeat behavior across multi-step scenarios.
- Keep Swing responsible for key bindings, gameplay eligibility, timers, repainting, overlays, and focus.

## Out Of Scope
- No RPC server, network port, remote-control protocol, CLI command parser, or external process automation.
- No screenshot recognition or native keyboard/mouse automation.
- No pause, overlay, focus-loss, or window lifecycle scenarios in the first phase.
- No gameplay rule, replay format, DAS, ARR, soft-drop interval, key binding, or rendering changes.
- No always-on debug logging.

## Expected Workflow
1. Express an operation bug as a deterministic controller scenario using a seeded board and fake clock.
2. Reproduce the bug headlessly and observe the failing assertion.
3. Fix the controller, repeater, or model boundary.
4. Run focused scenarios and the full Maven test suite.
5. Ask the player only for subjective feel and visual confirmation that cannot be asserted from state.
