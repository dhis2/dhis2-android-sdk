# Synchronization

<!--DHIS2-SECTION-ID:sync-->

In order to by able to be fully functional when offline, the SDK reproduces a reduced and simplified copy of DHIS2 server database.

## Metadata

<!--DHIS2-SECTION-ID:sync_metadata-->

## Tracker data

<!--DHIS2-SECTION-ID:sync_tracker_data-->

### Download

Two main strategies to download tracker data: bulk or by uid.

#### Bulk download

Download a maximum of N trackedEntityInstances.

- Paging is used to split the payload (page size 50). 
- If a page throws an error, it continues to the next.
- The download is finished when it gets N instances or the last call gives 0 results.
- Once finished, it looks for uncompleted tei of type RELATIONSHIP. They are downloaded one by one. Their relationships are not downloaded.

About scopes:

- Only CAPTURE scope is used to download.

#### By UID

Download a list of trackedEntityInstances by providing a uid list.

OPEN programs:

- No restrictions.

PROTECTED programs:

- 

#### Elements handling

For tracked entity instances, enrollments and events the SDK will check that the property **state** of each element is set to `SYNCED` after overwriting the element with the downloaded data.
This step ensures that no updated data or data with errors or warnings is lost in the download process.

### Upload

The SDK can be used to upload data to the server.

For data upload, the SDK collects the elements which are set to be uploaded and creates a payload which will be sent to the server.
The **state** property of each element is used to store the synchronization status of each instance.
After an upload, the SDK will analyze the import summaries and update the **state** property of each element synced.

#### Strategies

- **Tracked entity instances**. The SDK uses `CREATE_AND_UPDATE` for 2.29 and `SYNC` for later versions.

- **Single events**. The `CREATE_AND_UPDATE` strategy is used to synchronize single events.


#### States

###### TO_POST

This state is set each time an element is created.
When the upload method is executed all the data set to `TO_POST` is collected and added to the payload for the upload.

###### TO_UPDATE

The `TO_UPDATE` state is set when an existing element is retrofitted.
All the elements with this state will be collected to generate the payload for the upload.

###### TO_DELETE

`TO_DELETE` state is set to delete an element from the server.
The different instances set with the *to delete* state will be deleted from the server when the upload method is executed.

###### SYNCED

After the upload all the elements correctly uploaded will be set automatically as `SYNCED`.
The elements *synced* won't be collected to form the payload for uploads.

###### ERROR

If one element displays an error while syncing it will be set automatically as `ERROR` after the upload.

The SDK reads the import summary of each upload call and checks if there are elements with errors.
If a *tracker event* displays an error, the `ERROR` state will be propagated automatically to the enrollment and the tracked entity instance associated.
Also, if a *tracker enrollment* displays an error, it will be propagated automatically to its tracked entity instance.

###### WARNING

As with the state `ERROR`, if one element displays a warning, the state will be set automatically as `WARNING`. 
The state will be propagated to the enrollment and tracked entity instance or just the tracked entity instance if the tracker element requires it.

#### Errors and warnings propagation

The SDK will propagate errors and warnings in enrollments and events until the tracked entity instance.
- A warning will be propagated if a child displays a warning but there are no errors among children after the upload.
- An error will be propagated whenever at least one child displays an error after the upload.

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