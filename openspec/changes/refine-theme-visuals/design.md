# Design

## Visual Direction
JTetris will use a flat board style in both light and dark themes. Blocks keep a one-pixel outline for readability, but the outline is derived from the block color and theme background rather than from `darker()` shadow calls. This avoids accidental bevels and keeps pieces readable against the grid.

The grid remains visible enough to support spatial planning, but it should sit behind blocks instead of competing with them. Light and dark themes use similar contrast relationships: quiet frame, slightly distinct stage, subtle grid, clear pieces, and readable side-panel text.

## Theme Palette
`UiTheme` remains the single source for non-piece UI colors. The light theme moves away from the current beige-heavy palette toward neutral warm gray surfaces. The dark theme keeps the existing navy direction but lowers the contrast between frame, board, side panel, and grid so the block colors carry the game state.

`ColorPalette` remains the piece-color source. It gains a small helper for flat cell outlines so stage and preview rendering use the same rule.

FlatLaf native helper loading is disabled by default with `flatlaf.useNativeLibrary=false`. JTetris does not rely on FlatLaf native window-border helpers, and disabling them avoids Java 26 restricted native-access warnings while keeping the Swing look and feel in place.

## Stage Rendering
`GamePanel` continues to draw the background, grid, locked blocks, line-clear flash, ghost piece, and current piece in that order. Filled cells are inset within the grid and outlined inside their own cell bounds, preventing block outlines and grid lines from stacking into a heavy edge.

The ghost piece stays translucent and neutral. Its fill and stroke use theme-aware muted colors so it is visible in both themes without resembling an active piece.

## Side Panel
`SidePanel` keeps the same information: score, level, lines, event, combo, B2B, hold, next, and controls. The panel gets clearer spacing, smaller controls text, and preview blocks rendered with the same flat cell style as the stage. This improves consistency without changing game controls or model data flow.

## Overlays
Stage overlays should size to their content instead of using one fixed tall panel for every state. Simple confirmation and feedback dialogs should feel compact, while leaderboard and score-entry overlays can request more space without exceeding the stage bounds.

Action rows use a shared helper with top and bottom padding so buttons never sit on the panel edge. Form and table controls use bounded preferred sizes that fit within the overlay max width.

## Verification
Automated verification remains `mvn clean test`. Manual UI verification should include launching light and dark themes and checking that:
- stage grid lines do not create a bevel-like effect with block outlines;
- block identities remain distinct;
- hold/next previews match stage block styling;
- side-panel text remains readable in both themes.
- Java 26 startup does not print FlatLaf native-access warnings.
- exit, score-entry, leaderboard, and game-over overlays keep content and buttons inside the overlay panel without bottom-edge clipping.
