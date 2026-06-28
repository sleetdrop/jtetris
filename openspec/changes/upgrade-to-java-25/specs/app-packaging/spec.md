## MODIFIED Requirements

### Requirement: macOS app image packaging
JTetris SHALL provide a Maven packaging command that builds a standard macOS application image with the project Java 25 LTS baseline.

#### Scenario: Build macOS app image
- **Given** a macOS development machine with Java 25 available
- **When** an agent runs `./mvnw -Pmac clean package`
- **Then** the build completes successfully
- **And** a `JTetris.app` application image is produced under the Maven target output tree

### Requirement: release runtime distribution
JTetris release automation SHOULD package bundled-runtime artifacts with an OpenJDK distribution that has clear redistribution terms and long-term binary updates.

#### Scenario: Package release app with CI-managed JDK
- **Given** release automation builds a native app artifact
- **When** the workflow provisions Java
- **Then** it uses `actions/setup-java` with Java 25
- **And** it uses an OpenJDK distribution such as Eclipse Temurin for bundled-runtime release builds
- **And** it does not document Oracle JDK as the default bundled runtime
