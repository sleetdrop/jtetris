# Gameplay Input Specification Delta

## ADDED Requirements

### Requirement: Delayed input polling must not replay stale movement
JTetris SHALL emit at most one horizontal movement step and at most one soft-drop step from each input polling callback, regardless of how many repeat intervals elapsed since the previous callback.

#### Scenario: Horizontal polling is delayed across multiple ARR intervals
- **Given** a horizontal direction is held and DAS has expired
- **When** the next input poll occurs after multiple ARR intervals have elapsed
- **Then** the poll emits exactly one horizontal step
- **And** the next repeat deadline is measured from that delayed poll

#### Scenario: Soft-drop polling is delayed across multiple repeat intervals
- **Given** soft drop is held
- **When** the next input poll occurs after multiple soft-drop intervals have elapsed
- **Then** the poll emits exactly one downward step
- **And** the next repeat deadline is measured from that delayed poll

### Requirement: Horizontal direction priority must follow genuine press order
When both horizontal directions are held, JTetris SHALL use the direction whose most recent genuine press transition occurred last.

#### Scenario: Opposite directions are pressed at the same timestamp
- **Given** left is pressed
- **When** right is pressed afterward with the same timestamp value
- **Then** right becomes the active direction

#### Scenario: A held key receives a duplicate press notification
- **Given** a horizontal key is already held
- **When** another press notification arrives for that same key without a release
- **Then** no immediate movement is emitted
- **And** its direction priority does not change

#### Scenario: The active direction is released while the other remains held
- **Given** both horizontal directions are held
- **And** the most recently pressed direction is active
- **When** the active direction is released
- **Then** the remaining direction becomes active
- **And** one immediate step is emitted in that direction

### Requirement: Input duration measurement must use monotonic time
JTetris SHALL schedule DAS, ARR, and soft-drop repeats from a monotonic elapsed-time source.

#### Scenario: Wall-clock time changes during play
- **Given** gameplay input is active
- **When** the operating system adjusts wall-clock time
- **Then** held-input repeat scheduling remains based only on elapsed time

### Requirement: Default horizontal handling must distinguish observed taps from deliberate holds
JTetris SHALL use a default horizontal DAS of 180ms and ARR of 35ms. A new press
SHALL move immediately, and automatic horizontal repeat SHALL not begin before
the DAS deadline.

#### Scenario: An observed-duration tap remains one cell
- **Given** a horizontal press moved one cell immediately
- **When** input is polled 176ms after the press
- **Then** no automatic repeat step is emitted

#### Scenario: A deliberate hold reaches DAS
- **Given** a horizontal direction remains held
- **When** input is polled 180ms after the press
- **Then** exactly one automatic repeat step is emitted

### Requirement: Existing handling transitions must remain compatible
The input hardening change SHALL preserve the existing key bindings, ARR value, soft-drop interval, input polling interval, and held-input clearing on pause, restart, overlay transitions, and focus loss.

#### Scenario: A held direction crosses a blocked gameplay transition
- **Given** a horizontal direction or soft drop is held
- **When** gameplay is paused, restarted, covered by an overlay, or loses window focus
- **Then** held input state is cleared
- **And** movement does not resume until a new key press occurs
