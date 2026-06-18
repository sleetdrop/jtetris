# Tasks

- [x] Confirm the current package build reports the overlapping manifest warning.
- [x] Add a FlatLaf-specific Shade filter for `META-INF/MANIFEST.MF`.
- [x] Verify packaging output no longer contains the warning.
- [x] Verify the standalone JAR manifest contains the JTetris main class.
- [x] Run the complete test suite.
- [ ] Merge the canonical spec delta and archive the change.

## Verification Notes
- Baseline: `./mvnw -Djava.awt.headless=true clean package` succeeded with 63 tests but reported that `flatlaf-3.4.1.jar` and `jtetris-1.0.0.jar` both define `META-INF/MANIFEST.MF`.
- Fixed package: `./mvnw -Djava.awt.headless=true clean package` succeeded with 63 tests and no `overlapping resource` warning.
- JAR structure: `jar tf target/jtetris-1.0.0-standalone.jar` reported exactly one `META-INF/MANIFEST.MF`.
- Manifest: `unzip -p target/jtetris-1.0.0-standalone.jar META-INF/MANIFEST.MF` reported `Main-Class: net.vetcafe.jtetris.ui.TetrisFrame`.
- Regression: `./mvnw -Djava.awt.headless=true clean test` passed 63 tests, 0 failures, 0 errors, 0 skipped.
