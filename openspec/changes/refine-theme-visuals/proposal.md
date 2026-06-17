# Refine Theme Visuals

## Summary
Refine the light and dark JTetris themes so the stage, tetromino blocks, ghost piece, and side information panel share a deliberate flat visual style.

## Motivation
The current stage grid and block outlines can create an accidental 3D effect, especially when block edges overlap with high-contrast grid lines. The light and dark themes also differ in visual weight: the light theme reads warm and heavy, while the dark theme reads flatter but still uses strong outlines. The UI should either use 3D intentionally in both themes or avoid it. This change chooses a flat presentation with consistent contrast and hierarchy.

## Scope
- Rebalance light and dark theme colors for the frame, stage, grid, side panel, text, dialogs, overlays, and table accents.
- Update tetromino palettes so pieces retain clear identities while matching a flat theme.
- Render stage cells and preview cells with flat fills and subtle same-system outlines instead of shadow-like dark edges.
- Improve side-panel visual hierarchy while preserving existing content and gameplay behavior.
- Improve in-stage auxiliary overlays so simple dialogs size to content, action buttons keep visible padding, and larger table/form overlays stay within the stage bounds.
- Suppress FlatLaf native helper loading by default so Java 26 does not emit restricted native-access warnings during normal startup.
- Add focused validation for theme and palette invariants where practical.

## Out Of Scope
- No gameplay, scoring, replay, input, timing, packaging, or persistence changes.
- No localization changes; UI text remains English.
- No new theme modes or runtime configuration flags.
- No broad Swing layout rewrite outside the existing stage and side-panel presentation.
