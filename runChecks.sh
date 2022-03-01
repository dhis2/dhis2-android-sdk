#!/bin/bash
set -xe

# You can run it from any directory.
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR=$DIR/

# This will: compile the project, run lint, package apk and check the code quality.
"$PROJECT_DIR"/gradlew clean ktlintCheck detekt checkstyleDebug pmdDebug lintDebug