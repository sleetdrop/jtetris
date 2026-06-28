# Changelog

All notable user-facing changes to JTetris releases are tracked here.

## [1.1.0] - 2026-06-28

### Added
- Active Endless Marathon session time, shown beside score, level, and lines.
- Modern in-window Help content covering controls, Hold, Next, Ghost, scoring, combos, back-to-back, and T-Spins.
- Local score management in platform application data directories, including safe migration from the legacy `~/.tetris_scores.properties` file.
- Leaderboard player deletion and remembered last score-entry user.
- Remembered theme selection through local `preferences.properties`.
- Rolling application logs, configurable log levels, input diagnostics, and a debug-only EDT responsiveness watchdog.
- Headless gameplay input harness and regression coverage for production input operations.
- Java 25 CI quality gates with Spotless, Checkstyle, and the headless test suite.
- Release automation that creates a draft GitHub Release with standalone jar, macOS app images, and a Windows 11 x64 installer.

### Changed
- Upgraded the project baseline to Java 25 LTS.
- Redesigned the side panel with clearer stats, Hold, and Next previews.
- Refined ghost piece rendering so the landing projection is visually distinct from locked blocks.
- Standardized Endless Marathon timing rules, including active-time pause behavior and level-based gravity.
- Hardened keyboard input timing to avoid horizontal and soft-drop repeat bursts.
- Tuned horizontal DAS from real player trace evidence to better separate taps from deliberate holds.
- Restored correct system theme behavior when using Auto mode.
- Improved release artifact naming with the public `JTetris-<version>-<platform>` scheme.
- README screenshots were refreshed for the `1.1.0` release.
- Windows arm64 is documented as a standalone-jar fallback for `1.1.0` because Temurin Java 25 arm64 was unavailable on the hosted runner.

### Fixed
- Filtered conflicting dependency manifests from shaded runnable jars.
- Tolerated malformed local score files without losing usable score data.
- Made path-related tests portable across Unix-like and Windows CI hosts.
- Trimmed Swing repaint work for better UI responsiveness.

### Documentation
- Added reader-first Java style guidance and Java 25 quality-gate documentation.
- Clarified that JTetris is Guideline-inspired rather than a claim of full official Tetris Guideline compliance.
- Kept OpenSpec usage selective for durable behavior, release, workflow, and compatibility decisions.

### Compatibility
- Local score data remains in `scores.properties` under the platform application data directory.
- Existing legacy `~/.tetris_scores.properties` migration remains one-time and non-destructive on failed writes.
- Local theme preferences remain in `preferences.properties` beside score data.

## [1.0.0] - 2026-06-17

### Added
- Initial public release.
- Standalone runnable jar.
- Apple Silicon macOS app image.
- Endless Marathon gameplay with local scores, theme support, and core modern Tetris mechanics.
