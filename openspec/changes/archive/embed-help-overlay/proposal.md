# Embed Help Overlay

## Summary
Move JTetris Help from a separate Swing window into the existing main-window overlay layer, using a scrollable Swing content panel for longer help text.

## Motivation
Game-over, score, leaderboard, and exit prompts already appear as in-window overlays. Help should follow the same interaction model so the game feels like one cohesive desktop app. Help content is longer than the existing prompts, so the overlay host needs a size policy that can support scrollable content without changing the compact sizing of small prompts.

## Scope
- Replace the separate Help dialog with an in-window overlay.
- Add a larger overlay size option for Help while keeping existing prompt overlays compact.
- Keep Help content Swing-native and dependency-free.
- Keep Help content player-facing; developer backlog remains in OpenSpec/docs only.

## Out Of Scope
- No scoring, input, model, or packaging behavior changes.
- No new libraries.
- No new help content beyond the current player-facing explanations.
