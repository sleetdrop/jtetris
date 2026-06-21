# Refine Ghost Piece Visibility

## Summary
Render the ghost piece as a neutral, unfilled double outline so players can
distinguish the landing projection from active and locked tetrominoes during
fast play.

## Motivation
The current ghost piece uses a translucent fill and border. Although subdued,
it keeps the same filled-cell silhouette as real blocks. In the light theme,
and especially when the stack is nearly empty or dangerously high, the ghost
can be mistaken for a locked piece. The distinction must come from structure,
not only from lower opacity.

## Scope
- Replace the translucent ghost fill with a theme-aware double outline.
- Keep the ghost neutral rather than inheriting the active tetromino color.
- Preserve clear visibility over empty grid cells and dense stacks in both
  light and dark themes.
- Avoid drawing ghost cells that occupy the same grid coordinates as the
  active piece.
- Add focused rendering tests for the ghost visual treatment.

## Out Of Scope
- No changes to ghost projection, hard drop, collision, locking, replay, or
  scoring behavior.
- No changes to tetromino palette colors, stage grid colors, or preview cells.
- No user preference for disabling or restyling the ghost piece.
- No changes to other stage effects or side-panel layout.
