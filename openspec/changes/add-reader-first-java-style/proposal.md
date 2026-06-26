# Add Reader-First Java Style

## Why
JTetris has grown beyond a small experiment. Future work needs a stable coding
style and local quality gate so the code remains easy to read, learn from, and
maintain.

The target style is not strictness for its own sake. It should preserve the
plain, modular readability found in mature systems code: clear file
responsibilities, direct control flow, meaningful domain names, and comments
that explain constraints rather than restating code.

## What Changes
- Add a JTetris Java style guide focused on long-term readability.
- Use a deterministic Java formatter so whitespace and wrapping are not debated.
- Add a Maven lint gate for low-risk Java style checks.
- Update quality gate documentation to include formatting and lint commands.
- Format existing Java sources after the toolchain is configured.

## Scope
In scope:
- Style documentation.
- Maven formatter and Checkstyle configuration.
- Mechanical formatting of Java source and tests.
- Low-risk lint fixes that do not change behavior.

Out of scope:
- Gameplay behavior changes.
- Keyboard input timing or focus changes.
- Package moves or dependency architecture changes beyond build plugins.
- Large class extraction, including splitting `TetrisFrame`.
- Non-mechanical readability refactors.

## Verification
- `./mvnw spotless:check`
- `./mvnw checkstyle:check`
- `./mvnw -Djava.awt.headless=true clean test`

Formatter and lint commands should run with JDK 17, matching the project
runtime.
