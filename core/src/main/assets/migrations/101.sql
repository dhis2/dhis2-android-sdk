# Fix missing ON CASCADE modifiers ANDROSDK-1398

ALTER TABLE DataElementOperand RENAME TO DataElementOperand_Old;
CREATE TABLE DataElementOperand (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, dataElement TEXT, categoryOptionCombo TEXT, FOREIGN KEY (dataElement) REFERENCES DataElement (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (categoryOptionCombo) REFERENCES CategoryOptionCombo (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO DataElementOperand (_id, uid, dataElement, categoryOptionCombo) SELECT _id, uid, dataElement, categoryOptionCombo FROM DataElementOperand_Old;
DROP TABLE DataElementOperand_Old;

ALTER TABLE TrackerImportConflict RENAME TO TrackerImportConflict_Old;
CREATE TABLE TrackerImportConflict (_id INTEGER PRIMARY KEY AUTOINCREMENT, conflict TEXT, value TEXT, trackedEntityInstance TEXT, enrollment TEXT, event TEXT, tableReference TEXT, errorCode TEXT, status TEXT, created TEXT, displayDescription TEXT, trackedEntityAttribute TEXT, dataElement TEXT, FOREIGN KEY (trackedEntityInstance) REFERENCES TrackedEntityInstance (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (enrollment) REFERENCES Enrollment (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (event) REFERENCES Event (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO TrackerImportConflict (_id, conflict, value, trackedEntityInstance, enrollment, event, tableReference, errorCode, status, created, displayDescription, trackedEntityAttribute, dataElement) SELECT _id, conflict, value, trackedEntityInstance, enrollment, event, tableReference, errorCode, status, created, displayDescription, trackedEntityAttribute, dataElement FROM TrackerImportConflict_Old;
DROP TABLE TrackerImportConflict_Old;