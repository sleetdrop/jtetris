# Spec 工作流（中文）

本目录用于定义项目的 Spec 机制，确保优化按小步、可验证方式推进。

## 目标
- 将优化任务切分为可评审的小步变更。
- 所有非简单改动都绑定明确 Spec。
- 合并前必须有清晰验收证据。

## 状态模型
- `Draft`：草稿，尚未确认范围。
- `Approved`：范围确认，可进入开发。
- `In-Progress`：正在实现。
- `Done`：已合并并验证完成。
- `Deferred`：暂缓。

## Spec 粒度（已选）
采用**混合粒度**：
- 里程碑 Spec（说明边界与目标）。
- 里程碑下多个小型实现 Spec。

对应你选择的 **C**。

## 验收策略（已选）
采用**严格门禁**：
- 必须有手工验证记录。
- 在可行情况下必须有自动化检查（当前至少 `mvn clean test`）。
- 无验收证据的 PR 不合并。

对应你选择的 **C**。

## 命名规则
- 里程碑：`mX-<topic>.md`（例如 `m1-core-rules.md`）
- 子任务：`mX.Y-<topic>.md`（例如 `m2.1-7bag-randomizer.md`）

## 每个 Spec 必备章节
1. `Spec ID`
2. `Status`
3. `Scope`
4. `Out of scope`
5. `Design notes`
6. `Implementation checklist`
7. `Acceptance criteria`
8. `Verification`
9. `Rollback plan`

## 执行流程
1. 先在本目录新增/更新 Spec。
2. 创建 GitHub Issue，并引用 `Spec ID`。
3. 尽量做到一个 PR 只完成一个小 checklist 项。
4. 在 Spec 与 PR 中填写验证结果。
5. 合并并验证后再将 Spec 标记为 `Done`。
6. 按 `m2-context-compression.md` 执行上下文压缩，并在 `context-pack.md` 追加一条记录。

