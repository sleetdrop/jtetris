# Design

## Public Metadata
Use MIT as the project license and identify the copyright holder by full name, `Yuan Jiang`. Maven metadata will include license, developer, and SCM placeholders that can resolve once the GitHub repository URL is known.

## Fonts And Notices
The repository should not claim to bundle Inter fonts unless the tracked files are valid font binaries. For this release preparation, remove malformed font resources and keep the UI font loader fallback path harmless. Third-party notices should document FlatLaf, JUnit, and the former Inter-font intent clearly without making false bundled-font claims.

## Contributor Entry Points
Keep `AGENTS.md`, `CONTRIBUTING.md`, and OpenSpec docs as the agent/developer workflow. Add lightweight `SECURITY.md` and `CODE_OF_CONDUCT.md` files suitable for a learning/showcase project without over-promising formal support.

## GitHub Cleanup
Remove obsolete Copilot/java-upgrade artifacts from `.github`. Do not add GitHub Actions in this change. Future GitHub workflow work can create a separate OpenSpec change.

## OpenSpec Archive
Move completed active changes into `openspec/changes/archive/` and promote their final requirements into canonical specs under `openspec/specs/`.
