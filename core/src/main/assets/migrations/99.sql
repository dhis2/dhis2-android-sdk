# New table for tracker importer
DROP TABLE TrackerJob;
CREATE TABLE TrackerJobObject (_id INTEGER PRIMARY KEY AUTOINCREMENT, trackerType TEXT NOT NULL, objectUid TEXT NOT NULL, jobUid TEXT NOT NULL, lastUpdated TEXT NOT NULL);
