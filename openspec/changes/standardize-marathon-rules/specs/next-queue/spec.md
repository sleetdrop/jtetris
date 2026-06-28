# Next Queue Specification Delta

## MODIFIED Requirements

### Requirement: Board maintains five upcoming pieces
JTetris SHALL maintain an ordered, model-owned queue containing exactly five
upcoming tetromino types after board initialization and throughout active
gameplay.

#### Scenario: New board exposes five upcoming pieces
- **Given** a board has been constructed
- **When** its upcoming queue is queried
- **Then** the result contains exactly five tetromino types
- **And** the first type is the next type that will become active

#### Scenario: Normal spawn advances and refills the queue
- **Given** a board has five upcoming pieces
- **When** the active piece locks and the next piece spawns
- **Then** the former queue head becomes the active piece
- **And** the remaining queue entries retain their order
- **And** one new type from the 7-bag is appended so the queue again contains
  five entries
