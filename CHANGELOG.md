# Changelog

All notable user-facing changes to JTetris releases are tracked here.

## [1.1.0] - 2026-06-28

### Added
- GitHub Actions CI for Java 25 formatting, Checkstyle, and headless tests.
- GitHub Actions release-build workflow that creates a draft GitHub Release with standalone jar, macOS app images, and a Windows installer.
- Windows 11 x64 installer packaging through the Maven `windows` profile.
- Release documentation for platform-specific app archives and the standalone jar fallback.
- Cross-platform CI coverage for path-related tests that previously only ran on Unix-like hosts.

### Changed
- Release artifact names now use the public `JTetris-<version>-<platform>` naming scheme.
- README screenshots were refreshed for the `1.1.0` release.
- Windows arm64 is documented as a standalone-jar fallback for `1.1.0` because Temurin Java 25 arm64 was unavailable on the hosted runner.

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
