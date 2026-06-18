# Redesign Side Panel and Add Next Queue

## Summary
Redesign the JTetris side panel around the approved option A hierarchy and replace the single upcoming-piece preview with a model-backed queue of three upcoming pieces.

## Motivation
The in-game Help overlay now provides the complete controls reference, so permanently reserving the bottom of the side panel for the same shortcuts duplicates information and limits the space available for gameplay state. Modern Tetris interfaces also commonly expose multiple upcoming pieces so players can plan beyond the immediate next placement.

The current model stores only one `next` piece. Drawing additional previews by inspecting `PieceBag` would couple the UI to randomizer internals and would make replay-state verification incomplete. The queue must therefore be real board state.

## Scope
- Remove the persistent controls cheat-sheet from the side panel.
- Apply the approved option A hierarchy:
  - prominent score;
  - compact level and lines;
  - meaningful scoring feedback with combo and back-to-back status;
  - a separate Hold section;
  - a vertically arranged Next section containing three upcoming pieces.
- Add a read-only, three-item upcoming queue owned by `Board`.
- Advance and refill the queue consistently after normal spawn and first-use Hold promotion.
- Preserve deterministic seeded replay behavior and expose queue state for replay verification.
- Update Help and stable project documentation to describe the multi-piece queue and the new side-panel responsibilities.

## Out Of Scope
- No scoring-rule, combo, back-to-back, T-Spin, gravity, lock-delay, SRS, or 7-bag algorithm changes.
- No keyboard binding or Help-overlay interaction changes.
- No window-size, stage-size, theme-palette, dependency, packaging, or release changes.
- No queue-length setting or player customization.
- No preview animation.

## Success Criteria
- The side panel contains no persistent controls list.
- Hold and three upcoming pieces remain legible in both light and dark themes at the existing window size.
- The first Next preview is visually primary while the second and third previews remain clearly readable.
- Every queue transition preserves a size of exactly three while the game is initialized or running.
- Seeded replay reconstructs the same current, hold, grid, score, and complete upcoming queue state.
- `./mvnw clean test` passes.
