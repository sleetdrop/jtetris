# Gameplay Input Specification Delta

## ADDED Requirements

### Requirement: Core gameplay input must be executable without a native window
JTetris SHALL expose its production gameplay input orchestration through an in-process controller that can operate on a real `Board` without creating a Swing window.

#### Scenario: A test drives a horizontal tap
- **Given** a seeded board and a controller using a fake monotonic clock
- **When** the test presses and releases left
- **Then** the active piece moves exactly one valid column left
- **And** no screenshot, native keyboard event, or wall-clock sleep is required

#### Scenario: Swing handles a gameplay action
- **Given** gameplay input is enabled
- **When** a registered Swing gameplay action runs
- **Then** the frame delegates the operation to the same controller used by headless tests
- **And** the frame repaints only when the controller reports visible state change

### Requirement: Controller scenarios must use deterministic time
The controller SHALL receive monotonic milliseconds from an injected clock and SHALL use that clock for press and polling operations.

#### Scenario: A test crosses the DAS threshold
- **Given** a horizontal direction is held
- **When** the fake clock advances to just before DAS
- **Then** polling emits no movement
- **When** the fake clock advances to the DAS deadline
- **Then** polling emits one movement step

#### Scenario: A delayed poll crosses multiple repeat intervals
- **Given** a horizontal direction or soft drop is held
- **When** the fake clock advances across multiple repeat intervals before polling
- **Then** the controller applies at most one step for that input in the poll

### Requirement: First-phase controller coverage must include core game operations
The headless controller SHALL support horizontal movement, soft drop, clockwise rotation, counterclockwise rotation, hard drop, hold, polling, and held-input reset.

#### Scenario: A test executes mixed gameplay operations
- **Given** a seeded board
- **When** a scenario holds a direction, polls, rotates, soft drops, holds, and hard drops
- **Then** each successful operation produces the expected board state transition
- **And** the final state is reproducible from the same seed and scenario

### Requirement: GUI lifecycle policy remains outside the first-phase controller
The controller SHALL NOT own pause, overlays, window focus, repaint scheduling, session timing, or native event dispatch.

#### Scenario: The frame blocks input for GUI policy
- **Given** gameplay input is disabled by frame policy
- **When** a Swing input action occurs
- **Then** the frame does not call the controller operation
