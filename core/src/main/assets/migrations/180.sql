# v1.14.0 migrations

# Add new columns for operators on TrackedEntityAttribute table (ANDROSDK-2232)

ALTER TABLE TrackedEntityAttribute ADD COLUMN preferredSearchOperator TEXT;
ALTER TABLE TrackedEntityAttribute ADD COLUMN blockedSearchOperators TEXT;
ALTER TABLE TrackedEntityAttribute ADD COLUMN minCharactersToSearch INTEGER;


# Add and populate sourceDataSet column to DataValue table (ANDROSDK-2219)

ALTER TABLE DataValue RENAME TO DataValue_Old;
CREATE TABLE DataValue(dataElement TEXT NOT NULL, period TEXT NOT NULL, organisationUnit TEXT NOT NULL, categoryOptionCombo TEXT NOT NULL, attributeOptionCombo TEXT NOT NULL, sourceDataSet TEXT, value TEXT, storedBy TEXT, created TEXT, lastUpdated TEXT, comment TEXT, followUp INTEGER, syncState TEXT, deleted INTEGER, PRIMARY KEY(dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo), FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(period) REFERENCES Period(periodId) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryOptionCombo) REFERENCES CategoryOptionCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(attributeOptionCombo) REFERENCES CategoryOptionCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(sourceDataSet) REFERENCES DataSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO DataValue(dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo, sourceDataSet, value, storedBy, created, lastUpdated, comment, followUp, syncState, deleted) SELECT dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo, (SELECT ds.uid FROM DataSet ds INNER JOIN DataSetDataElementLink dsdel ON dsdel.dataSet = ds.uid INNER JOIN DataElement de ON de.uid = dsdel.dataElement INNER JOIN DataSetOrganisationUnitLink dsoul ON dsoul.dataSet = ds.uid INNER JOIN CategoryOptionCombo aoc ON aoc.categoryCombo = ds.categoryCombo INNER JOIN CategoryOptionCombo coc ON coc.categoryCombo = COALESCE(dsdel.categoryCombo, de.categoryCombo) INNER JOIN Period p ON p.periodType = ds.periodType WHERE dsdel.dataElement = DataValue_Old.dataElement AND dsoul.organisationUnit = DataValue_Old.organisationUnit AND aoc.uid = DataValue_Old.attributeOptionCombo AND coc.uid = DataValue_Old.categoryOptionCombo AND p.periodId = DataValue_Old.period ORDER BY ds.uid ASC LIMIT 1) AS sourceDataSet, value, storedBy, created, lastUpdated, comment, followUp, syncState, deleted FROM DataValue_Old;
DROP TABLE IF EXISTS DataValue_Old;


# Make categoryCombo non-nullable in Program table (ANDROSDK-2149)

ALTER TABLE Program RENAME TO Program_Old;
CREATE TABLE Program(uid TEXT NOT NULL, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, version INTEGER, onlyEnrollOnce INTEGER, displayEnrollmentDateLabel TEXT, displayIncidentDate INTEGER, displayIncidentDateLabel TEXT, registration INTEGER, selectEnrollmentDatesInFuture INTEGER, dataEntryMethod INTEGER, ignoreOverdueEvents INTEGER, selectIncidentDatesInFuture INTEGER, useFirstStageDuringRegistration INTEGER, displayFrontPageList INTEGER, programType TEXT, relatedProgram TEXT, trackedEntityType TEXT, categoryCombo TEXT NOT NULL, accessDataWrite INTEGER, expiryDays INTEGER, completeEventsExpiryDays INTEGER, expiryPeriodType TEXT, minAttributesRequiredToSearch INTEGER, maxTeiCountToReturn INTEGER, featureType TEXT, accessLevel TEXT, color TEXT, icon TEXT, displayEnrollmentLabel TEXT, displayFollowUpLabel TEXT, displayOrgUnitLabel TEXT, displayRelationshipLabel TEXT, displayNoteLabel TEXT, displayTrackedEntityAttributeLabel TEXT, displayProgramStageLabel TEXT, displayEventLabel TEXT, PRIMARY KEY(uid), FOREIGN KEY(trackedEntityType) REFERENCES TrackedEntityType(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryCombo) REFERENCES CategoryCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO Program(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, version, onlyEnrollOnce, displayEnrollmentDateLabel, displayIncidentDate, displayIncidentDateLabel, registration, selectEnrollmentDatesInFuture, dataEntryMethod, ignoreOverdueEvents, selectIncidentDatesInFuture, useFirstStageDuringRegistration, displayFrontPageList, programType, relatedProgram, trackedEntityType, categoryCombo, accessDataWrite, expiryDays, completeEventsExpiryDays, expiryPeriodType, minAttributesRequiredToSearch, maxTeiCountToReturn, featureType, accessLevel, color, icon, displayEnrollmentLabel, displayFollowUpLabel, displayOrgUnitLabel, displayRelationshipLabel, displayNoteLabel, displayTrackedEntityAttributeLabel, displayProgramStageLabel, displayEventLabel) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, version, onlyEnrollOnce, displayEnrollmentDateLabel, displayIncidentDate, displayIncidentDateLabel, registration, selectEnrollmentDatesInFuture, dataEntryMethod, ignoreOverdueEvents, selectIncidentDatesInFuture, useFirstStageDuringRegistration, displayFrontPageList, programType, relatedProgram, trackedEntityType, COALESCE(categoryCombo, (SELECT uid FROM CategoryCombo WHERE isDefault = 1 LIMIT 1)), accessDataWrite, expiryDays, completeEventsExpiryDays, expiryPeriodType, minAttributesRequiredToSearch, maxTeiCountToReturn, featureType, accessLevel, color, icon, displayEnrollmentLabel, displayFollowUpLabel, displayOrgUnitLabel, displayRelationshipLabel, displayNoteLabel, displayTrackedEntityAttributeLabel, displayProgramStageLabel, displayEventLabel FROM Program_Old;
DROP TABLE IF EXISTS Program_Old;


# Do not persist DataApproval with null state (ANDROSDK-2242)

ALTER TABLE DataApproval RENAME TO DataApproval_Old;
CREATE TABLE DataApproval(workflow TEXT NOT NULL, organisationUnit TEXT NOT NULL, period TEXT NOT NULL, attributeOptionCombo TEXT NOT NULL, state TEXT NOT NULL, PRIMARY KEY(workflow, attributeOptionCombo, period, organisationUnit), FOREIGN KEY(attributeOptionCombo) REFERENCES CategoryOptionCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(period) REFERENCES Period(periodId) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataApproval(workflow, organisationUnit, period, attributeOptionCombo, state) SELECT workflow, organisationUnit, period, attributeOptionCombo, state FROM DataApproval_Old WHERE state IS NOT NULL;
DROP TABLE IF EXISTS DataApproval_Old;


# Add linkedLayerUid for composite map layers (ANDROSDK-2191)

ALTER TABLE MapLayer ADD COLUMN linkedLayerUid TEXT;

