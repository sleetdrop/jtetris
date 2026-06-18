# Score Storage Specification

## Purpose
Define where JTetris stores local best scores and how legacy score data is migrated.

## Requirements

### Requirement: Scores use platform application data directories
JTetris MUST store local score data in a package-namespaced persistent application data directory appropriate to the host platform.

#### Scenario: macOS score storage
- **Given** JTetris runs on macOS
- **When** the score store path is resolved
- **Then** the path is `~/Library/Application Support/net.vetcafe.jtetris/scores.properties`

#### Scenario: Linux score storage with XDG override
- **Given** JTetris runs on Linux
- **And** `XDG_DATA_HOME` contains an absolute path
- **When** the score store path is resolved
- **Then** JTetris stores scores under `XDG_DATA_HOME/net.vetcafe.jtetris/scores.properties`

#### Scenario: Linux score storage without a valid XDG override
- **Given** JTetris runs on Linux
- **And** `XDG_DATA_HOME` is absent, blank, or relative
- **When** the score store path is resolved
- **Then** JTetris stores scores at `~/.local/share/net.vetcafe.jtetris/scores.properties`

#### Scenario: Windows score storage
- **Given** JTetris runs on Windows
- **And** `LOCALAPPDATA` is available
- **When** the score store path is resolved
- **Then** JTetris stores scores under `LOCALAPPDATA/net.vetcafe.jtetris/scores.properties`

#### Scenario: Windows environment fallback
- **Given** JTetris runs on Windows
- **And** `LOCALAPPDATA` is absent or blank
- **When** the score store path is resolved
- **Then** JTetris stores scores at `~/AppData/Local/net.vetcafe.jtetris/scores.properties`

### Requirement: Legacy scores migrate without destructive failure
JTetris MUST migrate the legacy `~/.tetris_scores.properties` store when no new-platform store exists.

#### Scenario: Successful legacy migration
- **Given** the new score file does not exist
- **And** the legacy score file exists and is readable
- **When** `ScoreManager` initializes
- **Then** the legacy records are written to the new platform store
- **And** the legacy file is deleted only after the new file is saved successfully

#### Scenario: Migration write fails
- **Given** the new score file does not exist
- **And** the legacy score file exists
- **When** JTetris cannot write the new platform store
- **Then** the legacy file remains in place
- **And** the loaded scores remain available for the current process

#### Scenario: Legacy store is unreadable
- **Given** the new score file does not exist
- **And** the legacy score file cannot be read
- **When** `ScoreManager` initializes
- **Then** JTetris leaves the legacy file in place
- **And** does not create an empty replacement store

#### Scenario: Both stores exist
- **Given** both the new score file and the legacy score file exist
- **When** `ScoreManager` initializes
- **Then** only the new score file is loaded
- **And** legacy values are not merged into or allowed to overwrite the new store

### Requirement: Score storage format remains compatible
JTetris MUST preserve the existing properties-based best-score format and case-insensitive username behavior.

#### Scenario: Existing migrated property is queried
- **Given** a migrated property contains a lowercase username key and integer score
- **When** the player is queried using any letter case
- **Then** JTetris returns the stored best score

