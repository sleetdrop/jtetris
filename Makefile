# Simple helpers for build/run/package

APP_NAME := Tetris
JAR := target/tetris-1.0-SNAPSHOT.jar

ICON ?=

.PHONY: run build clean package mac-app mac-dmg

run: $(JAR)
	mvn -q -DskipTests exec:java

build: $(JAR)

$(JAR):
	mvn -q package

clean:
	mvn -q clean

package: build
	mvn -q -Pmac -Djpackage.type=app-image -Djpackage.icon=$(ICON) package

mac-app: package

mac-dmg: build
	mvn -q -Pmac -Djpackage.type=dmg -Djpackage.icon=$(ICON) package
