# Repository Publication Specification

## ADDED Requirements

### Requirement: Public repository metadata is explicit
JTetris MUST include public-facing ownership, license, contributor, and Maven metadata suitable for GitHub hosting.

#### Scenario: A new contributor inspects repository metadata
- **Given** the repository is published on GitHub
- **When** a contributor opens the root project files
- **Then** the project exposes an MIT license with the full copyright holder name
- **And** Maven metadata identifies the project license and developer contact
- **And** contributor-facing files explain basic contribution, conduct, and security reporting expectations

### Requirement: Third-party notices are accurate
JTetris MUST document third-party software and asset licenses without claiming bundled assets that are not actually present.

#### Scenario: A redistributor checks included dependencies
- **Given** JTetris depends on third-party libraries or assets
- **When** a redistributor reads the repository notice files
- **Then** the redistributor can identify runtime dependencies, test dependencies, and bundled assets
- **And** malformed or placeholder resources are not documented as valid bundled assets

### Requirement: Public workflow avoids obsolete tool-specific artifacts
JTetris MUST keep agent and contributor workflows tool-neutral unless a file is intentionally scoped to a hosting integration.

#### Scenario: A maintainer prepares GitHub workflows later
- **Given** prior Copilot/java-upgrade artifacts existed locally
- **When** the repository is prepared for public release
- **Then** obsolete tool-generated artifacts are removed
- **And** future GitHub Actions or release workflows remain a separate change

### Requirement: Completed OpenSpec changes are archived
Completed OpenSpec changes MUST be moved out of the active changes list after their requirements are promoted to canonical specs.

#### Scenario: An agent inspects active OpenSpec changes
- **Given** a change has completed implementation and verification
- **When** the repository is prepared for public release
- **Then** its final requirements are available under `openspec/specs/`
- **And** its change directory is moved under `openspec/changes/archive/`
