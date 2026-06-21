# Application Logging Specification Delta

## ADDED Requirements

### Requirement: Application logging must use a pluggable standard API
JTetris SHALL use SLF4J 2 for application logging and SHALL bundle Logback as the default provider.

#### Scenario: A different SLF4J provider is supplied
- **Given** application classes log only through SLF4J
- **When** a compatible provider replaces the bundled backend
- **Then** application source does not require logger API changes

### Requirement: Normal play must remain quiet
JTetris SHALL default the application logging threshold to `ERROR`.

#### Scenario: The game runs without logging parameters
- **Given** the bundled logging configuration is used
- **When** normal gameplay produces informational, debug, or trace events
- **Then** those events are not written
- **And** error events remain eligible for the rolling log

### Requirement: Diagnostic verbosity must be configurable
JTetris SHALL support global and input-domain levels using standard
`ERROR`, `WARN`, `INFO`, `DEBUG`, and `TRACE` names.

#### Scenario: Debug mode is enabled
- **Given** `jtetris.debug=true`
- **And** no explicit global level is provided
- **When** logging initializes
- **Then** the effective global level is `DEBUG`

#### Scenario: Deep input tracing is enabled
- **Given** `jtetris.log.input.level=TRACE`
- **When** input events and repeater polls occur
- **Then** the input-domain logger emits trace-level state-machine decisions
- **And** unrelated logger categories retain their configured levels

#### Scenario: An explicit global level overrides debug mode
- **Given** `jtetris.debug=true`
- **And** `jtetris.log.level=WARN`
- **When** logging initializes
- **Then** the effective global level is `WARN`

### Requirement: File destination and retention must be configurable
JTetris SHALL write bundled file logs to the platform application-data log
directory by default and SHALL support an absolute directory override.

#### Scenario: No directory override is provided on macOS
- **Given** the bundled configuration is used on macOS
- **When** logging initializes
- **Then** logs are written under
  `~/Library/Application Support/net.vetcafe.jtetris/logs`

#### Scenario: A valid absolute directory override is provided
- **Given** `jtetris.log.dir` contains an absolute writable path
- **When** logging initializes
- **Then** rolling logs are written under that directory

#### Scenario: An invalid directory or retention value is provided
- **Given** a logging path or rolling value is invalid
- **When** logging initializes
- **Then** JTetris reports the invalid value to stderr
- **And** falls back to a safe default
- **And** game startup continues

### Requirement: External backend configuration must take precedence
JTetris SHALL honor `logback.configurationFile` as a complete backend override.

#### Scenario: An external Logback configuration is selected
- **Given** `logback.configurationFile` is set
- **When** logging initializes
- **Then** JTetris does not replace its appenders or levels with bundled defaults

### Requirement: Input diagnostics must identify the failing boundary
When enabled, JTetris SHALL log stable fields across Swing action, gameplay
controller, and repeater boundaries.

#### Scenario: A horizontal press moves more than one perceived cell
- **Given** input logging is enabled
- **When** the player presses and releases a horizontal key
- **Then** the log identifies each Swing action and monotonic timestamp
- **And** records key hold duration
- **And** records each emitted repeater step and reason
- **And** records piece coordinates before and after controller operations

### Requirement: High-frequency diagnostics must be opt-in
JTetris SHALL log zero-step polls and detailed repeater internals only at input
`TRACE`.

#### Scenario: Input logging is at DEBUG
- **Given** `jtetris.log.input.level=DEBUG`
- **When** the input timer performs polls that emit no movement
- **Then** zero-step poll details are not written
- **And** press, release, and movement-result diagnostics remain available

### Requirement: Debug mode may detect EDT stalls
JTetris SHALL provide an optional, rate-limited EDT responsiveness watchdog that
is enabled by default only in debug mode.

#### Scenario: The EDT exceeds the configured response threshold
- **Given** the watchdog is enabled
- **When** the EDT does not acknowledge its marker before the threshold
- **Then** JTetris writes a warning containing the observed delay and EDT stack
- **And** does not terminate or interrupt the EDT

### Requirement: Serious failures must be captured without preventing fallback startup
JTetris SHALL install uncaught-exception logging and SHALL degrade to stderr if
file logging cannot initialize.

#### Scenario: An uncaught exception reaches a thread boundary
- **Given** logging initialized successfully
- **When** a thread terminates with an uncaught exception
- **Then** JTetris writes an `ERROR` event with the exception stack

#### Scenario: File logging initialization fails
- **Given** the configured log destination cannot be initialized
- **When** the application starts
- **Then** JTetris reports the failure to stderr
- **And** retains stderr logging
- **And** continues startup where otherwise possible
