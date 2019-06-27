<!--DHIS2-SECTION-ID:index-->

# SDK Developer user guide

## Compatibility: SDK / core / Android

| SDK      | DHIS2 core       | Android SDK |
|----------|------------------|-------------|
| 0.17.0   | 2.30, 2.31, 2.32 | 19 - 28     |

## Overview

- Approach: the sdk is currently oriented to end-user apps (i.e, data encoders)
- Works offline
- 

## Libraries



## Getting started

### Installation

Include dependency in build.gradle.

```
dependencies {
    implementation "org.hisp.dhis:android-core:0.17.0-SNAPSHOT"
    ...
}
```

### D2 initialization

In order to start using the SDK, the first step is to initialize a `D2` object. The helper class `D2Manager` offers static methods to setup and initialize the `D2` instance. Also, it ensures that `D2` is a singleton across the application.

```
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

```
d2.programModule().programs
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .getAsync()
    .subscribe(programs -> {}); //List<Program>
```

*Using AsyncTask*

```
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

```
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

```
d2.eventModule().events
    .byOrganisationUnitUid().eq("DiszpKrYNg8")
    .byEventDate().after(Date("2019-05-05"))
    ...
```

will return the events assigned to "DiszpKrYNg8" **and** whose eventDate is after "2019-05-05".

#### Order by

Ordering modifiers are prefixed by the keyword "orderBy".

Several "orderBy" modifiers can be appended to the same query. The order of the "orderBy" modifiers within the query determines the order priority. This means that a query like

```
d2.eventModule().events
    .orderByEventDate(DESC)
    .orderByLastUpdated(DESC)
    ...
```

will order by EventDate descendant in first place, and then by LastUpdated descendant.

### Include nested fields



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

```
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

```
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

```
d2.maintenanceModule().foreignKeyViolations
```

## Error management

## SMS module

## DHIS2 version compatibility strategy

The SDK guarantees compatibility with the latest three DHIS 2 releases. To avoid accidental login into unsupported DHIS 2 instances, the SDK blocks connections to version that are not supported yet or that have been deprecated.

Regarding data model and compatibility, the main approach is to extend the data model to be able to support all the DHIS 2 versions. It usually happens that new DHIS 2 versions introduce extra functionality and do not remove existing one, so supporting a new DHIS 2 version usually mean to use the latest data model.

As a general rule the SDK tries to avoid breaking changes in its API and to make new features optional to the user. This rule is followed as much as possible, but there are cases where supporting old and new APIs to avoid breaking has a very high cost. In this scenario the **SDK might introduce breaking changes to be compatible with the new DHIS 2 version**.

### Example: minor change

...

### Example: breaking change

...

## Program indicator engine

The SDK includes its own Program Indicator engine for the evaluation of **in-line Program Indicators**. These kind of indicators are evaluated within the context of an enrollment and they are usually placed in the data entry form offering additional information to the data encoder. This means that, even though they are regular Program Indicators and can be calculated across enrollments, they have provide useful information within a single enrollment.

A good example, "Average time between visits".

A bad example, "Number of active TEIs": it would always be 1.

In order to trigger the Program Indicator Engine, just execute:

```
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

```
dependencies {
    implementation 'com.facebook.stetho:stetho:1.5.0'
    implementation 'com.facebook.stetho:stetho-okhttp3:1.5.0'
}
```

Then add a network interceptor in `D2Configuration` object:

```
D2Configuration.builder()
    ...
    .networkInterceptors(Collections.singletonList(new StethoInterceptor()))
    ...
    .build();
```

Finally enable initialize Stetho in the `Application` class:

```
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
