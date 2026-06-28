## ADDED Requirements

### Requirement: Windows installer packaging
JTetris MUST support native Windows 11 x64 installer packaging through Maven on Windows hosts.

#### Scenario: Build Windows installer
- **Given** a Windows 11 x64 build host with Java 25, `jpackage`, and WiX
- **When** an agent runs `./mvnw -Djava.awt.headless=true -Pwindows clean package`
- **Then** Maven stages the application jar and runtime dependencies for `jpackage`
- **And** `jpackage` creates a `target/dist/JTetris-<version>.exe` installer using the Windows icon asset.

## MODIFIED Requirements

### Requirement: macOS app-image packaging
JTetris MUST keep macOS app-image packaging available through Maven on macOS hosts.

#### Scenario: Build macOS app image
- **Given** a macOS build host with Java 25 and `jpackage`
- **When** an agent runs `./mvnw -Djava.awt.headless=true -Pmac clean package`
- **Then** Maven stages the application jar, runtime dependencies, and macOS resources for `jpackage`
- **And** `jpackage` creates a `target/dist/JTetris.app` app image using the macOS icon asset.
