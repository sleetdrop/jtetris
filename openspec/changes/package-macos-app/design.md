# Design

## Packaging
JTetris will continue to use Maven for local validation and packaging. The existing `mac` profile remains the documented entry point, but its `jpackage` plugin configuration will be corrected so `mvn -Pmac clean package` creates a macOS app image.

The app image will use the existing Java entry point `net.vetcafe.jtetris.ui.TetrisFrame` and the packaged jar produced by Maven. The profile should reference the generated macOS `.icns` icon asset.

The macOS build should pass a filtered resource directory to `jpackage` so `Info.plist` metadata is owned by the project. The override keeps the bundle identifier, executable, icon, and version aligned with Maven properties and omits privacy usage strings for capabilities JTetris does not use.

## Icon Assets
The source of truth is `art/icon.svg`. It should be simple enough to stay legible at small sizes and deterministic enough for agents to edit without binary design tools.

Generated assets live alongside the source:
- `art/icon.icns` for macOS packaging.
- `art/icon.ico` for Windows usage.
- `art/icons/icon-*.png` for Linux desktop/AppImage-style packaging and general reuse.

The icon should avoid embedded text so it scales cleanly and does not require localization.

## Verification
Automated verification remains `mvn clean test`. Packaging verification is `mvn -Pmac clean package` on macOS, followed by checking that the app image and icon assets exist.
