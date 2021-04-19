# Known issues { #android_sdk_known_issues }

## Data set completion

- In DHIS2 version 2.33.0 and 2.33.1, if a dataset is mark as uncompleted in the server, this value is not updated in the SDK. In those versions the API did not expose enough information to know if the status was complete or uncomplete.

## Tracker relationships

- In DHIS2 version 2.35.1, TEI relationships might fail to be downloaded at data sync. 