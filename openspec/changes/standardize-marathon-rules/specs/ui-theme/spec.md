# UI Theme Specification Delta

## MODIFIED Requirements

### Requirement: Side panel shows five upcoming pieces
JTetris SHALL render the five model-owned upcoming pieces vertically in promotion
order while preserving the fixed side-panel footprint.

#### Scenario: Five upcoming pieces remain readable
- **Given** the Board upcoming queue contains five types
- **When** the Next section renders at the default window size
- **Then** all five types are visible in queue order
- **And** the first upcoming piece is visually primary
- **And** the second through fifth pieces remain readable without clipping or
  overlapping the panel bounds
