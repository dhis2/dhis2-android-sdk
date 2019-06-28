# Synchronization

<!--DHIS2-SECTION-ID:sync-->

In order to by able to be fully functional when offline, the SDK reproduces a reduced and simplified copy of DHIS2 server database.

## Metadata

<!--DHIS2-SECTION-ID:sync_metadata-->

## Tracker data

<!--DHIS2-SECTION-ID:sync_tracker_data-->

### Download

Two main strategies to download tracker data: bulk or by uid.

#### Download strategies 

##### Bulk download

Download a maximum of N trackedEntityInstances.

- Paging is used to split the payload (page size 50). 
- If a page throws an error, it continues to the next.
- The download is finished when it gets N instances or the last call gives 0 results.
- Once finished, it looks for uncompleted tei of type RELATIONSHIP. They are downloaded one by one. Their relationships are not downloaded.

About scopes:

- Only CAPTURE scope is used to download.

##### By UID

Download a list of trackedEntityInstances by providing a uid list.

OPEN programs:

- No restrictions.

PROTECTED programs:

- 

#### Overwriting existing data

In general all the downloaded data is stored and set as `SYNCED`. Although there are some exceptions:

- Tracker elements with errors.
- Tracker elements with warnings.
- Tracker elements with pending updates.

These exceptions ensure that no updated data or data with errors or warnings is lost during the download process.

### Upload

The SDK can be used to upload data to the server.

For data upload, the SDK collects the tracker elements which are set to be uploaded and creates a payload. This payload will be sent to the server.
The **state** property of each tracker element is used to store the synchronization status of each instance.
After an upload, the SDK will analyze the import summaries and update the **state** property of each synced element.

#### Strategies

- **Tracked entity instances**. The SDK uses `CREATE_AND_UPDATE` for 2.29 and `SYNC` for later versions.
- **Single events**. The SDK uses `CREATE_AND_UPDATE` for 2.29 and `SYNC` for later versions.

#### States

- `TO_POST`. This state is set each time an element is created. When the upload method is executed all the data set to to post is collected and added to the payload for the upload.
- `TO_UPDATE`. This state is set when an existing element is retrofitted. All the elements with this state will be collected to generate the payload for the upload.
- `TO_DELETE`. The elements set with *to delete* state will be deleted from the server when the upload method is executed.
- `SYNCED`. The element is synced. The elements *synced* won't be collected to form the payload for uploads.
- `ERROR`. If one element displays an error while syncing it will be set automatically as `ERROR` after the upload. The elements with *errors* won't be collected to form the payload for uploads.
- `WARNING`. If one element displays a warning while syncing it will be set automatically as `WARNING` after the upload. The elements with *warnings* won't be collected to form the payload for uploads.

#### Server response management

After an upload, the SDK will analyze the import summaries and take the next actions.

- If the response success:
  - Set the **state** property of each element to `SYNCED`.
- If there are conflicts: 
  - Set the **state** property to `ERROR` or `WARNING` for the element with the conflict.
  - Propagate errors and warnings from enrollments and events up to tracked entity instance level giving errors higher priority than warnings.
  - Store the import conflicts.

## Reserved values

Reserved values for TrackedEntityAttribute are downloaded in advance so they are available when the application operates offline.

When the "sync" process is triggered in the SDK, these actions are taken:

1. Delete expired reserved values.
2. Count the remaining values. If the count is under a certain threshold (50 by default), the sdk will fetch values up to the defined limit (100 by default).

When a value is requested from the app, these actions are taken:

1. Sync the reserved values for that attribute (which implies filling the table if needed).
2. Retrieve from the db the first value, remove it from the table and return it to the app.

A value is considered as "expired" when one of the following conditions is true:

- "expiryDate" is overdue. By default, the server sets the expiry period to 2 months.
- If the attribute pattern is dependent on time, i.e., it contains the segment `CURRENT_DATE(format)`, the sdk calculates an extra expiry date based on that pattern.
