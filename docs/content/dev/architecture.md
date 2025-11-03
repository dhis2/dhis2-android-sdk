# Architecture

<!--DHIS2-SECTION-ID:architecture-->

## Frameworks and auxiliary libraries

- [Koin](https://insert-koin.io/) - Dependency injection
- [Room](https://developer.android.com/training/data-storage/room) - Database persistence layer
- [KSP](https://github.com/google/ksp) - Kotlin Symbol Processing for code generation
- [Autovalue](https://github.com/google/auto/blob/master/value/userguide/index.md) - Immutable value classes
- [Ktor](https://github.com/ktorio/ktor)/[OkHttp3](https://github.com/square/okhttp) - HTTP client
- [SQLCipher](https://www.zetetic.net/sqlcipher/) - Database encryption

## Public API overview

D2 class is the entry point to access the whole SDK functionality.

## Package structure

The SDK uses package-by-feature style: each package corresponds to a particular feature and it contains all the items related to that feature (layer stack).

Since features in DHIS2 (API resources) usually share common characteristics and the actions over them are pretty similar (fetch/persist/access), the sdk makes heavy use of generic types.

- Packages: arch, calls, common.

Feature packages contain at least:

- A main Model class.
- `*Module`: exposes the public functionality of the feature.
- `*ModuleWiper`: defines the logic to remove database tables related to the feature.
- `*CollectionRepository`: allows the database access of objects of a given type. They are exposed so the client can't use them to access the database.
- `*Store`: allows the database access of objecs of a given type. They meant for internal use only during synchronization and are not exposed.
- `*TableInfo`: defines the table structure. It is used by the `*Store`.
- `*Handler`: manages de synchronization of objects of a given type. It receives a list of Model objects and uses the `*Store` to persist them in the database. It also calls the handler of child objects.
- `*Fields`: API fields.
- `*Service`: Ktor service defining the API calls. Methods in this file usually receive a list of fields, which is defined in `*Fields`. And they usually return a List or a Payload of Model objects.
- `*Call`/ `*CallFactory`: defines the call. It usually links the service and the handler.
- `*EntityDIModule`: Dagger class that injects the classes related to a concrete type, like store, handler and service. It optionally includes other auxiliary classes.
- `*PackageDIModule`: Dagger class that injects the classes related to a given package. This typically wraps several `*EntityDIModule` classes.

## Local database

The SDK uses **Room Persistence Library** as its database layer. Room provides:
- Compile-time verification of SQL queries
- Convenient database access with DAOs (Data Access Objects)
- Automatic database migrations
- Better integration with Android architecture components

### Database entities

Database entities are defined using Room annotations and follow these patterns:
- Entity classes are suffixed with `DB` (e.g., `ProgramDB`, `EventDB`)
- Entities implement common interfaces like `BaseIdentifiableObjectDB`, `BaseNameableObjectDB`
- Each entity has `toDomain()` and `toDB()` mapper functions for conversion between database and domain models

### Database migrations

The SDK handles database migrations automatically. Migrations are generated using custom KSP processors that convert SQL migration files into Room migration classes. The database version is managed by Room, and migrations are applied automatically when the SDK detects an outdated database version.
