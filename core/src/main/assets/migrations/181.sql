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

# SectionDataElementLink: make sortOrder non-null (ANDROSDK-2317)

# Remove rows with null sortOrder
DELETE FROM SectionDataElementLink WHERE sortOrder IS NULL;

# Recreate table with sortOrder as NOT NULL
ALTER TABLE SectionDataElementLink RENAME TO SectionDataElementLink_Old;
CREATE TABLE SectionDataElementLink(section TEXT NOT NULL, dataElement TEXT NOT NULL, sortOrder INTEGER NOT NULL, PRIMARY KEY(section, dataElement), FOREIGN KEY(section) REFERENCES Section(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO SectionDataElementLink(section, dataElement, sortOrder) SELECT section, dataElement, sortOrder FROM SectionDataElementLink_Old;
DROP TABLE IF EXISTS SectionDataElementLink_Old;

# ProgramIndicatorLegendSetLink: make sortOrder non-null (ANDROSDK-2321)

# Remove rows with null sortOrder
DELETE FROM ProgramIndicatorLegendSetLink WHERE sortOrder IS NULL;

# Recreate table with sortOrder as NOT NULL
ALTER TABLE ProgramIndicatorLegendSetLink RENAME TO ProgramIndicatorLegendSetLink_Old;
CREATE TABLE ProgramIndicatorLegendSetLink(programIndicator TEXT NOT NULL, legendSet TEXT NOT NULL, sortOrder INTEGER NOT NULL, PRIMARY KEY(programIndicator, legendSet), FOREIGN KEY(programIndicator) REFERENCES ProgramIndicator(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(legendSet) REFERENCES LegendSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO ProgramIndicatorLegendSetLink(programIndicator, legendSet, sortOrder) SELECT programIndicator, legendSet, sortOrder FROM ProgramIndicatorLegendSetLink_Old;
DROP TABLE IF EXISTS ProgramIndicatorLegendSetLink_Old;

# DataElementLegendSetLink: make sortOrder non-null (ANDROSDK-2321)

# Remove rows with null sortOrder
DELETE FROM DataElementLegendSetLink WHERE sortOrder IS NULL;

# Recreate table with sortOrder as NOT NULL
ALTER TABLE DataElementLegendSetLink RENAME TO DataElementLegendSetLink_Old;
CREATE TABLE DataElementLegendSetLink(dataElement TEXT NOT NULL, legendSet TEXT NOT NULL, sortOrder INTEGER NOT NULL, PRIMARY KEY(dataElement, legendSet), FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(legendSet) REFERENCES LegendSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO DataElementLegendSetLink(dataElement, legendSet, sortOrder) SELECT dataElement, legendSet, sortOrder FROM DataElementLegendSetLink_Old;
DROP TABLE IF EXISTS DataElementLegendSetLink_Old;

# IndicatorLegendSetLink: make sortOrder non-null (ANDROSDK-2321)

# Remove rows with null sortOrder
DELETE FROM IndicatorLegendSetLink WHERE sortOrder IS NULL;

# Recreate table with sortOrder as NOT NULL
ALTER TABLE IndicatorLegendSetLink RENAME TO IndicatorLegendSetLink_Old;
CREATE TABLE IndicatorLegendSetLink(indicator TEXT NOT NULL, legendSet TEXT NOT NULL, sortOrder INTEGER NOT NULL, PRIMARY KEY(indicator, legendSet), FOREIGN KEY(indicator) REFERENCES Indicator(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(legendSet) REFERENCES LegendSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO IndicatorLegendSetLink(indicator, legendSet, sortOrder) SELECT indicator, legendSet, sortOrder FROM IndicatorLegendSetLink_Old;
DROP TABLE IF EXISTS IndicatorLegendSetLink_Old;

# Add isEmpty to  FilterOperator tables table (ANDROSDK-2309)

ALTER TABLE AttributeValueFilter ADD COLUMN isEmpty INTEGER;
ALTER TABLE EventDataFilter ADD COLUMN isEmpty INTEGER;
ALTER TABLE ProgramStageWorkingListAttributeValueFilter ADD COLUMN isEmpty INTEGER;
ALTER TABLE ProgramStageWorkingListEventDataFilter ADD COLUMN isEmpty INTEGER;

# MapLayer: make external non-null (ANDROSDK-2330)

# Remove rows with null external
DELETE FROM MapLayer WHERE external IS NULL;

# Recreate table with external as NOT NULL
ALTER TABLE MapLayer RENAME TO MapLayer_Old;
CREATE TABLE MapLayer(uid TEXT NOT NULL, name TEXT NOT NULL, displayName TEXT NOT NULL, external INTEGER NOT NULL, mapLayerPosition TEXT NOT NULL, style TEXT, imageUrl TEXT NOT NULL, subdomains TEXT, subdomainPlaceholder TEXT, code TEXT, mapService TEXT, imageFormat TEXT, layers TEXT, linkedLayerUid TEXT, PRIMARY KEY(uid));
INSERT INTO MapLayer(uid, name, displayName, external, mapLayerPosition, style, imageUrl, subdomains, subdomainPlaceholder, code, mapService, imageFormat, layers, linkedLayerUid) SELECT uid, name, displayName, external, mapLayerPosition, style, imageUrl, subdomains, subdomainPlaceholder, code, mapService, imageFormat, layers, linkedLayerUid FROM MapLayer_Old;
DROP TABLE IF EXISTS MapLayer_Old;

# StockUseCase: make itemCode, itemDescription, programType, description and stockOnHand non-null (ANDROSDK-2336)

# Remove rows with null itemCode, itemDescription, programType, description or stockOnHand
DELETE FROM StockUseCase WHERE itemCode IS NULL OR itemDescription IS NULL OR programType IS NULL OR description IS NULL OR stockOnHand IS NULL;

# Recreate table with itemCode, itemDescription, programType, description and stockOnHand as NOT NULL
ALTER TABLE StockUseCase RENAME TO StockUseCase_Old;
CREATE TABLE StockUseCase(uid TEXT NOT NULL, itemCode TEXT NOT NULL, itemDescription TEXT NOT NULL, programType TEXT NOT NULL, description TEXT NOT NULL, stockOnHand TEXT NOT NULL, PRIMARY KEY(uid), FOREIGN KEY(uid) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO StockUseCase(uid, itemCode, itemDescription, programType, description, stockOnHand) SELECT uid, itemCode, itemDescription, programType, description, stockOnHand FROM StockUseCase_Old;
DROP TABLE IF EXISTS StockUseCase_Old;

# StockUseCaseTransaction: make sortOrder non-null (ANDROSDK-2336)

# Remove rows with null sortOrder
DELETE FROM StockUseCaseTransaction WHERE sortOrder IS NULL;

# Recreate table with sortOrder as NOT NULL
ALTER TABLE StockUseCaseTransaction RENAME TO StockUseCaseTransaction_Old;
CREATE TABLE StockUseCaseTransaction(programUid TEXT NOT NULL, sortOrder INTEGER NOT NULL, transactionType TEXT NOT NULL, distributedTo TEXT, stockDistributed TEXT, stockDiscarded TEXT, stockCount TEXT, PRIMARY KEY(programUid, transactionType), FOREIGN KEY(programUid) REFERENCES StockUseCase(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO StockUseCaseTransaction(programUid, sortOrder, transactionType, distributedTo, stockDistributed, stockDiscarded, stockCount) SELECT programUid, sortOrder, transactionType, distributedTo, stockDistributed, stockDiscarded, stockCount FROM StockUseCaseTransaction_Old;
DROP TABLE IF EXISTS StockUseCaseTransaction_Old;

# TrackedEntityAttributeLegendSetLink: make sortOrder non-null (ANDROSDK-2334)

# Remove rows with null sortOrder
DELETE FROM TrackedEntityAttributeLegendSetLink WHERE sortOrder IS NULL;

# Recreate table with sortOrder as NOT NULL
ALTER TABLE TrackedEntityAttributeLegendSetLink RENAME TO TrackedEntityAttributeLegendSetLink_Old;
CREATE TABLE TrackedEntityAttributeLegendSetLink(trackedEntityAttribute TEXT NOT NULL, legendSet TEXT NOT NULL, sortOrder INTEGER NOT NULL, PRIMARY KEY(trackedEntityAttribute, legendSet), FOREIGN KEY(trackedEntityAttribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(legendSet) REFERENCES LegendSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO TrackedEntityAttributeLegendSetLink(trackedEntityAttribute, legendSet, sortOrder) SELECT trackedEntityAttribute, legendSet, sortOrder FROM TrackedEntityAttributeLegendSetLink_Old;
DROP TABLE IF EXISTS TrackedEntityAttributeLegendSetLink_Old;


# Add tracker custom terminology plurals labels (ANDROSDK-2275)
ALTER TABLE Program ADD COLUMN displayEnrollmentsLabel TEXT;
ALTER TABLE Program ADD COLUMN displayProgramStagesLabel TEXT;
ALTER TABLE Program ADD COLUMN displayEventsLabel TEXT;
ALTER TABLE ProgramStage ADD COLUMN displayEventsLabel TEXT;
ALTER TABLE TrackedEntityType ADD COLUMN displayTrackedEntityTypesLabel TEXT;

# Add image upload quality settings to program and dataSet settings (ANDROSDK-2348)
ALTER TABLE ProgramSetting ADD COLUMN imageSettings TEXT;
ALTER TABLE DataSetSetting ADD COLUMN imageSettings TEXT;