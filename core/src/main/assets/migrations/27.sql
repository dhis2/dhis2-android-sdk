ALTER TABLE IndicatorType RENAME TO IndicatorType_Old;
CREATE TABLE IndicatorType (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, number INTEGER, factor INTEGER);
INSERT INTO IndicatorType (_id, uid, code, name, displayName, created, lastUpdated, number, factor) SELECT _id, uid, code, name, displayName, created, lastUpdated, number, factor FROM IndicatorType_Old;
DROP TABLE IndicatorType_Old;