## MODIFIED Requirements

### Requirement: Theme visuals use deliberate flat contrast
JTetris SHALL render stage cells, grid lines, ghost cells, and preview cells
with a flat visual style in both light and dark themes.

#### Scenario: Ghost projection is structurally distinct from real blocks
- **Given** either the light or dark theme is active
- **And** the active tetromino has a visible landing projection
- **When** the ghost cells are rendered
- **Then** each ghost cell has a transparent interior
- **And** each normally sized ghost cell uses nested neutral outlines
- **And** the outlines do not inherit the active tetromino color
- **And** the ghost remains lower in visual priority than active and locked
  pieces

#### Scenario: Resting active piece is not overdrawn by its ghost
- **Given** an active tetromino already occupies its projected landing cells
- **When** the stage is rendered
- **Then** ghost outlines are not drawn over the overlapping active cells
- **And** the active tetromino remains visually unchanged
