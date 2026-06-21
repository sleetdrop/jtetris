# UI Theme Specification

## Purpose
Define JTetris visual theme behavior for the stage, piece cells, side panel, and auxiliary stage overlays.

## Requirements

### Requirement: Theme visuals use deliberate flat contrast
JTetris SHALL render stage cells, grid lines, ghost cells, and preview cells with a flat visual style in both light and dark themes.

#### Scenario: Stage blocks do not create accidental bevels
- **Given** either the light or dark theme is active
- **When** a tetromino cell is drawn on the stage grid
- **Then** the cell uses a flat fill and a subtle outline derived from the palette/theme
- **And** the cell outline does not use a generic darkening chain that creates a shadow-like bevel

#### Scenario: Preview blocks match stage style
- **Given** a hold or next preview contains a tetromino
- **When** the preview is rendered
- **Then** its cells use the same flat color and outline rule as stage cells

#### Scenario: Ghost projection is structurally distinct from real blocks
- **Given** either the light or dark theme is active
- **And** the active tetromino has a visible landing projection
- **When** the ghost cells are rendered
- **Then** each ghost cell has a transparent interior
- **And** each normally sized ghost cell uses nested neutral outlines
- **And** the outlines do not inherit the active tetromino color
- **And** ghost outlines are not drawn over overlapping active cells
- **And** the ghost remains lower in visual priority than active and locked
  pieces

### Requirement: Side panel preserves content with improved hierarchy
JTetris SHALL present side-panel game information as player-facing state using a clear hierarchy, without duplicating the full controls reference already available in Help.

#### Scenario: Side panel shows core stats and advanced status clearly
- **Given** a game is running in either theme
- **When** the side panel refreshes
- **Then** it displays score prominently
- **And** it displays level, lines, active session time, meaningful scoring feedback, combo status, and back-to-back status in a compact performance section
- **And** the core statistic order is Score, Level, Lines, Time
- **And** Time uses the same visual style as Level and Lines
- **And** regular information labels including Hold and Next use the primary text color consistently
- **And** combo and back-to-back inactive states use subdued text instead of looking like broken counters
- **And** active combo and back-to-back states use the primary text color

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

#### Scenario: Scoring feedback appears only when meaningful
- **Given** the most recent locked piece cleared one or more lines
- **When** the side panel refreshes
- **Then** it shows a human-readable scoring feedback label such as `Single`, `Double`, `Triple`, `Tetris`, or `T-Spin Single`
- **And** it includes combo or back-to-back terms when those bonuses apply
- **And** when the most recent locked piece did not clear a line, the scoring feedback area is hidden or visually inactive rather than showing `Event: -`

#### Scenario: Hold preview explains empty and spent states
- **Given** the player has not used hold in the current game
- **When** the side panel renders the Hold preview
- **Then** it shows a subdued empty state
- **And** after hold is used for the current piece, the held piece preview is visually marked as temporarily unavailable until the piece locks

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

### Requirement: In-app Help explains controls and scoring concepts
JTetris SHALL provide a Swing-native Help page inside the main window overlay layer that explains controls and the modern Tetris concepts surfaced by the UI.

#### Scenario: Player opens Help from the menu
- **Given** the game window is focused
- **When** the player chooses Help from the menu
- **Then** JTetris shows a scrollable Help overlay within the main window
- **And** the Help content includes controls, Hold, Next, Ghost, Combo, Back-to-Back, T-Spin, and scoring feedback explanations
- **And** closing Help returns focus to the game when no other modal layer is active
- **And** Help does not open a separate top-level window

#### Scenario: Player opens Help from the keyboard
- **Given** the game window is focused
- **When** the player presses the Help shortcut
- **Then** JTetris shows the same Help overlay as the menu entry
- **And** the shortcut does not mutate board state

### Requirement: Theme startup avoids unused native helper warnings
JTetris SHALL disable unused FlatLaf native helper loading by default so startup remains clean on Java versions that warn about restricted native access.

#### Scenario: Jar startup does not load FlatLaf native helper by default
- **Given** JTetris is launched from the runnable jar
- **When** the FlatLaf look and feel is initialized
- **Then** `flatlaf.useNativeLibrary` defaults to `false`
- **And** Java does not print restricted native-access warnings from FlatLaf native helper loading during normal startup

### Requirement: Runtime Auto theme reflects system appearance
JTetris SHALL resolve Auto from system appearance rather than from a manually
selected FlatLaf light or dark look and feel.

#### Scenario: Auto restores light after manual dark selection
- **Given** JTetris observed a light system appearance before installing FlatLaf
- **And** the player manually selected Dark
- **When** the player selects Auto
- **Then** JTetris activates the light application theme
- **And** the currently installed FlatDarkLaf does not override the system signal

#### Scenario: Explicit modes remain deterministic
- **Given** either Light or Dark is selected explicitly
- **When** JTetris resolves the active theme
- **Then** it uses the selected explicit theme without consulting Auto detection

### Requirement: Stage overlays fit content without clipping actions
JTetris SHALL render auxiliary stage overlays with content-sized surfaces and action rows that remain fully visible inside the overlay bounds.

#### Scenario: Simple confirmation overlay is compact
- **Given** a simple overlay such as confirm exit is shown
- **When** the overlay lays out its title, message, and action buttons
- **Then** the overlay height is based on its content instead of a fixed tall panel
- **And** action buttons keep visible bottom padding inside the surface

#### Scenario: Larger overlay stays within stage bounds
- **Given** a larger overlay such as leaderboard or score entry is shown
- **When** the overlay lays out table, form, and action components
- **Then** all visible components remain inside the overlay surface
- **And** the overlay surface remains inside the stage bounds
