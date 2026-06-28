## ADDED Requirements

### Requirement: GitHub Actions CI validation
JTetris MUST validate ordinary repository changes with GitHub Actions.

#### Scenario: Validate push and pull request changes
- **Given** a push or pull request updates the repository
- **When** the CI workflow runs
- **Then** it provisions Java 25
- **And** it runs Spotless check, Checkstyle check, and the headless clean test suite.

### Requirement: GitHub Actions release build
JTetris MUST build release artifacts with GitHub Actions and prepare draft GitHub Releases for release tags.

#### Scenario: Build release artifacts from a tag or manual dispatch
- **Given** a maintainer pushes `v1.1.0` or manually dispatches the release-build workflow with version `1.1.0`
- **When** the workflow runs
- **Then** it builds versioned release artifacts from the checked-out commit
- **And** tag-triggered runs create a draft GitHub Release with the release assets attached
- **And** manually dispatched runs upload workflow artifacts for maintainer review.
