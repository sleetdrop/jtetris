# M1 Core Rules Spec

## Spec ID
`M1-CORE-RULES`

## Status
`Approved`

## Scope
- Define a practical competition-ready baseline for this project.
- Compare current behavior against that baseline.
- Produce a prioritized implementation queue for M2 and M3.

## Out of scope
- Full UI redesign
- Network multiplayer
- Cloud leaderboard

## Design notes
Target baseline is a Guideline-inspired subset with explicit engineering constraints for Swing:
- Fair randomization (7-bag)
- Predictable rotation semantics (SRS + kicks)
- Competitive action set (hold, ghost, hard/soft drop)
- Stable input loop semantics

## Implementation checklist
- [ ] Document the exact rule set and terms used in this project.
- [ ] Audit current mechanics in `src/tetris/model/Board.java`.
- [ ] Audit current key handling in `src/tetris/ui/TetrisFrame.java`.
- [ ] Create a gap matrix with `Current`, `Target`, `Severity`, `Owner`.
- [ ] Split M2 and M3 work into child specs with measurable acceptance.

## Acceptance criteria
- A complete gap matrix exists and is committed.
- Every `P0` gap has a planned child spec ID.
- No ambiguity remains for rotation, randomizer, and input timing terms.

## Verification
- Manual review against code and docs.
- Team review checkpoint: one pass focused only on unclear wording.

## Rollback plan
- If scope becomes too broad, freeze this spec and split into two specs:
  - `M1A` rules glossary
  - `M1B` implementation gap audit

