# Tasks

- [x] Add Maven shade configuration for a standalone jar.
- [x] Update README jar command to use the standalone jar.
- [x] Update release distribution spec to require directly executable artifacts.
- [x] Verify Maven test and macOS package builds.
- [x] Replace GitHub Release Java assets with the standalone fat jar.

## Verification Notes

- `./mvnw -Djava.awt.headless=true clean test` passed on 2026-06-17 with 47 tests, 0 failures, 0 errors, 0 skipped.
- `./mvnw -Pmac -Djava.awt.headless=true clean package` initially failed in the sandbox because Maven Shade Plugin needed to download plugin dependencies and write to `~/.m2`; rerunning with approval passed on 2026-06-17 with 47 tests, 0 failures, 0 errors, 0 skipped.
- `target/jtetris-1.0.0-standalone.jar` was built and copied to `target/release/JTetris-1.0.0.jar`.
- The standalone jar manifest contains `Main-Class: net.vetcafe.jtetris.ui.TetrisFrame`.
- The standalone jar contains both `net/vetcafe/jtetris/ui/TetrisFrame.class` and `com/formdev/flatlaf/FlatLaf.class`.
- `target/release/JTetris-1.0.0-macos-aarch64.zip` was rebuilt from `target/dist/JTetris.app`.
- `target/dist/JTetris.app/Contents/MacOS/JTetris` and the bundled runtime `libjli.dylib` were verified as `arm64`.
