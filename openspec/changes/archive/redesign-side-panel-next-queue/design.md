# Design: Side Panel Hierarchy and Three-Piece Queue

## Context
`SidePanel` currently uses a north/center/south layout: statistics and scoring state at the top, Hold and one Next preview in a custom-painted center panel, and a permanent controls `JTextArea` at the bottom. The Help overlay now contains the authoritative controls documentation, making the bottom section redundant.

`Board` currently owns `current`, `next`, and `hold`. Normal spawning and first-use Hold both promote `next` and draw one replacement from `PieceBag`. Seeded replay verification compares only the single next piece.

## Decisions

### 1. Board owns a fixed three-item upcoming queue
Replace the single `next` field with an `ArrayDeque<TetrominoType>` whose target size is three.

`Board` will:
- draw the current piece first during initialization;
- fill the upcoming queue to exactly three entries;
- promote and remove the queue head whenever a new random piece becomes current;
- immediately refill the tail from `PieceBag`;
- clear and rebuild the queue during reset;
- leave the queue unchanged when swapping with an already populated Hold slot.

The queue stores `TetrominoType`, not positioned `Tetromino` instances. Spawn position and orientation belong to the active piece; previews only need type identity.

### 2. Expose immutable queue state
Add `Board.getNextQueue()` returning an immutable snapshot in promotion order.

Keep `Board.getNext()` as a compatibility accessor during this change. It returns a spawn-oriented `Tetromino` for the queue head. Existing callers and focused tests can migrate incrementally without broad API churn, while new UI and replay assertions use the complete queue.

No queue mutation API is exposed outside `Board`.

### 3. Keep 7-bag ownership unchanged
`PieceBag` remains a sequential producer with `next()`. It does not gain `peek`, iterator, or snapshot behavior.

This keeps randomizer responsibilities narrow and ensures all random values consumed for future pieces become explicit `Board` state. Seeded games remain deterministic because initialization and every promotion consume pieces in a fixed order.

### 4. Apply option A side-panel hierarchy
The side panel keeps its existing width and theme pipeline. It becomes one vertical information surface with three visual groups:

1. **Performance**
   - Score receives the strongest typographic emphasis.
   - Level and Lines remain compact and easy to scan.
   - Transient scoring feedback appears only when meaningful.
   - Combo and B2B use the existing active/inactive semantics.

2. **Hold**
   - A divider separates Hold from performance state.
   - Hold keeps its empty and unavailable visual states.
   - The preview has a stable drawing area so state changes do not move surrounding content.

3. **Next**
   - A divider separates Next from Hold.
   - Three pieces are arranged vertically in queue order.
   - The first preview uses the current preview cell size and full color.
   - The second and third use a slightly smaller stable cell size and remain full-color but visually secondary through scale and spacing, not reduced contrast.

The permanent controls `JTextArea` and its surrounding panel are removed. Help remains accessible from `H` and the menu.

### 5. Preserve rendering style and accessibility
Preview cells continue using `ColorPalette.colorFor(...)` and `outlineFor(...)`, matching the flat stage rendering in both themes. Labels and dividers continue using `UiFonts` and `UiTheme` tokens. No hard-coded theme-specific colors are introduced.

Regular information labels use the primary text color consistently. `Score` remains visually dominant through size, while `Hold` and `Next` use the same primary color as `Level` and `Lines`. Secondary text color is reserved for inactive state semantics such as `Combo -` and `B2B Ready`; those labels switch to primary color when active.

The side panel must fit the existing 200 by 520 preferred size without clipping or overlap. Painted sections use stable vertical bounds rather than deriving positions from changing text content.

### 6. Update player-facing explanations
The Help playfield section will explain that Next displays the next three pieces in order. Stable documentation will describe `Board` as owning an upcoming queue and `SidePanel` as showing gameplay state rather than a controls cheat-sheet.

## Data Flow
1. Board construction draws one current type from `PieceBag`.
2. Board fills the upcoming queue until it contains three types.
3. `SidePanel` refresh reads `Board.getNextQueue()` and paints the immutable snapshot in order.
4. On lock, Board removes the queue head, creates the new active piece, and refills the queue tail.
5. On first-use Hold with an empty Hold slot, Board uses the same promotion/refill operation.
6. On Hold swap with a populated Hold slot, the upcoming queue is not changed.
7. Replay reconstruction repeats the same actions against the same seed and must produce an equal queue.

## Error and Boundary Handling
- Queue refill is internal and synchronous; `PieceBag.next()` always supplies a type.
- `getNextQueue()` never returns `null` and does not expose the mutable deque.
- `getNext()` treats an unexpectedly empty queue as an internal invariant violation rather than inventing a piece. Tests protect the invariant at construction, reset, spawn, and Hold transitions.
- Side-panel painting tolerates a temporarily short snapshot defensively by drawing only available entries, although normal model behavior guarantees three.

## Test Strategy

### Model tests
- Construction exposes exactly three upcoming types.
- Lock promotion makes the former queue head current and preserves three queued entries.
- First-use empty Hold promotes the queue head and refills to three.
- Populated Hold swap leaves the queue unchanged.
- Reset rebuilds a three-entry queue and clears previous gameplay state.
- Queue snapshots are immutable and cannot mutate board state.
- Seeded replay compares the entire queue, not only the first entry.

### UI tests
- Side-panel structure no longer contains the controls text area.
- Side-panel preview state consumes all three queue entries in order.
- Help content describes three upcoming pieces and retains controls documentation.

### Verification
- Run focused model and UI tests during implementation.
- Run `./mvnw clean test`.
- Manually inspect light and dark themes at the existing window size for clipping, hierarchy, preview order, and Hold availability styling.

## Implementation Allowlist
- `src/main/java/net/vetcafe/jtetris/model/Board.java`
- `src/main/java/net/vetcafe/jtetris/ui/SidePanel.java`
- `src/main/java/net/vetcafe/jtetris/ui/HelpContent.java`
- focused files under `src/test/java/net/vetcafe/jtetris/model/`
- focused files under `src/test/java/net/vetcafe/jtetris/ui/`
- `doc/overview.md`
- `doc/algorithms.md`
- `openspec/changes/redesign-side-panel-next-queue/**`
- canonical OpenSpec files affected during archive

Changes outside this allowlist require explicit re-approval.
