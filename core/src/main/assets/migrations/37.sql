CREATE TABLE Relationship_temp (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, name TEXT, created TEXT, lastUpdated TEXT, relationshipType TEXT NOT NULL, FOREIGN KEY (relationshipType) REFERENCES RelationshipType (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO Relationship_temp SELECT _id, uid, name, created, lastUpdated, relationshipType FROM Relationship;
DROP TABLE IF EXISTS Relationship;
ALTER TABLE Relationship_temp RENAME TO Relationship;