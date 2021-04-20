# New table for tracker importer
DROP TABLE TrackerJob;
CREATE TABLE TrackerJobObject (_id INTEGER PRIMARY KEY AUTOINCREMENT, objectType TEXT, objectUid TEXT, jobUid TEXT);
