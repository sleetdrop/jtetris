# JTetris Java Style

JTetris uses a reader-first Java style. The goal is source that is pleasant to
read months later: direct names, stable file responsibilities, explicit state,
and comments that explain rules or constraints.

The formatter settles whitespace. The style guide settles intent.

## Formatting

- Java source and tests are formatted with the Maven formatter gate.
- Run formatter and lint commands with JDK 17, matching the project runtime.
- Do not hand-align code. Let the formatter choose wrapping and indentation.
- Use 120 columns as the normal line-length target.
- Do not use wildcard imports.
- Keep one top-level type per file, and name the file after that type.

## Naming

- Use domain names over generic names.
- Prefer `currentPiece`, `clearedRows`, and `lineClearEffectVersion` over names
  like `value`, `data`, `tmp`, or `flag`.
- Boolean names should read as predicates: `isPaused`, `hasFocus`,
  `shouldAdvance`.
- Constants should name the game or UI concept they tune, not only the unit.
  `SOFT_DROP_REPEAT_MS` is better than `DELAY`.
- Test names should describe observable behavior:
  `horizontalTapMovesExactlyOneColumn`.

## File Shape

- A file should have one main reason to change.
- Keep deterministic game rules in model classes.
- Keep active session time, Swing state, and rendering state in UI classes.
- Keep persistence details in score or platform classes.
- Avoid opportunistic class splitting. Large-file decomposition should be a
  dedicated OpenSpec change with focused tests.

## Method Shape

- Public methods should tell the class story before private helpers when that is
  the clearest reading order.
- Prefer guard clauses for invalid or inactive states.
- Keep local variables near the operation they explain.
- Extract a helper when it names a real domain step, not just to reduce line
  count.
- Avoid hidden side effects in helpers with vague names such as `process`,
  `handle`, or `update`.

## Comments

Comments should explain why a rule exists, what invariant must hold, or why a
surprising fallback is intentional.

Good comment targets:
- scoring rule differences
- replay determinism requirements
- Swing focus and timer constraints
- platform data migration constraints
- formatter or lint exceptions

Avoid comments that restate the next line of code.

## Error And Edge Handling

- Validate constructor arguments before storing them.
- Use clear exception messages for programmer errors.
- Keep user-facing failure behavior in UI classes.
- Preserve deterministic replay hooks when model behavior changes.

## Tests

- Prefer tests that state one behavior and one reason to fail.
- Use seeded boards for model and input-controller scenarios.
- For UI timing, test controller logic with fake clocks before relying on
  manual Swing reproduction.
- Keep regression tests close to the package they protect.

## Refactoring Policy

Mechanical formatting and semantic refactoring should be separate commits.

When improving readability:
1. Preserve behavior first.
2. Add or identify tests that protect the behavior.
3. Make the smallest extraction that gives a better name or boundary.
4. Run the formatter, lint gate, and tests.

Files that already deserve later attention:
- `TetrisFrame`: frame setup, menu actions, overlays, input wiring, session
  timing, and leaderboard flow are all present in one class. Split only through
  a dedicated behavior-preserving change.
- `Board`: core rules are readable, but future scoring or lock-delay growth
  should move behind named rule helpers before the class becomes harder to
  learn from.
