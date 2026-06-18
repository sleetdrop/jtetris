# Tasks

## 1. Specify
- [x] Approve option A visual hierarchy.
- [x] Define the model-backed three-piece queue and compatibility boundary.
- [x] Define UI, documentation, replay, and test requirements.
- [ ] Review and approve the written OpenSpec.

## 2. Model Queue
- [ ] Add failing tests for queue initialization, promotion, refill, Hold behavior, reset, immutability, and replay equality.
- [ ] Replace the single stored next piece with a Board-owned three-type queue.
- [ ] Retain `getNext()` as a queue-head compatibility accessor and add immutable `getNextQueue()`.
- [ ] Run focused model tests.
- [ ] Commit the model queue as one focused change.

## 3. Side Panel
- [ ] Add/update focused UI tests for removal of the controls area and rendering data for three previews.
- [ ] Remove the persistent controls cheat-sheet.
- [ ] Implement the approved option A hierarchy at the existing side-panel size.
- [ ] Render Hold plus three vertically ordered upcoming pieces with stable bounds.
- [ ] Run focused UI tests.
- [ ] Commit the side-panel redesign as one focused change.

## 4. Help and Documentation
- [ ] Update Help to explain the three-piece Next queue.
- [ ] Update `doc/overview.md` and `doc/algorithms.md`.
- [ ] Update focused Help-content tests.
- [ ] Commit documentation and Help wording as one focused change.

## 5. Verify and Archive
- [ ] Run `./mvnw clean test`.
- [ ] Manually verify light and dark themes at the default window size.
- [ ] Record verification evidence below.
- [ ] Merge the spec deltas into canonical OpenSpec specs.
- [ ] Archive this change.
- [ ] Commit the completed OpenSpec archive.

## Verification Notes
- Not yet implemented.
