# API documentation {#api_documentation}

DHIS2 Android SDK API documentation is hosted in [Github repository](https://dhis2.github.io/dhis2-android-sdk/api/index.html).

This documentation provides information about the DHIS2 Android SDK's public API, including available data models and the methods for interacting with them. It serves as a reference for developers integrating the SDK into their applications.

## Domain models { #android_sdk_domain_models }

Domain models (`Program`, `Event`, `Enrollment`, `TrackedEntityInstance`, `DataSet`, `DataValue`, …) are immutable Kotlin data classes. Each one offers:

- **Accessor methods**, e.g. `event.program()`. These keep the same names and signatures they have always had, so existing code is unaffected.
- **Kotlin property getters**, e.g. `event.program`, for use from Kotlin.
- **A builder**, obtained with `Event.builder()` for a new instance or `event.toBuilder()` to derive one from an existing object.
- **The usual data class members**: `copy()`, `equals()`, `hashCode()` and `toString()`.

```kotlin
val event = d2.eventModule().events().uid(eventUid).blockingGet()

// Accessor, property and copy
val programUid = event.program()
val sameUid = event.program
val completed = event.copy(status = EventStatus.COMPLETED)

// Builder
val modified = event.toBuilder().status(EventStatus.COMPLETED).build()
```

## Binary compatibility { #android_sdk_binary_compatibility }

The public API surface is tracked in a dump file (`core/api/core.api`) that is validated on every build, so unintended breaking changes are caught before a release. Deprecated members are kept for at least one minor release cycle and are annotated with a `ReplaceWith` hint that Android Studio can apply automatically.
