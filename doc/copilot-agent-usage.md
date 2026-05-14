# Copilot Agent Usage (Project-local)

This project uses GitHub Copilot with repository-local instructions and prompt files.

## What is project-local here
- Base instruction file: `.github/copilot-instructions.md`
- Reusable prompt files: `.github/prompts/*.prompt.md`
- Workflow source of truth: `AGENTS.md` and `doc/specs/README.md`

These settings are scoped to this repository and are intended to keep behavior stable across context resets.

## Recommended prompts
- Start a session: `.github/prompts/session-start.prompt.md`
- Start a feature task: `.github/prompts/feature-task.prompt.md`
- End and handoff: `.github/prompts/session-handoff.prompt.md`

## Minimal routine
1. Start with `session-start` prompt.
2. For non-trivial changes, use `feature-task` prompt.
3. Before context ends, use `session-handoff` prompt.

## If prompt files are not shown in UI
Some IDE builds/extensions show prompt files differently.
If not visible:
- Open the prompt file and copy its code block into chat.
- Keep the same flow (`start` -> `feature` -> `handoff`).

## Practical tips when using multiple agents
- Keep GitHub Copilot tasks scoped by file allowlist.
- Keep other agents out of the same active task branch.
- Use one commit per checklist step for easy rollback.

