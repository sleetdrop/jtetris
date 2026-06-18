# Tasks

## 1. Specify
- [x] Approve option A visual hierarchy.
- [x] Define the model-backed three-piece queue and compatibility boundary.
- [x] Define UI, documentation, replay, and test requirements.
- [x] Review and approve the written OpenSpec.

## 2. Model Queue
- [x] Add failing tests for queue initialization, promotion, refill, Hold behavior, reset, immutability, and replay equality.
- [x] Replace the single stored next piece with a Board-owned three-type queue.
- [x] Retain `getNext()` as a queue-head compatibility accessor and add immutable `getNextQueue()`.
- [x] Run focused model tests.
- [x] Commit the model queue as one focused change.

## 3. Side Panel
- [x] Add/update focused UI tests for removal of the controls area and rendering data for three previews.
- [x] Remove the persistent controls cheat-sheet.
- [x] Implement the approved option A hierarchy at the existing side-panel size.
- [x] Render Hold plus three vertically ordered upcoming pieces with stable bounds.
- [x] Run focused UI tests.
- [x] Commit the side-panel redesign as one focused change.

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
- Model RED: `./mvnw -Dtest=NextQueueTest,HoldPieceTest,ReplayHooksTest,BoardRegressionGateTest test` failed at test compilation because `Board.getNextQueue()` did not exist.
- Model focused GREEN: `./mvnw -Dtest=NextQueueTest,HoldPieceTest,PieceBagTest,ReplayHooksTest,BoardRegressionGateTest test` passed 20 tests.
- Model regression GREEN: `./mvnw -Djava.awt.headless=true test` passed 59 tests.
- UI RED: `./mvnw -Djava.awt.headless=true -Dtest=SidePanelLayoutTest test` failed because the persistent controls `JTextArea` was present; the second RED failed at compilation because `displayedNextTypes()` did not exist.
- UI focused GREEN: `./mvnw -Djava.awt.headless=true -Dtest=SidePanelLayoutTest,ThemeVisualsTest,ScoreFeedbackFormatterTest test` passed 9 tests.
- UI regression GREEN: `./mvnw -Djava.awt.headless=true test` passed 62 tests.
- Environment note: running the new Swing construction test without headless mode aborted the Java 26/macOS AWT process before JUnit execution; all Swing test commands use `-Djava.awt.headless=true`.
