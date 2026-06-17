# release-distribution Specification

## Requirements

### Requirement: Initial Release Artifacts
JTetris MUST provide initial `1.0.0` release artifacts for Java users and Apple Silicon macOS users.

#### Scenario: Publish runnable jar
- **Given** the release tag is `v1.0.0`
- **When** the GitHub Release is created
- **Then** it includes a runnable JTetris jar built from the tagged commit
- **And** it includes any runtime dependency files needed by that jar.

#### Scenario: Publish Apple Silicon macOS app
- **Given** the release tag is `v1.0.0`
- **When** the GitHub Release is created from an Apple Silicon macOS build host
- **Then** it includes a compressed `JTetris.app` app-image suitable for Apple Silicon macOS.
