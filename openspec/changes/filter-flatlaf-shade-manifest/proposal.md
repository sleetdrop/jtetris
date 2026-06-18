# Filter FlatLaf Manifest During Shading

## Summary
Exclude FlatLaf's dependency manifest from the standalone JAR shading input so Maven packaging no longer reports an irrelevant overlapping `META-INF/MANIFEST.MF` resource.

## Motivation
Both the project JAR and FlatLaf dependency JAR contain a manifest. The Shade plugin already creates the standalone JAR manifest through `ManifestResourceTransformer`, so copying FlatLaf's manifest is unnecessary and produces a warning on every package build.

## Scope
- Add a Shade filter scoped specifically to `com.formdev:flatlaf`.
- Exclude only `META-INF/MANIFEST.MF` from that dependency.
- Verify the warning disappears.
- Verify the standalone JAR retains the JTetris `Main-Class`.

## Out Of Scope
- No dependency or plugin upgrades.
- No changes to application code, runtime behavior, release assets, or macOS packaging.
- No global exclusion of manifests from other dependencies.
