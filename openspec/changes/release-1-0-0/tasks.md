# Tasks

- [x] Set Maven project release version to `1.0.0`.
- [x] Verify with `./mvnw clean test`.
- [x] Build jar and Apple Silicon macOS app artifacts.
- [ ] Tag and push `v1.0.0`.
- [ ] Create GitHub Release with jar and macOS artifacts.

## Verification Notes

- `./mvnw -Djava.awt.headless=true clean test` passed on 2026-06-17 with 47 tests, 0 failures, 0 errors, 0 skipped.
- `./mvnw -Pmac -Djava.awt.headless=true clean package` passed on 2026-06-17 with 47 tests, 0 failures, 0 errors, 0 skipped.
- Built `target/release/jtetris-1.0.0.jar`.
- Built `target/release/JTetris-1.0.0-java17.zip` with `jtetris-1.0.0.jar` and `lib/flatlaf-3.4.1.jar`.
- Built `target/release/JTetris-1.0.0-macos-aarch64.zip` from `target/dist/JTetris.app`; app launcher and runtime library were verified as `arm64`.
