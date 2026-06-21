# Configurable Logging Design

## Decision
Application classes use SLF4J 2. Logback Classic is the bundled default provider. JTetris adds a narrow configuration/bootstrap layer, but does not define a competing logger interface: SLF4J itself is the pluggable abstraction.

The default experience remains quiet. Normal startup writes only `ERROR` events. Diagnostic detail is opt-in through JVM properties or an external Logback configuration file.

## Levels

### `ERROR`
Use for:
- uncaught exceptions;
- unrecoverable initialization failure;
- explicit invariant violations that make continued behavior unreliable;
- failure to initialize the configured logging destination, followed by stderr fallback.

### `WARN`
Use for:
- invalid logging configuration values that are replaced by safe defaults;
- recoverable file/persistence failures;
- EDT responsiveness violations detected by the debug watchdog;
- abnormal but recoverable state transitions.

### `INFO`
Use sparingly for low-frequency lifecycle events when the effective level enables it:
- application startup and shutdown;
- logging mode and destination summary;
- game restart or game-over transition when useful for incident context.

### `DEBUG`
Use for diagnostic actions and state transitions:
- Swing press/release actions;
- controller operation results;
- key hold durations;
- movement before/after coordinates;
- reset and input-state clearing reasons.

### `TRACE`
Use for high-frequency state-machine internals:
- DAS/ARR and soft-drop deadlines;
- poll decisions, including zero-step polls when input trace is explicitly `TRACE`;
- held-key state and active-direction transitions.

No custom levels such as `FATAL` or `NOTICE` will be added.

## Logger Categories
- Root/application: `net.vetcafe.jtetris`
- Input domain convenience category: `net.vetcafe.jtetris.input`
- EDT watchdog: `net.vetcafe.jtetris.edt`

Input-related classes emit diagnostic events through the input-domain logger even though their Java package remains `net.vetcafe.jtetris.ui`. The stable functional category avoids coupling operator configuration to future package refactors.

## Configuration Properties

```text
jtetris.debug
jtetris.log.level
jtetris.log.input.level
jtetris.log.dir
jtetris.log.maxFileSize
jtetris.log.maxHistory
jtetris.log.totalSizeCap
jtetris.log.edtWatchdog.enabled
jtetris.log.edtWatchdog.thresholdMs
logback.configurationFile
```

### Defaults

```text
jtetris.debug=false
jtetris.log.level=ERROR
jtetris.log.input.level=<inherits global level>
jtetris.log.dir=<platform application data>/logs
jtetris.log.maxFileSize=10MB
jtetris.log.maxHistory=7
jtetris.log.totalSizeCap=100MB
jtetris.log.edtWatchdog.enabled=<true only when debug mode is active>
jtetris.log.edtWatchdog.thresholdMs=500
```

`jtetris.debug=true` sets the default global level to `DEBUG` unless
`jtetris.log.level` is explicitly provided. It does not automatically enable
input `TRACE`; that requires `jtetris.log.input.level=TRACE`.

### Precedence
1. `-Dlogback.configurationFile=...` selects a complete external backend configuration. JTetris does not override levels or appenders from that file.
2. Otherwise, JTetris system properties configure the bundled Logback setup.
3. Missing properties use the defaults above.

Invalid levels, sizes, counts, relative directory overrides, or watchdog
thresholds emit a warning to stderr during bootstrap and fall back to defaults.

## Platform Log Directory
The default log root reuses the platform conventions already used by score data:

- macOS: `~/Library/Application Support/net.vetcafe.jtetris/logs`
- Linux: `${XDG_DATA_HOME:-~/.local/share}/net.vetcafe.jtetris/logs`
- Windows: `%LOCALAPPDATA%\net.vetcafe.jtetris\logs`, with the existing home-directory fallback

The path resolver moves into a shared application-data utility so score and log
paths cannot drift. This package move/update is confined to the logging change.

`jtetris.log.dir` must be an absolute path. The bootstrap creates missing
directories. If creation or file initialization fails, logging falls back to
stderr and the game continues.

## Rolling Files
The default filename is:

```text
jtetris.log
```

Archived files use date and index naming. Logback size-and-time rolling enforces:
- maximum active/archive file size;
- retained history in days;
- total retained size cap.

The default encoder is plain text optimized for diagnosis:

```text
timestamp level thread logger message key=value...
```

Stack traces follow the event. JSON output is not required in the bundled
configuration; structured input fields remain stable `key=value` tokens that are
easy to search and parse.

## Input Diagnostics
Evidence must cross all relevant boundaries:

### Swing action boundary
At `DEBUG`, record:
- action (`leftPressed`, `leftReleased`, `rightPressed`, `rightReleased`);
- monotonic time;
- EDT thread name;
- gameplay eligibility;
- piece coordinates before and after delegation.

This reveals duplicate or missing Swing actions.

### Controller boundary
At `DEBUG`, record:
- operation;
- emitted horizontal/vertical steps;
- movement success;
- piece coordinates before and after;
- key hold duration for releases;
- reset reason when supplied by the frame.

### Repeater boundary
At `TRACE`, record:
- held directions;
- active direction;
- press order;
- next repeat deadline;
- current monotonic time;
- emitted step and decision reason (`press`, `before-das`, `repeat`, `idle`,
  `duplicate-press`, `release`, `reset`).

High-frequency zero-step poll events appear only at `TRACE`. `DEBUG` remains
usable for a full play session without excessive volume.

## Sensitive Data
Input logs contain key names, timing, game state coordinates, piece type, thread,
and operation result. They do not contain usernames, score-file contents,
absolute home paths in event bodies, arbitrary text input, or screenshots.

## EDT Watchdog
The watchdog is diagnostic, not a production health service.

- It is disabled during normal `ERROR` operation.
- It is enabled by default when `jtetris.debug=true`, unless explicitly disabled.
- A daemon scheduler periodically posts a marker to the EDT.
- If acknowledgement exceeds the threshold, it logs one `WARN` with delay and
  EDT stack trace.
- It rate-limits repeated warnings until the EDT responds again.
- It never terminates, interrupts, or restarts the EDT.

This can reveal whether perceived input pauses correspond to EDT stalls.

## Bootstrap and Failure Handling
Logging initializes before Swing UI construction.

1. Parse JTetris logging properties.
2. Resolve/create the directory when bundled configuration is used.
3. Set Logback-substitution properties.
4. Initialize the SLF4J provider.
5. Install a default uncaught-exception handler.
6. Start the optional EDT watchdog after Swing startup.

Bootstrap errors are printed directly to stderr because the logger may not be
available yet. The system then uses a stderr console fallback and continues
launching the game.

## Initial Diagnostic Workflow

General diagnostic run:

```bash
java -Djtetris.debug=true -jar target/jtetris-1.0.0-standalone.jar
```

Deep input run:

```bash
java \
  -Djtetris.debug=true \
  -Djtetris.log.input.level=TRACE \
  -jar target/jtetris-1.0.0-standalone.jar
```

Custom destination:

```bash
java \
  -Djtetris.debug=true \
  -Djtetris.log.input.level=TRACE \
  -Djtetris.log.dir=/absolute/path/to/jtetris-logs \
  -jar target/jtetris-1.0.0-standalone.jar
```

After the player reproduces the issue, diagnosis compares:
- number and timing of Swing press/release actions;
- actual hold duration versus the 130ms DAS threshold;
- emitted repeater steps;
- controller movement results;
- EDT watchdog warnings.

Only after this evidence identifies the failing boundary will input behavior be
changed.

## Testing
- property parsing, precedence, defaults, and invalid-value fallbacks;
- platform log-directory resolution;
- Logback bootstrap to a temporary directory;
- rolling-policy configuration values;
- default `ERROR`, debug `DEBUG`, and input `TRACE` effective levels;
- no high-frequency trace work when the category is disabled;
- stable input diagnostic fields using a test logger sink;
- uncaught-exception handler delegation;
- EDT watchdog delayed-acknowledgement detection using fake scheduler/clock
  boundaries where practical;
- existing full headless Maven suite.
