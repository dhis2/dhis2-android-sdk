# Modules and repositories { #android_sdk_modules_and_repositories }

`D2` object is the entry point to interact with the SDK. The SDK forces the `D2` object to be a singleton across the application.

Modules are the layer below `D2`. They act as a wrapper for related functionality. A module includes some related repositories and might expose some services and helpers.

Repositories act as a facade for the DB (or web API in some cases). They offer read capabilities for metadata and read/write for data.

## Dealing with return types: RxJava { #android_sdk_dealing_with_rxjava }

The SDK uses RxJava classes (Observable, Single, Completable, Flowable) as the preferred return type for all the methods. The reasons for choosing RxJava classes are mainly two:

- **To facilitate the asynchronous treatment of returned objects.** Most of the actions in the SDK are time consuming and must be executed in a secondary thread. These return types force the app to deal with this asynchronous behavior.
- **To notify about progress.** Methods like metadata or data sync might take several minutes to finish. From a user perspective, it is very helpful to have a sense of progress.

This does not mean that applications are forced to use RxJava in their code: they are only forced to deal with the asynchronous behavior of some methods. The SDK usually exposes *blocking* version of every method.

For example, the same query using RxJava and AsyncTask:

*Using RxJava*

```java
d2.programModule().programs()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .get()
    .subscribe(programs -> {}); //List<Program>
```

*Using AsyncTask*

```java
new AsyncTask<Void, Void, List<Program>>() {
    protected List<Program> doInBackground() {
        return d2.programModule().programs().blockingGet();
    }

    protected void onPostExecute(List<Program> programs) {

    }
}.execute();
```

Accessing the database is time consuming and it's recommended to do it in a separate thread using any of the recommended
methods. However, procedures that involve accessing the web API, like log in, metadata or data download or upload **must**
run in a separate thread, otherwise Android will throw an error.

## Query building { #android_sdk_query_building }

Repositories offer a builder syntax with compile-time validation to access the resources. A typical query is composed of some modifiers (filter, order, nested fields) and ends with an action (get, count, getPaged,...).

```java
// Generic syntax
d2.<module>.<repository>
    .[ filter | orderBy | nested fields ]
    .<action>;

// An example for events
d2.eventModule().events()
    .byOrganisationUnitUid().eq("DiszpKrYNg8")
    .byEventDate().after(Date("2019-05-05"))
    .orderByEventDate(DESC)
    .withTrackedEntityDataValues()
    .get();
```

### Filters { #android_sdk_filters }

Repositories expose the list of available filters prefixed by the keyword "by". The list of filter operators available for each filter is dependant on the filter value type: for example, a value type `Date` will offer operators like `after`, `before`, `inPeriods`, while a value type `Boolean` will offer `isFalse` or `isTrue`.

Several filters can be appended to the same query in any order. Filters are joined globally using the operator "AND". This means that a query like

```java
d2.eventModule().events()
    .byOrganisationUnitUid().eq("DiszpKrYNg8")
    .byEventDate().after(Date("2019-05-05"))
    ...
```

will return the events assigned to the orgunit "DiszpKrYNg8" **AND** whose eventDate is after "2019-05-05".

### Order by { #android_sdk_order_by }

Ordering modifiers are prefixed by the keyword "orderBy".

Several "orderBy" modifiers can be appended to the same query. The order of the "orderBy" modifiers within the query determines the order priority. This means that a query like

```java
d2.eventModule().events()
    .orderByEventDate(DESC)
    .orderByLastUpdated(DESC)
    ...
```

will order by EventDate descendant in first place, and then by LastUpdated descendant.

### Include nested fields { #android_sdk_nested_fields }

Repositories return classes that are not an exact match of database tables: they are more complex objects that might include some properties obtained from other tables. For example, the `Event` class has a property called `trackedEntityDataValues` that include a list of TrackedEntityDataValues. The main reason to choose this kind of objects is to absorb the complexity of dealing with link tables so the app does not have to care about building links between objects.

Due to performance issues, this kind of properties are not included by default: they must be queried explicitly. In the repositories, the properties that are not included by default and need to be queried are prefixed by the keyword "with".

Several properties can be appended in the same query in any order. For example, a query like

```java
d2.programModule().programs()
    .withTrackedEntityType()
    ...
```

will return a nested `TrackedEntityType` object.

## Helpers { #android_sdk_helpers }

The SDK include some helpers in the package `org.hisp.dhis.android.core.arch.helpers`. They can be easily found in Android Studio by searching `Helper` in class names. They include some helpful methods to perform common operations:

- `AccessHelper`: related to access (sharing settings) object.
- `CollectionsHelper`: common operations to collections.
- `CoordinateHelper`, `GeometryHelper`: geospatial data manipulation.
- `FileResizeHelper`, `FileResourceDirectoryHelper`: file resource manipulation.
- `UidsHelper`: common operations to collections of objects with uid.
- `UserHelper`: operations related to user authentication.
- `ValueType`: list of different value types and their validators.

## Module list { #android_sdk_module_list }

System modules:

- importModule
- maintenanceModule
- systemInfoModule
- settingModule
- dataStoreModule
- wipeModule

Big block modules:

- metadataModule
- aggregatedDataModule

Concrete modules:

- categoryModule
- constantModule
- dataElementModule
- dataSetModule
- dataValueModule
- enrollmentModule
- eventModule
- fileResourceModule
- indicatorModule
- legendSetModule
- noteModule
- organisationUnitModule
- optionModule
- periodModule
- programModule
- relationshipModule
- smsModule
- trackedEntityModule
- userModule
- validationModule
