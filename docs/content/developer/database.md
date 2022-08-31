# Database { #android_sdk_database }

## Database scope
The SDK keeps the data of a [server, user] pair in an isolated database. As of version 1.6.0, the SDK supports multiple accounts (pairs [server, user]) and the information for each account is stored in an isolated database. The database is deleted only when the account is deleted. Databases are created automatically on a successful login.

## Encryption
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