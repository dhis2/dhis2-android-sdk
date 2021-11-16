# Known issues { #android_sdk_known_issues }

## Data set completion

Affected versions: 2.33.0, 2.33.1.

If a dataset is mark as uncompleted in the server, this value is not updated in the SDK. In those versions the API did not expose enough information to know if the status was complete or uncomplete.

## Tracker relationships

Affected versions: 2.35.1.

TEI relationships might fail to be downloaded at data sync.

## Enrollments are not downloaded if program shared at user level

Affected versions: 2.36.3, 2.36.4, 2.37.0 (check [DHIS2-11557](https://jira.dhis2.org/browse/DHIS2-11557)).

Due to [DHIS2-11557](https://jira.dhis2.org/browse/DHIS2-11557), enrollments are not downloaded if the program is shared at user level. The workaround for this is to share the program using a group (even if the usergroup only contains a single user).

## Non-repeatable events cannot be replaced in a single query

Affected versions: 2.34.6, 2.35.6, 2.36.3, 2.37.0 (check [DHIS2-11526](https://jira.dhis2.org/browse/DHIS2-11526)).

Due to [DHIS2-11526](https://jira.dhis2.org/browse/DHIS2-11526), non-repeatable tracker events cannot be removed and created again within the same synchronization. If the event is removed, created and then uploaded to the server, the server will return an error because the event already exists and it is non-repeatable. The event will be marked as `ERROR` in the app. If the event is uploaded again it will succeed.

In order to prevent the `ERROR`, the event must be removed in the device, uploaded to the server, created in the device and then uploaded to the server again.

## Single-event's relationships are not downloaded

Affected versions: 2.35.6, 2.36.3 (check [DHIS2-11541](https://jira.dhis2.org/browse/DHIS2-11541)).

Due to [DHIS2-11541](https://jira.dhis2.org/browse/DHIS2-11541), single-event's relationships are not included in the payload and are not downloaded from the server. It means that the device is not aware about existing relationships where to "FROM" component is a single-event.