# Database { #android_sdk_database }

## Database scope { #android_sdk_database_scope }
The SDK keeps the data of a [server, user] pair in an isolated database. As of version 1.6.0, the SDK supports multiple accounts (pairs [server, user]) and the information for each account is stored in an isolated database. The database is deleted only when the account is deleted. Databases are created automatically on a successful login.

## Encryption { #android_sdk_database_encryption }
As of SDK version 1.1.0, it is possible to store the data in an encrypted database. The encryption key is generated randomly
by the SDK and kept secure.

The encryption status (if the database is encrypted or not) can be configured at server level in the android-settings-app.
The default status is false: If the app is not installed, the database won't be encrypted.

During the first login for a given server and user, the encryption status will be downloaded from the API and a
database of the given type will be created.

In later logins or metadata synchronizations, the SDK will download again the encryption status from the server and,
if changed, will encrypt or decrypt the current database without data loss.

### Encryption performance
- Database size: the database size is approximately the same, regardless of being encrypted or not.
- Speed: reads and writes are on average 5 to 10% slower using an encrypted database.

## Import / export { #android_sdk_database_import_export }
The database can be exported and imported in a different device. 

One of the main use cases of this functionality is debugging: sometimes it is hard to know the reason for a sync problem or a bug, and it is very useful to replicate the issue in an emulator or a different device.  

```kt
// Export database
val database = d2.maintenanceModule().databaseImportExport().exportLoggedUserDatabase()

// Import database
val metadata = d2.maintenanceModule().databaseImportExport().importDatabase(database)

// The metadata object contains information about the database (serverUrl, username,...) 

// Once the database is imported, it is possible to login as usual
d2.userModule().login("username", "password", "serverUrl")
```

The export process encrypts the database using ZIP encryption, so the database file can't be read unless the right user credentials are provided. 

Things to consider:
- The exported file only contains the database, it does not contain file resources (images, icons, files,...).
- The receiver device must run an **SDK version that is equal or higher** than the SDK version used to export the database.