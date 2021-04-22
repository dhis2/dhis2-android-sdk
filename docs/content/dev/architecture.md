# Architecture

<!--DHIS2-SECTION-ID:architecture-->

## Frameworks and auxiliary libraries

- [Dagger](https://google.github.io/dagger/)
- [Autovalue](https://github.com/google/auto/blob/master/value/userguide/index.md)
- [Retrofit](https://square.github.io/retrofit/)/[OkHttp3](https://github.com/square/okhttp)
- [SQLBrite](https://github.com/square/sqlbrite)

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
- `*Service`: Retrofit service defining the API calls. Methods in this file usually receive a list of fields, which is defined in `*Fields`. And they usually return a List or a Payload of Model objects.
- `*Call`/ `*CallFactory`: defines the call. It usually links the service and the handler.
- `*EntityDIModule`: Dagger class that injects the classes related to a concrete type, like store, handler and service. It optionally includes other auxiliary classes.
- `*PackageDIModule`: Dagger class that injects the classes related to a given package. This typically wraps several `*EntityDIModule` classes.

## Local database

SQLBrite library is used for migrations. The database has a version number defined in `DbOpenHelper`. In case the device has an outdated database, it will execute the pending migrations up to the current version number. Migrations are defined in `assets/migrations`.
