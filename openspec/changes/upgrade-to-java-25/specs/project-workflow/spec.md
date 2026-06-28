## MODIFIED Requirements

### Requirement: Verification evidence is required
Agents MUST run the project validation command under the project Java baseline and record the result before marking work complete.

#### Scenario: Agent completes a change
- **Given** implementation and documentation edits are complete
- **When** the agent prepares final handoff
- **Then** the agent runs `./mvnw -Djava.awt.headless=true clean test` with Java 25 available as the active JDK
- **And** records the pass or fail result in the final handoff
- **And** records the pass or fail result in the active OpenSpec change when one exists
- **And** records any manual verification needed for UI or workflow changes
