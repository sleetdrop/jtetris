# ui-theme Specification Delta

## MODIFIED Requirements

### Requirement: Side panel preserves content with improved hierarchy
JTetris SHALL present side-panel game information as player-facing state, keeping professional Tetris mechanics visible without showing inactive debug-style placeholders.

#### Scenario: Side panel shows core stats and advanced status clearly
- **Given** a game is running in either theme
- **When** the side panel refreshes
- **Then** it displays score, level, lines, hold preview, next preview, combo status, back-to-back status, and controls
- **And** combo and back-to-back inactive states use subdued text instead of looking like broken counters
- **And** no gameplay input, scoring, or model state behavior changes as part of the side-panel rendering

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

## ADDED Requirements

### Requirement: In-app Help explains controls and scoring concepts
JTetris SHALL provide a Swing-native Help page that explains controls and the modern Tetris concepts surfaced by the UI.

#### Scenario: Player opens Help from the menu
- **Given** the game window is focused
- **When** the player chooses Help from the menu
- **Then** JTetris shows a non-gameplay Help page or overlay using Swing components
- **And** the Help content includes controls, Hold, Next, Ghost, Combo, Back-to-Back, T-Spin, and scoring feedback explanations
- **And** closing Help returns focus to the game when no other modal layer is active

#### Scenario: Player opens Help from the keyboard
- **Given** the game window is focused
- **When** the player presses the Help shortcut
- **Then** JTetris shows the same Help content as the menu entry
- **And** the shortcut does not mutate board state
