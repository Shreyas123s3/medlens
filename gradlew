#!/bin/sh
# Bootstraps the checked-in Gradle wrapper files from an installed Gradle distribution.
# GitHub Actions installs Gradle 8.9 before invoking this script.
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
if [ ! -f "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" ]; then
  if ! command -v gradle >/dev/null 2>&1; then
    echo "Gradle 8.9 is required to bootstrap the wrapper. See README.md for the cloud build." >&2
    exit 1
  fi
  gradle -p "$APP_HOME" wrapper --gradle-version 8.9 --distribution-type bin || exit $?
fi
exec "$APP_HOME/gradlew" "$@"
