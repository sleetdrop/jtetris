## ADDED Requirements

### Requirement: macOS app image packaging
JTetris SHALL provide a Maven packaging command that builds a standard macOS application image.

#### Scenario: Build macOS app image
Given a macOS development machine with Java 17+ and Maven available
When an agent runs `mvn -Pmac clean package`
Then the build completes successfully
And a `JTetris.app` application image is produced under the Maven target output tree.

### Requirement: macOS application metadata
JTetris SHALL override generated macOS app metadata so the application bundle does not advertise unused privacy capabilities.

#### Scenario: Build app without unused microphone usage string
Given JTetris does not use microphone APIs
When an agent builds the macOS app image
Then `JTetris.app/Contents/Info.plist` does not contain `NSMicrophoneUsageDescription`
And the plist still contains the JTetris bundle identifier, app version, executable name, and icon file.

### Requirement: cross-platform icon assets
JTetris SHALL maintain a deterministic source icon and generated platform icon assets for native packaging.

#### Scenario: Reuse icon assets across platforms
Given the repository icon source under `art/icon.svg`
When an agent needs platform-specific app icons
Then macOS can use `art/icon.icns`
And Windows can use `art/icon.ico`
And Linux-oriented packaging can use PNG assets under `art/icons/`.
