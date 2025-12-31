# Tetris (Java Swing) — 简体中文简介

本项目是一个精简的 Tetris（俄罗斯方块）实现，用于学习 Java、Swing UI 以及基本的游戏循环与计分机制。游戏界面与代码保持英文，本文档为中文说明。

## 快速开始
```bash
mvn clean package
java -jar target/tetris-1.0-SNAPSHOT.jar
```

## 项目结构
- 核心代码：`src/tetris`（模型、UI、计分）
- 构建：`pom.xml`（Java 17）
- 文档（中文）：`doc/overview_zh.md`、`doc/algorithms_zh.md`

## 文档
- [概览](doc/overview_zh.md)
- [算法](doc/algorithms_zh.md)

## 控制键
- 移动：← / →
- 软降：↓
- 旋转：↑ 或 Z
- 直落：Space
- 暂停/继续：P
- 重开：R
- 查看排行榜：L（或菜单）
- 退出：Esc（或菜单）

## 计分
本地最佳分数保存在 `~/.tetris_scores.properties`。游戏结束时可以选择将分数记到已有或新用户名，仅保留每个用户的最高分。
