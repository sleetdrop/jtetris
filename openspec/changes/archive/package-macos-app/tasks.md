# Tasks

- [x] Inspect the local `jpackage` plugin parameters and available icon conversion tools.
- [x] Replace the temporary icon SVG with a cross-platform JTetris app icon source.
- [x] Generate `.icns`, `.ico`, and PNG icon assets from the SVG source.
- [x] Fix the Maven `mac` profile so `mvn -Pmac clean package` builds the macOS app image.
- [x] Add a macOS `Info.plist` resource override that omits unused privacy usage strings.
- [x] Update project documentation for the packaging command and icon asset locations.
- [x] Verify with `mvn clean test`, `mvn -Pmac clean package`, and an `Info.plist` check.

## Verification Notes
- `mvn clean test` passed locally on 2026-06-15: 42 tests, 0 failures, 0 errors, 0 skipped.
- `mvn -Pmac clean package` passed locally on 2026-06-15: 42 tests, 0 failures, 0 errors, 0 skipped, and output ended with `BUILD SUCCESS`.
- The packaging warning was `Trying to remove destination /Users/jiangyuan/IdeaProjects/tetris/target/dist`; this is expected because the profile sets `removeDestination=true` so repeated packaging runs can replace the previous app image.
- Verified `target/dist/JTetris.app` exists with:
  - `Contents/app/jtetris-1.0-SNAPSHOT.jar`
  - `Contents/app/lib/flatlaf-3.4.1.jar`
  - `Contents/Resources/JTetris.icns`
- Verified `Info.plist` contains `CFBundleIdentifier=net.vetcafe.jtetris`, `CFBundleIconFile=JTetris.icns`, and version `1.0.0`.
- Added a filtered `packaging/macos/Info.plist` resource override and verified `target/dist/JTetris.app/Contents/Info.plist` does not contain `NSMicrophoneUsageDescription`, `NSCameraUsageDescription`, or `NSLocation...` usage keys.
- `plutil -lint packaging/macos/Info.plist target/dist/JTetris.app/Contents/Info.plist` passed.
