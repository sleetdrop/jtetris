# Design

## Visual Direction
The ghost piece will communicate a projected destination through geometry
rather than through a faded version of a real block. Each visible ghost cell
will have a transparent interior and two nested one-pixel rectangular strokes.
The unchanged board and grid remain visible through the cell.

This creates a stable visual hierarchy:

1. Active and locked pieces remain solid, saturated blocks.
2. The ghost remains visible as a positional guide.
3. Grid and background remain the quietest stage elements.

## Theme Treatment
Both themes use the same structural treatment and a neutral cool-gray color
family.

- Light theme: a medium cool-gray outer stroke with a softer inner stroke.
- Dark theme: a light cool-gray outer stroke with a dimmer inner stroke.
- Neither stroke inherits the active tetromino color.
- Neither theme draws a ghost fill.

The outer stroke provides reliable recognition against the board and nearby
piece colors. The inner stroke provides continuity when one edge aligns with a
grid line or a locked block outline. Stroke contrast should remain below solid
piece contrast so the projection does not compete with the active piece.

## Geometry
Normal tetromino cells continue to use the existing filled-cell inset.
Ghost-cell geometry is separate:

- The outer rectangle is inset from the grid boundary so it does not merge with
  grid lines.
- The inner rectangle is inset again from the outer rectangle.
- At very small cell sizes, rendering degrades to a single outline rather than
  producing invalid or overlapping rectangles.
- The interior is never painted.

## Active-Piece Overlap
When the active piece is already resting at its projected destination, the
ghost and active piece can occupy the same coordinates. Those ghost cells will
not be drawn. This avoids placing outline noise over the active piece and keeps
the active piece as the highest-priority object.

The overlap decision remains a rendering concern. `Board.getGhost()` and all
model behavior remain unchanged.

## Code Boundaries
- `GamePanel` owns ghost-cell geometry and overlap filtering.
- `UiTheme` continues to supply board context, while ghost colors remain a
  small rendering token local to the stage unless tests show a reusable theme
  token is necessary.
- `ColorPalette` remains dedicated to real tetromino fills and outlines.

## Verification
Automated tests will render representative ghost cells in light and dark
themes and verify:

- the center of a ghost cell preserves the board background;
- both outline bands are visible;
- the ghost differs structurally from a filled tetromino cell;
- active-piece overlap does not add ghost pixels over the active cell;
- existing model ghost tests and the full Maven test suite still pass.

Manual verification will use user-supplied screenshots from both themes,
including a sparse board and a high stack where fast visual discrimination is
most important.
