# Release 1.1.0 CI Build

## Summary
Prepare JTetris `1.1.0` for repeatable GitHub Actions validation and release artifact builds across the most useful desktop platforms.

## Motivation
The initial `1.0.0` release was packaged manually and only included an Apple Silicon macOS app image plus the runnable Java artifact. The next release should make CI validation routine, produce platform artifacts from the tagged commit, start maintaining a changelog, and explicitly double-check compatibility with `1.0.0` local score and preference data.

## Scope
- Add GitHub Actions CI for formatting, Checkstyle, and the headless test suite on Java 25.
- Add a release-build workflow for `v1.1.0` artifacts:
  - standalone runnable jar;
  - macOS Apple Silicon app image archive;
  - macOS Intel x64 app image archive;
  - Windows 11 x64 installer.
- Create a draft GitHub Release from release-tag workflow runs and upload release assets for maintainer review.
- Add Windows jpackage support using the existing Windows icon asset.
- Start `CHANGELOG.md` with `1.1.0` and prior `1.0.0` context.
- Update release and packaging documentation, including the screenshot refresh requirement.
- Verify that score storage migration and preference storage remain compatible with `1.0.0` data.

## Out Of Scope
- No gameplay, scoring, replay, input, theme, or UI behavior changes.
- No signing, notarization, ready-to-publish Release publishing, or installer support beyond Windows 11 x64.
- No dependency upgrades unless required for Java 25 CI or existing packaging behavior.
- No change to score or preference file formats.
