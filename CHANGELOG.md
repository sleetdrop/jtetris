# Changelog

All notable user-facing changes to JTetris releases are tracked here.

## [1.1.0] - Unreleased

### Added
- GitHub Actions CI for Java 25 formatting, Checkstyle, and headless tests.
- GitHub Actions release-build workflow for standalone jar, macOS app images, and Windows app images.
- Windows app-image packaging through the Maven `windows` profile.
- Release documentation for platform-specific app archives and the standalone jar fallback.

### Changed
- Release artifact names now use the public `JTetris-<version>-<platform>` naming scheme.
- README screenshots are planned to be refreshed before the `1.1.0` release.

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
