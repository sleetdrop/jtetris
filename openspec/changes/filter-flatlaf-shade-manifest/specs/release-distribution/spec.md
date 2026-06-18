# Release Distribution Specification Delta

## ADDED Requirements

### Requirement: Standalone JAR shading uses one authoritative manifest
JTetris SHALL build the standalone JAR without copying dependency manifests that conflict with the project-generated manifest.

#### Scenario: Package standalone JAR without FlatLaf manifest overlap
- **Given** FlatLaf is included as a runtime dependency
- **When** an agent runs the Maven package lifecycle
- **Then** the Shade plugin excludes FlatLaf's `META-INF/MANIFEST.MF`
- **And** packaging does not report an overlapping manifest resource
- **And** the standalone JAR manifest identifies `net.vetcafe.jtetris.ui.TetrisFrame` as the main class
