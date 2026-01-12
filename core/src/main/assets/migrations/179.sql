# SDK 1.13.1 to 1.14.0 migrations
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


# Add sourceDataSet column with FK constraint to DataValue table (ANDROSDK-2219)

ALTER TABLE DataValue RENAME TO DataValue_Old;
CREATE TABLE DataValue(_id INTEGER PRIMARY KEY AUTOINCREMENT, dataElement TEXT NOT NULL, period TEXT NOT NULL, organisationUnit TEXT NOT NULL, categoryOptionCombo TEXT NOT NULL, attributeOptionCombo TEXT NOT NULL, sourceDataSet TEXT, value TEXT, storedBy TEXT, created TEXT, lastUpdated TEXT, comment TEXT, followUp INTEGER, syncState TEXT, deleted INTEGER, FOREIGN KEY (dataElement) REFERENCES DataElement (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (period) REFERENCES Period (periodId) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (organisationUnit) REFERENCES OrganisationUnit (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (categoryOptionCombo) REFERENCES CategoryOptionCombo (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (attributeOptionCombo) REFERENCES CategoryOptionCombo (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (sourceDataSet) REFERENCES DataSet (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo));
INSERT INTO DataValue(_id, dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo, sourceDataSet, value, storedBy, created, lastUpdated, comment, followUp, syncState, deleted) SELECT _id, dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo,
    (SELECT ds.uid FROM DataSet ds INNER JOIN DataSetDataElementLink dsdel ON dsdel.dataSet = ds.uid INNER JOIN DataElement de ON de.uid = dsdel.dataElement INNER JOIN DataSetOrganisationUnitLink dsoul ON dsoul.dataSet = ds.uid INNER JOIN CategoryOptionCombo aoc ON aoc.categoryCombo = ds.categoryCombo INNER JOIN CategoryOptionCombo coc ON coc.categoryCombo = COALESCE(dsdel.categoryCombo, de.categoryCombo) INNER JOIN Period p ON p.periodType = ds.periodType WHERE dsdel.dataElement = DataValue_Old.dataElement AND dsoul.organisationUnit = DataValue_Old.organisationUnit AND aoc.uid = DataValue_Old.attributeOptionCombo AND coc.uid = DataValue_Old.categoryOptionCombo AND p.periodId = DataValue_Old.period ORDER BY ds.uid ASC LIMIT 1) AS sourceDataSet,
    value, storedBy, created, lastUpdated, comment, followUp, syncState, deleted FROM DataValue_Old;
DROP TABLE IF EXISTS DataValue_Old;

