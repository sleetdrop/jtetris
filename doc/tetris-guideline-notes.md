# Tetris Guideline Notes

Last refreshed: 2026-06-28

These notes summarize stable, public guideline-style Tetris concepts for local
JTetris development. They are orientation notes, not an authority. If gameplay
standardization work resumes after 2026-09-28, refresh the references from the
web before relying on this file.

There is an official Tetris Guideline used by licensed games, but the current
complete specification is not maintained as a single public RFC-style document.
JTetris therefore treats public references and observed modern-game behavior as
guidance for a practical Guideline-inspired subset.

## Local direction
- JTetris currently targets Endless Marathon quality before adding Sprint,
  Ultra, versus, or other modes.
- The implementation should stay guideline-inspired and practical, not a claim
  of full official Guideline compliance.

## Stable mechanics to preserve
- 10-column playfield with hidden spawn rows.
- 7-bag random generator for fair tetromino distribution.
- SRS-style rotation and wall kicks.
- Hold, ghost piece, and ordered Next preview.
- Level progression from cleared lines in Marathon.
- Natural gravity should speed up with level.
- Normal placement needs a lock-delay window so high gravity remains playable.
- Guideline-style scoring concepts include line clears, Tetris, T-Spin,
  back-to-back, combo, soft-drop points, and hard-drop points.

## Current JTetris interpretation
- Next preview shows five upcoming pieces.
- Marathon gravity starts at 700 ms and accelerates to a 50 ms high-level floor.
- UI-driven normal locking uses a fixed 500 ms lock delay.
- Soft drop awards 1 point per moved cell; hard drop awards 2 points per moved
  cell.

## References to refresh
- https://tetris.wiki/Tetris_Guideline
- https://tetris.wiki/Random_Generator
- https://tetris.wiki/Super_Rotation_System
- https://tetris.wiki/Scoring
- https://tetris.wiki/TETR.IO
