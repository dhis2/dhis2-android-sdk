# Add restriction (ANDROSDK-1502);

ALTER TABLE EventDataFilter RENAME TO EventDataFilter_Old;
CREATE TABLE ItemFilter (_id INTEGER PRIMARY KEY AUTOINCREMENT, eventFilter TEXT, dataItem TEXT, trackedEntityInstanceFilter TEXT, attribute TEXT, sw TEXT, ew TEXT, le TEXT, ge TEXT, gt TEXT, lt TEXT, eq TEXT, inProperty TEXT, like TEXT, dateFilter TEXT, FOREIGN KEY (eventFilter) REFERENCES EventFilter (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (trackedEntityInstanceFilter) REFERENCES TrackedEntityInstanceFilter (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO ItemFilter (_id, eventFilter, dataItem, le, ge, gt, lt, eq, inProperty, like, dateFilter) SELECT _id, eventFilter, dataItem, le, ge, gt, lt, eq, inProperty, like, dateFilter FROM EventDataFilter_Old;
DROP TABLE IF EXISTS  EventDataFilter_Old;

ALTER TABLE TrackedEntityInstanceFilter RENAME TO TrackedEntityInstanceFilter_Old;
CREATE TABLE TrackedEntityInstanceFilter (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, color TEXT, icon TEXT, program TEXT NOT NULL, description TEXT, sortOrder INTEGER, enrollmentStatus TEXT, followUp INTEGER, organisationUnit TEXT, ouMode TEXT, assignedUserMode TEXT, orderProperty TEXT, displayColumnOrder TEXT, eventStatus TEXT, eventDate TEXT, lastUpdatedDate TEXT, programStage TEXT, trackedEntityInstances TEXT, enrollmentIncidentDate TEXT, enrollmentCreatedDate TEXT, trackedEntityType TEXT, FOREIGN KEY (program) REFERENCES Program (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO TrackedEntityInstanceFilter (_id, uid, code, name, displayName, created, lastUpdated, color, icon, program, description, sortOrder, enrollmentStatus, followUp) SELECT _id, uid, code, name, displayName, created, lastUpdated, color, icon, program, description, sortOrder, enrollmentStatus, followUp FROM TrackedEntityInstanceFilter_Old;
DROP TABLE IF EXISTS TrackedEntityInstanceFilter_Old;
