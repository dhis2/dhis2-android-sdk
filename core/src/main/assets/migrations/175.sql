# Removes _id primary key (ANDROSDK-2128)

ALTER TABLE Configuration RENAME TO Configuration_Old;
CREATE TABLE Configuration(serverUrl TEXT NOT NULL PRIMARY KEY);
INSERT OR IGNORE INTO Configuration(serverUrl) SELECT serverUrl FROM Configuration_Old;

ALTER TABLE User RENAME TO User_Old;
CREATE TABLE User(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, birthday TEXT, education TEXT, gender TEXT, jobTitle TEXT, surname TEXT, firstName TEXT, introduction TEXT, employer TEXT, interests TEXT, languages TEXT, email TEXT, phoneNumber TEXT, nationality TEXT, username TEXT);
INSERT OR IGNORE INTO User(uid, code, name, displayName, created, lastUpdated, birthday, education, gender, jobTitle, surname, firstName, introduction, employer, interests, languages, email, phoneNumber, nationality, username) SELECT uid, code, name, displayName, created, lastUpdated, birthday, education, gender, jobTitle, surname, firstName, introduction, employer, interests, languages, email, phoneNumber, nationality, username FROM User_Old;

ALTER TABLE OrganisationUnit RENAME TO OrganisationUnit_Old;
CREATE TABLE OrganisationUnit(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, path TEXT, openingDate TEXT, closedDate TEXT, level INTEGER, parent TEXT, displayNamePath TEXT, geometryType TEXT, geometryCoordinates TEXT);
INSERT OR IGNORE INTO OrganisationUnit(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, path, openingDate, closedDate, level, parent, displayNamePath, geometryType, geometryCoordinates) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, path, openingDate, closedDate, level, parent, displayNamePath, geometryType, geometryCoordinates FROM OrganisationUnit_Old;

ALTER TABLE OptionSet RENAME TO OptionSet_Old;
CREATE TABLE OptionSet(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, version INTEGER, valueType TEXT);
INSERT OR IGNORE INTO OptionSet(uid, code, name, displayName, created, lastUpdated, version, valueType) SELECT uid, code, name, displayName, created, lastUpdated, version, valueType FROM OptionSet_Old;

ALTER TABLE Option RENAME TO Option_Old;
CREATE TABLE Option(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, optionSet TEXT NOT NULL, sortOrder INTEGER, color TEXT, icon TEXT, FOREIGN KEY(optionSet) REFERENCES OptionSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO Option(uid, code, name, displayName, created, lastUpdated, optionSet, sortOrder, color, icon) SELECT uid, code, name, displayName, created, lastUpdated, optionSet, sortOrder, color, icon FROM Option_Old;

ALTER TABLE TrackedEntityType RENAME TO TrackedEntityType_Old;
CREATE TABLE TrackedEntityType(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, featureType TEXT, color TEXT, icon TEXT, accessDataWrite INTEGER);
INSERT OR IGNORE INTO TrackedEntityType(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, featureType, color, icon, accessDataWrite) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, featureType, color, icon, accessDataWrite FROM TrackedEntityType_Old;

ALTER TABLE ProgramStageSection RENAME TO ProgramStageSection_Old;
CREATE TABLE ProgramStageSection(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, sortOrder INTEGER, programStage TEXT NOT NULL, desktopRenderType TEXT, mobileRenderType TEXT, description TEXT, displayDescription TEXT, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramStageSection(uid, code, name, displayName, created, lastUpdated, sortOrder, programStage, desktopRenderType, mobileRenderType, description, displayDescription) SELECT uid, code, name, displayName, created, lastUpdated, sortOrder, programStage, desktopRenderType, mobileRenderType, description, displayDescription FROM ProgramStageSection_Old;

ALTER TABLE ProgramRuleVariable RENAME TO ProgramRuleVariable_Old;
CREATE TABLE ProgramRuleVariable(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, useCodeForOptionSet INTEGER, program TEXT NOT NULL, programStage TEXT, dataElement TEXT, trackedEntityAttribute TEXT, programRuleVariableSourceType TEXT, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(trackedEntityAttribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramRuleVariable(uid, code, name, displayName, created, lastUpdated, useCodeForOptionSet, program, programStage, dataElement, trackedEntityAttribute, programRuleVariableSourceType) SELECT uid, code, name, displayName, created, lastUpdated, useCodeForOptionSet, program, programStage, dataElement, trackedEntityAttribute, programRuleVariableSourceType FROM ProgramRuleVariable_Old;

ALTER TABLE ProgramTrackedEntityAttribute RENAME TO ProgramTrackedEntityAttribute_Old;
CREATE TABLE ProgramTrackedEntityAttribute(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, mandatory INTEGER, trackedEntityAttribute TEXT NOT NULL, allowFutureDate INTEGER, displayInList INTEGER, program TEXT NOT NULL, sortOrder INTEGER, searchable INTEGER, FOREIGN KEY(trackedEntityAttribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramTrackedEntityAttribute(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, mandatory, trackedEntityAttribute, allowFutureDate, displayInList, program, sortOrder, searchable) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, mandatory, trackedEntityAttribute, allowFutureDate, displayInList, program, sortOrder, searchable FROM ProgramTrackedEntityAttribute_Old;

ALTER TABLE Constant RENAME TO Constant_Old;
CREATE TABLE Constant(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, value TEXT);
INSERT OR IGNORE INTO Constant(uid, code, name, displayName, created, lastUpdated, value) SELECT uid, code, name, displayName, created, lastUpdated, value FROM Constant_Old;

ALTER TABLE SystemInfo RENAME TO SystemInfo_Old;
CREATE TABLE SystemInfo(serverDate TEXT, dateFormat TEXT, version TEXT, contextPath TEXT NOT NULL PRIMARY KEY, systemName TEXT);
INSERT OR IGNORE INTO SystemInfo(serverDate, dateFormat, version, contextPath, systemName) SELECT serverDate, dateFormat, version, contextPath, systemName FROM SystemInfo_Old;

ALTER TABLE ProgramRule RENAME TO ProgramRule_Old;
CREATE TABLE ProgramRule(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, priority INTEGER, condition TEXT, program TEXT NOT NULL, programStage TEXT, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramRule(uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage) SELECT uid, code, name, displayName, created, lastUpdated, priority, condition, program, programStage FROM ProgramRule_Old;

ALTER TABLE ProgramIndicator RENAME TO ProgramIndicator_Old;
CREATE TABLE ProgramIndicator(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, displayInForm INTEGER, expression TEXT, dimensionItem TEXT, filter TEXT, decimals INTEGER, program TEXT NOT NULL, aggregationType TEXT, analyticsType TEXT, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramIndicator(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, displayInForm, expression, dimensionItem, filter, decimals, program, aggregationType, analyticsType) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, displayInForm, expression, dimensionItem, filter, decimals, program, aggregationType, analyticsType FROM ProgramIndicator_Old;

ALTER TABLE Resource RENAME TO Resource_Old;
CREATE TABLE Resource(resourceType TEXT NOT NULL PRIMARY KEY, lastSynced TEXT);
INSERT OR IGNORE INTO Resource(resourceType, lastSynced) SELECT resourceType, lastSynced FROM Resource_Old;

ALTER TABLE OrganisationUnitProgramLink RENAME TO OrganisationUnitProgramLink_Old;
CREATE TABLE OrganisationUnitProgramLink(organisationUnit TEXT NOT NULL, program TEXT NOT NULL, PRIMARY KEY(organisationUnit, program), FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO OrganisationUnitProgramLink(organisationUnit, program) SELECT organisationUnit, program FROM OrganisationUnitProgramLink_Old;

ALTER TABLE UserRole RENAME TO UserRole_Old;
CREATE TABLE UserRole(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT);
INSERT OR IGNORE INTO UserRole(uid, code, name, displayName, created, lastUpdated) SELECT uid, code, name, displayName, created, lastUpdated FROM UserRole_Old;

ALTER TABLE ProgramStageSectionProgramIndicatorLink RENAME TO ProgramStageSectionProgramIndicatorLink_Old;
CREATE TABLE ProgramStageSectionProgramIndicatorLink(programStageSection TEXT NOT NULL, programIndicator TEXT NOT NULL, PRIMARY KEY(programStageSection, programIndicator), FOREIGN KEY(programStageSection) REFERENCES ProgramStageSection(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programIndicator) REFERENCES ProgramIndicator(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramStageSectionProgramIndicatorLink(programStageSection, programIndicator) SELECT programStageSection, programIndicator FROM ProgramStageSectionProgramIndicatorLink_Old;

ALTER TABLE Category RENAME TO Category_Old;
CREATE TABLE Category(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, dataDimensionType TEXT);
INSERT OR IGNORE INTO Category(uid, code, name, displayName, created, lastUpdated, dataDimensionType) SELECT uid, code, name, displayName, created, lastUpdated, dataDimensionType FROM Category_Old;

ALTER TABLE CategoryOption RENAME TO CategoryOption_Old;
CREATE TABLE CategoryOption(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, startDate TEXT, endDate TEXT, accessDataWrite INTEGER);
INSERT OR IGNORE INTO CategoryOption(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, startDate, endDate, accessDataWrite) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, startDate, endDate, accessDataWrite FROM CategoryOption_Old;

ALTER TABLE CategoryCategoryOptionLink RENAME TO CategoryCategoryOptionLink_Old;
CREATE TABLE CategoryCategoryOptionLink(category TEXT NOT NULL, categoryOption TEXT NOT NULL, sortOrder INTEGER, PRIMARY KEY(category, categoryOption), FOREIGN KEY(category) REFERENCES Category(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryOption) REFERENCES CategoryOption(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO CategoryCategoryOptionLink(category, categoryOption, sortOrder) SELECT category, categoryOption, sortOrder FROM CategoryCategoryOptionLink_Old;

ALTER TABLE CategoryCombo RENAME TO CategoryCombo_Old;
CREATE TABLE CategoryCombo(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, isDefault INTEGER);
INSERT OR IGNORE INTO CategoryCombo(uid, code, name, displayName, created, lastUpdated, isDefault) SELECT uid, code, name, displayName, created, lastUpdated, isDefault FROM CategoryCombo_Old;

ALTER TABLE CategoryCategoryComboLink RENAME TO CategoryCategoryComboLink_Old;
CREATE TABLE CategoryCategoryComboLink(category TEXT NOT NULL, categoryCombo TEXT NOT NULL, sortOrder INTEGER, PRIMARY KEY(category, categoryCombo), FOREIGN KEY(category) REFERENCES Category(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryCombo) REFERENCES CategoryCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO CategoryCategoryComboLink(category, categoryCombo, sortOrder) SELECT category, categoryCombo, sortOrder FROM CategoryCategoryComboLink_Old;

ALTER TABLE CategoryOptionCombo RENAME TO CategoryOptionCombo_Old;
CREATE TABLE CategoryOptionCombo(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, categoryCombo TEXT, FOREIGN KEY(categoryCombo) REFERENCES CategoryCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO CategoryOptionCombo(uid, code, name, displayName, created, lastUpdated, categoryCombo) SELECT uid, code, name, displayName, created, lastUpdated, categoryCombo FROM CategoryOptionCombo_Old;

ALTER TABLE DataSet RENAME TO DataSet_Old;
CREATE TABLE DataSet(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, periodType TEXT, categoryCombo TEXT NOT NULL, mobile INTEGER, version INTEGER, expiryDays INTEGER, timelyDays INTEGER, notifyCompletingUser INTEGER, openFuturePeriods INTEGER, fieldCombinationRequired INTEGER, validCompleteOnly INTEGER, noValueRequiresComment INTEGER, skipOffline INTEGER, dataElementDecoration INTEGER, renderAsTabs INTEGER, renderHorizontally INTEGER, accessDataWrite INTEGER, workflow TEXT, color TEXT, icon TEXT, header TEXT, subHeader TEXT, customTextAlign TEXT, tabsDirection TEXT, FOREIGN KEY(categoryCombo) REFERENCES CategoryCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataSet(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, periodType, categoryCombo, mobile, version, expiryDays, timelyDays, notifyCompletingUser, openFuturePeriods, fieldCombinationRequired, validCompleteOnly, noValueRequiresComment, skipOffline, dataElementDecoration, renderAsTabs, renderHorizontally, accessDataWrite, workflow, color, icon, header, subHeader, customTextAlign, tabsDirection) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, periodType, categoryCombo, mobile, version, expiryDays, timelyDays, notifyCompletingUser, openFuturePeriods, fieldCombinationRequired, validCompleteOnly, noValueRequiresComment, skipOffline, dataElementDecoration, renderAsTabs, renderHorizontally, accessDataWrite, workflow, color, icon, header, subHeader, customTextAlign, tabsDirection FROM DataSet_Old;

ALTER TABLE DataSetDataElementLink RENAME TO DataSetDataElementLink_Old;
CREATE TABLE DataSetDataElementLink(dataSet TEXT NOT NULL, dataElement TEXT NOT NULL, categoryCombo TEXT, PRIMARY KEY(dataSet, dataElement), FOREIGN KEY(dataSet) REFERENCES DataSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryCombo) REFERENCES CategoryCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataSetDataElementLink(dataSet, dataElement, categoryCombo) SELECT dataSet, dataElement, categoryCombo FROM DataSetDataElementLink_Old;

ALTER TABLE Indicator RENAME TO Indicator_Old;
CREATE TABLE Indicator(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, annualized INTEGER, indicatorType TEXT, numerator TEXT, numeratorDescription TEXT, denominator TEXT, denominatorDescription TEXT, url TEXT, decimals INTEGER, color TEXT, icon TEXT, FOREIGN KEY(indicatorType) REFERENCES IndicatorType(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO Indicator(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, annualized, indicatorType, numerator, numeratorDescription, denominator, denominatorDescription, url, decimals, color, icon) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, annualized, indicatorType, numerator, numeratorDescription, denominator, denominatorDescription, url, decimals, color, icon FROM Indicator_Old;

ALTER TABLE DataSetIndicatorLink RENAME TO DataSetIndicatorLink_Old;
CREATE TABLE DataSetIndicatorLink(dataSet TEXT NOT NULL, indicator TEXT NOT NULL, PRIMARY KEY(dataSet, indicator), FOREIGN KEY(dataSet) REFERENCES DataSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(indicator) REFERENCES Indicator(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataSetIndicatorLink(dataSet, indicator) SELECT dataSet, indicator FROM DataSetIndicatorLink_Old;

ALTER TABLE Period RENAME TO Period_Old;
CREATE TABLE Period(periodId TEXT PRIMARY KEY, periodType TEXT, startDate TEXT, endDate TEXT);
INSERT OR IGNORE INTO Period(periodId, periodType, startDate, endDate) SELECT periodId, periodType, startDate, endDate FROM Period_Old;

ALTER TABLE ValueTypeDeviceRendering RENAME TO ValueTypeDeviceRendering_Old;
CREATE TABLE ValueTypeDeviceRendering(uid TEXT, objectTable TEXT, deviceType TEXT, type TEXT, min INTEGER, max INTEGER, step INTEGER, decimalPoints INTEGER, PRIMARY KEY(uid, deviceType));
INSERT OR IGNORE INTO ValueTypeDeviceRendering(uid, objectTable, deviceType, type, min, max, step, decimalPoints) SELECT uid, objectTable, deviceType, type, min, max, step, decimalPoints FROM ValueTypeDeviceRendering_Old;

ALTER TABLE Note RENAME TO Note_Old;
CREATE TABLE Note(noteType TEXT, event TEXT, enrollment TEXT, value TEXT, storedBy TEXT, storedDate TEXT, uid TEXT, syncState TEXT, deleted INTEGER, FOREIGN KEY(event) REFERENCES Event(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(enrollment) REFERENCES Enrollment(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, PRIMARY KEY(noteType, event, enrollment, value, storedBy, storedDate));
INSERT OR IGNORE INTO Note(noteType, event, enrollment, value, storedBy, storedDate, uid, syncState, deleted) SELECT noteType, event, enrollment, value, storedBy, storedDate, uid, syncState, deleted FROM Note_Old;

ALTER TABLE Legend RENAME TO Legend_Old;
CREATE TABLE Legend(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, startValue REAL, endValue REAL, color TEXT, legendSet TEXT, FOREIGN KEY(legendSet) REFERENCES LegendSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO Legend(uid, code, name, displayName, created, lastUpdated, startValue, endValue, color, legendSet) SELECT uid, code, name, displayName, created, lastUpdated, startValue, endValue, color, legendSet FROM Legend_Old;

ALTER TABLE LegendSet RENAME TO LegendSet_Old;
CREATE TABLE LegendSet(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, symbolizer TEXT);
INSERT OR IGNORE INTO LegendSet(uid, code, name, displayName, created, lastUpdated, symbolizer) SELECT uid, code, name, displayName, created, lastUpdated, symbolizer FROM LegendSet_Old;

ALTER TABLE ProgramIndicatorLegendSetLink RENAME TO ProgramIndicatorLegendSetLink_Old;
CREATE TABLE ProgramIndicatorLegendSetLink(programIndicator TEXT NOT NULL, legendSet TEXT NOT NULL, sortOrder INTEGER, PRIMARY KEY(programIndicator, legendSet), FOREIGN KEY(programIndicator) REFERENCES ProgramIndicator(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(legendSet) REFERENCES LegendSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramIndicatorLegendSetLink(programIndicator, legendSet, sortOrder) SELECT programIndicator, legendSet, sortOrder FROM ProgramIndicatorLegendSetLink_Old;

ALTER TABLE SystemSetting RENAME TO SystemSetting_Old;
CREATE TABLE SystemSetting(key TEXT NOT NULL PRIMARY KEY, value TEXT);
INSERT OR IGNORE INTO SystemSetting(key, value) SELECT key, value FROM SystemSetting_Old;

ALTER TABLE ProgramSectionAttributeLink RENAME TO ProgramSectionAttributeLink_Old;
CREATE TABLE ProgramSectionAttributeLink(programSection TEXT NOT NULL, attribute TEXT NOT NULL, sortOrder INTEGER, PRIMARY KEY(programSection, attribute), FOREIGN KEY(programSection) REFERENCES ProgramSection(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(attribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramSectionAttributeLink(programSection, attribute, sortOrder) SELECT programSection, attribute, sortOrder FROM ProgramSectionAttributeLink_Old;

ALTER TABLE TrackedEntityAttributeReservedValue RENAME TO TrackedEntityAttributeReservedValue_Old;
CREATE TABLE TrackedEntityAttributeReservedValue(ownerObject TEXT, ownerUid TEXT, key TEXT, value TEXT, created TEXT, expiryDate TEXT, organisationUnit TEXT, temporalValidityDate TEXT, pattern TEXT, PRIMARY KEY(ownerObject, ownerUid, value));
INSERT OR IGNORE INTO TrackedEntityAttributeReservedValue(ownerObject, ownerUid, key, value, created, expiryDate, organisationUnit, temporalValidityDate, pattern) SELECT ownerObject, ownerUid, key, value, created, expiryDate, organisationUnit, temporalValidityDate, pattern FROM TrackedEntityAttributeReservedValue_Old;

ALTER TABLE CategoryOptionComboCategoryOptionLink RENAME TO CategoryOptionComboCategoryOptionLink_Old;
CREATE TABLE CategoryOptionComboCategoryOptionLink(categoryOptionCombo TEXT NOT NULL, categoryOption TEXT NOT NULL, PRIMARY KEY(categoryOptionCombo, categoryOption), FOREIGN KEY(categoryOptionCombo) REFERENCES CategoryOptionCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryOption) REFERENCES CategoryOption(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO CategoryOptionComboCategoryOptionLink(categoryOptionCombo, categoryOption) SELECT categoryOptionCombo, categoryOption FROM CategoryOptionComboCategoryOptionLink_Old;

ALTER TABLE Section RENAME TO Section_Old;
CREATE TABLE Section(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, description TEXT, sortOrder INTEGER, dataSet TEXT NOT NULL, showRowTotals INTEGER, showColumnTotals INTEGER, disableDataElementAutoGroup INTEGER, pivotMode TEXT, pivotedCategory TEXT, afterSectionText TEXT, beforeSectionText TEXT, FOREIGN KEY(dataSet) REFERENCES DataSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO Section(uid, code, name, displayName, created, lastUpdated, description, sortOrder, dataSet, showRowTotals, showColumnTotals, disableDataElementAutoGroup, pivotMode, pivotedCategory, afterSectionText, beforeSectionText) SELECT uid, code, name, displayName, created, lastUpdated, description, sortOrder, dataSet, showRowTotals, showColumnTotals, disableDataElementAutoGroup, pivotMode, pivotedCategory, afterSectionText, beforeSectionText FROM Section_Old;

ALTER TABLE SectionDataElementLink RENAME TO SectionDataElementLink_Old;
CREATE TABLE SectionDataElementLink(section TEXT NOT NULL, dataElement TEXT NOT NULL, sortOrder INTEGER, PRIMARY KEY(section, dataElement), FOREIGN KEY(section) REFERENCES Section(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO SectionDataElementLink(section, dataElement, sortOrder) SELECT section, dataElement, sortOrder FROM SectionDataElementLink_Old;

ALTER TABLE DataSetCompulsoryDataElementOperandsLink RENAME TO DataSetCompulsoryDataElementOperandsLink_Old;
CREATE TABLE DataSetCompulsoryDataElementOperandsLink(dataSet TEXT NOT NULL, dataElementOperand TEXT NOT NULL, PRIMARY KEY(dataSet, dataElementOperand), FOREIGN KEY(dataSet) REFERENCES DataSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataElementOperand) REFERENCES DataElementOperand(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataSetCompulsoryDataElementOperandsLink(dataSet, dataElementOperand) SELECT dataSet, dataElementOperand FROM DataSetCompulsoryDataElementOperandsLink_Old;

ALTER TABLE DataInputPeriod RENAME TO DataInputPeriod_Old;
CREATE TABLE DataInputPeriod(dataSet TEXT NOT NULL, period TEXT NOT NULL, openingDate TEXT, closingDate TEXT, FOREIGN KEY(dataSet) REFERENCES DataSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(period) REFERENCES Period(periodId) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, PRIMARY KEY(dataSet, period, openingDate, closingDate));
INSERT OR IGNORE INTO DataInputPeriod(dataSet, period, openingDate, closingDate) SELECT dataSet, period, openingDate, closingDate FROM DataInputPeriod_Old;

ALTER TABLE RelationshipConstraint RENAME TO RelationshipConstraint_Old;
CREATE TABLE RelationshipConstraint(relationshipType TEXT NOT NULL, constraintType TEXT NOT NULL, relationshipEntity TEXT, trackedEntityType TEXT, program TEXT, programStage TEXT, trackerDataViewAttributes TEXT, trackerDataViewDataElements TEXT, FOREIGN KEY(trackedEntityType) REFERENCES TrackedEntityType(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, PRIMARY KEY(relationshipType, constraintType));
INSERT OR IGNORE INTO RelationshipConstraint(relationshipType, constraintType, relationshipEntity, trackedEntityType, program, programStage, trackerDataViewAttributes, trackerDataViewDataElements) SELECT relationshipType, constraintType, relationshipEntity, trackedEntityType, program, programStage, trackerDataViewAttributes, trackerDataViewDataElements FROM RelationshipConstraint_Old;

ALTER TABLE RelationshipItem RENAME TO RelationshipItem_Old;
CREATE TABLE RelationshipItem(relationship TEXT NOT NULL, relationshipItemType TEXT NOT NULL, trackedEntityInstance TEXT, enrollment TEXT, event TEXT, FOREIGN KEY(relationship) REFERENCES Relationship(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(trackedEntityInstance) REFERENCES TrackedEntityInstance(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(enrollment) REFERENCES Enrollment(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(event) REFERENCES Event(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, PRIMARY KEY(relationship, relationshipItemType));
INSERT OR IGNORE INTO RelationshipItem(relationship, relationshipItemType, trackedEntityInstance, enrollment, event) SELECT relationship, relationshipItemType, trackedEntityInstance, enrollment, event FROM RelationshipItem_Old;

ALTER TABLE OrganisationUnitGroup RENAME TO OrganisationUnitGroup_Old;
CREATE TABLE OrganisationUnitGroup(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT);
INSERT OR IGNORE INTO OrganisationUnitGroup(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName FROM OrganisationUnitGroup_Old;

ALTER TABLE OrganisationUnitOrganisationUnitGroupLink RENAME TO OrganisationUnitOrganisationUnitGroupLink_Old;
CREATE TABLE OrganisationUnitOrganisationUnitGroupLink(organisationUnit TEXT NOT NULL, organisationUnitGroup TEXT NOT NULL, PRIMARY KEY(organisationUnit, organisationUnitGroup), FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnitGroup) REFERENCES OrganisationUnitGroup(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO OrganisationUnitOrganisationUnitGroupLink(organisationUnit, organisationUnitGroup) SELECT organisationUnit, organisationUnitGroup FROM OrganisationUnitOrganisationUnitGroupLink_Old;

ALTER TABLE ProgramStageDataElement RENAME TO ProgramStageDataElement_Old;
CREATE TABLE ProgramStageDataElement(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, displayInReports INTEGER, compulsory INTEGER, allowProvidedElsewhere INTEGER, sortOrder INTEGER, allowFutureDate INTEGER, dataElement TEXT NOT NULL, programStage TEXT NOT NULL, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramStageDataElement(uid, code, name, displayName, created, lastUpdated, displayInReports, compulsory, allowProvidedElsewhere, sortOrder, allowFutureDate, dataElement, programStage) SELECT uid, code, name, displayName, created, lastUpdated, displayInReports, compulsory, allowProvidedElsewhere, sortOrder, allowFutureDate, dataElement, programStage FROM ProgramStageDataElement_Old;

ALTER TABLE ProgramStageSectionDataElementLink RENAME TO ProgramStageSectionDataElementLink_Old;
CREATE TABLE ProgramStageSectionDataElementLink(programStageSection TEXT NOT NULL, dataElement TEXT NOT NULL, sortOrder INTEGER NOT NULL, PRIMARY KEY(programStageSection, dataElement), FOREIGN KEY(programStageSection) REFERENCES ProgramStageSection(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramStageSectionDataElementLink(programStageSection, dataElement, sortOrder) SELECT programStageSection, dataElement, sortOrder FROM ProgramStageSectionDataElementLink_Old;

ALTER TABLE DataElementOperand RENAME TO DataElementOperand_Old;
CREATE TABLE DataElementOperand(uid TEXT NOT NULL PRIMARY KEY, dataElement TEXT, categoryOptionCombo TEXT, FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryOptionCombo) REFERENCES CategoryOptionCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataElementOperand(uid, dataElement, categoryOptionCombo) SELECT uid, dataElement, categoryOptionCombo FROM DataElementOperand_Old;

ALTER TABLE IndicatorType RENAME TO IndicatorType_Old;
CREATE TABLE IndicatorType(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, number INTEGER, factor INTEGER);
INSERT OR IGNORE INTO IndicatorType(uid, code, name, displayName, created, lastUpdated, number, factor) SELECT uid, code, name, displayName, created, lastUpdated, number, factor FROM IndicatorType_Old;

ALTER TABLE ForeignKeyViolation RENAME TO ForeignKeyViolation_Old;
CREATE TABLE ForeignKeyViolation(fromTable TEXT, fromColumn TEXT, toTable TEXT, toColumn TEXT, notFoundValue TEXT, fromObjectUid TEXT, fromObjectRow TEXT, created TEXT, PRIMARY KEY(fromTable, fromColumn, toTable, toColumn, notFoundValue, fromObjectUid));
INSERT OR IGNORE INTO ForeignKeyViolation(fromTable, fromColumn, toTable, toColumn, notFoundValue, fromObjectUid, fromObjectRow, created) SELECT fromTable, fromColumn, toTable, toColumn, notFoundValue, fromObjectUid, fromObjectRow, created FROM ForeignKeyViolation_Old;

ALTER TABLE D2Error RENAME TO D2Error_Old;
CREATE TABLE D2Error(url TEXT, errorComponent TEXT, errorCode TEXT, errorDescription TEXT, httpErrorCode INTEGER, created TEXT);
INSERT OR IGNORE INTO D2Error(resourceType, uid, url, errorComponent, errorCode, errorDescription, httpErrorCode, created) SELECT resourceType, uid, url, errorComponent, errorCode, errorDescription, httpErrorCode, created FROM D2Error_Old;

ALTER TABLE Authority RENAME TO Authority_Old;
CREATE TABLE Authority(name TEXT NOT NULL PRIMARY KEY);
INSERT OR IGNORE INTO Authority(name) SELECT name FROM Authority_Old;

ALTER TABLE TrackedEntityTypeAttribute RENAME TO TrackedEntityTypeAttribute_Old;
CREATE TABLE TrackedEntityTypeAttribute(trackedEntityType TEXT, trackedEntityAttribute TEXT, displayInList INTEGER, mandatory INTEGER, searchable INTEGER, sortOrder INTEGER, PRIMARY KEY(trackedEntityType, trackedEntityAttribute), FOREIGN KEY(trackedEntityType) REFERENCES TrackedEntityType(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(trackedEntityAttribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO TrackedEntityTypeAttribute(trackedEntityType, trackedEntityAttribute, displayInList, mandatory, searchable, sortOrder) SELECT trackedEntityType, trackedEntityAttribute, displayInList, mandatory, searchable, sortOrder FROM TrackedEntityTypeAttribute_Old;

ALTER TABLE Relationship RENAME TO Relationship_Old;
CREATE TABLE Relationship(uid TEXT NOT NULL PRIMARY KEY, name TEXT, created TEXT, lastUpdated TEXT, relationshipType TEXT NOT NULL, syncState TEXT, deleted INTEGER, FOREIGN KEY(relationshipType) REFERENCES RelationshipType(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO Relationship(uid, name, created, lastUpdated, relationshipType, syncState, deleted) SELECT uid, name, created, lastUpdated, relationshipType, syncState, deleted FROM Relationship_Old;

ALTER TABLE DataElement RENAME TO DataElement_Old;
CREATE TABLE DataElement(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, valueType TEXT, zeroIsSignificant INTEGER, aggregationType TEXT, formName TEXT, domainType TEXT, displayFormName TEXT, optionSet TEXT, categoryCombo TEXT NOT NULL, fieldMask TEXT, color TEXT, icon TEXT, FOREIGN KEY(optionSet) REFERENCES OptionSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryCombo) REFERENCES CategoryCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataElement(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, valueType, zeroIsSignificant, aggregationType, formName, domainType, displayFormName, optionSet, categoryCombo, fieldMask, color, icon) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, valueType, zeroIsSignificant, aggregationType, formName, domainType, displayFormName, optionSet, categoryCombo, fieldMask, color, icon FROM DataElement_Old;

ALTER TABLE OptionGroup RENAME TO OptionGroup_Old;
CREATE TABLE OptionGroup(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, optionSet TEXT NOT NULL, FOREIGN KEY(optionSet) REFERENCES OptionSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO OptionGroup(uid, code, name, displayName, created, lastUpdated, optionSet) SELECT uid, code, name, displayName, created, lastUpdated, optionSet FROM OptionGroup_Old;

ALTER TABLE OptionGroupOptionLink RENAME TO OptionGroupOptionLink_Old;
CREATE TABLE OptionGroupOptionLink(optionGroup TEXT NOT NULL, option TEXT NOT NULL, PRIMARY KEY(optionGroup, option), FOREIGN KEY(optionGroup) REFERENCES OptionGroup(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(option) REFERENCES Option(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO OptionGroupOptionLink(optionGroup, option) SELECT optionGroup, option FROM OptionGroupOptionLink_Old;

ALTER TABLE ProgramRuleAction RENAME TO ProgramRuleAction_Old;
CREATE TABLE ProgramRuleAction(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, data TEXT, content TEXT, location TEXT, trackedEntityAttribute TEXT, programIndicator TEXT, programStageSection TEXT, programRuleActionType TEXT, programStage TEXT, dataElement TEXT, programRule TEXT NOT NULL, option TEXT, optionGroup TEXT, displayContent TEXT, FOREIGN KEY(programRule) REFERENCES ProgramRule(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(trackedEntityAttribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programIndicator) REFERENCES ProgramIndicator(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStageSection) REFERENCES ProgramStageSection(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(option) REFERENCES Option(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(optionGroup) REFERENCES OptionGroup(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramRuleAction(uid, code, name, displayName, created, lastUpdated, data, content, location, trackedEntityAttribute, programIndicator, programStageSection, programRuleActionType, programStage, dataElement, programRule, option, optionGroup, displayContent) SELECT uid, code, name, displayName, created, lastUpdated, data, content, location, trackedEntityAttribute, programIndicator, programStageSection, programRuleActionType, programStage, dataElement, programRule, option, optionGroup, displayContent FROM ProgramRuleAction_Old;

ALTER TABLE OrganisationUnitLevel RENAME TO OrganisationUnitLevel_Old;
CREATE TABLE OrganisationUnitLevel(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, level INTEGER);
INSERT OR IGNORE INTO OrganisationUnitLevel(uid, code, name, displayName, created, lastUpdated, level) SELECT uid, code, name, displayName, created, lastUpdated, level FROM OrganisationUnitLevel_Old;

ALTER TABLE ProgramSection RENAME TO ProgramSection_Old;
CREATE TABLE ProgramSection(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, description TEXT, program TEXT, sortOrder INTEGER, formName TEXT, color TEXT, icon TEXT, desktopRenderType TEXT, mobileRenderType TEXT, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramSection(uid, code, name, displayName, created, lastUpdated, description, program, sortOrder, formName, color, icon, desktopRenderType, mobileRenderType) SELECT uid, code, name, displayName, created, lastUpdated, description, program, sortOrder, formName, color, icon, desktopRenderType, mobileRenderType FROM ProgramSection_Old;

ALTER TABLE DataApproval RENAME TO DataApproval_Old;
CREATE TABLE DataApproval(workflow TEXT NOT NULL, organisationUnit TEXT NOT NULL, period TEXT NOT NULL, attributeOptionCombo TEXT NOT NULL, state TEXT, PRIMARY KEY(workflow, attributeOptionCombo, period, organisationUnit), FOREIGN KEY(attributeOptionCombo) REFERENCES CategoryOptionCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(period) REFERENCES Period(periodId) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataApproval(workflow, organisationUnit, period, attributeOptionCombo, state) SELECT workflow, organisationUnit, period, attributeOptionCombo, state FROM DataApproval_Old;

ALTER TABLE TrackedEntityAttribute RENAME TO TrackedEntityAttribute_Old;
CREATE TABLE TrackedEntityAttribute(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, pattern TEXT, sortOrderInListNoProgram INTEGER, optionSet TEXT, valueType TEXT, expression TEXT, programScope INTEGER, displayInListNoProgram INTEGER, generated INTEGER, displayOnVisitSchedule INTEGER, orgunitScope INTEGER, uniqueProperty INTEGER, inherit INTEGER, formName TEXT, fieldMask TEXT, color TEXT, icon TEXT, displayFormName TEXT, aggregationType TEXT, confidential INTEGER, FOREIGN KEY(optionSet) REFERENCES OptionSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO TrackedEntityAttribute(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, pattern, sortOrderInListNoProgram, optionSet, valueType, expression, programScope, displayInListNoProgram, generated, displayOnVisitSchedule, orgunitScope, uniqueProperty, inherit, formName, fieldMask, color, icon, displayFormName, aggregationType, confidential) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, pattern, sortOrderInListNoProgram, optionSet, valueType, expression, programScope, displayInListNoProgram, generated, displayOnVisitSchedule, orgunitScope, uniqueProperty, inherit, formName, fieldMask, color, icon, displayFormName, aggregationType, confidential FROM TrackedEntityAttribute_Old;

ALTER TABLE TrackerImportConflict RENAME TO TrackerImportConflict_Old;
CREATE TABLE TrackerImportConflict(conflict TEXT, value TEXT, trackedEntityInstance TEXT, enrollment TEXT, event TEXT, tableReference TEXT, errorCode TEXT, status TEXT, created TEXT, displayDescription TEXT, trackedEntityAttribute TEXT, dataElement TEXT, PRIMARY KEY(conflict, value, trackedEntityInstance, enrollment, event, tableReference), FOREIGN KEY(trackedEntityInstance) REFERENCES TrackedEntityInstance(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(enrollment) REFERENCES Enrollment(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(event) REFERENCES Event(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO TrackerImportConflict(conflict, value, trackedEntityInstance, enrollment, event, tableReference, errorCode, status, created, displayDescription, trackedEntityAttribute, dataElement) SELECT conflict, value, trackedEntityInstance, enrollment, event, tableReference, errorCode, status, created, displayDescription, trackedEntityAttribute, dataElement FROM TrackerImportConflict_Old;

ALTER TABLE DataSetOrganisationUnitLink RENAME TO DataSetOrganisationUnitLink_Old;
CREATE TABLE DataSetOrganisationUnitLink(dataSet TEXT NOT NULL, organisationUnit TEXT NOT NULL, PRIMARY KEY(dataSet, organisationUnit), FOREIGN KEY(dataSet) REFERENCES DataSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataSetOrganisationUnitLink(dataSet, organisationUnit) SELECT dataSet, organisationUnit FROM DataSetOrganisationUnitLink_Old;

ALTER TABLE UserOrganisationUnit RENAME TO UserOrganisationUnit_Old;
CREATE TABLE UserOrganisationUnit(user TEXT NOT NULL, organisationUnit TEXT NOT NULL, organisationUnitScope TEXT NOT NULL, root INTEGER, userAssigned INTEGER, PRIMARY KEY(organisationUnitScope, user, organisationUnit), FOREIGN KEY(user) REFERENCES User(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO UserOrganisationUnit(user, organisationUnit, organisationUnitScope, root, userAssigned) SELECT user, organisationUnit, organisationUnitScope, root, userAssigned FROM UserOrganisationUnit_Old;

ALTER TABLE RelationshipType RENAME TO RelationshipType_Old;
CREATE TABLE RelationshipType(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, fromToName TEXT, toFromName TEXT, bidirectional INTEGER, accessDataWrite INTEGER);
INSERT OR IGNORE INTO RelationshipType(uid, code, name, displayName, created, lastUpdated, fromToName, toFromName, bidirectional, accessDataWrite) SELECT uid, code, name, displayName, created, lastUpdated, fromToName, toFromName, bidirectional, accessDataWrite FROM RelationshipType_Old;

ALTER TABLE ProgramStage RENAME TO ProgramStage_Old;
CREATE TABLE ProgramStage(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, displayExecutionDateLabel TEXT, allowGenerateNextVisit INTEGER, validCompleteOnly INTEGER, reportDateToUse TEXT, openAfterEnrollment INTEGER, repeatable INTEGER, formType TEXT, displayGenerateEventBox INTEGER, generatedByEnrollmentDate INTEGER, autoGenerateEvent INTEGER, sortOrder INTEGER, hideDueDate INTEGER, blockEntryForm INTEGER, minDaysFromStart INTEGER, standardInterval INTEGER, program TEXT NOT NULL, periodType TEXT, accessDataWrite INTEGER, remindCompleted INTEGER, description TEXT, displayDescription TEXT, featureType TEXT, color TEXT, icon TEXT, enableUserAssignment INTEGER, displayDueDateLabel TEXT, validationStrategy TEXT, displayProgramStageLabel TEXT, displayEventLabel TEXT, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramStage(uid, code, name, displayName, created, lastUpdated, displayExecutionDateLabel, allowGenerateNextVisit, validCompleteOnly, reportDateToUse, openAfterEnrollment, repeatable, formType, displayGenerateEventBox, generatedByEnrollmentDate, autoGenerateEvent, sortOrder, hideDueDate, blockEntryForm, minDaysFromStart, standardInterval, program, periodType, accessDataWrite, remindCompleted, description, displayDescription, featureType, color, icon, enableUserAssignment, displayDueDateLabel, validationStrategy, displayProgramStageLabel, displayEventLabel) SELECT uid, code, name, displayName, created, lastUpdated, displayExecutionDateLabel, allowGenerateNextVisit, validCompleteOnly, reportDateToUse, openAfterEnrollment, repeatable, formType, displayGenerateEventBox, generatedByEnrollmentDate, autoGenerateEvent, sortOrder, hideDueDate, blockEntryForm, minDaysFromStart, standardInterval, program, periodType, accessDataWrite, remindCompleted, description, displayDescription, featureType, color, icon, enableUserAssignment, displayDueDateLabel, validationStrategy, displayProgramStageLabel, displayEventLabel FROM ProgramStage_Old;

ALTER TABLE Program RENAME TO Program_Old;
CREATE TABLE Program(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, version INTEGER, onlyEnrollOnce INTEGER, displayEnrollmentDateLabel TEXT, displayIncidentDate INTEGER, displayIncidentDateLabel TEXT, registration INTEGER, selectEnrollmentDatesInFuture INTEGER, dataEntryMethod INTEGER, ignoreOverdueEvents INTEGER, selectIncidentDatesInFuture INTEGER, useFirstStageDuringRegistration INTEGER, displayFrontPageList INTEGER, programType TEXT, relatedProgram TEXT, trackedEntityType TEXT, categoryCombo TEXT, accessDataWrite INTEGER, expiryDays INTEGER, completeEventsExpiryDays INTEGER, expiryPeriodType TEXT, minAttributesRequiredToSearch INTEGER, maxTeiCountToReturn INTEGER, featureType TEXT, accessLevel TEXT, color TEXT, icon TEXT, displayEnrollmentLabel TEXT, displayFollowUpLabel TEXT, displayOrgUnitLabel TEXT, displayRelationshipLabel TEXT, displayNoteLabel TEXT, displayTrackedEntityAttributeLabel TEXT, displayProgramStageLabel TEXT, displayEventLabel TEXT, FOREIGN KEY(trackedEntityType) REFERENCES TrackedEntityType(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryCombo) REFERENCES CategoryCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO Program(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, version, onlyEnrollOnce, displayEnrollmentDateLabel, displayIncidentDate, displayIncidentDateLabel, registration, selectEnrollmentDatesInFuture, dataEntryMethod, ignoreOverdueEvents, selectIncidentDatesInFuture, useFirstStageDuringRegistration, displayFrontPageList, programType, relatedProgram, trackedEntityType, categoryCombo, accessDataWrite, expiryDays, completeEventsExpiryDays, expiryPeriodType, minAttributesRequiredToSearch, maxTeiCountToReturn, featureType, accessLevel, color, icon, displayEnrollmentLabel, displayFollowUpLabel, displayOrgUnitLabel, displayRelationshipLabel, displayNoteLabel, displayTrackedEntityAttributeLabel, displayProgramStageLabel, displayEventLabel) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, version, onlyEnrollOnce, displayEnrollmentDateLabel, displayIncidentDate, displayIncidentDateLabel, registration, selectEnrollmentDatesInFuture, dataEntryMethod, ignoreOverdueEvents, selectIncidentDatesInFuture, useFirstStageDuringRegistration, displayFrontPageList, programType, relatedProgram, trackedEntityType, categoryCombo, accessDataWrite, expiryDays, completeEventsExpiryDays, expiryPeriodType, minAttributesRequiredToSearch, maxTeiCountToReturn, featureType, accessLevel, color, icon, displayEnrollmentLabel, displayFollowUpLabel, displayOrgUnitLabel, displayRelationshipLabel, displayNoteLabel, displayTrackedEntityAttributeLabel, displayProgramStageLabel, displayEventLabel FROM Program_Old;

ALTER TABLE TrackedEntityInstance RENAME TO TrackedEntityInstance_Old;
CREATE TABLE TrackedEntityInstance(uid TEXT NOT NULL PRIMARY KEY, created TEXT, lastUpdated TEXT, createdAtClient TEXT, lastUpdatedAtClient TEXT, organisationUnit TEXT, trackedEntityType TEXT, geometryType TEXT, geometryCoordinates TEXT, syncState TEXT, aggregatedSyncState TEXT, deleted INTEGER, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(trackedEntityType) REFERENCES TrackedEntityType(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO TrackedEntityInstance(uid, created, lastUpdated, createdAtClient, lastUpdatedAtClient, organisationUnit, trackedEntityType, geometryType, geometryCoordinates, syncState, aggregatedSyncState, deleted) SELECT uid, created, lastUpdated, createdAtClient, lastUpdatedAtClient, organisationUnit, trackedEntityType, geometryType, geometryCoordinates, syncState, aggregatedSyncState, deleted FROM TrackedEntityInstance_Old;

ALTER TABLE Enrollment RENAME TO Enrollment_Old;
CREATE TABLE Enrollment(uid TEXT NOT NULL PRIMARY KEY, created TEXT, lastUpdated TEXT, createdAtClient TEXT, lastUpdatedAtClient TEXT, organisationUnit TEXT NOT NULL, program TEXT NOT NULL, enrollmentDate TEXT, incidentDate TEXT, followup INTEGER, status TEXT, trackedEntityInstance TEXT NOT NULL, syncState TEXT, aggregatedSyncState TEXT, geometryType TEXT, geometryCoordinates TEXT, deleted INTEGER, completedDate TEXT, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(trackedEntityInstance) REFERENCES TrackedEntityInstance(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO Enrollment(uid, created, lastUpdated, createdAtClient, lastUpdatedAtClient, organisationUnit, program, enrollmentDate, incidentDate, followup, status, trackedEntityInstance, syncState, aggregatedSyncState, geometryType, geometryCoordinates, deleted, completedDate) SELECT uid, created, lastUpdated, createdAtClient, lastUpdatedAtClient, organisationUnit, program, enrollmentDate, incidentDate, followup, status, trackedEntityInstance, syncState, aggregatedSyncState, geometryType, geometryCoordinates, deleted, completedDate FROM Enrollment_Old;

ALTER TABLE Event RENAME TO Event_Old;
CREATE TABLE Event(uid TEXT NOT NULL PRIMARY KEY, enrollment TEXT, created TEXT, lastUpdated TEXT, createdAtClient TEXT, lastUpdatedAtClient TEXT, status TEXT, geometryType TEXT, geometryCoordinates TEXT, program TEXT NOT NULL, programStage TEXT NOT NULL, organisationUnit TEXT NOT NULL, eventDate TEXT, completedDate TEXT, dueDate TEXT, syncState TEXT, aggregatedSyncState TEXT, attributeOptionCombo TEXT, deleted INTEGER, assignedUser TEXT, completedBy TEXT, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(enrollment) REFERENCES Enrollment(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(attributeOptionCombo) REFERENCES CategoryOptionCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO Event(uid, enrollment, created, lastUpdated, createdAtClient, lastUpdatedAtClient, status, geometryType, geometryCoordinates, program, programStage, organisationUnit, eventDate, completedDate, dueDate, syncState, aggregatedSyncState, attributeOptionCombo, deleted, assignedUser, completedBy) SELECT uid, enrollment, created, lastUpdated, createdAtClient, lastUpdatedAtClient, status, geometryType, geometryCoordinates, program, programStage, organisationUnit, eventDate, completedDate, dueDate, syncState, aggregatedSyncState, attributeOptionCombo, deleted, assignedUser, completedBy FROM Event_Old;

ALTER TABLE DataValue RENAME TO DataValue_Old;
CREATE TABLE DataValue(dataElement TEXT NOT NULL, period TEXT NOT NULL, organisationUnit TEXT NOT NULL, categoryOptionCombo TEXT NOT NULL, attributeOptionCombo TEXT NOT NULL, value TEXT, storedBy TEXT, created TEXT, lastUpdated TEXT, comment TEXT, followUp INTEGER, syncState TEXT, deleted INTEGER, PRIMARY KEY(dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo), FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(period) REFERENCES Period(periodId) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryOptionCombo) REFERENCES CategoryOptionCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(attributeOptionCombo) REFERENCES CategoryOptionCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataValue(dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo, value, storedBy, created, lastUpdated, comment, followUp, syncState, deleted) SELECT dataElement, period, organisationUnit, categoryOptionCombo, attributeOptionCombo, value, storedBy, created, lastUpdated, comment, followUp, syncState, deleted FROM DataValue_Old;

ALTER TABLE TrackedEntityDataValue RENAME TO TrackedEntityDataValue_Old;
CREATE TABLE TrackedEntityDataValue(event TEXT NOT NULL, dataElement TEXT NOT NULL, storedBy TEXT, value TEXT, created TEXT, lastUpdated TEXT, providedElsewhere INTEGER, syncState TEXT, FOREIGN KEY(event) REFERENCES Event(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, PRIMARY KEY(event, dataElement));
INSERT OR IGNORE INTO TrackedEntityDataValue(event, dataElement, storedBy, value, created, lastUpdated, providedElsewhere, syncState) SELECT event, dataElement, storedBy, value, created, lastUpdated, providedElsewhere, syncState FROM TrackedEntityDataValue_Old;

ALTER TABLE TrackedEntityAttributeValue RENAME TO TrackedEntityAttributeValue_Old;
CREATE TABLE TrackedEntityAttributeValue (created TEXT, lastUpdated TEXT, value TEXT, trackedEntityAttribute TEXT NOT NULL, trackedEntityInstance TEXT NOT NULL, syncState TEXT, PRIMARY KEY(trackedEntityInstance, trackedEntityAttribute), FOREIGN KEY (trackedEntityAttribute) REFERENCES trackedEntityAttribute (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (trackedEntityInstance) REFERENCES TrackedEntityInstance (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO TrackedEntityAttributeValue(created, lastUpdated, value, trackedEntityAttribute, trackedEntityInstance, syncState) SELECT created, lastUpdated, value, trackedEntityAttribute, trackedEntityInstance, syncState FROM TrackedEntityAttributeValue_Old;

ALTER TABLE FileResource RENAME TO FileResource_Old;
CREATE TABLE FileResource(uid TEXT NOT NULL PRIMARY KEY, name TEXT, created TEXT, lastUpdated TEXT, contentType TEXT, contentLength INTEGER, path TEXT, syncState TEXT, domain TEXT);
INSERT OR IGNORE INTO FileResource(uid, name, created, lastUpdated, contentType, contentLength, path, syncState, domain) SELECT uid, name, created, lastUpdated, contentType, contentLength, path, syncState, domain FROM FileResource_Old;

ALTER TABLE DataSetCompleteRegistration RENAME TO DataSetCompleteRegistration_Old;
CREATE TABLE DataSetCompleteRegistration(period TEXT NOT NULL, dataSet TEXT NOT NULL, organisationUnit TEXT NOT NULL, attributeOptionCombo TEXT, date TEXT, storedBy TEXT, syncState TEXT, deleted INTEGER, PRIMARY KEY(period, dataSet, organisationUnit, attributeOptionCombo), FOREIGN KEY(dataSet) REFERENCES DataSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(period) REFERENCES Period(periodId) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(attributeOptionCombo) REFERENCES CategoryOptionCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataSetCompleteRegistration(period, dataSet, organisationUnit, attributeOptionCombo, date, storedBy, syncState, deleted) SELECT period, dataSet, organisationUnit, attributeOptionCombo, date, storedBy, syncState, deleted FROM DataSetCompleteRegistration_Old;

ALTER TABLE SectionGreyedFieldsLink RENAME TO SectionGreyedFieldsLink_Old;
CREATE TABLE SectionGreyedFieldsLink(section TEXT NOT NULL, dataElementOperand TEXT NOT NULL, categoryOptionCombo TEXT, PRIMARY KEY(section, dataElementOperand, categoryOptionCombo), FOREIGN KEY(section) REFERENCES Section(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataElementOperand) REFERENCES DataElementOperand(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(categoryOptionCombo) REFERENCES CategoryOptionCombo(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO SectionGreyedFieldsLink(section, dataElementOperand, categoryOptionCombo) SELECT section, dataElementOperand, categoryOptionCombo FROM SectionGreyedFieldsLink_Old;

ALTER TABLE AuthenticatedUser RENAME TO AuthenticatedUser_Old;
CREATE TABLE AuthenticatedUser(user TEXT NOT NULL PRIMARY KEY, hash TEXT, FOREIGN KEY(user) REFERENCES User(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO AuthenticatedUser(user, hash) SELECT user, hash FROM AuthenticatedUser_Old;

ALTER TABLE GeneralSetting RENAME TO GeneralSetting_Old;
CREATE TABLE GeneralSetting(encryptDB INTEGER PRIMARY KEY, lastUpdated TEXT, reservedValues INTEGER, smsGateway TEXT, smsResultSender TEXT, matomoID INTEGER, matomoURL TEXT, allowScreenCapture INTEGER, messageOfTheDay TEXT, experimentalFeatures TEXT, bypassDHIS2VersionCheck INTEGER);
INSERT OR IGNORE INTO GeneralSetting(encryptDB, lastUpdated, reservedValues, smsGateway, smsResultSender, matomoID, matomoURL, allowScreenCapture, messageOfTheDay, experimentalFeatures, bypassDHIS2VersionCheck) SELECT encryptDB, lastUpdated, reservedValues, smsGateway, smsResultSender, matomoID, matomoURL, allowScreenCapture, messageOfTheDay, experimentalFeatures, bypassDHIS2VersionCheck FROM GeneralSetting_Old;

ALTER TABLE DataSetSetting RENAME TO DataSetSetting_Old;
CREATE TABLE DataSetSetting(uid TEXT PRIMARY KEY, name TEXT, lastUpdated TEXT, periodDSDownload INTEGER, periodDSDBTrimming INTEGER, FOREIGN KEY(uid) REFERENCES DataSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataSetSetting(uid, name, lastUpdated, periodDSDownload, periodDSDBTrimming) SELECT uid, name, lastUpdated, periodDSDownload, periodDSDBTrimming FROM DataSetSetting_Old;

ALTER TABLE ProgramSetting RENAME TO ProgramSetting_Old;
CREATE TABLE ProgramSetting(uid TEXT PRIMARY KEY, name TEXT, lastUpdated TEXT, teiDownload INTEGER, teiDBTrimming INTEGER, eventsDownload INTEGER, eventsDBTrimming INTEGER, updateDownload TEXT, updateDBTrimming TEXT, settingDownload TEXT, settingDBTrimming TEXT, enrollmentDownload TEXT, enrollmentDBTrimming TEXT, eventDateDownload TEXT, eventDateDBTrimming TEXT, enrollmentDateDownload TEXT, enrollmentDateDBTrimming TEXT, FOREIGN KEY(uid) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramSetting(uid, name, lastUpdated, teiDownload, teiDBTrimming, eventsDownload, eventsDBTrimming, updateDownload, updateDBTrimming, settingDownload, settingDBTrimming, enrollmentDownload, enrollmentDBTrimming, eventDateDownload, eventDateDBTrimming, enrollmentDateDownload, enrollmentDateDBTrimming) SELECT uid, name, lastUpdated, teiDownload, teiDBTrimming, eventsDownload, eventsDBTrimming, updateDownload, updateDBTrimming, settingDownload, settingDBTrimming, enrollmentDownload, enrollmentDBTrimming, eventDateDownload, eventDateDBTrimming, enrollmentDateDownload, enrollmentDateDBTrimming FROM ProgramSetting_Old;

ALTER TABLE SynchronizationSetting RENAME TO SynchronizationSetting_Old;
CREATE TABLE SynchronizationSetting(dataSync TEXT NOT NULL PRIMARY KEY, metadataSync TEXT, trackerImporterVersion TEXT, trackerExporterVersion TEXT, fileMaxLengthBytes INTEGER);
INSERT OR IGNORE INTO SynchronizationSetting(dataSync, metadataSync, trackerImporterVersion, trackerExporterVersion, fileMaxLengthBytes) SELECT dataSync, metadataSync, trackerImporterVersion, trackerExporterVersion, fileMaxLengthBytes FROM SynchronizationSetting_Old;

ALTER TABLE FilterSetting RENAME TO FilterSetting_Old;
CREATE TABLE FilterSetting(scope TEXT, filterType TEXT, uid TEXT, sort INTEGER, filter INTEGER, PRIMARY KEY(scope, filterType, uid));
INSERT OR IGNORE INTO FilterSetting(scope, filterType, uid, sort, filter) SELECT scope, filterType, uid, sort, filter FROM FilterSetting_Old;

ALTER TABLE ProgramConfigurationSetting RENAME TO ProgramConfigurationSetting_Old;
CREATE TABLE ProgramConfigurationSetting(uid TEXT PRIMARY KEY, completionSpinner INTEGER, optionalSearch INTEGER, disableReferrals INTEGER, disableCollapsibleSections INTEGER, itemHeaderProgramIndicator TEXT, minimumLocationAccuracy INTEGER, disableManualLocation INTEGER, quickActions TEXT);
INSERT OR IGNORE INTO ProgramConfigurationSetting(uid, completionSpinner, optionalSearch, disableReferrals, disableCollapsibleSections, itemHeaderProgramIndicator, minimumLocationAccuracy, disableManualLocation, quickActions) SELECT uid, completionSpinner, optionalSearch, disableReferrals, disableCollapsibleSections, itemHeaderProgramIndicator, minimumLocationAccuracy, disableManualLocation, quickActions FROM ProgramConfigurationSetting_Old;

ALTER TABLE DataSetConfigurationSetting RENAME TO DataSetConfigurationSetting_Old;
CREATE TABLE DataSetConfigurationSetting(uid TEXT PRIMARY KEY, minimumLocationAccuracy INTEGER, disableManualLocation INTEGER);
INSERT OR IGNORE INTO DataSetConfigurationSetting(uid, minimumLocationAccuracy, disableManualLocation) SELECT uid, minimumLocationAccuracy, disableManualLocation FROM DataSetConfigurationSetting_Old;

ALTER TABLE CustomIntent RENAME TO CustomIntent_Old;
CREATE TABLE CustomIntent(uid TEXT NOT NULL PRIMARY KEY, name TEXT, action TEXT, packageName TEXT, requestArguments TEXT, responseDataArgument TEXT, responseDataPath TEXT);
INSERT OR IGNORE INTO CustomIntent(uid, name, action, packageName, requestArguments, responseDataArgument, responseDataPath) SELECT uid, name, action, packageName, requestArguments, responseDataArgument, responseDataPath FROM CustomIntent_Old;

ALTER TABLE CustomIntentDataElement RENAME TO CustomIntentDataElement_Old;
CREATE TABLE CustomIntentDataElement(uid TEXT NOT NULL, customIntentUid TEXT NOT NULL, FOREIGN KEY(customIntentUid) REFERENCES CustomIntent(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, PRIMARY KEY(customIntentUid, uid));
INSERT OR IGNORE INTO CustomIntentDataElement(uid, customIntentUid) SELECT uid, customIntentUid FROM CustomIntentDataElement_Old;

ALTER TABLE CustomIntentAttribute RENAME TO CustomIntentAttribute_Old;
CREATE TABLE CustomIntentAttribute(uid TEXT NOT NULL, customIntentUid TEXT NOT NULL, FOREIGN KEY(customIntentUid) REFERENCES CustomIntent(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, PRIMARY KEY(customIntentUid, uid));
INSERT OR IGNORE INTO CustomIntentAttribute(uid, customIntentUid) SELECT uid, customIntentUid FROM CustomIntentAttribute_Old;

ALTER TABLE AnalyticsTeiSetting RENAME TO AnalyticsTeiSetting_Old;
CREATE TABLE AnalyticsTeiSetting(uid TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, shortName TEXT NOT NULL, program TEXT NOT NULL, programStage TEXT, period TEXT, type TEXT NOT NULL, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO AnalyticsTeiSetting(uid, name, shortName, program, programStage, period, type) SELECT uid, name, shortName, program, programStage, period, type FROM AnalyticsTeiSetting_Old;

ALTER TABLE AnalyticsTeiDataElement RENAME TO AnalyticsTeiDataElement_Old;
CREATE TABLE AnalyticsTeiDataElement(teiSetting TEXT NOT NULL, whoComponent TEXT, programStage TEXT, dataElement TEXT NOT NULL, PRIMARY KEY(teiSetting, dataElement), FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(teiSetting) REFERENCES AnalyticsTeiSetting(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO AnalyticsTeiDataElement(teiSetting, whoComponent, programStage, dataElement) SELECT teiSetting, whoComponent, programStage, dataElement FROM AnalyticsTeiDataElement_Old;

ALTER TABLE AnalyticsTeiIndicator RENAME TO AnalyticsTeiIndicator_Old;
CREATE TABLE AnalyticsTeiIndicator(teiSetting TEXT NOT NULL, whoComponent TEXT, programStage TEXT, indicator TEXT NOT NULL, PRIMARY KEY(teiSetting, indicator), FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(indicator) REFERENCES ProgramIndicator(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(teiSetting) REFERENCES AnalyticsTeiSetting(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO AnalyticsTeiIndicator(teiSetting, whoComponent, programStage, indicator) SELECT teiSetting, whoComponent, programStage, indicator FROM AnalyticsTeiIndicator_Old;

ALTER TABLE AnalyticsTeiAttribute RENAME TO AnalyticsTeiAttribute_Old;
CREATE TABLE AnalyticsTeiAttribute(teiSetting TEXT NOT NULL, whoComponent TEXT, attribute TEXT NOT NULL, PRIMARY KEY(teiSetting, attribute), FOREIGN KEY(attribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(teiSetting) REFERENCES AnalyticsTeiSetting(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO AnalyticsTeiAttribute(teiSetting, whoComponent, attribute) SELECT teiSetting, whoComponent, attribute FROM AnalyticsTeiAttribute_Old;

ALTER TABLE AnalyticsTeiWHONutritionData RENAME TO AnalyticsTeiWHONutritionData_Old;
CREATE TABLE AnalyticsTeiWHONutritionData(teiSetting TEXT NOT NULL, chartType TEXT, genderAttribute TEXT NOT NULL, genderFemale TEXT, genderMale TEXT, PRIMARY KEY(teiSetting, genderAttribute), FOREIGN KEY(genderAttribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(teiSetting) REFERENCES AnalyticsTeiSetting(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO AnalyticsTeiWHONutritionData(teiSetting, chartType, genderAttribute, genderFemale, genderMale) SELECT teiSetting, chartType, genderAttribute, genderFemale, genderMale FROM AnalyticsTeiWHONutritionData_Old;

ALTER TABLE ValidationRule RENAME TO ValidationRule_Old;
CREATE TABLE ValidationRule(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, instruction TEXT, importance TEXT, operator TEXT, periodType TEXT, skipFormValidation INTEGER, leftSideExpression TEXT, leftSideDescription TEXT, leftSideMissingValueStrategy TEXT, rightSideExpression TEXT, rightSideDescription TEXT, rightSideMissingValueStrategy TEXT, organisationUnitLevels TEXT);
INSERT OR IGNORE INTO ValidationRule(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, instruction, importance, operator, periodType, skipFormValidation, leftSideExpression, leftSideDescription, leftSideMissingValueStrategy, rightSideExpression, rightSideDescription, rightSideMissingValueStrategy, organisationUnitLevels) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, instruction, importance, operator, periodType, skipFormValidation, leftSideExpression, leftSideDescription, leftSideMissingValueStrategy, rightSideExpression, rightSideDescription, rightSideMissingValueStrategy, organisationUnitLevels FROM ValidationRule_Old;

ALTER TABLE DataSetValidationRuleLink RENAME TO DataSetValidationRuleLink_Old;
CREATE TABLE DataSetValidationRuleLink(dataSet TEXT NOT NULL, validationRule TEXT NOT NULL, PRIMARY KEY(dataSet, validationRule), FOREIGN KEY(dataSet) REFERENCES DataSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(validationRule) REFERENCES ValidationRule(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataSetValidationRuleLink(dataSet, validationRule) SELECT dataSet, validationRule FROM DataSetValidationRuleLink_Old;

ALTER TABLE UserSettings RENAME TO UserSettings_Old;
CREATE TABLE UserSettings(keyUiLocale TEXT NOT NULL PRIMARY KEY, keyDbLocale TEXT);
INSERT OR IGNORE INTO UserSettings(keyUiLocale, keyDbLocale) SELECT keyUiLocale, keyDbLocale FROM UserSettings_Old;

ALTER TABLE AggregatedDataSync RENAME TO AggregatedDataSync_Old;
CREATE TABLE AggregatedDataSync(dataSet TEXT NOT NULL PRIMARY KEY, periodType TEXT NOT NULL, pastPeriods INTEGER NOT NULL, futurePeriods INTEGER NOT NULL, dataElementsHash INTEGER NOT NULL, organisationUnitsHash INTEGER NOT NULL, lastUpdated TEXT NOT NULL, FOREIGN KEY (dataSet) REFERENCES DataSet (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO AggregatedDataSync(dataSet, periodType, pastPeriods, futurePeriods, dataElementsHash, organisationUnitsHash, lastUpdated) SELECT dataSet, periodType, pastPeriods, futurePeriods, dataElementsHash, organisationUnitsHash, lastUpdated FROM AggregatedDataSync_Old;

ALTER TABLE TrackedEntityInstanceSync RENAME TO TrackedEntityInstanceSync_Old;
CREATE TABLE TrackedEntityInstanceSync(program TEXT, organisationUnitIdsHash INTEGER, downloadLimit INTEGER NOT NULL, lastUpdated TEXT NOT NULL, PRIMARY KEY(program, organisationUnitIdsHash), FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO TrackedEntityInstanceSync(program, organisationUnitIdsHash, downloadLimit, lastUpdated) SELECT program, organisationUnitIdsHash, downloadLimit, lastUpdated FROM TrackedEntityInstanceSync_Old;

ALTER TABLE EventSync RENAME TO EventSync_Old;
CREATE TABLE EventSync(program TEXT NOT NULL, organisationUnitIdsHash INTEGER NOT NULL, downloadLimit INTEGER NOT NULL, lastUpdated TEXT NOT NULL, PRIMARY KEY(program, organisationUnitIdsHash), FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO EventSync(program, organisationUnitIdsHash, downloadLimit, lastUpdated) SELECT program, organisationUnitIdsHash, downloadLimit, lastUpdated FROM EventSync_Old;

ALTER TABLE CategoryOptionOrganisationUnitLink RENAME TO CategoryOptionOrganisationUnitLink_Old;
CREATE TABLE CategoryOptionOrganisationUnitLink(categoryOption TEXT NOT NULL, organisationUnit TEXT, restriction TEXT, FOREIGN KEY(categoryOption) REFERENCES CategoryOption(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, PRIMARY KEY(categoryOption, organisationUnit));
INSERT OR IGNORE INTO CategoryOptionOrganisationUnitLink(categoryOption, organisationUnit, restriction) SELECT categoryOption, organisationUnit, restriction FROM CategoryOptionOrganisationUnitLink_Old;

ALTER TABLE TrackedEntityInstanceFilter RENAME TO TrackedEntityInstanceFilter_Old;
CREATE TABLE TrackedEntityInstanceFilter(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, color TEXT, icon TEXT, program TEXT NOT NULL, description TEXT, sortOrder INTEGER, enrollmentStatus TEXT, followUp INTEGER, organisationUnit TEXT, ouMode TEXT, assignedUserMode TEXT, orderProperty TEXT, displayColumnOrder TEXT, eventStatus TEXT, eventDate TEXT, lastUpdatedDate TEXT, programStage TEXT, trackedEntityInstances TEXT, enrollmentIncidentDate TEXT, enrollmentCreatedDate TEXT, trackedEntityType TEXT, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO TrackedEntityInstanceFilter(uid, code, name, displayName, created, lastUpdated, color, icon, program, description, sortOrder, enrollmentStatus, followUp, organisationUnit, ouMode, assignedUserMode, orderProperty, displayColumnOrder, eventStatus, eventDate, lastUpdatedDate, programStage, trackedEntityInstances, enrollmentIncidentDate, enrollmentCreatedDate, trackedEntityType) SELECT uid, code, name, displayName, created, lastUpdated, color, icon, program, description, sortOrder, enrollmentStatus, followUp, organisationUnit, ouMode, assignedUserMode, orderProperty, displayColumnOrder, eventStatus, eventDate, lastUpdatedDate, programStage, trackedEntityInstances, enrollmentIncidentDate, enrollmentCreatedDate, trackedEntityType FROM TrackedEntityInstanceFilter_Old;

ALTER TABLE TrackedEntityInstanceEventFilter RENAME TO TrackedEntityInstanceEventFilter_Old;
CREATE TABLE TrackedEntityInstanceEventFilter(trackedEntityInstanceFilter TEXT NOT NULL, programStage TEXT, eventStatus TEXT, periodFrom INTEGER, periodTo INTEGER, assignedUserMode TEXT, PRIMARY KEY(trackedEntityInstanceFilter, programStage), FOREIGN KEY(trackedEntityInstanceFilter) REFERENCES TrackedEntityInstanceFilter(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO TrackedEntityInstanceEventFilter(trackedEntityInstanceFilter, programStage, eventStatus, periodFrom, periodTo, assignedUserMode) SELECT trackedEntityInstanceFilter, programStage, eventStatus, periodFrom, periodTo, assignedUserMode FROM TrackedEntityInstanceEventFilter_Old;

ALTER TABLE EventFilter RENAME TO EventFilter_Old;
CREATE TABLE EventFilter(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, program TEXT NOT NULL, programStage TEXT, description TEXT, followUp INTEGER, organisationUnit TEXT, ouMode TEXT, assignedUserMode TEXT, orderProperty TEXT, displayColumnOrder TEXT, events TEXT, eventStatus TEXT, eventDate TEXT, dueDate TEXT, lastUpdatedDate TEXT, completedDate TEXT, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(organisationUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO EventFilter(uid, code, name, displayName, created, lastUpdated, program, programStage, description, followUp, organisationUnit, ouMode, assignedUserMode, orderProperty, displayColumnOrder, events, eventStatus, eventDate, dueDate, lastUpdatedDate, completedDate) SELECT uid, code, name, displayName, created, lastUpdated, program, programStage, description, followUp, organisationUnit, ouMode, assignedUserMode, orderProperty, displayColumnOrder, events, eventStatus, eventDate, dueDate, lastUpdatedDate, completedDate FROM EventFilter_Old;

ALTER TABLE ReservedValueSetting RENAME TO ReservedValueSetting_Old;
CREATE TABLE ReservedValueSetting(uid TEXT PRIMARY KEY, numberOfValuesToReserve INTEGER, FOREIGN KEY(uid) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ReservedValueSetting(uid, numberOfValuesToReserve) SELECT uid, numberOfValuesToReserve FROM ReservedValueSetting_Old;

ALTER TABLE SectionIndicatorLink RENAME TO SectionIndicatorLink_Old;
CREATE TABLE SectionIndicatorLink(section TEXT NOT NULL, indicator TEXT NOT NULL, PRIMARY KEY(section, indicator), FOREIGN KEY(section) REFERENCES Section(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(indicator) REFERENCES Indicator(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO SectionIndicatorLink(section, indicator) SELECT section, indicator FROM SectionIndicatorLink_Old;

ALTER TABLE DataElementLegendSetLink RENAME TO DataElementLegendSetLink_Old;
CREATE TABLE DataElementLegendSetLink(dataElement TEXT NOT NULL, legendSet TEXT NOT NULL, sortOrder INTEGER, PRIMARY KEY(dataElement, legendSet), FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(legendSet) REFERENCES LegendSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataElementLegendSetLink(dataElement, legendSet, sortOrder) SELECT dataElement, legendSet, sortOrder FROM DataElementLegendSetLink_Old;

ALTER TABLE Attribute RENAME TO Attribute_Old;
CREATE TABLE Attribute(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, shortName TEXT, displayShortName TEXT, description TEXT, displayDescription TEXT, valueType TEXT, uniqueProperty INTEGER, mandatory INTEGER, indicatorAttribute INTEGER, indicatorGroupAttribute INTEGER, userGroupAttribute INTEGER, dataElementAttribute INTEGER, constantAttribute INTEGER, categoryOptionAttribute INTEGER, optionSetAttribute INTEGER, sqlViewAttribute INTEGER, legendSetAttribute INTEGER, trackedEntityAttributeAttribute INTEGER, organisationUnitAttribute INTEGER, dataSetAttribute INTEGER, documentAttribute INTEGER, validationRuleGroupAttribute INTEGER, dataElementGroupAttribute INTEGER, sectionAttribute INTEGER, trackedEntityTypeAttribute INTEGER, userAttribute INTEGER, categoryOptionGroupAttribute INTEGER, programStageAttribute INTEGER, programAttribute INTEGER, categoryAttribute INTEGER, categoryOptionComboAttribute INTEGER, categoryOptionGroupSetAttribute INTEGER, validationRuleAttribute INTEGER, programIndicatorAttribute INTEGER, organisationUnitGroupAttribute INTEGER, dataElementGroupSetAttribute INTEGER, organisationUnitGroupSetAttribute INTEGER, optionAttribute INTEGER);
INSERT OR IGNORE INTO Attribute(uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, valueType, uniqueProperty, mandatory, indicatorAttribute, indicatorGroupAttribute, userGroupAttribute, dataElementAttribute, constantAttribute, categoryOptionAttribute, optionSetAttribute, sqlViewAttribute, legendSetAttribute, trackedEntityAttributeAttribute, organisationUnitAttribute, dataSetAttribute, documentAttribute, validationRuleGroupAttribute, dataElementGroupAttribute, sectionAttribute, trackedEntityTypeAttribute, userAttribute, categoryOptionGroupAttribute, programStageAttribute, programAttribute, categoryAttribute, categoryOptionComboAttribute, categoryOptionGroupSetAttribute, validationRuleAttribute, programIndicatorAttribute, organisationUnitGroupAttribute, dataElementGroupSetAttribute, organisationUnitGroupSetAttribute, optionAttribute) SELECT uid, code, name, displayName, created, lastUpdated, shortName, displayShortName, description, displayDescription, valueType, uniqueProperty, mandatory, indicatorAttribute, indicatorGroupAttribute, userGroupAttribute, dataElementAttribute, constantAttribute, categoryOptionAttribute, optionSetAttribute, sqlViewAttribute, legendSetAttribute, trackedEntityAttributeAttribute, organisationUnitAttribute, dataSetAttribute, documentAttribute, validationRuleGroupAttribute, dataElementGroupAttribute, sectionAttribute, trackedEntityTypeAttribute, userAttribute, categoryOptionGroupAttribute, programStageAttribute, programAttribute, categoryAttribute, categoryOptionComboAttribute, categoryOptionGroupSetAttribute, validationRuleAttribute, programIndicatorAttribute, organisationUnitGroupAttribute, dataElementGroupSetAttribute, organisationUnitGroupSetAttribute, optionAttribute FROM Attribute_Old;

ALTER TABLE ProgramStageAttributeValueLink RENAME TO ProgramStageAttributeValueLink_Old;
CREATE TABLE ProgramStageAttributeValueLink(programStage TEXT NOT NULL, attribute TEXT NOT NULL, value TEXT, PRIMARY KEY(programStage, attribute), FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(attribute) REFERENCES Attribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramStageAttributeValueLink(programStage, attribute, value) SELECT programStage, attribute, value FROM ProgramStageAttributeValueLink_Old;

ALTER TABLE DataElementAttributeValueLink RENAME TO DataElementAttributeValueLink_Old;
CREATE TABLE DataElementAttributeValueLink(dataElement TEXT NOT NULL, attribute TEXT NOT NULL, value TEXT, PRIMARY KEY(dataElement, attribute), FOREIGN KEY(dataElement) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(attribute) REFERENCES Attribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO DataElementAttributeValueLink(dataElement, attribute, value) SELECT dataElement, attribute, value FROM DataElementAttributeValueLink_Old;

ALTER TABLE ProgramAttributeValueLink RENAME TO ProgramAttributeValueLink_Old;
CREATE TABLE ProgramAttributeValueLink(program TEXT NOT NULL, attribute TEXT NOT NULL, value TEXT, PRIMARY KEY(program, attribute), FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(attribute) REFERENCES Attribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramAttributeValueLink(program, attribute, value) SELECT program, attribute, value FROM ProgramAttributeValueLink_Old;

ALTER TABLE TrackerJobObject RENAME TO TrackerJobObject_Old;
CREATE TABLE TrackerJobObject(trackerType TEXT NOT NULL, objectUid TEXT NOT NULL, jobUid TEXT NOT NULL, lastUpdated TEXT NOT NULL, fileResources TEXT, PRIMARY KEY(jobUid, objectUid));
INSERT OR IGNORE INTO TrackerJobObject(trackerType, objectUid, jobUid, lastUpdated, fileResources) SELECT trackerType, objectUid, jobUid, lastUpdated, fileResources FROM TrackerJobObject_Old;

ALTER TABLE DataValueConflict RENAME TO DataValueConflict_Old;
CREATE TABLE DataValueConflict(conflict TEXT, value TEXT, attributeOptionCombo TEXT, categoryOptionCombo TEXT, dataElement TEXT, period TEXT, orgUnit TEXT, errorCode TEXT, status TEXT, created TEXT, displayDescription TEXT, PRIMARY KEY(dataElement, period, orgUnit, categoryOptionCombo, attributeOptionCombo));
INSERT OR IGNORE INTO DataValueConflict(conflict, value, attributeOptionCombo, categoryOptionCombo, dataElement, period, orgUnit, errorCode, status, created, displayDescription) SELECT conflict, value, attributeOptionCombo, categoryOptionCombo, dataElement, period, orgUnit, errorCode, status, created, displayDescription FROM DataValueConflict_Old;

ALTER TABLE AnalyticsDhisVisualization RENAME TO AnalyticsDhisVisualization_Old;
CREATE TABLE AnalyticsDhisVisualization(uid TEXT NOT NULL, scopeUid TEXT, scope TEXT, groupUid TEXT, groupName TEXT, timestamp TEXT, name TEXT, type TEXT NOT NULL, PRIMARY KEY(uid, groupUid));
INSERT OR IGNORE INTO AnalyticsDhisVisualization(uid, scopeUid, scope, groupUid, groupName, timestamp, name, type) SELECT uid, scopeUid, scope, groupUid, groupName, timestamp, name, type FROM AnalyticsDhisVisualization_Old;

ALTER TABLE Visualization RENAME TO Visualization_Old;
CREATE TABLE Visualization(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, description TEXT, displayDescription TEXT, displayFormName TEXT, title TEXT, displayTitle TEXT, subtitle TEXT, displaySubtitle TEXT, type TEXT, hideTitle INTEGER, hideSubtitle INTEGER, hideEmptyColumns INTEGER, hideEmptyRows INTEGER, hideEmptyRowItems TEXT, hideLegend INTEGER, showHierarchy INTEGER, rowTotals INTEGER, rowSubTotals INTEGER, colTotals INTEGER, colSubTotals INTEGER, showDimensionLabels INTEGER, percentStackedValues INTEGER, noSpaceBetweenColumns INTEGER, skipRounding INTEGER, displayDensity TEXT, digitGroupSeparator TEXT, legendShowKey TEXT, legendStyle TEXT, legendSetId TEXT, legendStrategy TEXT, aggregationType TEXT);
INSERT OR IGNORE INTO Visualization(uid, code, name, displayName, created, lastUpdated, description, displayDescription, displayFormName, title, displayTitle, subtitle, displaySubtitle, type, hideTitle, hideSubtitle, hideEmptyColumns, hideEmptyRows, hideEmptyRowItems, hideLegend, showHierarchy, rowTotals, rowSubTotals, colTotals, colSubTotals, showDimensionLabels, percentStackedValues, noSpaceBetweenColumns, skipRounding, displayDensity, digitGroupSeparator, legendShowKey, legendStyle, legendSetId, legendStrategy, aggregationType) SELECT uid, code, name, displayName, created, lastUpdated, description, displayDescription, displayFormName, title, displayTitle, subtitle, displaySubtitle, type, hideTitle, hideSubtitle, hideEmptyColumns, hideEmptyRows, hideEmptyRowItems, hideLegend, showHierarchy, rowTotals, rowSubTotals, colTotals, colSubTotals, showDimensionLabels, percentStackedValues, noSpaceBetweenColumns, skipRounding, displayDensity, digitGroupSeparator, legendShowKey, legendStyle, legendSetId, legendStrategy, aggregationType FROM Visualization_Old;

ALTER TABLE VisualizationDimensionItem RENAME TO VisualizationDimensionItem_Old;
CREATE TABLE VisualizationDimensionItem(visualization TEXT NOT NULL, position TEXT NOT NULL, dimension TEXT NOT NULL, dimensionItem TEXT, dimensionItemType TEXT, sortOrder INTEGER, PRIMARY KEY(visualization, dimensionItem), FOREIGN KEY(visualization) REFERENCES Visualization(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO VisualizationDimensionItem(visualization, position, dimension, dimensionItem, dimensionItemType, sortOrder) SELECT visualization, position, dimension, dimensionItem, dimensionItemType, _id FROM VisualizationDimensionItem_Old;

ALTER TABLE LocalDataStore RENAME TO LocalDataStore_Old;
CREATE TABLE LocalDataStore(key TEXT NOT NULL PRIMARY KEY, value TEXT);
INSERT OR IGNORE INTO LocalDataStore(key, value) SELECT key, value FROM LocalDataStore_Old;

ALTER TABLE AnalyticsPeriodBoundary RENAME TO AnalyticsPeriodBoundary_Old;
CREATE TABLE AnalyticsPeriodBoundary(programIndicator TEXT NOT NULL, boundaryTarget TEXT, analyticsPeriodBoundaryType TEXT, offsetPeriods INTEGER, offsetPeriodType TEXT, PRIMARY KEY(programIndicator, boundaryTarget, analyticsPeriodBoundaryType), FOREIGN KEY(programIndicator) REFERENCES ProgramIndicator(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO AnalyticsPeriodBoundary(programIndicator, boundaryTarget, analyticsPeriodBoundaryType, offsetPeriods, offsetPeriodType) SELECT programIndicator, boundaryTarget, analyticsPeriodBoundaryType, offsetPeriods, offsetPeriodType FROM AnalyticsPeriodBoundary_Old;

ALTER TABLE IndicatorLegendSetLink RENAME TO IndicatorLegendSetLink_Old;
CREATE TABLE IndicatorLegendSetLink(indicator TEXT NOT NULL, legendSet TEXT NOT NULL, sortOrder INTEGER, PRIMARY KEY(indicator, legendSet), FOREIGN KEY(indicator) REFERENCES Indicator(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(legendSet) REFERENCES LegendSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO IndicatorLegendSetLink(indicator, legendSet, sortOrder) SELECT indicator, legendSet, sortOrder FROM IndicatorLegendSetLink_Old;

ALTER TABLE ProgramTempOwner RENAME TO ProgramTempOwner_Old;
CREATE TABLE ProgramTempOwner(program TEXT NOT NULL, trackedEntityInstance TEXT NOT NULL, created TEXT NOT NULL, validUntil TEXT NOT NULL, reason TEXT NOT NULL, PRIMARY KEY(program, trackedEntityInstance, created), FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramTempOwner(program, trackedEntityInstance, created, validUntil, reason) SELECT program, trackedEntityInstance, created, validUntil, reason FROM ProgramTempOwner_Old;

ALTER TABLE ProgramOwner RENAME TO ProgramOwner_Old;
CREATE TABLE ProgramOwner(program TEXT NOT NULL, trackedEntityInstance TEXT NOT NULL, ownerOrgUnit TEXT NOT NULL, syncState TEXT, PRIMARY KEY(program, trackedEntityInstance), FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(trackedEntityInstance) REFERENCES TrackedEntityInstance(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(ownerOrgUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramOwner(program, trackedEntityInstance, ownerOrgUnit, syncState) SELECT program, trackedEntityInstance, ownerOrgUnit, syncState FROM ProgramOwner_Old;

ALTER TABLE SMSConfig RENAME TO SMSConfig_Old;
CREATE TABLE SMSConfig(key TEXT NOT NULL PRIMARY KEY, value TEXT);
INSERT OR IGNORE INTO SMSConfig(key, value) SELECT key, value FROM SMSConfig_Old;

ALTER TABLE SmsMetadataId RENAME TO SmsMetadataId_Old;
CREATE TABLE SmsMetadataId(type TEXT NOT NULL, uid TEXT NOT NULL, PRIMARY KEY(type, uid));
INSERT OR IGNORE INTO SmsMetadataId(type, uid) SELECT type, uid FROM SmsMetadataId_Old;

ALTER TABLE SMSOngoingSubmission RENAME TO SMSOngoingSubmission_Old;
CREATE TABLE SMSOngoingSubmission(submissionId INTEGER PRIMARY KEY, type TEXT);
INSERT OR IGNORE INTO SMSOngoingSubmission(submissionId, type) SELECT submissionId, type FROM SMSOngoingSubmission_Old;

ALTER TABLE TrackedEntityAttributeLegendSetLink RENAME TO TrackedEntityAttributeLegendSetLink_Old;
CREATE TABLE TrackedEntityAttributeLegendSetLink(trackedEntityAttribute TEXT NOT NULL, legendSet TEXT NOT NULL, sortOrder INTEGER, PRIMARY KEY(trackedEntityAttribute, legendSet), FOREIGN KEY(trackedEntityAttribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(legendSet) REFERENCES LegendSet(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO TrackedEntityAttributeLegendSetLink(trackedEntityAttribute, legendSet, sortOrder) SELECT trackedEntityAttribute, legendSet, sortOrder FROM TrackedEntityAttributeLegendSetLink_Old;

ALTER TABLE StockUseCase RENAME TO StockUseCase_Old;
CREATE TABLE StockUseCase(uid TEXT NOT NULL PRIMARY KEY, itemCode TEXT, itemDescription TEXT, programType TEXT, description TEXT, stockOnHand TEXT, FOREIGN KEY(uid) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO StockUseCase(uid, itemCode, itemDescription, programType, description, stockOnHand) SELECT uid, itemCode, itemDescription, programType, description, stockOnHand FROM StockUseCase_Old;

ALTER TABLE StockUseCaseTransaction RENAME TO StockUseCaseTransaction_Old;
CREATE TABLE StockUseCaseTransaction(programUid TEXT NOT NULL, sortOrder INTEGER, transactionType TEXT, distributedTo TEXT, stockDistributed TEXT, stockDiscarded TEXT, stockCount TEXT, PRIMARY KEY(programUid, transactionType), FOREIGN KEY(programUid) REFERENCES StockUseCase(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO StockUseCaseTransaction(programUid, sortOrder, transactionType, distributedTo, stockDistributed, stockDiscarded, stockCount) SELECT programUid, sortOrder, transactionType, distributedTo, stockDistributed, stockDiscarded, stockCount FROM StockUseCaseTransaction_Old;

ALTER TABLE MapLayer RENAME TO MapLayer_Old;
CREATE TABLE MapLayer(uid TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, displayName TEXT NOT NULL, external INTEGER, mapLayerPosition TEXT NOT NULL, style TEXT, imageUrl TEXT NOT NULL, subdomains TEXT, subdomainPlaceholder TEXT, code TEXT, mapService TEXT, imageFormat TEXT, layers TEXT);
INSERT OR IGNORE INTO MapLayer(uid, name, displayName, external, mapLayerPosition, style, imageUrl, subdomains, subdomainPlaceholder, code, mapService, imageFormat, layers) SELECT uid, name, displayName, external, mapLayerPosition, style, imageUrl, subdomains, subdomainPlaceholder, code, mapService, imageFormat, layers FROM MapLayer_Old;

ALTER TABLE MapLayerImageryProvider RENAME TO MapLayerImageryProvider_Old;
CREATE TABLE MapLayerImageryProvider(mapLayer TEXT NOT NULL, attribution TEXT NOT NULL, coverageAreas TEXT, PRIMARY KEY(mapLayer, attribution), FOREIGN KEY(mapLayer) REFERENCES MapLayer(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO MapLayerImageryProvider(mapLayer, attribution, coverageAreas) SELECT mapLayer, attribution, coverageAreas FROM MapLayerImageryProvider_Old;

ALTER TABLE DataStore RENAME TO DataStore_Old;
CREATE TABLE DataStore(namespace TEXT NOT NULL, key TEXT NOT NULL, value TEXT, syncState TEXT, deleted INTEGER, PRIMARY KEY(namespace, key));
INSERT OR IGNORE INTO DataStore(namespace, key, value, syncState, deleted) SELECT namespace, key, value, syncState, deleted FROM DataStore_Old;

ALTER TABLE ProgramStageWorkingList RENAME TO ProgramStageWorkingList_Old;
CREATE TABLE ProgramStageWorkingList(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, description TEXT, program TEXT NOT NULL, programStage TEXT NOT NULL, eventStatus TEXT, eventCreatedAt TEXT, eventOccurredAt TEXT, eventScheduledAt TEXT, enrollmentStatus TEXT, enrolledAt TEXT, enrollmentOccurredAt TEXT, orderProperty TEXT, displayColumnOrder TEXT, orgUnit TEXT, ouMode TEXT, assignedUserMode TEXT, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(orgUnit) REFERENCES OrganisationUnit(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramStageWorkingList(uid, code, name, displayName, created, lastUpdated, description, program, programStage, eventStatus, eventCreatedAt, eventOccurredAt, eventScheduledAt, enrollmentStatus, enrolledAt, enrollmentOccurredAt, orderProperty, displayColumnOrder, orgUnit, ouMode, assignedUserMode) SELECT uid, code, name, displayName, created, lastUpdated, description, program, programStage, eventStatus, eventCreatedAt, eventOccurredAt, eventScheduledAt, enrollmentStatus, enrolledAt, enrollmentOccurredAt, orderProperty, displayColumnOrder, orgUnit, ouMode, assignedUserMode FROM ProgramStageWorkingList_Old;

ALTER TABLE LatestAppVersion RENAME TO LatestAppVersion_Old;
CREATE TABLE LatestAppVersion(downloadURL TEXT, version TEXT NOT NULL PRIMARY KEY);
INSERT OR IGNORE INTO LatestAppVersion(downloadURL, version) SELECT downloadURL, version FROM LatestAppVersion_Old;

ALTER TABLE ExpressionDimensionItem RENAME TO ExpressionDimensionItem_Old;
CREATE TABLE ExpressionDimensionItem(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, expression TEXT);
INSERT OR IGNORE INTO ExpressionDimensionItem(uid, code, name, displayName, created, lastUpdated, expression) SELECT uid, code, name, displayName, created, lastUpdated, expression FROM ExpressionDimensionItem_Old;

ALTER TABLE TrackerVisualization RENAME TO TrackerVisualization_Old;
CREATE TABLE TrackerVisualization(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, description TEXT, displayDescription TEXT, type TEXT, outputType TEXT, program TEXT, programStage TEXT, trackedEntityType TEXT, sorting TEXT, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(trackedEntityType) REFERENCES TrackedEntityType(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO TrackerVisualization(uid, code, name, displayName, created, lastUpdated, description, displayDescription, type, outputType, program, programStage, trackedEntityType, sorting) SELECT uid, code, name, displayName, created, lastUpdated, description, displayDescription, type, outputType, program, programStage, trackedEntityType, sorting FROM TrackerVisualization_Old;

ALTER TABLE TrackerVisualizationDimension RENAME TO TrackerVisualizationDimension_Old;
CREATE TABLE TrackerVisualizationDimension(trackerVisualization TEXT NOT NULL, position TEXT NOT NULL, dimension TEXT NOT NULL, dimensionType TEXT, program TEXT, programStage TEXT, items TEXT, filter TEXT, repetition TEXT, sortOrder INTEGER, PRIMARY KEY(trackerVisualization, dimension), FOREIGN KEY(trackerVisualization) REFERENCES TrackerVisualization(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(program) REFERENCES Program(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(programStage) REFERENCES ProgramStage(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO TrackerVisualizationDimension(trackerVisualization, position, dimension, dimensionType, program, programStage, items, filter, repetition, sortOrder) SELECT trackerVisualization, position, dimension, dimensionType, program, programStage, items, filter, repetition, _id FROM TrackerVisualizationDimension_Old;

ALTER TABLE CustomIcon RENAME TO CustomIcon_Old;
CREATE TABLE CustomIcon(key TEXT NOT NULL PRIMARY KEY, fileResource TEXT NOT NULL, href TEXT NOT NULL);
INSERT OR IGNORE INTO CustomIcon(key, fileResource, href) SELECT key, fileResource, href FROM CustomIcon_Old;

ALTER TABLE UserGroup RENAME TO UserGroup_Old;
CREATE TABLE UserGroup(uid TEXT NOT NULL PRIMARY KEY, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT);
INSERT OR IGNORE INTO UserGroup(uid, code, name, displayName, created, lastUpdated) SELECT uid, code, name, displayName, created, lastUpdated FROM UserGroup_Old;

ALTER TABLE EventDataFilter RENAME TO EventDataFilter_Old;
CREATE TABLE EventDataFilter(eventFilter TEXT NOT NULL, dataItem TEXT NOT NULL, le TEXT, ge TEXT, gt TEXT, lt TEXT, eq TEXT, inProperty TEXT, like TEXT, dateFilter TEXT, PRIMARY KEY(eventFilter, dataItem), FOREIGN KEY(eventFilter) REFERENCES EventFilter(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataItem) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO EventDataFilter(eventFilter, dataItem, le, ge, gt, lt, eq, inProperty, like, dateFilter) SELECT eventFilter, dataItem, le, ge, gt, lt, eq, inProperty, like, dateFilter FROM EventDataFilter_Old;

ALTER TABLE AttributeValueFilter RENAME TO AttributeValueFilter_Old;
CREATE TABLE AttributeValueFilter(trackedEntityInstanceFilter TEXT NOT NULL, attribute TEXT NOT NULL, sw TEXT, ew TEXT, le TEXT, ge TEXT, gt TEXT, lt TEXT, eq TEXT, inProperty TEXT, like TEXT, dateFilter TEXT, PRIMARY KEY(trackedEntityInstanceFilter, attribute), FOREIGN KEY(trackedEntityInstanceFilter) REFERENCES TrackedEntityInstanceFilter(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(attribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO AttributeValueFilter(trackedEntityInstanceFilter, attribute, sw, ew, le, ge, gt, lt, eq, inProperty, like, dateFilter) SELECT trackedEntityInstanceFilter, attribute, sw, ew, le, ge, gt, lt, eq, inProperty, like, dateFilter FROM AttributeValueFilter_Old;

ALTER TABLE ProgramStageWorkingListEventDataFilter RENAME TO ProgramStageWorkingListEventDataFilter_Old;
CREATE TABLE ProgramStageWorkingListEventDataFilter(programStageWorkingList TEXT NOT NULL, dataItem TEXT NOT NULL, le TEXT, ge TEXT, gt TEXT, lt TEXT, eq TEXT, inProperty TEXT, like TEXT, dateFilter TEXT, PRIMARY KEY(programStageWorkingList, dataItem), FOREIGN KEY(programStageWorkingList) REFERENCES ProgramStageWorkingList(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(dataItem) REFERENCES DataElement(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramStageWorkingListEventDataFilter(programStageWorkingList, dataItem, le, ge, gt, lt, eq, inProperty, like, dateFilter) SELECT programStageWorkingList, dataItem, le, ge, gt, lt, eq, inProperty, like, dateFilter FROM ProgramStageWorkingListEventDataFilter_Old;

ALTER TABLE ProgramStageWorkingListAttributeValueFilter RENAME TO ProgramStageWorkingListAttributeValueFilter_Old;
CREATE TABLE ProgramStageWorkingListAttributeValueFilter(programStageWorkingList TEXT NOT NULL, attribute TEXT NOT NULL, sw TEXT, ew TEXT, le TEXT, ge TEXT, gt TEXT, lt TEXT, eq TEXT, inProperty TEXT, like TEXT, dateFilter TEXT, PRIMARY KEY(programStageWorkingList, attribute), FOREIGN KEY(programStageWorkingList) REFERENCES ProgramStageWorkingList(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(attribute) REFERENCES TrackedEntityAttribute(uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT OR IGNORE INTO ProgramStageWorkingListAttributeValueFilter(programStageWorkingList, attribute, sw, ew, le, ge, gt, lt, eq, inProperty, like, dateFilter) SELECT programStageWorkingList, attribute, sw, ew, le, ge, gt, lt, eq, inProperty, like, dateFilter FROM ProgramStageWorkingListAttributeValueFilter_Old;

DROP TABLE Configuration_Old;
DROP TABLE User_Old;
DROP TABLE OrganisationUnit_Old;
DROP TABLE OptionSet_Old;
DROP TABLE Option_Old;
DROP TABLE TrackedEntityType_Old;
DROP TABLE ProgramStageSection_Old;
DROP TABLE ProgramRuleVariable_Old;
DROP TABLE ProgramTrackedEntityAttribute_Old;
DROP TABLE Constant_Old;
DROP TABLE SystemInfo_Old;
DROP TABLE ProgramRule_Old;
DROP TABLE ProgramIndicator_Old;
DROP TABLE Resource_Old;
DROP TABLE OrganisationUnitProgramLink_Old;
DROP TABLE UserRole_Old;
DROP TABLE ProgramStageSectionProgramIndicatorLink_Old;
DROP TABLE Category_Old;
DROP TABLE CategoryOption_Old;
DROP TABLE CategoryCategoryOptionLink_Old;
DROP TABLE CategoryCombo_Old;
DROP TABLE CategoryCategoryComboLink_Old;
DROP TABLE CategoryOptionCombo_Old;
DROP TABLE DataSet_Old;
DROP TABLE DataSetDataElementLink_Old;
DROP TABLE Indicator_Old;
DROP TABLE DataSetIndicatorLink_Old;
DROP TABLE Period_Old;
DROP TABLE ValueTypeDeviceRendering_Old;
DROP TABLE Note_Old;
DROP TABLE Legend_Old;
DROP TABLE LegendSet_Old;
DROP TABLE ProgramIndicatorLegendSetLink_Old;
DROP TABLE SystemSetting_Old;
DROP TABLE ProgramSectionAttributeLink_Old;
DROP TABLE TrackedEntityAttributeReservedValue_Old;
DROP TABLE CategoryOptionComboCategoryOptionLink_Old;
DROP TABLE Section_Old;
DROP TABLE SectionDataElementLink_Old;
DROP TABLE DataSetCompulsoryDataElementOperandsLink_Old;
DROP TABLE DataInputPeriod_Old;
DROP TABLE RelationshipConstraint_Old;
DROP TABLE RelationshipItem_Old;
DROP TABLE OrganisationUnitGroup_Old;
DROP TABLE OrganisationUnitOrganisationUnitGroupLink_Old;
DROP TABLE ProgramStageDataElement_Old;
DROP TABLE ProgramStageSectionDataElementLink_Old;
DROP TABLE DataElementOperand_Old;
DROP TABLE IndicatorType_Old;
DROP TABLE ForeignKeyViolation_Old;
DROP TABLE D2Error_Old;
DROP TABLE Authority_Old;
DROP TABLE TrackedEntityTypeAttribute_Old;
DROP TABLE Relationship_Old;
DROP TABLE DataElement_Old;
DROP TABLE OptionGroup_Old;
DROP TABLE OptionGroupOptionLink_Old;
DROP TABLE ProgramRuleAction_Old;
DROP TABLE OrganisationUnitLevel_Old;
DROP TABLE ProgramSection_Old;
DROP TABLE DataApproval_Old;
DROP TABLE TrackedEntityAttribute_Old;
DROP TABLE TrackerImportConflict_Old;
DROP TABLE DataSetOrganisationUnitLink_Old;
DROP TABLE UserOrganisationUnit_Old;
DROP TABLE RelationshipType_Old;
DROP TABLE ProgramStage_Old;
DROP TABLE Program_Old;
DROP TABLE TrackedEntityInstance_Old;
DROP TABLE Enrollment_Old;
DROP TABLE Event_Old;
DROP TABLE DataValue_Old;
DROP TABLE TrackedEntityDataValue_Old;
DROP TABLE TrackedEntityAttributeValue_Old;
DROP TABLE FileResource_Old;
DROP TABLE DataSetCompleteRegistration_Old;
DROP TABLE SectionGreyedFieldsLink_Old;
DROP TABLE AuthenticatedUser_Old;
DROP TABLE GeneralSetting_Old;
DROP TABLE DataSetSetting_Old;
DROP TABLE ProgramSetting_Old;
DROP TABLE SynchronizationSetting_Old;
DROP TABLE FilterSetting_Old;
DROP TABLE ProgramConfigurationSetting_Old;
DROP TABLE DataSetConfigurationSetting_Old;
DROP TABLE CustomIntent_Old;
DROP TABLE CustomIntentDataElement_Old;
DROP TABLE CustomIntentAttribute_Old;
DROP TABLE AnalyticsTeiSetting_Old;
DROP TABLE AnalyticsTeiDataElement_Old;
DROP TABLE AnalyticsTeiIndicator_Old;
DROP TABLE AnalyticsTeiAttribute_Old;
DROP TABLE AnalyticsTeiWHONutritionData_Old;
DROP TABLE ValidationRule_Old;
DROP TABLE DataSetValidationRuleLink_Old;
DROP TABLE UserSettings_Old;
DROP TABLE AggregatedDataSync_Old;
DROP TABLE TrackedEntityInstanceSync_Old;
DROP TABLE EventSync_Old;
DROP TABLE CategoryOptionOrganisationUnitLink_Old;
DROP TABLE TrackedEntityInstanceFilter_Old;
DROP TABLE TrackedEntityInstanceEventFilter_Old;
DROP TABLE EventFilter_Old;
DROP TABLE ReservedValueSetting_Old;
DROP TABLE SectionIndicatorLink_Old;
DROP TABLE DataElementLegendSetLink_Old;
DROP TABLE Attribute_Old;
DROP TABLE ProgramStageAttributeValueLink_Old;
DROP TABLE DataElementAttributeValueLink_Old;
DROP TABLE ProgramAttributeValueLink_Old;
DROP TABLE TrackerJobObject_Old;
DROP TABLE DataValueConflict_Old;
DROP TABLE AnalyticsDhisVisualization_Old;
DROP TABLE Visualization_Old;
DROP TABLE VisualizationDimensionItem_Old;
DROP TABLE LocalDataStore_Old;
DROP TABLE AnalyticsPeriodBoundary_Old;
DROP TABLE IndicatorLegendSetLink_Old;
DROP TABLE ProgramTempOwner_Old;
DROP TABLE ProgramOwner_Old;
DROP TABLE SMSConfig_Old;
DROP TABLE SmsMetadataId_Old;
DROP TABLE SMSOngoingSubmission_Old;
DROP TABLE TrackedEntityAttributeLegendSetLink_Old;
DROP TABLE StockUseCase_Old;
DROP TABLE StockUseCaseTransaction_Old;
DROP TABLE MapLayer_Old;
DROP TABLE MapLayerImageryProvider_Old;
DROP TABLE DataStore_Old;
DROP TABLE ProgramStageWorkingList_Old;
DROP TABLE LatestAppVersion_Old;
DROP TABLE ExpressionDimensionItem_Old;
DROP TABLE TrackerVisualization_Old;
DROP TABLE TrackerVisualizationDimension_Old;
DROP TABLE CustomIcon_Old;
DROP TABLE UserGroup_Old;
DROP TABLE EventDataFilter_Old;
DROP TABLE AttributeValueFilter_Old;
DROP TABLE ProgramStageWorkingListEventDataFilter_Old;
DROP TABLE ProgramStageWorkingListAttributeValueFilter_Old;

CREATE INDEX optionset_optioncode ON Option(optionSet, code);
