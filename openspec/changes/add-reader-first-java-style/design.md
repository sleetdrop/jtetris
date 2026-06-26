# Design

## Style Direction
JTetris will use a reader-first Java style. The code should be mechanically
formatted, but the higher goal is source that can be opened and understood in a
single pass.

This style borrows discipline from Google Java Style for imports, braces, and
basic structure, but it is not a wholesale adoption of Google style. It also
borrows the old systems-code habit of making module boundaries, local state, and
failure paths explicit.

## Tooling
Formatting will be handled by Spotless with Palantir Java Format. Palantir's
formatter keeps the determinism of Google Java Format while being friendlier to
modern Java expressions and 120-column source.

Linting will be handled by Maven Checkstyle with a small project-owned rule set.
The first rule set intentionally focuses on checks that are unlikely to create
semantic churn:
- no wildcard imports
- no unused or redundant imports
- one top-level class per file
- file names match outer types
- conventional Java names
- 120-column line limit
- required braces for control flow

Broader smell detection, complexity thresholds, and large-class decomposition
belong in later changes after the mechanical baseline is stable.

## Readability Policy
The project style guide will describe the non-mechanical rules:
- prefer domain names over generic names
- keep files organized by responsibility
- keep public methods before local helpers when that tells the story best
- use comments for rules, invariants, and surprising constraints
- keep model state deterministic and UI state in UI classes
- split large files through dedicated OpenSpec changes rather than opportunistic
  cleanup

## Migration Plan
The migration is intentionally staged:
1. Document the style and add tooling.
2. Run formatter across Java source and tests.
3. Fix only low-risk lint issues.
4. Record verification in this change.

Future readability refactors, especially `TetrisFrame` decomposition, should be
separate behavior-preserving changes with focused tests.
