# Manage any file resource (ANDROSDK-1465);

ALTER TABLE FileResource RENAME TO FileResource_Old;
CREATE TABLE FileResource (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, name TEXT, created TEXT, lastUpdated TEXT, contentType TEXT, contentLength INTEGER, path TEXT, syncState TEXT, domain TEXT);
INSERT INTO FileResource (_id, uid, name, created, lastUpdated, contentType, contentLength, path, syncState, domain) SELECT _id, uid, name, created, lastUpdated, contentType, contentLength, path, syncState, 'DATA_VALUE' FROM FileResource_Old;
DROP TABLE IF EXISTS FileResource_Old;