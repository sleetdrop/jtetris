# Next Queue Specification

## Purpose
Define the ordered upcoming-piece state used by gameplay, Hold promotion, replay verification, and side-panel previews.

## Requirements

### Requirement: Board maintains three upcoming pieces
JTetris SHALL maintain an ordered, model-owned queue containing exactly three upcoming tetromino types after board initialization and throughout active gameplay.

#### Scenario: New board exposes three upcoming pieces
- **Given** a board has been constructed
- **When** its upcoming queue is queried
- **Then** the result contains exactly three tetromino types
- **And** the first type is the next type that will become active

#### Scenario: Normal spawn advances and refills the queue
- **Given** a board has three upcoming pieces
- **When** the active piece locks and the next piece spawns
- **Then** the former queue head becomes the active piece
- **And** the remaining queue entries retain their order
- **And** one new type from the 7-bag is appended so the queue again contains three entries

#### Scenario: Empty Hold advances the queue
- **Given** Hold is empty and the board has three upcoming pieces
- **When** the player uses Hold
- **Then** the former active type is stored in Hold
- **And** the former queue head becomes active
- **And** the upcoming queue is refilled to three entries

#### Scenario: Hold swap preserves the queue
- **Given** Hold already contains a piece and the board has three upcoming pieces
- **When** the player swaps the active piece with Hold
- **Then** the upcoming queue remains unchanged

#### Scenario: Reset rebuilds upcoming state
- **Given** a game has progressed
- **When** the board is reset
- **Then** a new current piece is created
- **And** the upcoming queue contains exactly three entries from the reset 7-bag sequence

### Requirement: Upcoming queue is read-only to consumers
JTetris SHALL expose upcoming queue state without allowing UI or test consumers to mutate Board state.

#### Scenario: Consumer receives an immutable snapshot
- **Given** a board has an upcoming queue
- **When** a consumer queries the queue
- **Then** it receives the entries in promotion order
- **And** modifying the returned value is not permitted
- **And** no queue mutation method is exposed to the consumer

### Requirement: Seeded replay includes complete upcoming state
JTetris SHALL preserve the complete upcoming queue when reconstructing a game from a seed and replay actions.

#### Scenario: Replayed queue matches source queue
- **Given** a seeded board has recorded gameplay actions
- **When** another board replays those actions from the same seed
- **Then** both boards have equal upcoming queues in the same order
- **And** their current and held piece states also match
