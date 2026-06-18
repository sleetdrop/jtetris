# UI Theme Specification Delta

## MODIFIED Requirements

### Requirement: Side panel preserves content with improved hierarchy
JTetris SHALL present side-panel game information as player-facing state using the approved option A hierarchy, without duplicating the full controls reference already available in Help.

#### Scenario: Side panel shows core stats and advanced status clearly
- **Given** a game is running in either theme
- **When** the side panel refreshes
- **Then** it displays score prominently
- **And** it displays level, lines, meaningful scoring feedback, combo status, and back-to-back status in a compact performance section
- **And** inactive combo and back-to-back states use subdued text instead of looking like broken counters

#### Scenario: Hold and Next use separate stable sections
- **Given** a game is running at the default window size
- **When** the side panel renders
- **Then** Hold appears in a section separated from performance state by a divider
- **And** Next appears in a section separated from Hold by a divider
- **And** neither section shifts or clips when its piece state changes

#### Scenario: Side panel omits persistent controls reference
- **Given** controls are documented in the in-app Help overlay
- **When** the side panel renders
- **Then** it does not display the permanent keyboard controls cheat-sheet
- **And** the Help menu action and Help keyboard shortcut remain available

### Requirement: Side panel shows three upcoming pieces
JTetris SHALL render the three model-owned upcoming pieces vertically in promotion order.

#### Scenario: First upcoming piece is visually primary
- **Given** the Board upcoming queue contains three types
- **When** the Next section renders
- **Then** all three types are visible in queue order
- **And** the first upcoming piece uses the primary preview size and full theme color
- **And** the second and third pieces remain readable as visually secondary previews

#### Scenario: Upcoming previews match flat theme styling
- **Given** either the light or dark theme is active
- **When** the three upcoming pieces are rendered
- **Then** each cell uses `ColorPalette` fill and outline routing
- **And** the previews do not introduce bevel, shadow, or theme-specific hard-coded colors
