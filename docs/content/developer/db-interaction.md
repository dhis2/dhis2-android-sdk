# Direct database interaction { #android_sdk_database_interaction }

Repository methods cover most of the needs of the application. But in some cases the application might want to interact directly with the database.

The SDK exposes a DatabaseAdapter object to execute raw statements in the database. Also, SDK model classes include helper methods to create instances from a `Cursor`.

For example, read the list of constants using repositories and interacting directly with the database.

```java
// Using repositories
d2.constantModule().constants().blockingGet() // List<Constant>

// Direct database interaction
String query = "SELECT * FROM " + ConstantTableInfo.TABLE_INFO.name();
try (Cursor cursor = Sdk.d2().databaseAdapter().rawQuery(query)) {
    List<Constant> constantList = new ArrayList<>();
    if (cursor.getCount() > 0) {
        cursor.moveToFirst();
        do {
            collection.add(Constant.create(cursor));
        }
        while (cursor.moveToNext());
    }
    return constantList; // List<Constant>
}
```

`TableInfo` classes include some useful information about table structure, like table and column names.
