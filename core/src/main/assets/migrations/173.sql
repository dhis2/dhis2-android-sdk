# Create Custom Intents configuration and related tables (ANDROSDK-1934)

CREATE TABLE CustomIntent (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, name TEXT, action TEXT, packageName TEXT, requestArguments TEXT, responseDataArgument TEXT, responseDataPath TEXT);
CREATE TABLE CustomIntentDataElement (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL, customIntentUid TEXT NOT NULL, FOREIGN KEY (customIntentUid) REFERENCES CustomIntent (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
CREATE TABLE CustomIntentAttribute (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL, customIntentUid TEXT NOT NULL, FOREIGN KEY (customIntentUid) REFERENCES CustomIntent (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
