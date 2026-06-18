# Design: FlatLaf Shade Manifest Filter

## Decision
Add a second `maven-shade-plugin` filter with artifact selector `com.formdev:flatlaf` and exclude `META-INF/MANIFEST.MF`.

The existing general filter remains responsible for excluding `module-info.class`. The existing `ManifestResourceTransformer` remains the single authority for the standalone JAR manifest and continues to set `Main-Class` to `net.vetcafe.jtetris.ui.TetrisFrame`.

## Verification
1. Capture the current `./mvnw -Djava.awt.headless=true clean package` output and confirm it contains the overlapping manifest warning.
2. Apply the dependency-specific filter.
3. Re-run packaging and confirm the warning text is absent.
4. Inspect `target/jtetris-1.0.0-standalone.jar` and confirm it contains one `META-INF/MANIFEST.MF`.
5. Read that manifest and confirm the expected `Main-Class`.
6. Run the complete test suite.

## File Allowlist
- `pom.xml`
- `openspec/changes/filter-flatlaf-shade-manifest/**`
- `openspec/specs/release-distribution/spec.md`

Changes outside this list require explicit approval.
