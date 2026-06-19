# Fix Runtime Auto Theme Resolution

## Problem
When JTetris starts on a light system, switching to Dark and then selecting Auto
keeps the application dark. Auto currently infers system appearance from the
active Swing look and feel, which is already FlatDarkLaf after the manual switch.

## Goal
Keep Auto tied to the system appearance observed before JTetris installs or
switches FlatLaf, so selecting Auto restores the expected system-matched theme.

## Scope
- Preserve the existing `auto|light|dark` startup and runtime contracts.
- Prevent a manually selected FlatLaf theme from becoming Auto's system signal.
- Add an automated regression test for `light system -> dark -> auto`.

## Out Of Scope
- Live subscription to operating-system appearance changes.
- Persisting the selected theme.
- Palette, layout, menu, gameplay, or dependency changes.
