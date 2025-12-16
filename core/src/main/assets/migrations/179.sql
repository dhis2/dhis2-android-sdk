# Add workingListsHash column and update indices to include it (ANDROSDK-2211)

ALTER TABLE TrackedEntityInstanceSync ADD COLUMN workingListsHash INTEGER;
ALTER TABLE EventSync ADD COLUMN workingListsHash INTEGER;

DROP INDEX IF EXISTS teisyncprogram_organisationunithash;
DROP INDEX IF EXISTS eventsyncprogram_organisationunithash;

CREATE UNIQUE INDEX teisync_program_orgunit_workinglists ON TrackedEntityInstanceSync(program, organisationUnitIdsHash, workingListsHash);
CREATE UNIQUE INDEX eventsync_program_orgunit_workinglists ON EventSync(program, organisationUnitIdsHash, workingListsHash);
