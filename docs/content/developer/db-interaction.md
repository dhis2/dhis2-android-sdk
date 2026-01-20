# Direct database interaction { #android_sdk_database_interaction }

Repository methods cover most of the needs of the application. But in some cases the application might want to interact directly with the database.

The SDK exposes a DatabaseAdapter object to execute raw SQL statements in the database. Since the migration to Room Database, the API uses suspend functions and returns results as lists of maps instead of cursors.

## Available methods

The DatabaseAdapter provides several methods for direct database interaction:

- **`rawQuery(sqlQuery: String, queryArgs: Array<Any>?)`**: Executes a SQL query and returns results as `List<Map<String, String?>>`. All values are returned as strings.
- **`rawQueryWithTypedValues(sqlQuery: String, queryArgs: Array<Any>?)`**: Similar to `rawQuery` but returns `List<Map<String, Any?>>` with typed values.
- **`delete(tableName: String, whereClause: String?, whereArgs: Array<Any>?)`**: Deletes rows from a table and returns the number of affected rows.
- **`execSQL(sql: String)`**: Executes a SQL statement without returning results.
- **`setForeignKeyConstraintsEnabled(enabled: Boolean)`**: Enables or disables foreign key constraints.

## TableInfo classes

The SDK automatically generates `TableInfo` classes for each Room entity. These classes provide type-safe access to table and column names, avoiding hardcoded strings in queries.

For example, the `ConstantTableInfo` class is generated from the `ConstantDB` entity:

```kotlin
import org.hisp.dhis.android.persistence.constant.ConstantTableInfo

// Access table name
val tableName = ConstantTableInfo.TABLE_NAME // "Constant"

// Access column names
val uidColumn = ConstantTableInfo.Columns.UID // "uid"
val nameColumn = ConstantTableInfo.Columns.NAME // "name"
val valueColumn = ConstantTableInfo.Columns.VALUE // "value"
```

## Example: Reading constants

```kotlin
// Using repositories
val constants = d2.constantModule().constants().blockingGet() // List<Constant>

// Direct database interaction with rawQuery using TableInfo
val query = "SELECT * FROM ${ConstantTableInfo.TABLE_NAME}"
val results = runBlocking {
    d2.databaseAdapter().rawQuery(query, null)
}

// Process results - each row is a Map<String, String?>
val constantList = results.map { row ->
    Constant.builder()
        .uid(row[ConstantTableInfo.Columns.UID])
        .code(row[ConstantTableInfo.Columns.CODE])
        .name(row[ConstantTableInfo.Columns.NAME])
        .displayName(row[ConstantTableInfo.Columns.DISPLAY_NAME])
        .value(row[ConstantTableInfo.Columns.VALUE]?.toDoubleOrNull())
        .build()
}
```

## Example: Using query parameters

```kotlin
// Query with parameters to prevent SQL injection
val query = "SELECT * FROM ${ConstantTableInfo.TABLE_NAME} WHERE ${ConstantTableInfo.Columns.UID} = ?"
val results = runBlocking {
    d2.databaseAdapter().rawQuery(query, arrayOf("constantUid"))
}
```

## Example: Deleting records

```kotlin
// Delete specific records using TableInfo
val deletedRows = runBlocking {
    d2.databaseAdapter().delete(
        tableName = ConstantTableInfo.TABLE_NAME,
        whereClause = "${ConstantTableInfo.Columns.UID} = ?",
        whereArgs = arrayOf("constantUid")
    )
}
```

## Important notes

- All database operations are **suspend functions** and must be called from a coroutine context or using `runBlocking`.
- **Use `TableInfo` classes** for type-safe access to table and column names. These classes are automatically generated from Room entities (e.g., `ConstantTableInfo`, `EventTableInfo`, `DataElementTableInfo`).
- Table names correspond to the `@Entity` annotations in the Room database schema (e.g., `"Constant"`, `"Event"`, `"DataElement"`).
- Query parameters support multiple types: `String`, `Long`, `Int`, `Double`, `Float`, `Boolean`, `ByteArray`, and `null`.
- Use parameterized queries (with `?` placeholders) to prevent SQL injection attacks.
- `TableInfo` classes are located in the same package as their corresponding entity (e.g., `org.hisp.dhis.android.persistence.constant.ConstantTableInfo`).
