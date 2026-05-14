# M2 Context Compression Protocol

Use this protocol **after each M2 sub-spec is completed** to keep context small and precise.

## Purpose
- Reduce prompt/context size for next task.
- Preserve only high-value implementation knowledge.
- Make handoff deterministic between consecutive sub-specs.

## Mandatory outputs after each sub-spec
1. Update the finished sub-spec status to `Done`.
2. Create or update `doc/specs/context-pack.md` with one new entry.
3. Add links to the merged PR/commit and verification evidence.

## Context pack entry template

```markdown
## <date> - <Spec ID>
- Decision summary (3 bullets max):
- Files changed (exact paths):
- Public behavior changes:
- Acceptance evidence:
  - Manual:
  - Automated:
- Regressions checked:
- Known risks left:
- Next spec input (what the next task needs to know):
```

## Compression rules
- Keep each entry under 20 lines.
- Avoid repeating code blocks; reference file paths and symbols.
- Include only deltas from previous spec, not full restatement.
- If behavior is unchanged, explicitly write `No public behavior change`.

## Recommended implementation rhythm
1. Finish one checklist item or one sub-spec scope.
2. Run verification (`mvn clean test` and targeted manual checks).
3. Update context pack and mark spec status.
4. Start next sub-spec with only:
   - current spec file,
   - latest context pack entry,
   - touched source files.

