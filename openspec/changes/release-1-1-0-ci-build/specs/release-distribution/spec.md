## MODIFIED Requirements

### Requirement: Release Artifacts
JTetris MUST provide directly executable release artifacts for Java users and the stable native app-image platforms covered by the release build workflow.

#### Scenario: Publish standalone runnable jar for 1.1.0
- **Given** the release tag is `v1.1.0`
- **When** the release-build workflow runs
- **Then** it builds `JTetris-1.1.0.jar` from the tagged commit
- **And** the jar includes its runtime library dependencies.

#### Scenario: Publish stable native app images for 1.1.0
- **Given** the release tag is `v1.1.0`
- **When** the release-build workflow runs on stable GitHub-hosted runner targets
- **Then** it builds compressed app-image archives for macOS arm64, macOS x64, and Windows x64.

#### Scenario: Publish best-effort Windows arm64 app image
- **Given** GitHub-hosted Windows arm64 runners are a preview capability
- **When** the release-build workflow runs the Windows arm64 job
- **Then** that job may produce `JTetris-1.1.0-windows-aarch64.zip`
- **And** failure of that preview job does not block stable release artifacts.

#### Scenario: Avoid non-runnable release assets
- **Given** a user downloads a project-provided release artifact
- **When** the user extracts the artifact if it is an archive
- **Then** the artifact is directly executable on its target platform or is the standalone runnable jar.

### Requirement: Release changelog
JTetris MUST maintain a changelog for public releases starting with `1.1.0`.

#### Scenario: Document 1.1.0 release changes
- **Given** `1.1.0` is prepared for release
- **When** a user opens `CHANGELOG.md`
- **Then** it summarizes notable release changes
- **And** it keeps `1.0.0` as the initial release reference.
