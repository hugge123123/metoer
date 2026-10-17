# Meteor Staff Look Alert

A small Fabric/Meteor addon project for Minecraft 1.21.11. It is designed to warn with a chat message and sound when a player from a configurable watch list appears to be looking toward you.

## Important limitation
Minecraft servers generally do not tell clients who is an administrator or where an administrator's camera is aimed. This module only estimates whether a player whose name you entered appears to be looking toward your player. It cannot reliably identify staff automatically.

## Build
- Install JDK 21 and Gradle.
- Clone this repository and run `gradle build`, or import it into IntelliJ IDEA as a Gradle project.
- Put the built addon JAR from `build/libs` into `.minecraft/mods` alongside a compatible Meteor Client and Fabric Loader.

GitHub Actions also attempts to build the project on push. Check the workflow result before using the JAR; version compatibility must be verified against the installed Meteor/Fabric versions.

## Configure
Enable **Staff Look Alert** in Meteor's module list and enter player names in the Watch names setting, separated by commas.

This is a standalone addon; it does not modify or merge into Meteor Client's own JAR.
