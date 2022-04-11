# Add analyticsType to ProgramIndicator

ALTER TABLE ProgramIndicator RENAME TO ProgramIndicator_Old;
CREATE TABLE ProgramIndicator (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, displayInForm INTEGER, expression TEXT, dimensionItem TEXT, filter TEXT, decimals INTEGER, program TEXT NOT NULL, aggregationType TEXT, analyticsType TEXT, FOREIGN KEY (program) REFERENCES Program (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO ProgramIndicator (_id, uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, displayInForm, expression, dimensionItem, filter, decimals, program, aggregationType) SELECT _id, uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, displayInForm, expression, dimensionItem, filter, decimals, program, aggregationType FROM ProgramIndicator_Old;
DROP TABLE IF EXISTS ProgramIndicator_Old;


# Add AnalyticsPeriodBoundary table

CREATE TABLE AnalyticsPeriodBoundary (_id INTEGER PRIMARY KEY AUTOINCREMENT, programIndicator TEXT NOT NULL, boundaryTarget TEXT, analyticsPeriodBoundaryType TEXT, offsetPeriods INTEGER, offsetPeriodType TEXT, FOREIGN KEY (programIndicator) REFERENCES ProgramIndicator (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
