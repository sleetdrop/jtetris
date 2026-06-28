# release-distribution Specification

## Requirements

### Requirement: Initial Release Artifacts
JTetris MUST provide initial `1.0.0` release artifacts that are directly executable for Java users and Apple Silicon macOS users.

#### Scenario: Publish standalone runnable jar
- **Given** the release tag is `v1.0.0`
- **When** the GitHub Release is created
- **Then** it includes one standalone JTetris fat jar built from the tagged commit
- **And** the jar includes its runtime library dependencies.

#### Scenario: Publish Apple Silicon macOS app
- **Given** the release tag is `v1.0.0`
- **When** the GitHub Release is created from an Apple Silicon macOS build host
- **Then** it includes a compressed `JTetris.app` app-image suitable for Apple Silicon macOS.

#### Scenario: Avoid non-runnable release assets
- **Given** a user visits the GitHub Release assets
- **When** they choose between downloadable JTetris artifacts
- **Then** each project-provided artifact is directly executable after download or extraction.

### Requirement: Standalone JAR shading uses one authoritative manifest
JTetris SHALL build the standalone JAR without copying dependency manifests that conflict with the project-generated manifest.

#### Scenario: Package standalone JAR without dependency manifest overlap
- **Given** runtime dependencies are included in the standalone JAR
- **When** an agent runs the Maven package lifecycle
- **Then** the Shade plugin excludes dependency `META-INF/MANIFEST.MF`
  resources that would conflict with the project-generated manifest
- **And** packaging does not report an overlapping manifest resource
- **And** the standalone JAR manifest identifies `net.vetcafe.jtetris.ui.TetrisFrame` as the main class
