# Professionalize Side Panel and Help

## Summary
Refine the JTetris side panel so guideline-style mechanics are presented as player-facing information instead of debug-like labels, and add an in-app Swing Help page that explains controls and advanced scoring concepts.

## Motivation
JTetris already implements modern Tetris mechanics such as hold, ghost, combo, back-to-back, and baseline T-Spin scoring. The current side panel exposes these states directly as `Event`, `Combo`, and `B2B`, which can look broken to casual players because many values are inactive most of the time. The game should keep professional mechanics visible while making their meaning and state clearer.

## Scope
- Keep score, level, lines, hold, next, combo, and back-to-back visible in a cleaner side-panel hierarchy.
- Replace the permanent `Event: -` label with player-facing scoring feedback that only appears when a meaningful clear event occurs.
- Add a hold empty state and a visual used-this-turn state so the Hold panel explains itself during play.
- Add a Swing-native Help page from the menu and keyboard shortcut, with controls and concise explanations of core mechanics and advanced scoring.
- Record future Tetris polish items for later specs.

## Out Of Scope
- No dependency additions.
- No change to scoring formulas, piece randomizer, SRS kicks, gravity, lock delay, or replay semantics.
- No multi-piece next queue, perfect clear scoring, drop scoring, or T-Spin Mini implementation in this change.
- No release artifact or packaging changes.

## Research Notes
- Public descriptions of the Tetris Guideline and common modern Tetris games consistently identify hold, next queue, ghost piece, SRS, 7-bag randomizer, combo, back-to-back, and T-Spin scoring as guideline-style mechanics.
- Back-to-back and combo are expected to be inactive in ordinary play; their UI should therefore communicate readiness/inactivity without implying an error.
- Multi-piece next queue, perfect clear, soft/hard drop points, and T-Spin Mini are appropriate follow-up improvements, but each affects model rules or layout more broadly and should be handled separately.
