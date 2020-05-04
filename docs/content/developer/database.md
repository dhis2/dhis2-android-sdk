# Database

<!--DHIS2-SECTION-ID:database-->

## Database scope
The SDK keeps the data of a [server, user] pair in an isolated database.

At the moment, just one [server, user] pair is supported, so logging out and logging in in with another [server, user]
pair will delete the current database and create a new one.

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