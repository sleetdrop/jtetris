# M1 核心规则 Spec

## Spec ID
`M1-CORE-RULES`

## Status
`Approved`

## Scope
- 定义本项目“可用于比赛”的基础规则。
- 对照当前实现做差距分析。
- 为 M2、M3 生成按优先级排序的实施队列。

## Out of scope
- 全量 UI 重构
- 联网对战
- 云端排行榜

## Design notes
目标是 Guideline 风格的实用子集，并结合 Swing 实现约束：
- 公平随机（7-bag）
- 可预测旋转语义（SRS + kicks）
- 竞技动作集合（hold、ghost、软降/硬降）
- 稳定的一致性输入循环

## Implementation checklist
- [ ] 文档化本项目使用的术语与规则定义。
- [ ] 审计 `src/tetris/model/Board.java` 当前机制。
- [ ] 审计 `src/tetris/ui/TetrisFrame.java` 当前输入处理。
- [ ] 产出差距矩阵：`Current`/`Target`/`Severity`/`Owner`。
- [ ] 将 M2、M3 拆成可验收的子 Spec。

## Acceptance criteria
- 差距矩阵完整并已提交。
- 每个 `P0` 差距都关联一个后续子 Spec ID。
- 旋转、随机、输入时序的术语定义无歧义。

## Verification
- 代码与文档对照手工审查。
- 进行一次只关注“术语是否清晰”的审阅。

## Rollback plan
- 若范围过大，将本 Spec 拆分为两个：
  - `M1A` 术语/规则词汇表
  - `M1B` 实现差距审计

