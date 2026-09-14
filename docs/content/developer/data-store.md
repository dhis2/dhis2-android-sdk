# Data Store { #android_sdk_data_store }

```kotlin
d2.dataStoreModule().dataStore()
```

The SDK provides read/write access to the online DataStore through the `DataStoreModule`. Additionally, the SDK offers a `localDataStore` to store local data in a schemaless repository.

Note that the SDK considers this information as data, not metadata and it will be removed when removing the local database data.

## Online Data Store { #android_sdk_online_data_store }

This is the well-known DataStore in the API. It is called "Online" in these docs in order to avoid confusion with the local data store offered by the SDK.

The behavior is similar to the rest of the data:

1. Download the online DataStore. It is strongly recommended to specify the namespaces to download; otherwise, the SDK will try to download them all, which might lead to a high synchronization time.
2. Read or modify the DataStore entries.
3. If there are any modifications in the entries, call the method to upload the entries to the server.

`DataStoreEntry.value()` holds the raw JSON value of the entry, exactly as stored in the server DataStore. Apps are expected to parse it according to the namespace schema.

```kotlin
// Download
d2.dataStoreModule().dataStoreDownloader()
        .byNamespace().`in`("namespace1", "namespace2")
        .flowDownload()

// Read example
val entries = d2.dataStoreModule().dataStore()
        .byNamespace().eq("namespace1")
        .byKey().`in`("key1", "key2")
        .suspendGet()

// Write example
d2.dataStoreModule().dataStore()
        .value("namespace1", "key1")
        .suspendSet("value")

// Upload
d2.dataStoreModule().dataStore().flowUpload()
```

## Local Data Store { #android_sdk_local_data_store }

```kotlin
d2.dataStoreModule().localDataStore()
```

This repository is ideal for storing any kind of information.

This collection supports key value pairs (`KeyValuePair`) and it can be stored as others values in the SDK.

```kotlin
// Access the object repository
val objectRepository = d2.dataStoreModule().localDataStore().value("key")

// Set or update a key value pair
objectRepository.suspendSet("value")

// Remove key value pair
objectRepository.suspendDelete()
```