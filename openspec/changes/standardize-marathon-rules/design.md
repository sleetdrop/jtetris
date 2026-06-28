# Design

## Rules
Endless Marathon remains the only active mode. Level continues to advance every
ten cleared lines, and the UI gravity timer derives its delay from the current
level instead of using one fixed delay for the whole run.

## Lock Delay
Normal gravity locking uses a fixed UI-layer delay so high levels do not shorten
the player's lock window. The deterministic `Board` replay API remains action
based; active session time still stays out of board state.

## Preview Layout
The model owns five upcoming pieces. The side panel keeps the current fixed
panel size and draws five compact previews under the existing Next divider, with
the first preview visually primary and later previews smaller.

## Reference Notes
`doc/tetris-guideline-notes.md` records stable guideline-style mechanics and
source URLs for local orientation. The note is not authoritative after three
months; gameplay-standardization work after that window should refresh from the
web before relying on it.
