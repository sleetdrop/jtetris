## ADDED Requirements

### Requirement: Theme Visuals Use Deliberate Flat Contrast
JTetris SHALL render stage cells, grid lines, ghost cells, and preview cells with a flat visual style in both light and dark themes.

#### Scenario: Stage Blocks Do Not Create Accidental Bevels
- **Given** either the light or dark theme is active
- **When** a tetromino cell is drawn on the stage grid
- **Then** the cell uses a flat fill and a subtle outline derived from the palette/theme
- **And** the cell outline does not use a generic darkening chain that creates a shadow-like bevel

#### Scenario: Preview Blocks Match Stage Style
- **Given** a hold or next preview contains a tetromino
- **When** the preview is rendered
- **Then** its cells use the same flat color and outline rule as stage cells

### Requirement: Side Panel Preserves Content With Improved Hierarchy
JTetris SHALL preserve the existing side-panel information while making spacing, text hierarchy, and preview rendering consistent across light and dark themes.

#### Scenario: Side Panel Content Remains Stable
- **Given** a game is running in either theme
- **When** the side panel refreshes
- **Then** it still displays score, level, lines, event, combo, B2B, hold, next, and controls
- **And** no gameplay input, scoring, or model state behavior changes as part of theme rendering

### Requirement: Theme Startup Avoids Unused Native Helper Warnings
JTetris SHALL disable unused FlatLaf native helper loading by default so startup remains clean on Java versions that warn about restricted native access.

#### Scenario: Jar Startup Does Not Load FlatLaf Native Helper By Default
- **Given** JTetris is launched from the runnable jar
- **When** the FlatLaf look and feel is initialized
- **Then** `flatlaf.useNativeLibrary` defaults to `false`
- **And** Java does not print restricted native-access warnings from FlatLaf native helper loading during normal startup

### Requirement: Stage Overlays Fit Content Without Clipping Actions
JTetris SHALL render auxiliary stage overlays with content-sized surfaces and action rows that remain fully visible inside the overlay bounds.

#### Scenario: Simple Confirmation Overlay Is Compact
- **Given** a simple overlay such as confirm exit is shown
- **When** the overlay lays out its title, message, and action buttons
- **Then** the overlay height is based on its content instead of a fixed tall panel
- **And** action buttons keep visible bottom padding inside the surface

#### Scenario: Larger Overlay Stays Within Stage Bounds
- **Given** a larger overlay such as leaderboard or score entry is shown
- **When** the overlay lays out table, form, and action components
- **Then** all visible components remain inside the overlay surface
- **And** the overlay surface remains inside the stage bounds
