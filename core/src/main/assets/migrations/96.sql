# Migrate settings app definition
ALTER TABLE GeneralSetting RENAME TO GeneralSetting_Old;
CREATE TABLE GeneralSetting (_id INTEGER PRIMARY KEY AUTOINCREMENT, encryptDB INTEGER, lastUpdated TEXT, reservedValues INTEGER, smsGateway TEXT, smsResultSender TEXT, matomoID TEXT, matomoUrl TEXT);
INSERT INTO GeneralSetting (_id, encryptDB, lastUpdated, reservedValues, smsGateway, smsResultSender, matomoID, matomoURL) SELECT _id, encryptDB, lastUpdated, reservedValues, numberSmsToSend, numberSmsConfirmation, null, null FROM GeneralSetting_Old;

CREATE TABLE SynchronizationSetting (_id INTEGER PRIMARY KEY AUTOINCREMENT, dataSync TEXT, metadataSync TEXT, newTrackerImporter INTEGER);
INSERT INTO SynchronizationSetting (dataSync, metadataSync) SELECT dataSync, metadataSync FROM GeneralSetting_Old;

DROP TABLE GeneralSetting_Old;

CREATE TABLE AnalyticsTeiSetting (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, name TEXT, shortName TEXT, period TEXT, type TEXT);
CREATE TABLE AnalyticsTeiDataElement (_id INTEGER PRIMARY KEY AUTOINCREMENT, teiSetting TEXT NOT NULL, programStage TEXT, dataElement TEXT NOT NULL, FOREIGN KEY (programStage) REFERENCES ProgramStage (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (dataElement) REFERENCES DataElement (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (teiSetting) REFERENCES AnalyticsTeiSetting (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
CREATE TABLE AnalyticsTeiIndicator (_id INTEGER PRIMARY KEY AUTOINCREMENT, teiSetting TEXT NOT NULL, programStage TEXT, indicator TEXT NOT NULL, FOREIGN KEY (programStage) REFERENCES ProgramStage (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (indicator) REFERENCES ProgramIndicator (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (teiSetting) REFERENCES AnalyticsTeiSetting (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
CREATE TABLE AnalyticsTeiAttribute (_id INTEGER PRIMARY KEY AUTOINCREMENT, teiSetting TEXT NOT NULL, attribute TEXT NOT NULL, FOREIGN KEY (attribute) REFERENCES trackedEntityAttribute (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (teiSetting) REFERENCES AnalyticsTeiSetting (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
