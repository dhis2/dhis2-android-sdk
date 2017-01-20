#!/bin/bash
# A quick shortcut to only run all the tests.
# Useful if debugging something and a quick pass over the tests is necessary.

set -xe

# You can run it from any directory.
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR=$DIR/

"$PROJECT_DIR"/gradlew --no-daemon --info connectedAndroidTest -PdisablePreDex -PwithDexCount
