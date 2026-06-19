## MODIFIED Requirements

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
