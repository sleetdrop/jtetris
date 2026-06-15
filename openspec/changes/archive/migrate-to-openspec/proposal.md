# Migrate JTetris Maintenance to OpenSpec

## Why
JTetris previously used a custom `doc/specs` workflow plus GitHub Copilot-specific instructions and prompt files. That worked for early solo-agent development, but it makes the project feel tied to one GitHub-centric toolchain.

The project now needs an agent-neutral workflow that works well for Codex first while remaining easy for other agents and human contributors to follow.

## What Changes
- Add OpenSpec project guidance under `openspec/project.md`.
- Add OpenSpec agent instructions under `openspec/AGENTS.md`.
- Add a canonical `project-workflow` capability under `openspec/specs/project-workflow/spec.md`.
- Move new non-trivial work to `openspec/changes/<change-id>/`.
- Reframe `doc/specs` as historical context, not the primary workflow.
- Remove or neutralize Copilot prompt files and GitHub issue templates as required workflow entry points.
- Update root docs so Codex and other agents start from `AGENTS.md` and OpenSpec.

## Impact
- New changes use OpenSpec structure.
- Historical specs remain readable and are not mass-converted.
- Java code, Maven configuration, gameplay behavior, score persistence, replay format, and UI behavior are unchanged.
- GitHub can still host PRs, but GitHub-specific files no longer define the agent workflow.
