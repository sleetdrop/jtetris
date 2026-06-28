# Project Workflow Specification

## Purpose
Define the agent-neutral maintenance workflow for JTetris after the OpenSpec migration.

## Requirements

### Requirement: Lightweight workflow is the default
Routine JTetris development MUST use the lightweight project workflow unless the
change needs durable traceability.

#### Scenario: Agent starts routine work
- **Given** an agent is asked to implement a localized bug fix, small refactor, test, or documentation edit
- **When** the work does not alter a long-lived behavior contract, product decision, architecture boundary, project workflow, build/release/dependency policy, or compatibility expectation
- **Then** the agent proceeds with the lightweight workflow in `AGENTS.md`
- **And** the agent does not create an OpenSpec change directory

### Requirement: OpenSpec is reserved for durable decisions
JTetris changes that need future traceability MUST use an OpenSpec change
directory.

#### Scenario: Agent starts traceability-sensitive work
- **Given** an agent is asked to change a long-lived behavior contract, product decision, architecture boundary, project workflow, build/release/dependency policy, or compatibility expectation
- **When** no active OpenSpec change covers the request
- **Then** the agent creates `openspec/changes/<change-id>/proposal.md`
- **And** the agent creates `openspec/changes/<change-id>/tasks.md`
- **And** the agent adds spec deltas under `openspec/changes/<change-id>/specs/<capability>/spec.md`

### Requirement: Agents declare scope before editing
Agents MUST restate the current goal, exact file allowlist, and out-of-scope
items before multi-file, risky, workflow-affecting, or OpenSpec-backed edits.

#### Scenario: Agent prepares to edit files
- **Given** the change is multi-file, risky, workflow-affecting, or OpenSpec-backed
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
Agents MUST run the project validation command under the project Java baseline and record the result before marking work complete.

#### Scenario: Agent completes a change
- **Given** implementation and documentation edits are complete
- **When** the agent prepares final handoff
- **Then** the agent runs `./mvnw -Djava.awt.headless=true clean test` with Java 25 available as the active JDK
- **And** records the pass or fail result in the final handoff
- **And** records the pass or fail result in the active OpenSpec change when one exists
- **And** records any manual verification needed for UI or workflow changes

### Requirement: Historical specs are preserved
The legacy `doc/specs` directory MUST remain available as historical context during and after migration.

#### Scenario: Agent needs prior implementation context
- **Given** a new OpenSpec change relates to previous gameplay, UI, replay, or quality-gate work
- **When** the agent needs historical decisions
- **Then** the agent may read `doc/specs` and `doc/specs/context-pack.md`
- **And** the agent treats those files as historical context rather than the required location for new changes
