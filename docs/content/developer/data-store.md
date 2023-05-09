# Data Store { #android_sdk_data_store }

```java
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

```java
// Download
d2.dataStoreModule().dataStoreDownloader()
        .byNamespace().in("namespace1", "namespace2")
        .download();

// Read example
List<DataStoreEntry> entries = d2.dataStoreModule().dataStore()
        .byNamespace().eq("namespace1")
        .byKey().in("key1", "key2")
        .get()

// Write example
d2.dataStoreModule().dataStore()
        .value("namespace1", "key1")
        .set("value");

// Upload
d2.dataStoreModule().dataStore().upload();
```

## Local Data Store { #android_sdk_local_data_store }

```java
d2.dataStoreModule().localDataStore()
```

This repository is ideal for storing any kind of information.

This collection supports key value pairs (`KeyValuePair`) and it can be stored as others values in the SDK.

```java
// Access the object repository
LocalDataStoreObjectRepository objectRepository = 
    d2.dataStoreModule().localDataStore().value("key");

// Set or update a key value pair
objectRepository.set("value");

// Remove key value pair
objectRepository.delete();
```