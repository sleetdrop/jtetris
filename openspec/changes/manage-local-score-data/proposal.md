# Manage Local Score Data

## Summary
Move JTetris score persistence from the legacy home-directory file to platform-appropriate application data directories, migrate existing scores without data loss, and let players delete individual leaderboard records.

## Motivation
The legacy `~/.tetris_scores.properties` path places application data directly in the user's home directory and uses a generic filename that is not namespaced to JTetris. Modern desktop platforms provide dedicated locations for persistent per-user application data.

The leaderboard currently supports viewing and adding score records but provides no way to remove a player record. Users should be able to manage locally stored score data from the existing in-window leaderboard flow.

## Scope
- Store scores under a `net.vetcafe.jtetris` application directory selected according to the host platform.
- Preserve the existing Java properties storage format and best-score-per-user semantics.
- Migrate the legacy `~/.tetris_scores.properties` file when the new store does not yet exist.
- Delete the legacy file only after the migrated store has been written successfully.
- Add single-player deletion to the leaderboard with an in-window confirmation step.
- Add automated tests for path selection, migration, persistence, and deletion behavior.
- Update public architecture and algorithm documentation for the new storage behavior.

## Out Of Scope
- No scoring-rule changes.
- No cloud synchronization, shared profiles, import/export, or bulk-delete feature.
- No username normalization or display-name format changes.
- No storage-format conversion away from Java properties.
- No new dependencies or `pom.xml` changes.
- No changes to replay persistence or gameplay controls.

