#!/usr/bin/env bash

export NEXUS_USERNAME=$1
export NEXUS_PASSWORD=$2
export REPOSITORY_DIR=$3

export API_ENDPOINT=https://oss.sonatype.org/service/local/staging

export DHIS2_PROFILE_ID="a87d019c1e07"

export DESCRIPTION_PAYLOAD="<promoteRequest>\
    <data>\
        <description>DHIS2 Android SDK Release</description>\
    </data>\
</promoteRequest>"

# Create staging repo
export STAGING_ID=$(curl -s -u $NEXUS_USERNAME:$NEXUS_PASSWORD \
    -X POST \
    -H "Content-Type:application/xml" \
    -d "$DESCRIPTION_PAYLOAD" \
    "$API_ENDPOINT/profiles/$DHIS2_PROFILE_ID/start" \
    | perl -nle 'print "$1" if ($_ =~ /.*<stagedRepositoryId>(.*)<\/stagedRepositoryId>.*/g);' \
    | awk '{$1=$1};1')

find $REPOSITORY_DIR -type f | while read f; do
    suffix=$(echo $f | sed "s%^$REPOSITORY_DIR/%%")
    echo "Uploading to: ${STAGING_ID}: ${suffix}"
    curl -s -u $NEXUS_USERNAME:$NEXUS_PASSWORD -H "Content-type: application/x-rpm" --upload-file $f ${API_ENDPOINT}/deployByRepositoryId/${STAGING_ID}/${suffix}
done

echo "sleep 20 seconds before closing"
sleep 20