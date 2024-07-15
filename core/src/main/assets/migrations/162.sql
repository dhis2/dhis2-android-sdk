# Add UserGroup (ANDROSDK-1817)

CREATE TABLE UserGroup (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT);
