# Rename eventStatus property to status on EventFilter class (ANDROSDK-2208)

ALTER TABLE EventFilter RENAME TO EventFilter_Old;
CREATE TABLE EventFilter(uid TEXT NOT NULL, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, program TEXT NOT NULL, programStage TEXT, description TEXT, followUp INTEGER, organisationUnit TEXT, ouMode TEXT, assignedUserMode TEXT, orderProperty TEXT, displayColumnOrder TEXT, events TEXT, status TEXT, eventDate TEXT, dueDate TEXT, lastUpdatedDate TEXT, completedDate TEXT, PRIMARY KEY(uid), FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO EventFilter(uid, code, name, displayName, created, lastUpdated, program, programStage, description, followUp, organisationUnit, ouMode, assignedUserMode, orderProperty, displayColumnOrder, events, status, eventDate, dueDate, lastUpdatedDate, completedDate) SELECT uid, code, name, displayName, created, lastUpdated, program, programStage, description, followUp, organisationUnit, ouMode, assignedUserMode, orderProperty, displayColumnOrder, events, eventStatus, eventDate, dueDate, lastUpdatedDate, completedDate FROM EventFilter_Old;
DROP TABLE EventFilter_Old;

# Add workingListsHash column and update indices to include it (ANDROSDK-2211)

ALTER TABLE TrackedEntityInstanceSync ADD COLUMN workingListsHash INTEGER;
ALTER TABLE EventSync ADD COLUMN workingListsHash INTEGER;

DROP INDEX IF EXISTS teisyncprogram_organisationunithash;
DROP INDEX IF EXISTS eventsyncprogram_organisationunithash;

CREATE UNIQUE INDEX teisync_program_orgunit_workinglists ON TrackedEntityInstanceSync(program, organisationUnitIdsHash, workingListsHash);
CREATE UNIQUE INDEX eventsync_program_orgunit_workinglists ON EventSync(program, organisationUnitIdsHash, workingListsHash);
