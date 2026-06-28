# Upgrade to Java 25 LTS

## Why
JTetris is a new Java/Swing project whose purpose includes showing clean,
modern Java code and a practical desktop packaging workflow. Java 17 was an
implicit early default; Java 25 is now the latest LTS baseline and is available
locally through Homebrew and in GitHub Actions through `actions/setup-java`.

## What Changes
- Move the project compile, test, formatter, and packaging baseline to Java 25
  LTS.
- Keep the release-runtime direction OpenJDK-compatible and avoid Oracle JDK as
  the documented release default.
- Upgrade build plugins only where needed for Java 25 compatibility.
- Preserve the existing Palantir Java Format style by upgrading the formatter
  instead of switching style families.
- Defer source-code modernization to later focused changes.

## Non-Goals
- No gameplay, UI, scoring, replay, or input behavior changes.
- No broad Java syntax rewrite in this change.
- No GitHub Actions release workflow implementation in this change.
