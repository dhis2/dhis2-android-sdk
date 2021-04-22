# Overview { #android_sdk_overview }

DHIS2 Android SDK is a library that abstracts the complexity of interacting with DHIS2 web api. It aims to be an starting point to build Android apps for DHIS2, covering some tasks that any Android app should implement, like metadata and data synchronization.

Main goals:

- **Abstract DHIS2 web api**. There is no need to perform api queries against the server. The SDK includes methods to interact with the web api.
- **Work offline**. It implements a simplified version of DHIS2 model that is persisted in a local database (SQLite). It ensures that all the metadata required to perform data entry tasks is available at any time to build the data entry forms. Data is saved locally and uploaded to the server when connection is available.
- **Ensure DHIS2 compatibility**. It encapsulates the changes between DHIS2 versions so the app does not have to care about them. In case the SDK introduces some changes to accommodate a new DHIS2 version, the app can safely detect these changes at compile-time.

## Technology overview { #android_sdk_technology_overview }

The SDK is mainly written in Java 8 using the reduced subset of features allowed in the minimum Android API version, although newer components are implemented in [Kotlin](https://kotlinlang.org/), which is the language recommend by Google for building Android apps. The SDK uses some Android-specific components, such as libraries to create paged list (LiveData, PagedList) or to access to file system. For this reason, currently **the SDK is only runnable in an Android environment**.

It uses [RxJava](https://github.com/ReactiveX/RxJava) to facilitate the asynchronous treatment of some methods. Although it is optional, we recommend this approach to ensure non-blocking calls.

Other libraries internally used by the SDK are: [Dagger](https://github.com/google/dagger) for dependency injection, [Jackson](https://github.com/FasterXML/jackson) for JSON parsing, [Retrofit](https://square.github.io/retrofit/) and [OkHttpClient](https://square.github.io/okhttp/) for API communication or [SQLBrite](https://github.com/square/sqlbrite) for DB migrations.
