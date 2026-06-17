# Prepare GitHub Release

## Summary
Prepare JTetris for a public GitHub release by cleaning repository metadata, licensing, contributor-facing documentation, and completed OpenSpec changes.

## Motivation
JTetris is moving from a local learning project toward a public Java showcase repository. The repository already builds and has project documentation, but public release needs clearer ownership metadata, license attribution, community files, dependency notices, a contributor-friendly Maven wrapper, and removal of obsolete GitHub/Copilot artifacts.

## Scope
- Update license ownership and Maven metadata for public repository hosting.
- Add community-facing files for security reporting and conduct expectations.
- Add third-party notices for bundled/runtime dependencies and assets.
- Make local build/test commands easier for new contributors with Maven Wrapper.
- Clean obsolete GitHub/Copilot maintenance artifacts from the repository.
- Archive completed OpenSpec changes into canonical specs.
- Prepare README structure for public release, leaving screenshot insertion as a later manual-input step.

## Out Of Scope
- No GitHub Actions, release automation, signing, notarization, or binary publishing workflow.
- No gameplay, scoring, replay, UI behavior, or package namespace changes.
- No screenshot generation or README screenshot embedding until the user provides screenshots.
