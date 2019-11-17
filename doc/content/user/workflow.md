# Workflow

<!--DHIS2-SECTION-ID:workflow-->

Currently, the SDK is primarily oriented to build apps that work most of the time in an offline mode. In short, the SDK maintains a local database instance that is used to get the work done locally (create forms, manage data, ...). From time to time, this local database is synchronized with the server.

A typical workflow would be like this:

1. Login
2. Sync metadata: the SDK the metadata so it is available to be used at any time. Metadata sync is totally user-dependent (see [Synchronization](...) for more details)
3. Download data:
4. Do the work: at this point the app is able to create the data entry forms and show some existing data. Then the user can edit/delete/update data.
5. Upload data: from time to time, the work done in the local database instance is sent to the server.
6. Sync metadata: it is recommended to sync metadata quite often to detect changes in metadata configuration.

## Login/Logout

<!--DHIS2-SECTION-ID:login_logout-->

Before interacting with the server it is required to login into the DHIS 2 instance. Currently, the SDK does only support one pair "user - server" simultaneously. That means that only one user can be authenticated in only one server at the same time.

```java
d2.userModule().logIn(username, password, serverUrl)

d2.userModule().logOut()
```

After a logout the SDK keeps track of the last logged user so that it is able to differentiate recurring and new users. It also keeps a hash of the user credentials in order to authenticate the user even when there is no connectivity. Given that said, the login method will:

- If an authenticated user already exists: throw an error.
- If Online:
  - If user is different than last logged user: wipe DB and try **login online**.
  - If server is different (even if the user is the same): wipe DB and try **login online**.
  - If user account has been disabled in server: wipe DB and throw an error.
- If Offline:
  - If the user has ever been authenticated:
    - If server is the same: try **login offline**.
    - If server is different: throw an error.
  - If the user has not been authenticated before: throw an error.

Logout method removes user credentials, so a new login is required before any interaction with the server. Metadata and data is preserved so a user is able to logout/login without losing any information.

## Metadata synchronization

<!--DHIS2-SECTION-ID:metadata_sync-->

Metadata synchronization is usually the first step after login. It fetches and persists the metadata needed by the current user. To launch metadata synchronization we must execute:

```java
d2.metadataModule().download();
```

In order to save bandwidth usage and storage space, the SDK does not synchronize all the metadata in the server but a subset. This subset is defined as the metadata required by the user in order to perform data entry tasks: render programs and datasets, execute program rules, evaluate in-line program indicators, etc.

Based on that, metadata sync includes the following elements:

|   Element             |   Condition or scope |
|-----------------------|-------------|
| System info           | All |
| System settings       | KeyFlag, KeyStyle |
| User                  | Only authenticated user |
| UserRole              | Roles assigned to authenticated user |
| Authority             | Authorities assigned to authenticated user |
| Program               | Programs that user has (at least) read data access to and that are assigned to any orgunit visible by the user |
| RelationshipTypes     | All |
| OptionGroups          | Only if server is greater than 2.29 |
| DataSet               | DataSets that user has (at least) read data access to and that are assigned to any orgunit visible by the user |
| Indicators            | Indicators assigned to any dataSet |
| OrganisationUnit      | OrganisationUnits in CAPTURE or SEARCH scope (descendants included) |
| OrganisationUnitGroup | Groups assigned to downloaded organisationUnits |
| OrganisationUnitLevel | - |
| Constant              | - |
| SMS Module metadata   | Only if SMS module enabled |

In the case of Programs and DataSets, metadata sync includes all the metadata related to them: stages, sections, dataElements, options, categories, etc. Those elements that are not related to any Program or DataSet are not included.

### Corrupted configurations

This partial metadata synchronization may expose server-side misconfiguration issues. For example, a ProgramRuleVariable pointing to a DataElement that does not belong to the program anymore. Due to the use of database-level constraints, this misconfiguration will appear as a Foreign Key error.

The SDK does not fail the synchronization, but it stores the errors in a table for inspection. They can be accessed by:

```java
d2.maintenanceModule().foreignKeyViolations()
```

## Data states

<!--DHIS2-SECTION-ID:data_states-->

Data objects have a read-only `state` property that indicates the current state of the object in terms of synchronization with the server. This state is maintained by the SDK.

The possible states are:

- `SYNCED`. The element is synced with the server. There are no local changes for this value.
- `TO_POST`. Data created locally that does not exist in the server yet.
- `TO_UPDATE`. Data modified locally that exists in the server.
- `UPLOADING`. Data is being uploaded. If it is modified before receiving any server response, its state is back to `TO_UPDATE`. When the server response arrives, its state does not change to `SYNCED`, but it remains in `TO_UPDATE` to indicate that there are local changes.
- `SENT_BY_SMS`. Data is sent by sms and there is no server response yet. Some servers does not have the capability to send a response, so this state means that data has been sent, but we do not know if it has been correctly imported in the server or not.
- `SYNCED_BY_SMS`. Data is sent by sms and there is a successful response from the server.
- `ERROR`. Data that received an error from the server after the last upload.
- `WARNING`. Data that received a warning from the server after the last upload.

Additionally, in `TrackedEntityInstance` we might have:

- `RELATIONSHIP`. This TrackedEntityInstance has been downloaded with the sole purpose of fulfilling a relationship to another TEI. This `RELATIONSHIP` TEI only has basic information (uid, type, etc) and the list of TrackedEntityAttributes to be able to print meaningful information about the relationship. Other data such enrollments, events or relationships is not downloaded for this TEI. Also, this TEI cannot be modified or uploaded to the server.

## Tracker data

<!--DHIS2-SECTION-ID:tracker_data-->

### Tracker data download

By default, the SDK only downloads TrackedEntityInstances and Events
that are located in user capture scope, but it is also possible to
download TrackedEntityInstances in search scope.

The tracked entity module contains the
`TrackedEntityInstanceDownloader`. The downloader follows a builder
pattern which allows the download of tracked entity instances filtering by
**different parameters** as well as define some **limits**. The same
behavior can be found within the event module for events.

The downloader track the latest successful download in order to avoid
downloading unmodified data. It makes use of paging with a best effort
strategy: in case a page fails to be downloaded or persisted, it is
skipped but it will continue with the next pages.

This is an example of how it can be used.
```java
d2.trackedEntityModule().trackedEntityInstanceDownloader()
    .[filters]
    .[limits]
    .download()
```
```java
d2.eventModule().eventDownloader()
    .[filters]
    .[limits]
    .download()
```

Currently it is possible to specify the next filters:

- `byProgramUid()`. Filters by program uid and downloads the not synced
  objects inside the program.
- `byUid()`. Filters by the tracked entity instance uid and downloads a
  unique object. This filter can be used to download the tracked entity
  instances found within search scope. (Only for tracked entity
  instances).

The downloader also allows to limit the number of downloaded objects.
These limits can also be combined with each other.

- `limit()`. Limit the maximum number of objects to download.
- `limitByProgram()`. Take the established limit and apply it for each
  program. The number of objects that will be downloaded will be the one
  obtained by multiplying the limit set by the number of user programs.
- `limitByOrgunit()`. Take the established limit and apply it for each
  organisation unit. The number of objects that will be downloaded will
  be the one obtained by multiplying the limit set by the number of user
  organisation units.

The next snippet of code shows an example of the
TrackedEntityInstanceDownloader usage.

```java
d2.trackedEntityModule().trackedEntityInstanceDownloader()
    .byProgramUid("program-uid")
    .limitByOrgunit(true)
    .limitByProgram(true)
    .limit(50)
    .download()
```

### Tracker data search

DHIS2 has a functionality to filter TrackedEntityInstances by related
properties, like attributes, organisation units, programs or enrollment
dates. The Sdk provides the the `TrackedEntityInstanceQueryCollectionRepository` 
with methods that allow the download of tracked entity
instances within the search scope. It can be found inside the tracked entity instance module.

The tracked entity instance query is a powerful tool that follows a
builder pattern and allows the download of tracked entity instances
filtering by **different parameters**.

```java
d2.trackedEntityModule().trackedEntityInstanceQuery()
    .[repository mode]
    .[filters]
    .download()
```

The source where the TEIs are retrieved from is defined by the **repository mode**.
These are the different repository modes available:

- `onlineOnly()`. Only TrackedEntityInstances coming from the server are
  returned in the list. Internet connection is required to use this mode.
- `offlineOnly()`. Only TrackedEntityInstances coming from local
  database are returned in the list.
- `onlineFirst()`. TrackedEntityInstances coming from the server are
  returned in first place. Once there are no more results online, it
  continues with TrackedEntityInstances in the local database. Internet
  connection is required to use this mode.
- `offlineFirst()`. TrackedEntityInstances coming from local database
  are returned in first place. Once there are no more results, it continues
  with TrackedEntityInstances coming from the server. This method may
  speed up the initial load. Internet connection is required to use this
  mode.

This repository follows the same syntax as other repositories.
Additionally, the repository offers different strategies to fetch data:

- `byAttribute()`. This method adds an *attribute* filter to the query.
  If this method is called several times, conditions are appended with an AND
  connector. For example:
    ```java
    d2.trackedEntityModule().trackedEntityInstanceQuery()
        .byAttribute("uid1").eq("value1")
        .byAttribute("uid2").eq("value2")
        .download()
    ```
    That means that the instance must have attribute `uid1` with value
    `value1` **AND** attribute `uid2` with value `value2`.
- `byFilter()`. This method adds a *filter* to the query. If this
  method is called several times, conditions are appended with an AND
  connector. For example:
    ```java
    d2.trackedEntityModule().trackedEntityInstanceQuery()
        .byFilter("uid1").eq("value1")
        .byFilter("uid2").eq("value2")
        .download()
    ```
    That means that the instance must have attribute `uid1` with value
    `value1` **AND** attribute `uid2` with value `value2`.
- `byQuery()`. Search tracked entity instances with **any** attribute
  matching the query.
- `byProgram()`. Filter by enrollment program. Only one program can be
  specified.
- `byOrgUnits()`. Filter by tracked entity instance organisation units.
  More than one organisation unit can be specified.
- `byOrgUnitMode()`. Define the organisation unit mode. The possible
  modes are the next:
  - **SELECTED**. Specified units only.
  - **CHILDREN**. Immediate children of specified units, including
    specified units.
  - **DESCENDANTS**. All units in sub-hierarchy of specified units,
    including specified units.
  - **ACCESSIBLE**. All organisation units accessible by the user
    (search scope).
  - **ALL**. All units in system.
- `byProgramStartDate()`. Define an enrollment start date. It only
  applies if a program has been specified.
- `byProgramEndDate()`. Define an enrollment end date. It only applies
  if a program has been specified.
- `byTrackedEntityType()`. Filter by TrackedEntityType. Only one type
  can be specified.
- `byIncludeDeleted()`. Whether to include or not deleted tracked entity
  instances. Currently this filter only applies to **offline**
  instances.
- `byStates()`. Filter by sync status. Using this filter forces
  **offline only** mode.

Example:

```java
d2.trackedEntityModule().trackedEntityInstanceQuery()
                .byOrgUnits().eq("orgunitUid")
                .byOrgUnitMode().eq(OrganisationUnitMode.DESCENDANTS)
                .byProgram().eq("programUid")
                .byAttribute("attributeUid").like("value")
                .offlineFirst()
```

After finding the tracked entity instances by searching it is possible
to fully download them using the `byUid()` filter of the
`TrackedEntityInstanceDownloader` within the tracked entity instance
module.

[//]: # (Include glass protected download)

### Tracker data write

In general, there are two different cases to manage data creation/edition/deletion: the case where the object is identifiable (that is, it has an `uid` property) and the case where the object is not identifiable.

**Identifiable objects** (TrackedEntityInstance, Enrollment, Event). These repositories have a `uid()` method that gives you access to edition methods for a single object. In case the object does not exist yet, it is required to create it first. A typical workflow to create/edit an object would be:

- Use the `CreateProjection` class to add a new instance in the repository.
- Save the uid returned by this method.
- Use the `uid()` method with the previous uid to get access to edition methods.

And in code this would look like:

```java
String eventUid = d2.eventModule().events().add(
    EventCreateProjection.create("enrollment", "program", "programStage", "orgUnit", "attCombo"));

d2.eventModule().events().uid(eventUid).setStatus(COMPLETED);
```

**Non-identifiable objects** (TrackedEntityAttributeValue, TrackedEntityDataValue). These repositories have a `value()` method that gives you access to edition methods for a single object. The parameters accepted by this method are the parameters that unambiguously identify a value.

For example, writing a TrackedEntityDataValue would be like:

```java
d2.trackedEntityModule().trackedEntityDataValues().value(eventUid, dataElementid).set(“5”);
```

### Tracker data upload

TrackedEntityInstance and Event repositories have an `upload()` method to upload Tracker data and Event data (without registration) respectively. If the repository scope has been reduced by filter methods, only filtered objects will be uploaded.

```java
d2.( trackedEntityModule() | eventModule() )
    .[ filters ]
    .upload();
```

#### Tracker conflicts

Server response is parsed to ensure that data has been correctly uploaded to the server. In case the server response includes import conflicts, these conflicts are stored in the database, so the app can check them and take an action to solve them.

```java
d2.importModule().trackerImportConflicts()
```

Conflicts linked to a TrackedEntityInstance, Enrollment or Event are
automatically removed after a successful upload of the object.

### Tracker data: reserved values

Tracked Entity Attributes configured as **unique** and **automatically generated** are generated by the server following a pattern defined by the user. These values can only be generated by the server, which means that we need to reserve them in advance so we can make use of them when operating offline.

The app is responsible for reserving generated values before going offline. This can be triggered by:

```java
// Reserve values for all the unique and automatically generated trackedEntityAttributes.
d2.trackedEntityModule().reservedValueManager().downloadAllReservedValues(numValuesToFillUp)

// Reserve values for a particular trackedEntityAttribute.
d2.trackedEntityModule().reservedValueManager().downloadReservedValues("attributeUid", numValuesToFillUp)
```

Depending on the time the app expects to be offline, it can decide the quantity of values to reserve. In case the attribute pattern is dependant on the orgunit code, the SDK will reserve values for all the relevant orgunits. More details about the logic in Javadoc.

Reserved values can be obtained by:

```java
d2.trackedEntityModule().reservedValueManager().getValue("attributeUid", "orgunitUid")
```

## Aggregated data

<!--DHIS2-SECTION-ID:aggregated_data-->

### Aggregated data download

```java
d2.aggregatedModule().data().download()
```

By default, the SDK downloads **aggregated data values**, **dataset
complete registration values** and **approvals** corresponding to:

- **DataSets**: all available dataSets (those the user has at least read
  data access to).
- **OrganisationUnits**: capture scope.
- **Periods**: all available periods, which means at least:
  - Days: last 60 days.
  - Weeks: last 13 weeks (including starting day variants).
  - Biweekly: last 13 bi-weeks.
  - Monthly: last 12 months.
  - Bimonthly: last 6 bi-months.
  - Quarters: last 5 quarters.
  - Sixmonthly: last 5 six-months (starting in January and April).
  - Yearly: last 5 years (including financial year variants).
  
  In addition, if any dataset allows data entry for **future periods**,
  the Sdk will download the data for those open periods and store them. 

The Sdk also keeps track of the latest successful download in order to
avoid downloading unmodified server data.

In the download of **data approvals**, workflow and attribute option
combination identifiers will be considered in addition to the
organisation units and periods. The different possible states for data
approval are:

- `UNAPPROVABLE`. Data approval does not apply to this selection. (Data
  is neither *approved* nor *unapproved*).
- `UNAPPROVED_WAITING`. Data could be approved for this selection, but
  is waiting for some lower-level approval before it is ready to be
  approved.
- `UNAPPROVED_ELSEWHERE`. Data is unapproved, and is waiting for
  approval somewhere else (can not be approved here).
- `UNAPPROVED_READY`. Data is unapproved, and is ready to be approved
  for this selection.
- `UNAPPROVED_ABOVE`. Data is unapproved above.
- `APPROVED_HERE`. Data is approved, and was approved here (so could be
  unapproved here).
- `APPROVED_ELSEWHERE`. Data is approved, but was not approved here (so
  cannot be unapproved here).
- `APPROVED_ABOVE`. Data is approved above.
- `ACCEPTED_HERE`. Data is approved and accepted here (so could be
  unapproved here).
- `ACCEPTED_ELSEWHERE`. Data is approved and accepted, but elsewhere.

Data approvals are downloaded only for versions greater than 2.29.

### Aggregated data write

#### Data value

DataValueCollectionRepository has a `value()` method that gives access to edition methods. The parameters accepted by this method are the parameters that unambiguosly identify a value.

```java
DataValueObjectRepository valueRepository = d2.dataValueModule().dataValues()
    .value("periodId", "orgunitId", "dataElementId", "categoryOptionComboId", "attributeOptionComboId");

valueRepository.set("value")
```

The data values of type `Image` have associated file resources to them.
The Sdk provides a repository to sync them, the **File resource
collection repository**. You can find more information about it in the
[*Dealing with FileResources*](#dealing-with-fileresources) section
below.

#### Data set complete registration

The Sdk provides within the data set module a collection repository for
data set complete registrations. This repository contains methods to add
new completions and delete them.

To add a new data set complete registration is available an `add()`
method:

```java
d2.dataSetModule().dataSetCompleteRegistrations()
    .add(dataSetCompleteRegistration);
```

In order to remove them from the database the repository has a `value()`
method that gives access to deletion methods (`delete()` and
`deleteIfExist()`). The parameters accepted by this method are the
parameters that unambiguosly identify the data set complete
registration.

```java
d2.dataSetModule().dataSetCompleteRegistrations()
    .value("periodId", "orgunitId", "dataSetUid","attributeOptionCombo")
    .delete()
```


### Aggregated data upload

DataValueCollectionRepository has an `uplaod()` method to upload aggregated data values.

```java
d2.dataValueModule().dataValues().upload();
```

### DataSet instances

A DataSetInstance in the SDK is a handy representation of the existing aggregated data. A DataSetInstance represents a unique combination of DataSet - Period - Orgunit - AttributeOptionCombo and includes extra information like sync state, value count or displayName for some properties.

```java
d2.dataSetModule().dataSetInstances()
    .[ filters ]
    .get()

// For example
d2.dataSetModule().dataSetInstances()
    .byDataSetUid().eq("datasetUid")
    .byOrganisationUnitUid().eq("orgunitUid")
    .byPeriod().in("201901", "201902")
    .get();
```

## Dealing with FileResources

<!--DHIS2-SECTION-ID:file_resources-->

The SDK offers a module (the `FileResourceModule`) and two helpers (the `FileResourceDirectoryHelper` and `FileResizerHelper`) that allow to work with files.

### File resources module

This module contains methods to download the file resources associated with the downloaded data and the file resources collection repository of the database.

- **File resources download**.
The `download()` method will search for the tracked entity attribute values ​​and tracked entity data values ​​whose tracked entity attribute type and data element type are of the image type and whose file resource has not been previously downloaded and the method will download the file resources associated.

    ```
    d2.fileResourceModule().download();
    ```

    After downloading the files, you can obtain the different file resources downloaded through the repository.

- **File resource collection repository**.
Through this repository it is possible to request files, save new ones and upload them to the server. 

    - **Get**. It behaves in a similiar fashion to any other Sdk repository. It allows to get collections by applying different filters if desired.
        ```
        d2.fileResourceModule().fileResources()
            .[ filters ]
            .get()
        ```
    - **Add**. To save a file you have to add it using the `add()` method of the repository by providing an object of type `File`. The `add()` method will return the uid that was generated when adding the file. This uid should be used to update the tracked entity attribute value or the tracked entity data value associated with the file resource.
        ```
        d2.fileResourceModule().fileResources()
            .add(file);
        ```
    - **Upload**. Calling the `upload()` method will trigger a series of successive calls in which all non-synchronized files will be sent to the server. After each upload, the server response will be processed. The server will provide a new uid to the file resource and the Sdk will automatically rename the file and update the `FileResource` object and the tracked entity attribute values ​​or tracked entity data values ​​associated with it.
        ```
        d2.fileResourceModule().fileResources()
            .upload()
        ```

### File resizer helper

The Sdk provides a helper to resize image files (`FileResizerHelper`). The helper is located in the `core.arch.helpers` package of the Sdk. This helper contains a static `resizeFile()` method that accepts the file you want to reduce and the dimension to which you want to reduce it.

The possible dimensions are in the following table.

| Small | Medium | Large  |
|-------|--------|--------|
| 256px | 512px  | 1024px |

The helper takes the file, measures the height and width of the image, determines which of the two sides is larger and reduces the largest of the sides to the given dimension and the other side is scaled to its proportional size. **Image scaling will always keep the proportions**.

In the event that the last image is smaller than the dimension to which you want to resize it, the same file will be returned without being modified.

The `resizeFile()` method will return a new file located in the same parent directory of the file to be resized under the name `resized-DIMENSION-` + the name of the file without resizing.

### File resource directory helper

Contained in the `core.arch.helpers` package of the Sdk is the `FileResourceDirectoryHelper`. This helper provides two methods.

- `getFileResourceDirectory()`. This method returns a `File` object whose path points to the `sdk_resources` directory where the Sdk will save the files associated with the file resources.

- `getFileCacheResourceDirectory()`. This method returns a `File` object whose path points to the `sdk_cache_resources` directory. This should be the place where volatile files are stored, such as camera photos or images to be resized. Since the directory is contained in the cache directory, Android may auto-delete the files in the cache directory once the system is about to run out of memory. Third party applications can also delete files from the cache directory. Even the user can manually clear the cache from Settings. However, the fact that the cache can be cleared in the methods explained above should not mean that the cache will automatically get cleared; therefore, the cache will need to be tidied up from time to time proactively.
