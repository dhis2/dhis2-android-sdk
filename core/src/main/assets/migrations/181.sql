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

# Uppercase ValidationRule.operator values to match new enum names (ANDROSDK-2298)
UPDATE ValidationRule SET operator = 'EQUAL_TO' WHERE operator = 'equal_to';
UPDATE ValidationRule SET operator = 'NOT_EQUAL_TO' WHERE operator = 'not_equal_to';
UPDATE ValidationRule SET operator = 'GREATER_THAN' WHERE operator = 'greater_than';
UPDATE ValidationRule SET operator = 'GREATER_THAN_OR_EQUAL_TO' WHERE operator = 'greater_than_or_equal_to';
UPDATE ValidationRule SET operator = 'LESS_THAN' WHERE operator = 'less_than';
UPDATE ValidationRule SET operator = 'LESS_THAN_OR_EQUAL_TO' WHERE operator = 'less_than_or_equal_to';
UPDATE ValidationRule SET operator = 'COMPULSORY_PAIR' WHERE operator = 'compulsory_pair';
UPDATE ValidationRule SET operator = 'EXCLUSIVE_PAIR' WHERE operator = 'exclusive_pair';

# AnalyticsTeiWHONutritionGenderValues: make genderFemale and genderMale non-null (ANDROSDK-2295)

# Remove rows with null gender values
DELETE FROM AnalyticsTeiWHONutritionData WHERE genderFemale IS NULL OR genderMale IS NULL;

# Recreate table with genderFemale and genderMale as NOT NULL
ALTER TABLE AnalyticsTeiWHONutritionData RENAME TO AnalyticsTeiWHONutritionData_Old;
CREATE TABLE AnalyticsTeiWHONutritionData(teiSetting TEXT NOT NULL, chartType TEXT, genderAttribute TEXT NOT NULL, genderFemale TEXT NOT NULL, genderMale TEXT NOT NULL, PRIMARY KEY(teiSetting, genderAttribute), FOREIGN KEY(genderAttribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(teiSetting) REFERENCES AnalyticsTeiSetting(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO AnalyticsTeiWHONutritionData(teiSetting, chartType, genderAttribute, genderFemale, genderMale) SELECT teiSetting, chartType, genderAttribute, genderFemale, genderMale FROM AnalyticsTeiWHONutritionData_Old;
DROP TABLE IF EXISTS AnalyticsTeiWHONutritionData_Old;

# AnalyticsDhisVisualization: make scope, groupUid and groupName non-null (ANDROSDK-2295)

# Remove rows with null scope, groupUid or groupName
DELETE FROM AnalyticsDhisVisualization WHERE scope IS NULL OR groupUid IS NULL OR groupName IS NULL;

# Recreate table with scope, groupUid and groupName as NOT NULL
ALTER TABLE AnalyticsDhisVisualization RENAME TO AnalyticsDhisVisualization_Old;
CREATE TABLE AnalyticsDhisVisualization (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, uid TEXT NOT NULL, scopeUid TEXT, scope TEXT NOT NULL, groupUid TEXT NOT NULL, groupName TEXT NOT NULL, timestamp TEXT, name TEXT, type TEXT NOT NULL);
INSERT INTO AnalyticsDhisVisualization(_id, uid, scopeUid, scope, groupUid, groupName, timestamp, name, type) SELECT _id, uid, scopeUid, scope, groupUid, groupName, timestamp, name, type FROM AnalyticsDhisVisualization_Old;
DROP TABLE IF EXISTS AnalyticsDhisVisualization_Old;

# SMSOngoingSubmission: make type non-null (ANDROSDK-2295)

# Remove rows with null type
DELETE FROM SMSOngoingSubmission WHERE type IS NULL;

# Recreate table with type as NOT NULL
ALTER TABLE SMSOngoingSubmission RENAME TO SMSOngoingSubmission_Old;
CREATE TABLE SMSOngoingSubmission(submissionId INTEGER NOT NULL, type TEXT NOT NULL, PRIMARY KEY(submissionId));
INSERT INTO SMSOngoingSubmission(submissionId, type) SELECT submissionId, type FROM SMSOngoingSubmission_Old;
DROP TABLE IF EXISTS SMSOngoingSubmission_Old;

# UserOrganisationUnit: make root and userAssigned non-null (ANDROSDK-2307)

# Remove rows with null root or userAssigned
DELETE FROM UserOrganisationUnit WHERE root IS NULL OR userAssigned IS NULL;

# Recreate table with root and userAssigned as NOT NULL
ALTER TABLE UserOrganisationUnit RENAME TO UserOrganisationUnit_Old;
CREATE TABLE UserOrganisationUnit(user TEXT NOT NULL, organisationUnit TEXT NOT NULL, organisationUnitScope TEXT NOT NULL, root INTEGER NOT NULL, userAssigned INTEGER NOT NULL, PRIMARY KEY(organisationUnitScope, user, organisationUnit), FOREIGN KEY(user) REFERENCES User(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO UserOrganisationUnit(user, organisationUnit, organisationUnitScope, root, userAssigned) SELECT user, organisationUnit, organisationUnitScope, root, userAssigned FROM UserOrganisationUnit_Old;
DROP TABLE IF EXISTS UserOrganisationUnit_Old;

# CategoryOptionOrganisationUnitLink: make restriction non-null (ANDROSDK-2308)

# Remove rows with null restriction
DELETE FROM CategoryOptionOrganisationUnitLink WHERE restriction IS NULL;

# Recreate table with restriction as NOT NULL
ALTER TABLE CategoryOptionOrganisationUnitLink RENAME TO CategoryOptionOrganisationUnitLink_Old;
CREATE TABLE CategoryOptionOrganisationUnitLink(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, categoryOption TEXT NOT NULL, organisationUnit TEXT, restriction TEXT NOT NULL, FOREIGN KEY(categoryOption) REFERENCES CategoryOption(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO CategoryOptionOrganisationUnitLink(_id, categoryOption, organisationUnit, restriction) SELECT _id, categoryOption, organisationUnit, restriction FROM CategoryOptionOrganisationUnitLink_Old;
DROP TABLE IF EXISTS CategoryOptionOrganisationUnitLink_Old;

# ValidationRuleExpression: make description and missingValueStrategy non-null (ANDROSDK-2310)

# Remove ValidationRule rows with null leftSide/rightSide description or missingValueStrategy
DELETE FROM ValidationRule WHERE leftSideDescription IS NULL OR leftSideMissingValueStrategy IS NULL OR rightSideDescription IS NULL OR rightSideMissingValueStrategy IS NULL;

# Recreate ValidationRule with leftSideDescription, leftSideMissingValueStrategy, rightSideDescription, rightSideMissingValueStrategy as NOT NULL
ALTER TABLE ValidationRule RENAME TO ValidationRule_Old;
CREATE TABLE ValidationRule(uid TEXT NOT NULL, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, instruction TEXT, importance TEXT, operator TEXT, periodType TEXT, skipFormValidation INTEGER, leftSideExpression TEXT, leftSideDescription TEXT NOT NULL, leftSideMissingValueStrategy TEXT NOT NULL, rightSideExpression TEXT, rightSideDescription TEXT NOT NULL, rightSideMissingValueStrategy TEXT NOT NULL, organisationUnitLevels TEXT, PRIMARY KEY(uid));
INSERT INTO ValidationRule(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, instruction, importance, operator, periodType, skipFormValidation, leftSideExpression, leftSideDescription, leftSideMissingValueStrategy, rightSideExpression, rightSideDescription, rightSideMissingValueStrategy, organisationUnitLevels) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, instruction, importance, operator, periodType, skipFormValidation, leftSideExpression, leftSideDescription, leftSideMissingValueStrategy, rightSideExpression, rightSideDescription, rightSideMissingValueStrategy, organisationUnitLevels FROM ValidationRule_Old;
DROP TABLE IF EXISTS ValidationRule_Old;
