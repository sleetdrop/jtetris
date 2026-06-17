# Release Fat Jar Assets

## Why
The initial release included both a raw project jar and a Java distribution zip. That made the download choice unclear because the raw jar was not directly runnable without its adjacent dependency directory.

## What Changes
- Add a standalone Maven shaded jar artifact for Java users.
- Publish the release jar asset as a fat jar.
- Remove non-directly-runnable Java release assets from `v1.0.0`.

## Impact
- Release users see only directly executable project assets: one fat jar and one Apple Silicon macOS app archive.
- The macOS app packaging path remains unchanged.
