# ui-theme Specification Delta

## MODIFIED Requirements

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
