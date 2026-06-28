# Game Session Specification Delta

## MODIFIED Requirements

### Requirement: Current game mode is Endless Marathon
JTetris SHALL identify the current ruleset as Endless Marathon, where a run
continues until the player tops out and level progression increases natural
gravity speed.

#### Scenario: Marathon gravity accelerates by level
- **Given** a Marathon run has reached a higher level
- **When** the Swing gravity loop schedules the next natural fall
- **Then** the delay is shorter than the opening level delay
- **And** the run still ends only on top-out, not at a fixed line target

### Requirement: Normal gravity locking preserves a fixed player lock window
JTetris SHALL use a fixed lock-delay duration for UI-driven natural locking so
higher gravity levels do not remove the player's placement window.

#### Scenario: Grounded piece waits for lock delay
- **Given** the current piece is grounded during normal play
- **When** gravity polling observes it before the lock delay expires
- **Then** the piece remains active
- **And** after the lock delay expires, the piece locks if it is still grounded
