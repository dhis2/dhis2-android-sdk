# Add UserDataStore table to support /api/userDataStore (ANDROSDK-2346)
CREATE TABLE UserDataStore(namespace TEXT NOT NULL, key TEXT NOT NULL, value TEXT, syncState TEXT, deleted INTEGER, PRIMARY KEY(namespace, key));
