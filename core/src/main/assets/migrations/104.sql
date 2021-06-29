# Rename state column to syncState

ALTER TABLE DataValue RENAME TO DataValue_Old;
CREATE TABLE DataValue (_id INTEGER PRIMARY KEY AUTOINCREMENT, dataElement TEXT NOT NULL, period TEXT NOT NULL, organisationUnit TEXT NOT NULL, categoryOptionCombo TEXT NOT NULL, attributeOptionCombo TEXT NOT NULL, value TEXT, storedBy TEXT, created TEXT, lastUpdated TEXT, comment TEXT, followUp INTEGER, syncState TEXT, deleted INTEGER, FOREIGN KEY (dataElement) REFERENCES DataElement (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (period) REFERENCES Period (periodId), FOREIGN KEY (organisationUnit) REFERENCES OrganisationUnit (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (categoryOptionCombo) REFERENCES CategoryOptionCombo (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (attributeOptionCombo) REFERENCES CategoryOptionCombo (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo));
INSERT INTO DataValue (_id, dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo, value, storedBy, created, lastUpdated, comment, followUp, syncState, deleted) SELECT _id, dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo, value, storedBy, created, lastUpdated, comment, followUp, state, deleted FROM DataValue_Old;
DROP TABLE DataValue_Old;


ALTER TABLE DataSetCompleteRegistration RENAME TO DataSetCompleteRegistration_Old;
CREATE TABLE DataSetCompleteRegistration (_id INTEGER PRIMARY KEY AUTOINCREMENT, period TEXT NOT NULL, dataSet TEXT NOT NULL, organisationUnit TEXT NOT NULL, attributeOptionCombo TEXT, date TEXT, storedBy TEXT, syncState TEXT, deleted INTEGER, FOREIGN KEY (dataSet) REFERENCES DataSet (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (period) REFERENCES Period (periodId) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (organisationUnit) REFERENCES OrganisationUnit (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (attributeOptionCombo) REFERENCES CategoryOptionCombo (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (period, dataSet, organisationUnit, attributeOptionCombo));
INSERT INTO DataSetCompleteRegistration(_id, period, dataSet, organisationUnit, attributeOptionCombo, date, storedBy, syncState, deleted) SELECT _id, period, dataSet, organisationUnit, attributeOptionCombo, date, storedBy, state, deleted FROM DataSetCompleteRegistration_Old;
DROP TABLE DataSetCompleteRegistration_Old;

ALTER TABLE Relationship RENAME TO Relationship_Old;
CREATE TABLE Relationship (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, name TEXT, created TEXT, lastUpdated TEXT, relationshipType TEXT NOT NULL, syncState TEXT, deleted INTEGER, FOREIGN KEY (relationshipType) REFERENCES RelationshipType (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO Relationship (_id, uid, name, created, lastUpdated, relationshipType, syncState, deleted) SELECT _id, uid, name, created, lastUpdated, relationshipType, state, deleted FROM Relationship_Old;
DROP TABLE Relationship_Old;

ALTER TABLE Note RENAME TO Note_Old;
CREATE TABLE Note (_id INTEGER PRIMARY KEY AUTOINCREMENT, noteType TEXT, event TEXT, enrollment TEXT, value TEXT, storedBy TEXT, storedDate TEXT, uid TEXT, syncState TEXT, deleted INTEGER, FOREIGN KEY (event) REFERENCES Event (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (enrollment) REFERENCES Enrollment (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (noteType, event, enrollment, value, storedBy, storedDate));
INSERT INTO Note (_id, noteType, event, enrollment, value, storedBy, storedDate, uid, syncState, deleted) SELECT _id, noteType, event, enrollment, value, storedBy, storedDate, uid, state, deleted FROM Note_Old;
DROP TABLE Note_Old;

ALTER TABLE FileResource RENAME TO FileResource_Old;
CREATE TABLE FileResource (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, name TEXT, created TEXT, lastUpdated TEXT, contentType TEXT, contentLength INTEGER, path TEXT, syncState TEXT);
INSERT INTO FileResource (_id, uid, name, created, lastUpdated, contentType, contentLength, path, syncState) SELECT _id, uid, name, created, lastUpdated, contentType, contentLength, path, state FROM FileResource_Old;
DROP TABLE FileResource_Old;

ALTER TABLE TrackedEntityInstance ADD COLUMN syncState TEXT;
UPDATE TrackedEntityInstance SET syncState = state;

ALTER TABLE Enrollment ADD COLUMN syncState TEXT;
UPDATE Enrollment SET syncState = state;

ALTER TABLE Event RENAME TO Event_Old;
CREATE TABLE Event (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, enrollment TEXT, created TEXT, lastUpdated TEXT, createdAtClient TEXT, lastUpdatedAtClient TEXT, status TEXT, geometryType TEXT, geometryCoordinates TEXT, program TEXT NOT NULL, programStage TEXT NOT NULL, organisationUnit TEXT NOT NULL, eventDate TEXT, completedDate TEXT, dueDate TEXT, syncState TEXT, attributeOptionCombo TEXT, deleted INTEGER, assignedUser TEXT, FOREIGN KEY (program) REFERENCES Program (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (programStage) REFERENCES ProgramStage (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (enrollment) REFERENCES Enrollment (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (organisationUnit) REFERENCES OrganisationUnit (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (attributeOptionCombo) REFERENCES CategoryOptionCombo (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO Event (_id, uid, enrollment, created, lastUpdated, createdAtClient, lastUpdatedAtClient, status, geometryType, geometryCoordinates, program, programStage, organisationUnit, eventDate, completedDate, dueDate, syncState, attributeOptionCombo, deleted, assignedUser) SELECT _id, uid, enrollment, created, lastUpdated, createdAtClient, lastUpdatedAtClient, status, geometryType, geometryCoordinates, program, programStage, organisationUnit, eventDate, completedDate, dueDate, state, attributeOptionCombo, deleted, assignedUser FROM Event_Old;
DROP TABLE Event_Old;