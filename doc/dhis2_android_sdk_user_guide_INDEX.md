<!--DHIS2-SECTION-ID:index-->

# SDK Developer user guide

## Compatibility: SDK / core / Android

| SDK      | DHIS2 core       | Android SDK |
|----------|------------------|-------------|
| 0.17.0   | 2.30, 2.31, 2.32 | 19 - 28     |

## Overview

Android library that abstracts the complexity of interaction with DHIS2 api.

Goals:

- Work offline (internal database).
- Communicate with DHIS2 instances.
- Facilitate the development of Android apps.

## Libraries

## Getting started

### Installation

Include dependency in build.gradle.

```gradle
dependencies {
    implementation "org.hisp.dhis:android-core:0.17.0-SNAPSHOT"
    ...
}
```

### D2 initialization

In order to start using the SDK, the first step is to initialize a `D2` object. The helper class `D2Manager` offers static methods to setup and initialize the `D2` instance. Also, it ensures that `D2` is a singleton across the application.

```java
D2Configuration configuration = D2Configuration.builder()
    .appName("app_name").appVersion("1.0.0")
    .context(context)
    .readTimeoutInSeconds(30)
    .connectTimeoutInSeconds(30)
    .writeTimeoutInSeconds(30)
    .build();

Single<D2> d2Single = D2Manager.setUp(d2Configuration)
                .andThen(D2Manager.setServerUrl(serverUrl))
                .andThen(D2Manager.instantiateD2());

D2 d2 = D2Manager.getD2();
```

The object `D2Configuration` receives the following attributes:

|  Attribute    |   Required    |   Description |
|-|-|-|
| context       | true          | Application context |
| appName       | true          | Use to create the "user-agent" header |
| appVersion    | true          | Use to create the "user-agent" header |
| readTimeoutInSeconds | true   | Read timeout for http queries |
| connectTimeoutInSeconds | true| Connect timeout for http queries |
| writeTimeoutInSeconds | true  | Write timeout for http queries |
| interceptors  | false         | Interceptors for OkHttpClient |
| networkInterceptors | false   | NetworkInterceptors for OkHttpClient |

## Modules and repositories

`D2` object is the entry point to interact with the SDK. The SDK forces `D2` object to be a singleton across the application.

Modules are the layer below `D2`. They act as a wrapper for related functionality. A module includes some related repositories and might expose some services and helpers.

Repositories act as a facade for the DB (or web API in some cases). They offer read capabilities for metadata and read/write for data.

### Dealing with return types: RxJava

The SDK uses RxJava classes (Single, Completable, Flowable) as the preferred return type for all the methods. The reasons for choosing RxJava classes are mainly two:

- **To facilitate the asynchronous treatment of returned objects.** Most of the actions in the SDK are time consuming and must be executed in a secondary thread. These return types force the app to deal with the asynchronous behavior.
- **To notify about progress.** Methods like metadata or data sync might take several minutes to finish. From user experience it is very helpful to have a sense of progress.

This does not mean that applications are forced to use RxJava in their code: they are only force to deal with their asynchronous behavior. RxJava classes include built-in methods to make them synchronous.

For example, the same query using RxJava and AsyncTask:

*Using RxJava*

```java
d2.programModule().programs
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .getAsync()
    .subscribe(programs -> {}); //List<Program>
```

*Using AsyncTask*

```java
new AsyncTask<Void, Void, List<Program>>() {
    protected List<Program> doInBackground() {
        return d2.programModule().programs.getAsync().blockingGet();
    }
    
    protected void onPostExecute(List<Program> programs) {

    }
}.execute();
```

### Query building

Repositories offer a builder syntax with compile-time validation to access the resources. A typical query is composed of some modifiers (filter, order, nested fields) and ends with an action (get, count, getPaged,...).

```java
// Generic syntax
d2.<module>.<repository>
    .[ filter | orderBy | nested fields ]
    .<action>;

// An example for events
d2.eventModule().events
    .byOrganisationUnitUid().eq("DiszpKrYNg8")
    .byEventDate().after(Date("2019-05-05"))
    .orderByEventDate(DESC)
    .withTrackedEntityDataValues()
    .get();
```

#### Filters

Repositories expose the list of available filters prefixed by the keyword "by". The list of filter operators available for each filter is dependant on the filter value type: for example, a value type `Date` will offer operators like `after`, `before`, `inPeriods`, while a value type `Boolean` will offer `isFalse` or `isTrue`.

Several filters can be appended to the same query in any order. Filters are joined globally using the operator "AND". This means that a query like

```java
d2.eventModule().events
    .byOrganisationUnitUid().eq("DiszpKrYNg8")
    .byEventDate().after(Date("2019-05-05"))
    ...
```

will return the events assigned to the orgunit "DiszpKrYNg8" **AND** whose eventDate is after "2019-05-05".

#### Order by

Ordering modifiers are prefixed by the keyword "orderBy".

Several "orderBy" modifiers can be appended to the same query. The order of the "orderBy" modifiers within the query determines the order priority. This means that a query like

```java
d2.eventModule().events
    .orderByEventDate(DESC)
    .orderByLastUpdated(DESC)
    ...
```

will order by EventDate descendant in first place, and then by LastUpdated descendant.

#### Include nested fields

Repositories return classes that are not an exact match of database tables: they are more complex objects that might include some properties obtained from other tables. For example, the `Event` class has a property called `trackedEntityDataValues` that include a list of TrackedEntityDataValues. The main reason to choose this kind of objects is to absorb the complexity of dealing with link tables so the app does not have to care about building links between objects.

Due to performance issues, this kind of properties are not included by default: they must be queried explicitly. In the repositories, the properties that are not included by default and need to be queried are prefixed by the keyword "with".

Several properties can be appended in the same query in any order. For example, a query like

```java
d2.programModule().programs
    .withStyle()
    .withTrackedEntityType()
    ...
```

will return a program with a nested `ObjectStyle` (color, icon) and a nested `TrackedEntityType`.

### Module list

System:

- maintenanceModule
- systemInfoModule
- systemSettingModule

Metadata / data:

- categoryModule
- constantModule
- dataElementModule
- dataSetModule
- optionModule
- dataValueModule
- enrollmentModule
- eventModule
- importModule
- indicatorModule
- legendSetModule
- programModule
- organisationUnitModule
- periodModuleModule
- relationshipModule
- trackedEntityModule
- userModule
- smsModule

## Workflow

Currently, the SDK is primarily oriented to build apps that work most of the time in an offline mode. In short, the SDK maintains a local database instance that is used to get the work done locally (create forms, manage data, ...). From time to time, this local database is synchronized with the server.

A typical workflow would be like this:

1. Login
2. Sync metadata: the SDK the metadata so it is available to be used at any time. Metadata sync is totally user-dependent (see [Synchronization](...) for more details)
3. Download data:
4. Do the work: at this point the app is able to create the data entry forms and show some existing data. Then the user can edit/delete/update data.
5. Upload data: from time to time, the work done in the local database instance is sent to the server.
6. Sync metadata: it is recommended to sync metadata quite often to detect changes in metadata configuration.

### Login/Logout

Before interacting with the server it is required to login into the DHIS 2 instance. Currently, the SDK does only support one pair "user - server" simultaneously. That means that only one user can be authenticated in only one server at the same time.

```java
d2.userModule().logIn(username, password)

d2.userModule().logOut()
```

After a logout the SDK keeps track of the last logged user so that it is able to differentiate recurring and new users. It also keeps a hash of the user credentials in order to authenticate the user even when there is no connectivity. Given that said, the login method will:

- If an authenticated user already exists: throw an error.
- If user account has been disabled in server: wipe DB and throw an error.
- If login is successful:
  - If user is different than last logged user: wipe DB and try **login online**.
  - If server is different (even if the user is the same): wipe DB and try **login online**.
- If no internet connection is present:
  - If the user has been ever authenticated:
    - If server is the same: try **login offline**.
    - If server is different: throw an error.
  - If the user has not been authenticated before: throw an error.

Logout method removes user credentials, so a new login is required before any interaction with the server. Metadata and data is preserved so a user is able to logout/login without losing any information.

### Metadata synchronization

Command to launch metadata synchronization:

```java
d2.syncMetaData()
```

In order to save bandwidth usage and storage space, the SDK does not synchronize all the metadata in the server but a subset. This subset is defined as the metadata required by the user in order to perform data entry tasks: render programs and datasets, execute program rules, evaluate in-line program indicators, etc.

Based on that, metadata sync includes the following elements:

|   Element             |   Condition |
|-----------------------|-------------|
| System info           | - |
| System settings       | KeyFlag, KeyStyle |
| User                  | Only authenticated user |
| UserRole              | Roles assigned to authenticated user |
| Authority             | Authorities assigned to authenticated user |
| Program               | Programs that user has (at least) read data access to and that are assigned to any orgunit visible by the user |
| RelationshipTypes     | - |
| OptionGroups          | Server is greater than 2.29 |
| DataSet               | DataSets that user has (at least) read data access to and that are assigned to any orgunit visible by the user |
| Indicators            | Indicators assigned to any dataSet |
| OrganisationUnit      | OrganisationUnits in CAPTURE or SEARCH scope (include descendants) |
| OrganisationUnitGroup | Groups assigned to downloaded organisationUnits |
| OrganisationUnitLevel | - |
| Constant              | - |
| SMS Module metadata   | Only if SMS module enabled |

#### Corrupted configurations

This partial metadata synchronization may expose server-side misconfiguration issues. For example, a ProgramRuleVariable pointing to a DataElement that does not belong to the program anymore. Due to the use of database-level constraints, this misconfiguration will appear as a Foreign Key error.

The SDK does not fail the synchronization, but it stores the errors in a table for inspection. They can be accessed by:

```java
d2.maintenanceModule().foreignKeyViolations
```

### Data states

Data objects have a read-only `state` property that indicates the current state of the object in terms of synchronization with the server. This state is maintained by the SDK.

The possible states are:

- `SYNCED`. The element is synced with the server. There are no local changes for this value.
- `TO_POST`. Data created locally that does not exist in the server yet.
- `TO_UPDATE`. Data modified locally that exists in the server.
- `TO_DELETE`. Data deleted locally that still exists in the server.
- `ERROR`. Data that received an error from the server after the last upload.
- `WARNING`. Data that received a warning from the server after the last upload.

### Tracker data

#### Tracker data download

By default, the SDK only downloads TrackedEntityInstances and Events that are located in user capture scope.

```java
d2.trackedEntityModule().downloadTrackedEntityInstances(500, false, false)
```

It keeps track of the latest successful download in order to void downloading unmodified data. It makes use of paging with a best effort strategy: in case a page fails to be downloaded or persisted, it is skipped and the rest of pages are persisted.

Currently it is possible to specify the maximum number of TEIs to download and apply this limit globally, per program and/or per orgunit. For example:

- Given a max number N, we can download the following number of TEIs in total:
  - Globally: N
  - Per orgunit: N x (Number of orgunits)
  - Per program: N x (Number of programs)
  - Per orgunit AND per program: N x (Number of combinations orgunit-program)

TrackedEntityInstances located in search scope can be downloaded by using a different method. In this case it is required to provide the TEI uid, which might be obtained with a search query.

```java
d2.downloadTrackedEntityInstancesByUid(uid-list)
```

[//]: # (Include glass protected download)

There is a similar method for Events with the same behavior.

```java
d2.eventModule().downloadSingleEvents(500, false, false)
```

#### Tracker data write

In general, there are two different cases to manage data creation/edition/deletion: the case where the object is identifiable (that is, it has an `uid` property) and the case where the object is not identifiable.

**Identifiable objects** (TrackedEntityInstance, Enrollment, Event). These repositories have an `uid()` method that gives you access to edition methods for a single object. In case the object does not exist yet, it is required to create it first. A typical workflow to create/edit an object would be:

- Use the `CreateProjection` class to add a new instance in the repository.
- Save the uid returned by this method.
- Use the `uid()` method with the previous uid to get access to edition methods.

And in code this would look like:

```java
String eventUid = d2.eventModule().events.add(
    EventCreateProjection.create("enrollent", "program", "programStage", "orgUnit", "attCombo"));

d2.eventModule().events.uid(eventUid).setStatus(COMPLETED);
```

**Non-identifiable objects** (TrackedEntityAttributeValue, TrackedEntityDataValue). These repositories have a `value()` method that gives you access to edition methods for a single object. The parameters accepted by this method are the parameters that unambiguously identify a value.

For example, writing a TrackedEntityDataValue would be like:

```java
d2.trackedEntityModule().trackedEntityDataValues.value(eventUid, dataElementid).set(“5”);
```

#### Tracker data upload

TrackedEntityInstance and Event repositories have an `upload()` method to upload Tracker data and Event data (without registration) respectively. If the repository scope has been reduced by filter methods, only filtered objects will be uploaded.

```java
d2.( trackedEntityModule() | eventModule() )
    .[ filters ]
    .upload();
```

##### Tracker conflicts

Server response is parsed to ensure that data has been correctly uploaded to the server. In case the server response includes import conflicts, these conflicts are stored in the database, so the app can check them and take an action to solve them.

```java
d2.importModule().trackerImportConflicts
```

Conflicts linked to a TrackedEntityInstance, Enrollment or Event are automatically removed after a successful upload of the object.

#### Tracker data query (search)

DHIS2 has a functionality to filter TrackedEntityInstances by related properties, like attributes, orgunits, programs or enrollment dates. In the SDK, this functionality is exposed in the `TrackedEntityInstanceQueryCollectionRepository`.

This repository requires a `TrackedEntityInstanceQuery` object, which contains the query filters.

Additionally, this repository offers different strategies to fetch data:

- **Offline only**: show only TEIs stored locally.
- **Offline first**: show TEIs stored locally in first place; then show TEIs in the server (duplicated TEIs are not shown).
- **Online only**: show only TEIs in the server.
- **Online fist**: show TEIs in the server in first place; then show TEIs stored locally.

Example:

```java
TrackedEntityInstanceQuery query = TrackedEntityInstanceQuery.builder()
                .paging(true).page(1).pageSize(50)
                .orgUnits("orgunitUid").orgUnitMode(DESCENDANTS)
                .program("programUid")
                .query(QueryFilter.builder()
                        .filter("filter")
                        .operator(LIKE)
                        .build())
                .build();

d2.trackedEntityModule().trackedEntityInstanceQuery.query(query).offlineFirst()
```

#### Tracker data: reserved values

Tracked Entity Attributes configured as "unique" and "automatically generated" are generated by the server following a pattern defined by the user. These values can only be generated by the server, which means that we need to reserve them in advance so we can make use of them when operating offline.

```java
d2.trackedEntityModule().reservedValueManager.syncReservedValues("attributeUid", "orgunitUid", numValues)
```

The app is responsible for triggering the synchronization of reserved values before going offline.

Depending on the time the app expects to be offline, it can decide the quantity of values to reserve. In case the attribute pattern is dependant on the orgunit code, the SDK will reserve values for all the relevant orgunits. More details about the logic in Javadoc.

Reserved values can be obtained by:

```java
d2.trackedEntityModule().reservedValueManager.getValue("attributeUid", "orgunitUid")
```

### Aggregated data

#### Aggregated data download

```java
d2.aggregatedModule().data().download()
```

By default, the SDK downloads aggregated data values and dataset complete registration values corresponding to:

- **DataSets**: all available dataSets (those the use has at least read data access to).
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

It keeps track of the latest successful download in order to void downloading unmodified server data.

#### Aggregated data write

DataValueCollectionRepository has a `value()` method that gives access to edition methods. The parameters accepted by this method are the parameters that unambiguosly identify a value.

```java
DataValueObjectRepository valueRepository =
    d2.dataValueModule().dataValues.value("periodId", "orgunitId", "dataElementId", "categoryOptionComboId", "attributeOptionComboId");

valueRepository.set("value")
```

#### Aggregated data upload

DataValueCollectionRepository has an `uplaod()` method to upload aggregated data values.

```java
d2.dataValueModule().dataValues.upload();
```

#### DataSet reports

A DataSetReport in the SDK is a handy representation of the existing aggregated data. It would the equivalent of some kind of DataSet instance: a DataSetReport represents a unique combination of DataSet - Period - Orgunit - AttributeOptionCombo and includes extra information like sync state, value count or displayName for some properties.

```java
d2.dataValueModule().dataSetReports
    .byDataSetUid().eq("dataSetUid")
    .[ filters ]
    .get()
```

**Important**: a Data set report in the SDK is not the same as a Data set report in Web UI. In Web UI, DataSetReports have a sense of aggregation of data values over time and hierarchy.

## Error management

[//]: # (Include ## SMS module)

## DHIS2 version compatibility strategy

The SDK guarantees compatibility with the latest three DHIS 2 releases. To avoid accidental login into unsupported DHIS 2 instances, the SDK blocks connections to version that are not supported yet or that have been deprecated.

Regarding data model and compatibility, the main approach is to extend the data model to be able to support all the DHIS 2 versions. It usually happens that new DHIS 2 versions introduce extra functionality and do not remove existing one, so supporting a new DHIS 2 version usually mean to use the latest data model.

As a general rule the SDK tries to avoid breaking changes in its API and to make new features optional to the user. This rule is followed as much as possible, but there are cases where supporting old and new APIs to avoid breaking has a very high cost. In this scenario the **SDK might introduce breaking changes to be compatible with the new DHIS 2 version**.

### Example: minor change

...

### Example: breaking change

...

## Direct database interaction

Repository methods cover most of the needs of the application. But in some cases the application might want to interact directly with the database.

The SDK exposes a DatabaseAdapter object to execute raw statements in the database. Also, SDK model classes include helper methods to create instances from a `Cursor`.

For example, read the list of constants using repositories and interacting directly with the database.

```java
// Using repositories
d2.constantModule().constants.get() // List<Constant>

// Direct database interaction
String query = "SELECT * FROM " + ConstantTableInfo.TABLE_INFO.name();
try (Cursor cursor = Sdk.d2().databaseAdapter().query(query)) {
    List<Constant> constantList = new ArrayList<>();
    if (cursor.getCount() > 0) {
        cursor.moveToFirst();
        do {
            collection.add(Constant.create(cursor));
        }
        while (cursor.moveToNext());
    }
    return constantList; // List<Constant>
}
```

`TableInfo` classes include some useful information about table structure, like table and column names.

## Program rule engine

The program rule engine is not provided within the SDK. It is implemented in a separate library, so the same code is used by backend and android apps.

More info [dhis2-rule-engine](https://github.com/dhis2/dhis2-rule-engine).

## Program indicator engine

The SDK includes its own Program Indicator engine for the evaluation of **in-line Program Indicators**. These kind of indicators are evaluated within the context of an enrollment and they are usually placed in the data entry form offering additional information to the data encoder. This means that, even though they are regular Program Indicators and can be calculated across enrollments, they have provide useful information within a single enrollment.

A good example, "Average time between visits".

A bad example, "Number of active TEIs": it would always be 1.

In order to trigger the Program Indicator Engine, just execute:

```java
d2.programModule()
    .programIndicatorEngine
    .getProgramIndicatorValue(<enrollment-uid>, <event-uid>, <program-indicator-uid>);
```

Compatibility table:

| Function (d2:)(doc)| Supported |
|--------------------|-----------|
| ceil              |   Yes     |
| floor             |   Yes     |
| round             |   Yes     |
| modulus           |   Yes     |
| zing              |   Yes     |
| oizp              |   Yes     |
| concatenate       |   Yes     |
| condition         |   Yes     |
| minutesBetween    |   No      |
| daysBetween       |   Yes     |
| monthsBetween     |   Yes     |
| yearsBetween      |   Yes     |
| relationshipCount |   No      |
| count             |   No      |
| countIfValue      |   No      |
| countIfZeroPos    |   No doc  |
| hasValue          |   No      |
| zpvc              |   Yes     |
| validatePatten    |   Yes     |
| left              |   Yes     |
| right             |   Yes     |
| substring         |   Yes     |
| split             |   Yes     |
| length            |   Yes     |
| inOrgUnitGroup    |   No doc  |
| hasUserRole       |   No doc  |

| Variables (doc)       | Supported |
|-----------------------|-----------|
| current_date          | Yes       |
| event_date            | Yes       |
| due_date              | Yes       |
| event_count           | Yes       |
| enrollment_date       | Yes       |
| incident_date         | Yes       |
| program_stage_id      | No        |
| program_stage_name    | No        |
| enrollment_status     | Yes       |
| value_count           | Yes       |
| zero_pos_value_count  | Yes       |
| reporting_period_start| N/A       |
| reporting_period_end  | N/A       |
| tei_count             | N/A       |
| enrollment_count      | N/A       |
| organisationunit_count| N/A       |

## Debugging

Besides the regular debugging tools in AndroidStudio, the library [Stetho](http://facebook.github.io/stetho/) allows the use of Chrome Developer Tools for debugging network traffic and explore the database.

Setup up Stetho by adding the following dependencies in your gradle file:

```gradle
dependencies {
    implementation 'com.facebook.stetho:stetho:1.5.0'
    implementation 'com.facebook.stetho:stetho-okhttp3:1.5.0'
}
```

Then add a network interceptor in `D2Configuration` object:

```java
D2Configuration.builder()
    ...
    .networkInterceptors(Collections.singletonList(new StethoInterceptor()))
    ...
    .build();
```

Finally enable initialize Stetho in the `Application` class:

```java
if (DEBUG) {
    Stetho.initializeWithDefaults(this);
}
```

At this point you should be able to debug the app/sdk by using Chrome Inspector Tools:

- Run a test in debug mode and set a breakpoint.
- In Chrome Browser open the [device inspector](chrome://inspect/devices#devices).
- Select the remote target and click on Inspect. A new windows will appear showing the Chrome developer tools.
- Explore database in "Resources > Web SQL".
- Explore network traffic in "Network".

## Troubleshooting
