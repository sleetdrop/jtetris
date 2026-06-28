# Design

## Runtime Baseline
JTetris will target Java 25 LTS as the build and development baseline. The code
should continue to use standard Java and Swing APIs rather than vendor-specific
runtime APIs.

Local development can use Homebrew `openjdk@25`. GitHub Actions should use
`actions/setup-java` with `distribution: temurin` and `java-version: '25'` when
release automation is added.

## Release Runtime Direction
Release artifacts that bundle a runtime should use an OpenJDK distribution with
clear redistribution terms and long-term binary updates. Eclipse Temurin 25 is
the default documented choice for CI/release packaging. Oracle JDK is not the
documented release default.

## Tooling Compatibility
The existing Palantir Java Format style is retained because it matches the
reader-first style baseline and avoids a repository-wide formatter style change.
The formatter is upgraded to a Java 25-compatible version. The Maven Compiler
Plugin is upgraded within the stable 3.x line rather than moving to a beta 4.x
plugin.

## Java Modernization Policy
This change intentionally does not rewrite application code for newer language
features. Later changes may use modern Java features when they reduce real
complexity and keep the code easier to read. Good candidates include records for
small immutable data carriers and pattern matching where it simplifies existing
branching. Broad `var` churn, clever stream rewrites, and sealed hierarchies
without a clear model benefit should stay out of scope.
