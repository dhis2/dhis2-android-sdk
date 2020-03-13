ALTER TABLE RelationshipType RENAME TO RelationshipType_Old;
CREATE TABLE RelationshipType (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, fromToName TEXT, toFromName TEXT, bidirectional INTEGER, accessDataWrite INTEGER );
INSERT INTO RelationshipType (_id, uid, code, name, displayName, created, lastUpdated, fromToName, toFromName, bidirectional, accessDataWrite) SELECT _id, uid, code, name, displayName, created, lastUpdated, bIsToA, AIsToB, 0, 1 FROM RelationshipType_Old;
DROP TABLE RelationshipType_Old;
DELETE FROM Resource WHERE resourceType = 'RELATIONSHIP_TYPE';