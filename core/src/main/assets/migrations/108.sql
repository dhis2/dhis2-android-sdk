# Add local data store table

CREATE TABLE LocalDataStore (_id INTEGER PRIMARY KEY AUTOINCREMENT, key TEXT NOT NULL UNIQUE, value TEXT);