# Data Store { #android_sdk_data_store }

The SDK provides a module for storing data called `DataStoreModule` that contains the `localDataStore` collection repository.

```java
d2.dataStoreModule().localDataStore()
```

This repository is ideal for storing any kind of information. Note that the SDK considers this information as data, not metadata and it will be removed when removing the local database data.

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