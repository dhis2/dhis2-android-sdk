# PI disaggregations: adapt metadata model (ANDROSDK-1994)

# Add categoryCombo, attributeCombo and categoryMappingIds columns to ProgramIndicator table
ALTER TABLE ProgramIndicator ADD COLUMN categoryCombo TEXT;
ALTER TABLE ProgramIndicator ADD COLUMN attributeCombo TEXT;
ALTER TABLE ProgramIndicator ADD COLUMN categoryMappingIds TEXT;

# Create CategoryMapping table
CREATE TABLE CategoryMapping(id TEXT NOT NULL, program TEXT NOT NULL, categoryId TEXT NOT NULL, mappingName TEXT NOT NULL, PRIMARY KEY(id), FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);

# Create CategoryOptionMapping table
CREATE TABLE CategoryOptionMapping(categoryMapping TEXT NOT NULL, optionId TEXT NOT NULL, filter TEXT NOT NULL, PRIMARY KEY(categoryMapping, optionId), FOREIGN KEY(categoryMapping) REFERENCES CategoryMapping(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
