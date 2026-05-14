# Optimization Roadmap (Competition-oriented)

## Priorities
- `P0`: fairness, responsiveness, deterministic behavior
- `P1`: advanced scoring and richer match UI
- `P2`: tooling and long-term maintainability

## Milestones

### M1 (`P0`) Rule baseline and gap audit
- Define the target ruleset (Guideline-inspired practical subset).
- Audit current implementation in `Board`, `Tetromino`, and input loop.
- Produce gap list with severity and implementation order.
- Deliverable spec: `m1-core-rules.md`

### M2 (`P0`) Core mechanics completion
- 7-bag randomizer
- SRS rotation + wall kicks
- Hold piece behavior
- Ghost piece behavior
- Lock delay baseline
- Split specs:
  - `m2.1-7bag-randomizer.md`
  - `m2.2-srs-rotation-kicks.md`
  - `m2.3-hold-piece.md`
  - `m2.4-ghost-piece.md`
  - `m2.5-lock-delay.md`
- Context compression protocol: `m2-context-compression.md`
- Context handoff ledger: `context-pack.md`

### M3 (`P0`) Input timing and game feel
- DAS/ARR handling
- Soft drop and hard drop consistency
- Pause/restart/quit state transitions without input loss
- Split specs:
  - `m3.1-das-arr-input.md`
  - `m3.2-soft-hard-drop-consistency.md`
  - `m3.3-input-state-transitions.md`

### M4 (`P1`) Competitive scoring and panel semantics
- T-spin detection
- Combo and back-to-back
- Score event breakdown display
- Split specs:
  - `m4.1-tspin-detection.md`
  - `m4.2-combo-b2b.md`
  - `m4.3-score-breakdown-panel.md`

### M5 (`P1-P2`) Regression gates and reproducibility
- Model-layer tests for collisions, rotation, line clear, top-out
- Seeded replay hooks for deterministic debugging
- CI checks and documented quality gates
- Split specs:
  - `m5.1-model-regression-gates.md`
  - `m5.2-seeded-replay-hooks.md`
  - `m5.3-quality-gates-docs.md`

## Definition of done (per milestone)
- All child specs are `Done`.
- Acceptance checks pass.
- No unresolved `P0` regressions introduced.
- `README` and docs updated when behavior changes.

## Risk notes
- Swing timer jitter may affect precise input timing.
- Rotation edge cases can create hidden regressions.
- Score-rule upgrades require careful compatibility decisions.

