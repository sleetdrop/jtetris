# Tasks

- [x] Confirm the current package build reports the overlapping manifest warning.
- [ ] Add a FlatLaf-specific Shade filter for `META-INF/MANIFEST.MF`.
- [ ] Verify packaging output no longer contains the warning.
- [ ] Verify the standalone JAR manifest contains the JTetris main class.
- [ ] Run the complete test suite.
- [ ] Merge the canonical spec delta and archive the change.

## Verification Notes
- Baseline: `./mvnw -Djava.awt.headless=true clean package` succeeded with 63 tests but reported that `flatlaf-3.4.1.jar` and `jtetris-1.0.0.jar` both define `META-INF/MANIFEST.MF`.
