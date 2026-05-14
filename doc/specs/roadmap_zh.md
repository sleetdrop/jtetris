# 优化路线图（偏比赛可用）

## 优先级
- `P0`：公平性、输入响应、可复现
- `P1`：高阶计分与对局信息
- `P2`：工具链与长期维护能力

## 里程碑

### M1（`P0`）规则基线与差距审计
- 明确本项目目标规则（Guideline 风格的实用子集）。
- 审计当前 `Board`、`Tetromino` 与输入循环行为。
- 输出差距清单并排序。
- 对应 Spec：`m1-core-rules.md`

### M2（`P0`）核心机制补齐
- 7-bag 随机
- SRS 旋转 + wall kick
- Hold
- Ghost
- Lock delay 基线
- 子 Spec 文件：
  - `m2.1-7bag-randomizer.md`
  - `m2.2-srs-rotation-kicks.md`
  - `m2.3-hold-piece.md`
  - `m2.4-ghost-piece.md`
  - `m2.5-lock-delay.md`
- 上下文压缩协议：`m2-context-compression.md`
- 上下文交接记录：`context-pack.md`

### M3（`P0`）输入时序与手感
- DAS/ARR
- 软降/硬降一致性
- 暂停/重开/退出状态切换一致性
- 建议拆分：`m3.1` 到 `m3.3`

### M4（`P1`）竞技计分与侧栏语义
- T-Spin 判定
- Combo / Back-to-back
- 计分事件拆分展示
- 建议拆分：`m4.1` 到 `m4.3`

### M5（`P1-P2`）回归门禁与可复现
- 模型层测试（碰撞、旋转、消行、顶死）
- 种子局回放/复现辅助
- CI 检查与质量门禁文档化
- 建议拆分：`m5.1` 到 `m5.3`

## 里程碑完成定义
- 该里程碑所有子 Spec 均为 `Done`。
- 验收项全部通过。
- 未引入未解决的 `P0` 回归。
- 行为变化已同步到 README 与文档。

## 风险提示
- Swing 定时器抖动会影响输入手感一致性。
- 旋转边界用例多，容易引入隐藏回归。
- 计分规则升级需要兼容策略。

