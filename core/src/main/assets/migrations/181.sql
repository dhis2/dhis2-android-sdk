# PI disaggregations: adapt metadata model (ANDROSDK-1994)

# Add categoryCombo, attributeCombo (NOT NULL) and categoryMappingIds columns to ProgramIndicator table
ALTER TABLE ProgramIndicator RENAME TO ProgramIndicator_Old;
CREATE TABLE ProgramIndicator(uid TEXT NOT NULL, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, displayInForm INTEGER, expression TEXT, dimensionItem TEXT, filter TEXT, decimals INTEGER, program TEXT NOT NULL, aggregationType TEXT, analyticsType TEXT, categoryCombo TEXT NOT NULL, attributeCombo TEXT NOT NULL, categoryMappingIds TEXT, PRIMARY KEY(uid), FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryCombo) REFERENCES CategoryCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(attributeCombo) REFERENCES CategoryCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO ProgramIndicator(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, displayInForm, expression, dimensionItem, filter, decimals, program, aggregationType, analyticsType, categoryCombo, attributeCombo) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, displayInForm, expression, dimensionItem, filter, decimals, program, aggregationType, analyticsType, (SELECT uid FROM CategoryCombo WHERE isDefault = 1 LIMIT 1), (SELECT uid FROM CategoryCombo WHERE isDefault = 1 LIMIT 1) FROM ProgramIndicator_Old;
DROP TABLE IF EXISTS ProgramIndicator_Old;

# Create CategoryMapping table
CREATE TABLE CategoryMapping(uid TEXT NOT NULL, program TEXT NOT NULL, categoryId TEXT NOT NULL, mappingName TEXT NOT NULL, PRIMARY KEY(uid), FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);

# Create CategoryOptionMapping table
CREATE TABLE CategoryOptionMapping(categoryMapping TEXT NOT NULL, optionId TEXT NOT NULL, filter TEXT NOT NULL, PRIMARY KEY(categoryMapping, optionId), FOREIGN KEY(categoryMapping) REFERENCES CategoryMapping(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
