#!/bin/bash
#
# Deploy a jar, source jar, and javadoc jar to Sonatype's snapshot repo.
#
# Adapted from https://coderwall.com/p/9b_lfq and
# http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/

SLUG="dhis2/dhis2-android-sdk"
JDK="oraclejdk8"
MASTER_BRANCH="master"
MASTERDEV_BRANCH="build-fixes-test"

set -e

if [ "$TRAVIS_REPO_SLUG" != "$SLUG" ]; then
  echo "Skipping snapshot deployment: wrong repository. Expected '$SLUG' but was '$TRAVIS_REPO_SLUG'."
elif [ "$TRAVIS_JDK_VERSION" != "$JDK" ]; then
  echo "Skipping snapshot deployment: wrong JDK. Expected '$JDK' but was '$TRAVIS_JDK_VERSION'."
elif [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
  echo "Skipping snapshot deployment: was pull request."
elif [ "$TRAVIS_BRANCH" != "$MASTER_BRANCH" ] && [ "$TRAVIS_BRANCH" != "$MASTERDEV_BRANCH" ]; then
  echo "Skipping snapshot deployment: wrong branch. Expected '$MASTER_BRANCH' or '$MASTERDEV_BRANCH' but was '$TRAVIS_BRANCH'."
else
  echo "Deploying snapshot..."

  # Ids used to decrypt the secret file located at Travis.
  chmod -R ug+x .travis
  openssl aes-256-cbc -K $encrypted_4e8719c171e4_key -iv $encrypted_4e8719c171e4_iv -in $ENCRYPTED_GPG_KEY_LOCATION -out $GPG_KEY_LOCATION -d
  ABSOLUTE_GPG_KEY_LOCATION=$PWD/$GPG_KEY_LOCATION

  ./gradlew uploadArchives publishToNexus -PNEXUS_USERNAME="${NEXUS_USERNAME}" -PNEXUS_PASSWORD="${NEXUS_PASSWORD}" -PGPG_KEY_ID="${GPG_KEY_ID}" -PGPG_KEY_LOCATION="${ABSOLUTE_GPG_KEY_LOCATION}" -PGPG_PASSPHRASE="${GPG_PASSPHRASE}"
  echo "Snapshot deployed!"
fi