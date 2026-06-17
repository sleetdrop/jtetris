# Tasks

- [x] Inspect current theme, palette, stage rendering, and side-panel rendering.
- [x] Create an OpenSpec delta for flat, consistent theme presentation.
- [x] Rebalance `UiTheme` light/dark surfaces and text hierarchy.
- [x] Rework `ColorPalette` and cell drawing to use flat fills and subtle outlines.
- [x] Refine side-panel spacing and preview rendering without changing gameplay content.
- [x] Add focused automated validation for theme/palette invariants.
- [x] Verify with `mvn clean test` and manual light/dark launch or screenshot checks.

## Verification Notes
- `mvn -Djava.awt.headless=true -Dtest=ThemeVisualsTest test` passed locally on 2026-06-16: 2 tests, 0 failures, 0 errors, 0 skipped.
- `mvn -Djava.awt.headless=true clean test` passed locally on 2026-06-16: 44 tests, 0 failures, 0 errors, 0 skipped.
- `mvn -Djava.awt.headless=true package` passed locally on 2026-06-16 and produced `target/jtetris-1.0-SNAPSHOT.jar`.
- Plain `mvn test` without headless crashed the forked JVM with `Abort trap: 6` while loading `ThemeVisualsTest`; rerunning with `-Djava.awt.headless=true` isolated this as a local graphics-environment issue and exposed normal assertions.
- Manual light/dark screenshots reviewed on 2026-06-16. The flat block direction is working; grid and divider contrast were reduced one more step so empty stage cells and side-panel separators do not compete with pieces.
- After the grid adjustment, `mvn -Djava.awt.headless=true -Dtest=ThemeVisualsTest test` passed locally on 2026-06-16: 2 tests, 0 failures, 0 errors, 0 skipped.
- After the grid adjustment, `mvn -Djava.awt.headless=true clean test` passed locally on 2026-06-16: 44 tests, 0 failures, 0 errors, 0 skipped.
- Disabled FlatLaf native helper loading by default with `flatlaf.useNativeLibrary=false` so Java 26 startup does not emit restricted native-access warnings.
- `java -Djava.awt.headless=true -jar target/jtetris-1.0-SNAPSHOT.jar 2>&1` no longer prints `restricted method`, `System::load`, or `NativeLibrary` warnings; it reaches the expected headless-only `HeadlessException`.
- `mvn -Djava.awt.headless=true -Pmac package` passed locally on 2026-06-16: 44 tests, 0 failures, 0 errors, 0 skipped; jpackage includes `--java-options -Dflatlaf.useNativeLibrary=false`.
- Added overlay layout regression coverage. `mvn -Djava.awt.headless=true -Dtest=StageOverlayHostLayoutTest test` first failed because simple overlays used a tall fixed panel, then passed after making overlay height content-based and adding action-row bottom padding.
- After overlay layout changes, `mvn -Djava.awt.headless=true clean test` passed locally on 2026-06-16: 46 tests, 0 failures, 0 errors, 0 skipped.
- Refined simple Game Over overlays again after screenshot review: compact overlays now use vertical content grouping and a narrower max width so the message and action buttons stay visually grouped instead of spreading to the lower-right corner.
- After compact overlay refinements, `mvn -Djava.awt.headless=true -Dtest=StageOverlayHostLayoutTest test`, `mvn -Djava.awt.headless=true clean test`, and `mvn -Djava.awt.headless=true package` passed locally on 2026-06-16.
- Fixed missing bottom border during overlay enter animation by moving the whole overlay surface instead of translating painting inside the surface clip. Added a regression test that failed before the fix and now verifies the bottom border pixel is painted during enter animation.
- After the border fix, `mvn -Djava.awt.headless=true -Dtest=StageOverlayHostLayoutTest#enteringOverlayStillPaintsBottomBorderInsideSurfaceBounds test`, `mvn -Djava.awt.headless=true clean test`, and `mvn -Djava.awt.headless=true package` passed locally on 2026-06-17.
- Final pre-commit verification on 2026-06-17: `mvn -Djava.awt.headless=true clean test` passed with 47 tests, 0 failures, 0 errors, 0 skipped.
