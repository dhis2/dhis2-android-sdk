# Modules and repositories { #android_sdk_modules_and_repositories }

`D2` object is the entry point to interact with the SDK. The SDK forces the `D2` object to be a singleton across the application.

Modules are the layer below `D2`. They act as a wrapper for related functionality. A module includes some related repositories and might expose some services and helpers.

Repositories act as a facade for the DB (or web API in some cases). They offer read capabilities for metadata and read/write for data.

## Dealing with return types { #android_sdk_dealing_with_return_types }

Most of the actions in the SDK are time consuming and must be executed in a secondary thread. To make that explicit, and to notify about the progress of long operations such as metadata or data sync, every asynchronous method is exposed in three flavours:

| Prefix | Return type | Notes |
|--------|-------------|-------|
| `suspend` | the value itself | Kotlin `suspend` function. **Recommended**, and used in the examples throughout this guide. |
| `rx` | RxJava (`Single`, `Completable`, `Observable`, `Flowable`) | For apps already built around RxJava. |
| `blocking` | the value itself | Must not be called from the main thread. |

Operations that report progress (metadata and data download, data upload, reserved value download) do not have a `suspend` variant, because a single return value cannot convey progress. They expose a `flow` variant instead,  which returns a `Flow<D2Progress>`: `flowDownload()`, `flowUpload()`, `flowDownloadReservedValues()`.

```kotlin
// Coroutines
val programs = d2.programModule().programs().suspendGet()

// RxJava
d2.programModule().programs()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .rxGet()
    .subscribe { programs -> }   // List<Program>

// Blocking, from a background thread
val programs = d2.programModule().programs().blockingGet()
```

> **Important**
>
> Unprefixed RxJava methods are **deprecated** in favour of their `rx` counterparts, so that the coroutine variants can coexist with them. The old names still work, but they will be removed in a future release.
>
> The table below lists the drop-in replacement, which keeps the RxJava return type. When migrating, consider moving to the `suspend` variant instead.
>
> | Deprecated | Drop-in replacement |
> |------------|---------------------|
> | `get()` | `rxGet()` |
> | `count()` | `rxCount()` |
> | `isEmpty()` | `rxIsEmpty()` |
> | `getUids()` | `rxGetUids()` |
> | `exists()` | `rxExists()` |
> | `add(o)` | `rxAdd(o)` |
> | `set(value)` | `rxSet(value)` |
> | `delete()` / `deleteIfExist()` | `rxDelete()` / `rxDeleteIfExist()` |
> | `download()` | `rxDownload()` |
> | `upload()` | `rxUpload()` |
> | `evaluate()` | `rxEvaluate()` |
>
> The same rename applies to most module and service methods (`logIn()`, `logOut()`, `isLogged()`, `checkServerUrl()`, `validate()`, the `EventService` and `EnrollmentService` methods, the reserved value manager, …): the replacement is the same name prefixed with `rx`, and a `suspend` variant is available as well. `D2Manager.instantiateD2()` follows the same pattern, replaced by `D2Manager.rxInstantiateD2()`.
>
> Independently of that rename, `getPaged(int)` is also deprecated, replaced by `getPagingData(int)`, which returns a `Flow<PagingData<M>>` instead of a `LiveData<PagedList<M>>`.

Accessing the database is time consuming and it's recommended to do it in a separate thread using any of the recommended
methods. However, procedures that involve accessing the web API, like log in, metadata or data download or upload **must**
run in a separate thread, otherwise Android will throw an error.

## Query building { #android_sdk_query_building }

Repositories offer a builder syntax with compile-time validation to access the resources. A typical query is composed of some modifiers (filter, order, nested fields) and ends with an action (`suspendGet`/`rxGet`/`blockingGet`, `suspendCount`/`rxCount`/`blockingCount`, `getPagingData`,...).

```kotlin
// Generic syntax
d2.<module>.<repository>
    .[ filter | orderBy | nested fields ]
    .<action>

// An example for events
d2.eventModule().events()
    .byOrganisationUnitUid().eq("DiszpKrYNg8")
    .byEventDate().after(Date("2019-05-05"))
    .orderByEventDate(DESC)
    .withTrackedEntityDataValues()
    .suspendGet()
```

### Filters { #android_sdk_filters }

Repositories expose the list of available filters prefixed by the keyword "by". The list of filter operators available for each filter is dependant on the filter value type: for example, a value type `Date` will offer operators like `after`, `before`, `inPeriods`, while a value type `Boolean` will offer `isFalse` or `isTrue`.

Common filter operators include:
- **Equality**: `eq()`, `notEq()`
- **Comparison**: `gt()`, `lt()`, `ge()`, `le()`
- **String matching**: `like()`, `notLike()`
- **Collection**: `in()` - matches any value in the provided list
- **Null checks**: `isNull()`, `isNotNull()`
- **Emptiness**: `isNullOrBlank()`, `isNotNullAndIsNotBlank()` - available in data value filters, where the event query repository exposes them as `isEmpty(Boolean)`

Several filters can be appended to the same query in any order. Filters are joined globally using the operator "AND". This means that a query like

```kotlin
d2.eventModule().events()
    .byOrganisationUnitUid().eq("DiszpKrYNg8")
    .byEventDate().after(Date("2019-05-05"))
    ...
```

will return the events assigned to the orgunit "DiszpKrYNg8" **AND** whose eventDate is after "2019-05-05".

The `in()` operator is particularly useful for querying multiple values at once:

```kotlin
// Query tracked entity instances with specific data values
d2.trackedEntityModule().trackedEntityInstanceQuery()
    .byDataValue("dataElementUid").`in`("value1", "value2", "value3")
    .onlineFirst()
    .suspendGet()
```

### Order by { #android_sdk_order_by }

Ordering modifiers are prefixed by the keyword "orderBy".

Several "orderBy" modifiers can be appended to the same query. The order of the "orderBy" modifiers within the query determines the order priority. This means that a query like

```kotlin
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

```kotlin
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
- `FileResizerHelper`, `FileCompressionHelper`, `FileResourceDirectoryHelper`: file resource manipulation.
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

- attributeModule
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
