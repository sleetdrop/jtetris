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

### M6 (`P1`) UI cohesion and feedback polish
- Theme + typography unification (light/dark + bundled Inter)
- Contrast/readability polish for board and dialogs
- LCD-style line-clear flash feedback (non-blocking)
- Split specs:
  - `m6.1-ui-theme-and-typography.md`
  - `m6.2-ui-visual-polish.md`
  - `m6.3-line-clear-flash.md`

### M7 (`P1-P2`) UX hardening and tooling automation
- Runtime theme switching without app restart
- Flash effect tuning knobs for readability across cell sizes
- Replay persistence (export/import) on top of seeded replay hooks
- CI automation for quality-gate checks documented in `doc/quality-gates.md`
- Optional competitive timing tuning (lock-reset cap, level-linked lock delay)
- Planned split specs:
  - `m7.1-runtime-theme-switching.md`
  - `m7.2-line-clear-flash-tuning.md`
  - `m7.3-replay-persistence.md`
  - `m7.4-quality-gates-ci-automation.md`
  - `m7.5-competitive-timing-tuning.md`

### M8 (`P1`) Cross-platform UI modernization and in-stage prompts
- Adopt FlatLaf for consistent native Swing component appearance across platforms.
- Replace primary `JOptionPane` gameplay prompts with in-stage overlay panels.
- Keep keyboard-first interaction and current score persistence semantics.
- Planned split specs:
  - `m8.1-flatlaf-bootstrap-and-theme-bridge.md`
  - `m8.2-stage-overlay-foundation.md`
  - `m8.3-stage-gameover-and-score-entry.md`
  - `m8.4-stage-leaderboard-and-exit-confirm.md`
  - `m8.5-dialog-retirement-and-polish.md`

## Definition of done (per milestone)
- All child specs are `Done`.
- Acceptance checks pass.
- No unresolved `P0` regressions introduced.
- `README` and docs updated when behavior changes.

## Risk notes
- Swing timer jitter may affect precise input timing.
- Rotation edge cases can create hidden regressions.
- Score-rule upgrades require careful compatibility decisions.

