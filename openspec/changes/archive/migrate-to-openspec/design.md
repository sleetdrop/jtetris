# Design: OpenSpec Migration

## Current State
- Root `AGENTS.md` defines strict mode, file allowlists, per-step commits, and legacy `doc/specs` handoff requirements.
- `doc/specs` contains useful historical specs, roadmap, templates, and `context-pack.md`.
- `.github/copilot-instructions.md` and `.github/prompts/*.prompt.md` make GitHub Copilot the documented agent entry point.
- `doc/copilot-agent-usage.md` repeats the Copilot-centered workflow.
- `openspec/` existed as empty directories before this migration.

## Target State
- Root `AGENTS.md` stays the repository-wide entry point.
- `openspec/project.md` describes project context, commands, constraints, and the OpenSpec workflow.
- `openspec/AGENTS.md` gives agents specific rules for creating and executing OpenSpec changes.
- `openspec/specs/project-workflow/spec.md` captures the canonical workflow requirements.
- `doc/specs` is preserved as historical memory and no longer receives new feature specs by default.
- GitHub-specific files are optional wrappers only; Copilot prompts are removed from the primary workflow.

## Migration Strategy
Use a bridge migration instead of converting every historical spec:
1. Create a legacy migration spec so this change respects the previous strict workflow.
2. Add OpenSpec baseline files and a migration proposal.
3. Update root documentation to point agents and contributors to OpenSpec.
4. Remove or neutralize old Copilot/GitHub-specific workflow entry points.
5. Run `mvn clean test` to confirm no accidental build impact.

## Non-Goals
- No Java source changes.
- No Maven dependency changes.
- No full rewrite of historical milestone specs.
- No OpenSpec CLI bootstrapping or network dependency.

## Risk Management
- Historical context loss is avoided by preserving `doc/specs`.
- Agent confusion is reduced by making root `AGENTS.md` and `openspec/project.md` the only primary starting points.
- GitHub hosting remains possible through a generic PR template, but issue templates and Copilot prompts stop defining the process.
