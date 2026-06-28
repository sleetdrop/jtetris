# Tasks

- [x] Add failing tests for five-piece Next, drop scoring, and Marathon timing.
- [x] Implement five-piece queue and compact five-piece side-panel preview.
- [x] Implement soft-drop/hard-drop scoring.
- [x] Implement level-based gravity and UI-layer fixed lock delay.
- [x] Update docs and local guideline notes with a three-month refresh rule.
- [x] Run focused tests.
- [x] Run `./mvnw -Djava.awt.headless=true clean test`.

## Verification

- RED: `./mvnw -Djava.awt.headless=true -Dtest=NextQueueTest,SidePanelLayoutTest,ScoringRulesTest,MarathonTimingTest test` failed at test compilation because `Board.softDrop()` and `MarathonTiming` did not exist.
- Focused GREEN: `./mvnw -Djava.awt.headless=true -Dtest=NextQueueTest,SidePanelLayoutTest,ScoringRulesTest,MarathonTimingTest test` passed 18 tests.
- Regression pass: `./mvnw -Djava.awt.headless=true test` passed 149 tests after updating old three-piece Help/Hold assertions.
- Stale-text scan: `rg -n "three-piece|next three|three upcoming|three model-owned|contains exactly three|three entries|three types|three vertically|Timer speed does not|fixed in code|Soft drop and hard drop scoring|Level-based fall speed" README.md doc src/test/java src/main/java openspec/specs openspec/changes/standardize-marathon-rules` returned no matches.
- Final pass: `./mvnw -Djava.awt.headless=true clean test` passed 149 tests.
