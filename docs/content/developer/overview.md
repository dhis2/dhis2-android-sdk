# Overview { #android_sdk_overview }

DHIS2 Android SDK is a library that abstracts the complexity of interacting with DHIS2 web api. It aims to be an starting point to build Android apps for DHIS2, covering some tasks that any Android app should implement, like metadata and data synchronization.

Main goals:

- **Abstract DHIS2 web api**. There is no need to perform api queries against the server. The SDK includes methods to interact with the web api.
- **Work offline**. It implements a simplified version of DHIS2 model that is persisted in a local database (Room Database). It ensures that all the metadata required to perform data entry tasks is available at any time to build the data entry forms. Data is saved locally and uploaded to the server when connection is available.
- **Ensure DHIS2 compatibility**. It encapsulates the changes between DHIS2 versions so the app does not have to care about them. In case the SDK introduces some changes to accommodate a new DHIS2 version, the app can safely detect these changes at compile-time.

## Technology overview { #android_sdk_technology_overview }

The SDK is entirely written in [Kotlin](https://kotlinlang.org/), which is the language recommended by Google for building Android apps. The SDK uses some Android-specific components, such as libraries to create paged list (LiveData, PagedList) or to access to file system. For this reason, currently **the SDK is only runnable in an Android environment**.

Every asynchronous operation is exposed in three flavours: a Kotlin `suspend` function (`suspendGet()`), an [RxJava](https://github.com/ReactiveX/RxJava) variant (`rxGet()`) and a blocking variant (`blockingGet()`). Coroutines are the recommended approach for new code; the RxJava variants are fully supported. See [Dealing with return types](#android_sdk_dealing_with_return_types).

Other libraries internally used by the SDK are: [Koin](https://insert-koin.io/) for dependency injection, [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for JSON parsing, [Ktor](https://github.com/ktorio/ktor) and [OkHttpClient](https://square.github.io/okhttp/) for API communication, [Room](https://developer.android.com/training/data-storage/room) for database persistence, or [SQLCipher](https://www.zetetic.net/sqlcipher/) for DB encryption.
