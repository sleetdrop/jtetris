# Game Session Specification

## Purpose
Define JTetris game-mode identity and active session lifecycle without coupling
wall-clock time to deterministic board or replay state.

## Requirements

### Requirement: Current game mode is Endless Marathon
JTetris SHALL identify the current ruleset as Endless Marathon, where a run
continues until the player tops out and level progression increases natural
gravity speed.

#### Scenario: Marathon run has no fixed completion target
- **Given** a new game has started
- **When** the player continues clearing lines
- **Then** the run does not end at a fixed line or level target
- **And** the run ends when the board reaches top-out

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

### Requirement: Session time measures active gameplay
JTetris SHALL track elapsed active gameplay time independently from board and
replay state.

#### Scenario: Active play accumulates time
- **Given** a Marathon run is active
- **When** no pause or gameplay-blocking overlay is present
- **Then** elapsed session time increases monotonically

#### Scenario: Manual pause excludes time
- **Given** a Marathon run is active
- **When** the player pauses the game
- **Then** elapsed session time stops increasing
- **And** resuming continues from the previously accumulated duration

#### Scenario: Blocking overlay excludes time
- **Given** a Marathon run is active
- **When** Help, leaderboard, confirmation, score, or another blocking overlay is visible
- **Then** elapsed session time stops increasing
- **And** dismissing the overlay resumes timing only if the run is otherwise active

#### Scenario: Game over freezes time
- **Given** the board reaches game over
- **When** the game-over flow begins
- **Then** elapsed session time stops at the completed run duration

#### Scenario: Restart resets time
- **Given** a run has accumulated elapsed time
- **When** the player restarts
- **Then** elapsed session time resets to zero
- **And** the new run begins accumulating active time

### Requirement: Side panel presents Marathon time as a secondary core statistic
JTetris SHALL display elapsed time directly below Lines without displacing
Score as the primary statistic.

#### Scenario: Time appears in Layout A
- **Given** the side panel is visible
- **When** it renders the core statistics
- **Then** the order is Score, Level, Lines, Time
- **And** Time uses the same visual style as Level and Lines
- **And** Hold and Next remain readable within the fixed panel size

#### Scenario: Time formatting remains stable
- **Given** elapsed time is less than one hour
- **Then** it is displayed as `MM:SS`
- **And** when elapsed time reaches one hour it is displayed as `H:MM:SS`
