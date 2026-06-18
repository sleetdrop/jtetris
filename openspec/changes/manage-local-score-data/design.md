# Design

## Storage Location

JTetris will keep score data in a package-namespaced application data directory:

- macOS: `~/Library/Application Support/net.vetcafe.jtetris/scores.properties`
- Linux and other XDG desktops: `${XDG_DATA_HOME:-~/.local/share}/net.vetcafe.jtetris/scores.properties`
- Windows: `%LOCALAPPDATA%\net.vetcafe.jtetris\scores.properties`

A focused path resolver will own platform detection and environment/property lookup. It will accept injectable values for tests while production construction reads `os.name`, `user.home`, `XDG_DATA_HOME`, and `LOCALAPPDATA`.

For an unrecognized operating system, the resolver will use the XDG-style fallback under `~/.local/share`. On Windows, if `LOCALAPPDATA` is absent or blank, it will fall back to `~/AppData/Local`. Relative XDG paths are ignored because the XDG Base Directory specification requires an absolute path.

## Score Store Boundary

`ScoreManager` remains responsible for loading, querying, updating, and deleting best-score records. It will receive the resolved store path through a testable constructor while its default constructor uses the platform resolver.

The on-disk format remains Java properties:

- lowercase username keys;
- integer best-score values;
- existing case-insensitive lookup behavior.

The target parent directory is created before saving. Save operations report success internally so migration and deletion do not claim success unless persistence completed.

## Legacy Migration

Migration runs only when the new score file does not exist.

1. If `~/.tetris_scores.properties` does not exist, load the new store normally as empty.
2. If the legacy file exists, load its valid properties into memory.
3. Create the target application data directory and write the new score file.
4. Verify that the write completed successfully.
5. Delete the legacy file.

If writing the new file fails, the legacy file remains untouched. If deleting the legacy file fails after a successful write, the new store remains authoritative on later starts because it now exists; the old file is not imported again.

If both legacy and new files exist, JTetris reads only the new file. It does not merge files because stale legacy values must not overwrite newer application data.

## Player Deletion

`ScoreManager` will expose a deletion operation using the same case-insensitive username key as score lookup. Deleting an existing player removes both the score property and the remembered display name, then persists the updated store. The operation reports whether the requested record was durably removed.

If persistence fails, the in-memory deletion is rolled back so the active leaderboard remains consistent with the last known stored data.

## Leaderboard Interaction

The leaderboard remains an in-window stage overlay.

- The table changes from disabled display-only mode to single-row selection.
- A `Delete` button appears next to `Close`.
- `Delete` is disabled until a row is selected.
- Activating `Delete` opens a confirmation overlay naming the selected player.
- Confirming permanently removes that player's local score data.
- Cancelling returns to the unchanged leaderboard.
- Successful deletion reopens the refreshed leaderboard.
- Deleting the last record produces the existing `No scores yet` empty state.
- A persistence failure shows an in-window error message and does not visually remove the record.

Gameplay input remains blocked during leaderboard, confirmation, and feedback overlays. Closing the flow restores the existing focus behavior.

## Testing

Automated tests will cover:

- macOS application data path;
- Linux explicit `XDG_DATA_HOME`;
- Linux default XDG path and rejection of relative XDG values;
- Windows `LOCALAPPDATA` and fallback path;
- unknown-platform fallback;
- successful legacy migration followed by legacy-file deletion;
- failed migration preserving the legacy file;
- new store taking precedence when both files exist;
- deletion persistence, missing-player behavior, and rollback after save failure;
- leaderboard single-selection and delete-button state;
- confirmation and refreshed/empty leaderboard flow where practical without launching a native window.

Final verification uses `./mvnw -Djava.awt.headless=true clean test`, followed by user-provided visual verification of the leaderboard flow if needed.

