set -x

branch=$(git rev-parse --abbrev-ref HEAD)

if [ "$branch" = "main" ] || [ "$branch" = "master" ]; then
  ./gradlew :core:publishToSonatype closeAndReleaseSonatypeStagingRepository -PremoveSnapshotSuffix
else
  ./gradlew :core:publishToSonatype
fi
