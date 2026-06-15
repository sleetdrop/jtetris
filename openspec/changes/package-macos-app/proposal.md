# Package macOS App

## Summary
Make JTetris produce a standard macOS application image and replace the temporary icon with a cross-platform app icon asset set.

## Motivation
The current `mvn -Pmac clean package` flow is documented but does not complete with the configured `jpackage` plugin settings. The repository also only has a temporary SVG icon, which is not enough for native app packaging across macOS, Windows, and Linux.

## Scope
- Fix the Maven `mac` profile so it builds a macOS app image for `JTetris`.
- Maintain a deterministic SVG source icon under `art/`.
- Generate platform icon assets from that SVG source:
  - macOS `.icns`
  - Windows `.ico`
  - Linux-friendly PNG sizes
- Override macOS app metadata so the generated bundle does not advertise unused privacy capabilities.
- Document the packaging command and generated asset locations.

## Out Of Scope
- No gameplay, scoring, replay, input, or Swing UI behavior changes.
- No installer/notarization/signing workflow.
- No Windows or Linux native package build automation beyond providing reusable icon assets.
