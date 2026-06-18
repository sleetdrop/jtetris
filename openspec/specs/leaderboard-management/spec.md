# Leaderboard Management Specification

## Purpose
Define how users inspect and remove individual local leaderboard records.

## Requirements

### Requirement: Players can delete individual local score records
JTetris MUST allow a player record to be permanently removed from local score storage.

#### Scenario: Delete an existing player
- **Given** a player exists in the local score store
- **When** deletion is confirmed
- **Then** the player's score and remembered display name are removed
- **And** the updated score store is persisted

#### Scenario: Delete persistence fails
- **Given** a player exists in the local score store
- **When** JTetris cannot persist the deletion
- **Then** the in-memory player record is restored
- **And** the leaderboard continues to show the player
- **And** JTetris reports that deletion failed

#### Scenario: Delete a missing player
- **Given** no matching player exists
- **When** deletion is requested
- **Then** JTetris reports that no record was removed
- **And** existing score data is unchanged

### Requirement: Leaderboard deletion requires explicit confirmation
The leaderboard MUST require row selection and a confirmation step before deleting local score data.

#### Scenario: No player is selected
- **Given** the leaderboard contains score records
- **And** no row is selected
- **When** the leaderboard is displayed
- **Then** the `Delete` action is disabled

#### Scenario: Player is selected
- **Given** a leaderboard row is selected
- **When** the selection changes
- **Then** the `Delete` action is enabled

#### Scenario: Deletion is requested
- **Given** a player row is selected
- **When** the user activates `Delete`
- **Then** JTetris shows an in-window confirmation naming that player
- **And** no data is deleted before confirmation

#### Scenario: Deletion is cancelled
- **Given** a deletion confirmation is visible
- **When** the user cancels
- **Then** the selected player's data remains unchanged
- **And** the leaderboard is shown again

#### Scenario: Deletion succeeds
- **Given** a deletion confirmation is visible
- **When** the user confirms deletion
- **Then** the selected player is removed from persistent storage
- **And** the refreshed leaderboard is shown

#### Scenario: Last player is deleted
- **Given** the leaderboard contains one player
- **When** that player's deletion succeeds
- **Then** the leaderboard displays `No scores yet`

