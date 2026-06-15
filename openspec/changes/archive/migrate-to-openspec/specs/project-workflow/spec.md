# Project Workflow Delta

## ADDED Requirements

### Requirement: OpenSpec is the default workflow
New non-trivial JTetris changes MUST start from an OpenSpec change directory.

#### Scenario: Agent starts non-trivial work
- **Given** an agent is asked to implement a non-trivial behavior, UI, build, dependency, or documentation workflow change
- **When** no active OpenSpec change covers the request
- **Then** the agent creates `openspec/changes/<change-id>/proposal.md`
- **And** the agent creates `openspec/changes/<change-id>/tasks.md`
- **And** the agent adds spec deltas under `openspec/changes/<change-id>/specs/<capability>/spec.md`

### Requirement: Agents declare scope before editing
Agents MUST restate the current goal, exact file allowlist, and out-of-scope items before editing files.

#### Scenario: Agent prepares to edit files
- **Given** an active OpenSpec change exists
- **When** the agent is ready to modify repository files
- **Then** the agent states the goal in one or two lines
- **And** the agent lists exact paths that may be edited
- **And** the agent lists out-of-scope items that must not be touched

### Requirement: Changes stay small and reviewable
Agents MUST complete one small task at a time and avoid unrelated refactors.

#### Scenario: Active task is narrow
- **Given** `tasks.md` contains multiple checklist items
- **When** the agent begins implementation
- **Then** the agent works on the next smallest relevant checklist item
- **And** the agent does not combine unrelated fixes, renames, moves, or dependency changes with that item

### Requirement: Verification evidence is required
Agents MUST run the project validation command and record the result before marking work complete.

#### Scenario: Agent completes a change
- **Given** implementation and documentation edits are complete
- **When** the agent prepares final handoff
- **Then** the agent runs `mvn clean test`
- **And** records the pass or fail result in the active OpenSpec change
- **And** records any manual verification needed for UI or workflow changes

### Requirement: Historical specs are preserved
The legacy `doc/specs` directory MUST remain available as historical context during and after migration.

#### Scenario: Agent needs prior implementation context
- **Given** a new OpenSpec change relates to previous gameplay, UI, replay, or quality-gate work
- **When** the agent needs historical decisions
- **Then** the agent may read `doc/specs` and `doc/specs/context-pack.md`
- **And** the agent treats those files as historical context rather than the required location for new changes
