# Workflow { #android_sdk_workflow }

Currently, the SDK is primarily oriented to build apps that work in an offline mode. In short, the SDK maintains a local database instance that is used to get the work done locally (create forms, manage data, ...). When requested by the client, this local database is synchronized with the server.

A typical workflow would be like this:

1. **Login**
2. **Sync metadata:** the SDK downloads a subset of the server metadata so it is available to be used at any time. Metadata sync is totally user-dependent (see [Synchronization](#android_sdk_metadata_synchronization) for more details)
3. **Download data:** if you want to have existing data available in the device even when offline, you can download and save existing tracker and aggregated data in the device.
4. **Do the work:** at this point the app is able to create the data entry forms and show some existing data. Then the user can edit/delete/update data.
5. **Upload data:** from time to time, the work done in the local database instance is sent to the server.
6. **Sync metadata:** it is recommended to sync metadata quite often to detect changes in metadata configuration.

## Login/Logout { #android_sdk_login_logout }

Before interacting with the server it is required to login into the DHIS 2 instance.

```java
d2.userModule().logIn(username, password, serverUrl)

d2.userModule().logOut()
```

As of version 1.6.0, the SDK supports the storage of information for multiple accounts, which means keeping a separate database for each pair user-server. Despite of that, only one account can active (or logged in) simultaneously. That means that only one user can be authenticated in only one server at the same time. 

The number of maximum allowed accounts can be configured by the app (it defaults to one). A new account is automatically created after a successful login for a new pair user-server. If the number of accounts exceeds the maximum configured, the oldest account and its related database are automatically removed.

```java
// Get the account list
d2.userModule().accountManager().getAccounts();

// Get/set the maximum number of accounts
d2.userModule().accountManager().getMaxAccounts();
d2.userModule().accountManager().setMaxAccounts();

// Delete account for current user
d2.userModule().accountManager().deleteCurrentAccount();
```

The accountManager exposes an observable that emits an event when the current account is deleted. It includes the reason why the account was deleted.

```java
// Emits an event when the current account is deleted
d2.userModule().accountManager().accountDeletionObservable();
```

After a logout, the SDK keeps track of the last logged user so that it is able to differentiate recurring and new users. It also keeps a hash of the user credentials in order to authenticate the user even when there is no connectivity. Given that said, the login method will:

- If an authenticated user already exists: throw an error.
- Else if *Online*:
  - Try **login online**: the SDK will send the username and password to the API, which will determine whether they are correct. If successful:
        - If no database exists: create new database with encryption value from server.
        - If database for another [serverUrl, user] exists, delete it and create new database with encryption value from server. Not synced data of previously logged user will be permanently lost.
        - If database for the current [serverUrl, user] pair exists, open the database and encrypt or decrypt database if encryption status has changed in the server.
  - If user account has been disabled in server: delete database and throw an error.
- Else if *Offline*:
  - If the [serverUrl, user] pair was the last authenticated:
    - Try **login offline**: the SDK will verify that the credentials are the same as the last provided, which were previously validated by the API.
  - If the [serverUrl, user] pair was not the last authenticated: throw an error

Calling module or repository methods before a successful login or after a logout will result in "Database not created" errors.

Logout method removes user credentials, so a new login is required before any interaction with the server. Metadata and data is preserved so a user is able to logout/login without losing any information.

## Login with OpenID { #android_sdk_login_open_id }

The SDK includes support for OpenID. To perform a login using OpenID an OpenIDConnectConfig is required:

```java
OpenIDConnectConfig openIdConfig = new OpenIDConnectConfig(clientId, redirectUri, discoveryUri, authorizationUrl, tokenUrl);
```

It is mandatory to either provide a discoveryUri or both authorizationUrl and tokenUrl.

This configuration can be used to perform a login.

```java
d2.userModule().openIdHandler().logIn(openIdConfig)
```

This call returns an IntentWithRequestCode which in an android app allows starting the OpenID login screen from the configuration provider.

```java
startActivityForResult(intentWithRequestCode.getIntent(), intentWithRequestCode.getRequestCode());
```

Upon a successful login, the returned intent data can be used alongside the server url to start the sync.

```java
d2.userModule().openIdHandler().handleLogInResponse(serverUrl, data, requestCode);
```

It is mandatory to include the following activity in the application Manifest file:

```xml
<activity   android:name="net.openid.appauth.RedirectUriReceiverActivity"
            android:exported="true"
            tools:node="replace">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="<your redirect url scheme>" />
            </intent-filter>
</activity>
```

In order to configure all parameters check the following OpenID providers guidelines the server implements:

|OpenID Providers|
|----------------|
|[Google](https://github.com/openid/AppAuth-Android/blob/master/app/README-Google.md)          |
|[GitHub](https://docs.github.com/en/developers/apps/authorizing-oauth-apps)          |
|[ID-porten](https://docs.digdir.no/oidc_protocol_authorize.html)       |
|[OKTA](https://github.com/openid/AppAuth-Android/blob/master/app/README-Okta.md)            |
|[KeyCloak](https://www.keycloak.org/docs/latest/authorization_services/index.html#_service_authorization_api)        |
|[Azure AD](https://docs.microsoft.com/es-es/azure/active-directory-b2c/signin-appauth-android?tabs=app-reg-ga)        |
|[WS02](https://medium.com/@maduranga.siriwardena/configuring-appauth-android-with-wso2-identity-server-8d378835c10a)            |

## Metadata synchronization { #android_sdk_metadata_synchronization }

Metadata synchronization is usually the first step after login. It fetches and persists the metadata needed by the current user. To launch metadata synchronization we must execute:

```java
d2.metadataModule().download();
```

In order to save bandwidth usage and storage space, the SDK does not synchronize all the metadata in the server but a subset. This subset is defined as the metadata required by the user in order to perform data entry tasks: render programs and datasets, execute program rules, evaluate in-line program indicators, etc.

Based on that, metadata sync includes the following elements:

|   Element                     |   Condition or scope |
|-----------------------        |-------------|
| System info                   | All |
| System settings               | KeyFlag, KeyStyle |
| Android Settings App          | General settings, Synchronization, Appearance, Analaytics |
| User settings                 | KeyDbLocale, KeyUiLocale |
| User                          | Only authenticated user |
| UserRole                      | Roles assigned to authenticated user |
| Authority                     | Authorities assigned to authenticated user |
| Program                       | Programs that user has (at least) read data access to and that are assigned to any orgunit visible by the user |
| RelationshipTypes             | All the types visible by the user |
| OptionGroups                  | Only if server is greater than 2.29 |
| EventFilters                  | Those related to downloaded programs |
| TrackedEntityInstanceFilters  | Those related to downloaded programs |
| DataSet                       | DataSets that user has (at least) read data access to and that are assigned to any orgunit visible by the user |
| Validation rules              | Validation rules associated to the dataSets |
| OrganisationUnit              | OrganisationUnits in CAPTURE or SEARCH scope (descendants included) |
| OrganisationUnitGroup         | Groups assigned to downloaded organisationUnits |
| OrganisationUnitLevel         | All |
| Constant                      | All |
| Visualizations                | Visualizations assigned to Analytics settings (Android Settings App) |
| Indicators                    | Indicators assigned to downloaded dataSets and visualizations |
| SMS Module metadata           | Only if SMS module enabled |

In the case of Programs and DataSets, metadata sync includes all the metadata related to them: stages, sections, dataElements, options, categories, etc. Those elements that are not related to any Program or DataSet are not included.

### Corrupted configurations

This partial metadata synchronization may expose server-side misconfiguration issues. For example, a ProgramRuleVariable pointing to a DataElement that does not belong to the program anymore. Due to the use of database-level constraints, this misconfiguration will appear as a Foreign Key error.

The SDK does not fail the synchronization, but it stores the errors in a table for inspection. These errors can be accessed by:

```java
d2.maintenanceModule().foreignKeyViolations()
```

## Data states { #android_sdk_data_states }

Data objects have a read-only `syncState` property that indicates the current state of the object in terms of synchronization with the server. This state is maintained by the SDK. 

The possible states are:

- **SYNCED**. The element is synced with the server. There are no local changes for this value.
- **TO_POST**. Data created locally that does not exist in the server yet.
- **TO_UPDATE**. Data modified locally that exists in the server.
- **UPLOADING**. Data is being uploaded. If it is modified before receiving any server response, its state is back to `TO_UPDATE`. When the server response arrives, its state does not change to `SYNCED`, but it remains in `TO_UPDATE` to indicate that there are local changes.
- **SENT_VIA_SMS**. Data is sent via sms and there is no server response yet. Some servers do not have the capability to send a response, so this state means that data has been sent, but we do not know if it has been correctly imported in the server or not.
- **SYNCED_VIA_SMS**. Data is sent via sms and there is a successful response from the server.
- **ERROR**. Data that received an error from the server after the last upload.
- **WARNING**. Data that received a warning from the server after the last upload.

Additionally, in `TrackedEntityInstance`, `Enrollment` and `Events` we might have:

- **RELATIONSHIP**. This element has been downloaded with the sole purpose of fulfilling a relationship to another element. This `RELATIONSHIP` element only has basic information (uid, type, etc) and the list of TrackedEntityAttributes (in case of TrackedEntityInstances) to be able to print meaningful information about the relationship. Other data such as enrollments, events, notes, values or relationships are not downloaded. Also, this element cannot be modified or uploaded to the server.

Besides the property `syncState`, the classes `TrackedEntityInstance`, `Enrollment` and `Events` have a property called `aggregatedSyncState` that represents the sync state of its children. For example, if a dataValue is modified in an `Event`, the resulting states for the related objects would be:

| Element               | SyncState   | AggregatedSyncState |
|-----------------------|-------------|---------------------|
| TrackedEntityInstance | SYNCED      | TO_UPDATE           |
| Enrollment            | SYNCED      | TO_UPDATE           |
| Event                 | TO_UPDATE   | TO_UPDATE           |

## Tracker data { #android_sdk_tracker_data }

### Tracker data download

> **Important**
>
> See [Settings App](#android_sdk_settings_app) section to know how this application can be used to control synchronization parameters.

By default, the SDK only downloads TrackedEntityInstances and Events
that are located in user capture scope, but it is also possible to
download TrackedEntityInstances in search scope.

The tracked entity module contains the
`TrackedEntityInstanceDownloader`. The downloader follows a builder
pattern which allows the download of tracked entity instances filtering by
**different parameters** as well as defining some **limits**. The same
behavior can be found within the event module for events.

The downloader tracks the latest successful download in order to avoid
downloading unmodified data. It makes use of paging with a best effort
strategy: in case a page fails to be downloaded or persisted, it is
skipped and it will continue with the next pages.

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

Currently, it is possible to specify the next filters:

- `byProgramUid()`. Filters by program uid and downloads the not synced
  objects inside the program.
- `byUid()`. Filters by the tracked entity instance uid and downloads a
  unique object. This filter can be used to download the tracked entity
  instances found within search scope. (Only for tracked entity
  instances).
- `byProgramStatus()`. Filters those tracked entity instances that have a enrollment with the given status.

The downloader also allows to limit the number of downloaded objects.
These limits can also be combined with each other.

- `limit()`. Limit the maximum number of objects to download.
- `limitByProgram()`. Take the established limit and apply it to each
  program. The number of objects that will be downloaded will be the one
  obtained by multiplying the limit set by the number of user programs.
- `limitByOrgunit()`. Take the established limit and apply it for each
  organisation unit. The number of objects that will be downloaded will
  be the one obtained by multiplying the limit set by the number of user
  organisation units.

Other properties:

- `overwrite()`. By default, the SDK does not overwrite data in the device in a status other than SYNCED. If you want to overwrite the data in the device, no matter the status it has, add this method to the query chain.

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

Additionally, if you want the images associated to `Image` data values available to be downloaded in the device, you must download them. See [*Dealing with FileResources*](#android_sdk_file_resources) section for more details.

### Tracker data search

DHIS2 has a functionality to filter TrackedEntityInstances by related
properties, like attributes, organisation units, programs or enrollment
dates. The Sdk provides the `TrackedEntityInstanceQueryCollectionRepository` 
with methods that allow the download of tracked entity
instances within the search scope. It can be found inside the tracked entity instance module.

The tracked entity instance query is a powerful tool that follows a
builder pattern and allows the download of tracked entity instances
filtering by **different parameters**.

```java
d2.trackedEntityModule().trackedEntityInstanceQuery()
    .[repository mode]
    .[filters]
    .get()
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
      .get()
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
      .get()
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
  - **ALL**. All units in system. Requires authority.
- `byProgramStartDate()`. Define an enrollment start date. It only
  applies if a program has been specified.
- `byProgramEndDate()`. Define an enrollment end date. It only applies
  if a program has been specified.
- `byTrackedEntityType()`. Filter by TrackedEntityType. Only one type
  can be specified.
- `byIncludeDeleted()`. Whether to include or not deleted tracked entity
  instances. Currently, this filter only applies to **offline**
  instances.
- `byStates()`. Filter by sync status. Using this filter forces
  **offline only** mode.
- `byTrackedEntityInstanceFilter()`. Also know as **working lists**, trackedEntityInstanceFilters are a predefined set of query parameters.

Example:

```java
d2.trackedEntityModule().trackedEntityInstanceQuery()
                .byOrgUnits().eq("orgunitUid")
                .byOrgUnitMode().eq(OrganisationUnitMode.DESCENDANTS)
                .byProgram().eq("programUid")
                .byAttribute("attributeUid").like("value")
                .offlineFirst()
```

> **Important**
>
> TrackedEntityInstances retrieved using this repository are not persisted in the database. It is possible
to fully download them using the `byUid()` filter of the `TrackedEntityInstanceDownloader` within the tracked entity instance module.

It could happen that you add filters to the query repository in different parts of the application and you don't have a clear picture about the filters applied, specially when using working lists because they add a set of parameters. In order to solve this, you can access the filter scope at any moment in the repository:

```java
d2.trackedEntityModule().trackedEntityInstanceQuery()
    .[ filters ]
    .getScope();
```

In addition to the standard `getPaged(int)` and `getDataSource()` methods that are available in all the repositories, the TrackedEntityInstanceQuery repository exposes a method to wrap the response in a `Result` object: the `getResultDataSource()`. This method is kind of a workaround to deal with the lack of error management in the Version 2 of the Android Paging Library (it is hardly improved in version 3). Using this dataSource you can catch search errors, such as "Min attributes required" or "Max tei count reached". 


*Working lists / Tracked entity instance filters*

Tracked entity instance filters are a predefined set of search parameters. They are defined in the server and can be used to create task-oriented filters for end-users.

```java
d2.trackedEntityModule().trackedEntityInstanceFilters()
    .[ filters ]
    .get();
```

### Ownership

The concept of ownership is supported in the SDK. In short, each pair trackedEntityInstance - program is owned by an organisationUnit. This ownership is used in the trackedEntityInstance search to determine the owner organisationUnit the TEI belongs to.

You can get the program owners for each trackedEntityInstance by using the repository:

```java
d2.trackedEntityModule().trackedEntityInstances()
        .withProgramOwners()
        .get();
```

Also, you can permanently transfer the ownership by using the OwnershipManager. This transfer will be automatically uploaded to the server in the next synchronization. 

```java
d2.trackedEntityModule().ownershipManager()
        .transfer(teiUid, programUid, ownerOrgunit);
```

### Break the glass

The "Break the glass" concept is based on the ownership of the pair trackedEntityInstance - enrollment. If the program is **PROTECTED** and the user does not have **DATA CAPTURE** to the organisation unit, it is required to break the glass in order to read and modify the data. The workflow would be:

1. Search for any tracked entity instances in **SEARCH** scope. It is important to not include the program uid in the query: the server will only return those TEIs that are accessible to the user, so protected TEIs in search scope won't be returned (otherwise, the user would know if the TEIs is enrolled or not without giving any reason).
2. Download the TEI using the downloader and specify the **TEI uid** and the **program uid**. It is important to include both parameters to force the ownership error.
3. Catch the error, if any, and check if it is an OWNERSHIP_ACCESS_DENIED error.
4. If so, request the ownwership using the ownership module (see code snippet below).
5. Try again the query in step 2.

```java
TrackedEntityInstanceDownloader teiRepository = d2.trackedEntityModule().trackedEntityInstanceDownloader()
        .byUid().eq(teiUid)
        .byProgramUid(programUid);

try {
    teiRepository.blockingDownload();
} catch (RuntimeException e) {
    if (e.getCause() instanceof D2Error &&
            ((D2Error) e.getCause()).errorCode() == D2ErrorCode.OWNERSHIP_ACCESS_DENIED) {
        // Show a dialog to the user and capture the reason to break the glass
        String reason = "Reason to break the glass";

        // Break the glass
        d2.trackedEntityModule().ownershipManager()
                .blockingBreakGlass(teiUid, programUid, reason);

        // Download again
        teiRepository.blockingDownload();
    } else {
        // Deal with other exceptions
    }
}
```

It is recommended to upload the data immediately after if has been edited because the ownership expires in two hours (it could depend on DHIS2 versions). If the ownership has expired when the user tries to upload the data, the SDK will automatically perform a "break-the-glass" query in the background using the original reason and add the prefix "Android App sync:". In this way, an administrator could easily identify that this operation is not a real break the glass, but just an auxiliary query to perform the synchronization.

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

Data values of type `Image` involve an additional step to create/update/read the associated file resource. More details in the [*Dealing with FileResources*](#android_sdk_file_resources) section below.

#### Write events in read-only TEIs

It is important to pay special attention to user's data access to the TEIs, enrollments and events. The SDK modify the status of the data when any *write* method is executed in order to upload it to the server in the next synchronization. If a user has no write data access to a particular element, the app should prevent the edition of this element.

The restrictions that must be followed by the app are these ones:

- **TrackedEntityInstances:** the user must have write data access to the **TrackedEntityType**.
- **Enrollemnts:** the user must have write data access to **both the TrackedEntityType and the Program** (this additional restriction is imposed by the SDK).
- **Events:** the user must have write data access to the **ProgramStage**.

### Tracker data upload

TrackedEntityInstance and Event repositories have an `upload()` method to upload Tracker data and Event data (without registration) respectively. If the repository scope has been reduced by filter methods, only filtered objects will be uploaded.

```java
d2.( trackedEntityModule() | eventModule() )
    .[ filters ]
    .upload();
```

Data whose state is `ERROR` or `WARNING` cannot be uploaded. It is required to solve the conflicts before attempting a new upload: this means to do a modification in the problematic data, which forces their state back to `TO_UPDATE`.

As of version 2.37, a new tracker importer was introduced (`/api/tracker` endpoint). The default tracker importer is still the legacy one (`/api/trackedEntityInstances`), but you can opt-in to use this new tracker importer by using the Android Settings webapp (see [Synchronization](#android_sdk_synchronization_settings)). This is internal to the SDK; the API exposed to the app does not change.

File resources must be uploaded in a different post call before tracker data upload. The query to post file resources is:

```java
d2.fileResourceModule().fileResources().upload();
```

More information about file resources in the section [*Dealing with FileResources*](#android_sdk_file_resources).

#### Tracker conflicts

Server response is parsed to ensure that data has been correctly uploaded to the server. In case the server response includes import conflicts, these conflicts are stored in the database, so the app can check them and take an action to solve them.

```java
d2.importModule().trackerImportConflicts()
```

Conflicts linked to a TrackedEntityInstance, Enrollment or Event are automatically removed after a successful upload of the object.

The SDK tries to identify the confliction dataElement or attribute by parsing the server response. If so, it also stores the value of the element when the conflict happened so that the application can highlight the element in form when the value has not been fixed yet.

### Tracker data: reserved values

Tracked Entity Attributes configured as **unique** and **automatically generated** are generated by the server following a pattern defined by the user. These values can only be generated by the server, which means that we need to reserve them in advance so we can make use of them when operating offline.

The app is responsible for reserving generated values before going offline. This can be triggered by:

```java
// Reserve values for all the unique and automatically generated trackedEntityAttributes.
d2.trackedEntityModule().reservedValueManager().downloadAllReservedValues(numValuesToFillUp)

// Reserve values for a particular trackedEntityAttribute.
d2.trackedEntityModule().reservedValueManager().downloadReservedValues("attributeUid", numValuesToFillUp)
```

Depending on how long the app expects to be offline, it can decide the quantity of values to reserve. In case the attribute pattern is dependant on the orgunit code, the SDK will reserve values for all the relevant orgunits. More details about the logic in Javadoc.

Reserved values can be obtained by:

```java
d2.trackedEntityModule().reservedValueManager().getValue("attributeUid", "orgunitUid")
```

### Tracker data: relationships

The SDK supports all types of relationships. They are downloaded when syncing and can be accessed and created or modified. 


|                    | TEI        | Enrollment   | Event      |
|--------------------|:----------:|:------------:|:----------:|
| **TEI**            | X          | X            | X          |
| **Enrollment**     | X          | X            | X          |
| **Event**          | X          | X            | X          |
_Supported relationships_

Relationships are accessed by using the relationships module.

Query relationships associated to a TEI.

```java
d2.relationshipModule().relationships().getByItem(
    RelationshipHelper.teiItem("trackedEntityInstanceUid")
)
```

Query relationships associated to an enrollment.

```java
d2.relationshipModule().relationships().getByItem(
    RelationshipHelper.enrollmentItem("enrollmentUid")
)
```

Or query relationships associated to an event.

```java
d2.relationshipModule().relationships().getByItem(
    RelationshipHelper.eventItem("eventUid")
)
```

In the same module you can create new relationships of any type using the `RelationshipHelper` to model the relationship and adding them later to the relationship collection repository:

```java
Relationship relationship = RelationshipHelper.teiToTeiRelationship("fromTEIUid", "toTEIUid", "relationshipTypeUid");

d2.relationshipModule().relationships().add(relationship);
```

If the related trackedEntityInstance does not exist yet and there are attribute values that must be inherited, you can use the following method to inherit attribute values from one TEI to another in the context of a certain program. Only those attribute marked as `inherit` will be inherited.

```java
d2.trackedEntityModule().trackedEntityInstanceService()
    .inheritAttributes("fromTeiUid", "toTeiUid", "programUid");
```

## Aggregated data { #android_sdk_aggregated_data }

### Aggregated data download

> **Important**
>
> See [Settings App](#android_sdk_settings_app) section to know how this application can be used to control synchronization parameters.

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
- `UNAPPROVED_ELSEWHERE`. Data is unapproved and is waiting for
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

#### Periods

In order to write data values or data set complete registrations, it's mandatory to provide a period id. Periods are stored in a table in the database and
the provided period ids must be already present in that table, otherwise, a Foreign Key error will be thrown. To prevent that situation, the `PeriodHelper` is
exposed inside the `PeriodModule`. Before adding aggregated data related to a dataSet, the following method must be called:

```java
Single<List<Period>> periods = d2.periodModule().periodHelper().getPeriodsForDataSet("dataSetUid");
```

This will ensure that: 
1. The app will pick one of the given periods, preventing malformed or wrong periods.
2. The app will only be able to pick the future periods defined by the field `DataSet.openFuturePeriods`.
3. The app will only be able to pick the past periods defined based on the limits declared on the section Aggregated Data Download.

#### Data value

DataValueCollectionRepository has a `value()` method that gives access to edition methods. The parameters accepted by this method are the parameters that unambiguously identify a value.

```java
DataValueObjectRepository valueRepository = d2.dataValueModule().dataValues()
    .value("periodId", "orgunitId", "dataElementId", "categoryOptionComboId", "attributeOptionComboId");

valueRepository.set("value")
```

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

In order to remove them from the database, the repository has a `value()`
method that gives access to deletion methods (`delete()` and
`deleteIfExist()`). The parameters accepted by this method are the
parameters that unambiguously identify the data set complete
registration.

```java
d2.dataSetModule().dataSetCompleteRegistrations()
    .value("periodId", "orgunitId", "dataSetUid","attributeOptionCombo")
    .delete()
```

### Aggregated data upload

DataValueCollectionRepository has an `upload()` method to upload aggregated data values.

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

If you only need a high level overview of the aggregated data status, you can use the repository `DataSetInstanceSummary`. It accepts the same filters and returns a count of `DataSetInstance` for each combination.

## Dealing with FileResources { #android_sdk_file_resources }

The SDK offers a module (the `FileResourceModule`) and two helpers (the `FileResourceDirectoryHelper` and `FileResizerHelper`) that allow to work with files.

In the context of a mobile connection, dealing with fileResources could be high bandwidth consuming. For this reason, fileResources are not downloaded by default when downloading data and they must be explicitly downloaded if wanted. The recommendation is to download to fileResources only if it is important to have them in the device. If they are not downloaded, there is no negative consequence in terms of data integrity; the only consequence is that they are not available in the device.

On the other hand, fileResource upload is not optional: the SDK will upload all the fileResources created in the device when uploading data. This is important in order to have successful synchronizations and keep data integrity.

### File resources module

This module contains methods to download the file resources associated with the downloaded data and the file resources collection repository of the database.

- **File resources download**.
The `fileResourceDownloader()` offers methods to filter the fileResources we want to download. It will search for values that match the filters and whose file resource has not been previously downloaded.

  ```java
  d2.fileResourceModule().fileResourceDownloader()
      .byDomainType().eq(FileResourceDomainType.TRACKER)
      .byElementType().eq(FileResourceElementType.DATA_ELEMENT)
      .byValueType().in(FileResourceValueType.IMAGE, FileResourceValueType.FILE_RESOURCE)
      .byMaxContentLength().eq(2000000)
      .download();
  ```

  The SDK has a default maxContentLength of 6000000.

  After downloading the files, you can obtain the different file resources downloaded through the repository.

- **File resource collection repository**.
Through this repository it is possible to request files, save new ones and upload them to the server. 

  - **Get**. It behaves in a similar fashion to any other SDK repository. It allows to get collections by applying different filters if desired.
  
    ```java
    d2.fileResourceModule().fileResources()
        .[ filters ]
        .get()
    ```

  - **Add**. To save a file you have to add it using the `add()` method of the repository by providing an object of type `File`. The `add()` method will return the uid that was generated when adding the file. This uid should be used to update the tracked entity attribute value or the tracked entity data value associated with the file resource.

    ```java
    d2.fileResourceModule().fileResources()
        .add(file); // Single<String> The fileResource uid
    ```

### File resizer helper

The Sdk provides a helper to resize image files (`FileResizerHelper`). This helper contains a `resizeFile()` method that accepts the file you want to reduce and the dimension to which you want to reduce it.

The possible dimensions are in the following table.

| Small | Medium | Large  |
|-------|--------|--------|
| 256px | 512px  | 1024px |

The helper takes the file, measures the height and width of the image, determines which of the two sides is larger and reduces the largest of the sides to the given dimension and the other side is scaled to its proportional size. **Image scaling will always keep the proportions**.

In the event that the last image is smaller than the dimension to which you want to resize it, the same file will be returned without being modified.

The `resizeFile()` method will return a new file located in the same parent directory of the file to be resized under the name `resized-DIMENSION-` + the name of the file without resizing.

### File resource directory helper

The `FileResourceDirectoryHelper` helper class provides two methods.

- `getFileResourceDirectory()`. This method returns a `File` object whose path points to the `sdk_resources` directory where the SDK will save the files associated with the file resources.

- `getFileCacheResourceDirectory()`. This method returns a `File` object whose path points to the `sdk_cache_resources` directory. This should be the place where volatile files are stored, such as camera photos or images to be resized. Since the directory is contained in the cache directory, Android may auto-delete the files in the cache directory once the system is about to run out of memory. Third party applications can also delete files from the cache directory. Even the user can manually clear the cache from Settings. However, the fact that the cache can be cleared in the methods explained above should not mean that the cache will automatically get cleared; therefore, the cache will need to be tidied up from time to time proactively.
