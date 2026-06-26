## ADDED Requirements

### Requirement: Java style gate
JTetris SHALL define a reader-first Java style and enforce deterministic Java
formatting through the Maven build.

#### Scenario: Developer checks Java formatting
Given a developer has a local checkout
When they run `./mvnw spotless:check`
Then the command reports whether Java source and tests match the configured
formatter.

### Requirement: Java lint gate
JTetris SHALL provide a Maven lint command that checks the low-risk Java style
baseline.

#### Scenario: Developer checks Java lint
Given a developer has a local checkout
When they run `./mvnw checkstyle:check`
Then the command reports violations of the configured JTetris Checkstyle rule
set.

### Requirement: Reader-first style documentation
JTetris SHALL document the Java readability principles that are not fully
captured by automatic formatting.

#### Scenario: Contributor reviews style guidance
Given a contributor wants to modify Java code
When they read `doc/java-style.md`
Then they can identify the formatter policy, naming expectations, comment
guidance, and staged refactoring policy.
