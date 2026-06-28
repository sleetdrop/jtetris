# Design

## CI Validation
Use a lightweight CI workflow for ordinary pushes and pull requests. The workflow provisions Java 25 with `actions/setup-java` using the Temurin distribution, then runs the same review gate documented for the repository: Spotless check, Checkstyle check, and a headless clean test run.

## Release Build Matrix
Use a separate manually-triggerable and tag-triggered workflow for release artifacts. The workflow builds artifacts from the checked-out commit. On tag-triggered runs, it creates a draft GitHub Release and uploads release assets for maintainer review.

The release matrix prioritizes stable GitHub-hosted runners:
- `macos-15` for macOS arm64.
- `macos-15-intel` for macOS x64.
- `windows-2025` for Windows x64.

Windows arm64 is not included in the `1.1.0` release matrix because release validation found no Temurin Java 25 arm64 package available on the hosted runner. The standalone jar remains the Windows arm64 fallback.

## Maven Packaging
Keep the existing `mac` Maven profile as the local macOS packaging entry point. Add a sibling `windows` profile that stages the same application jar and runtime dependencies into `target/jpackage-input`, then runs `jpackage` with the Windows `.ico` asset.

Native artifacts remain platform-built, not cross-compiled. macOS app images are produced on macOS runners, and the Windows installer is produced on a Windows runner with WiX available to `jpackage`. The standalone jar remains the fallback for platforms without a native artifact.

## Artifact Naming
Release build output uses versioned, user-facing names:
- `JTetris-1.1.0.jar`
- `JTetris-1.1.0-macos-aarch64.zip`
- `JTetris-1.1.0-macos-x64.zip`
- `JTetris-1.1.0-windows-x64.exe`

The Maven project version and `jpackage.appVersion` move to `1.1.0` for this release preparation.

## Compatibility Check
Compatibility is validated through existing focused tests for score path selection, legacy score migration, score format tolerance, and user preference storage. The release task also records the compatibility boundary in docs: `scores.properties` and `preferences.properties` remain Java properties files under the platform data root, and the legacy `~/.tetris_scores.properties` migration remains one-time and non-destructive on failure.

## Screenshots
README screenshots should be refreshed after release build behavior and README text stabilize. The expected replacement files are `doc/images/jtetris-light.png` and `doc/images/jtetris-dark.png`.
