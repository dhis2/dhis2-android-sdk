# Add allowScreenCapture to GeneralSetting

ALTER TABLE GeneralSetting ADD COLUMN allowScreenCapture INTEGER;

ALTER TABLE SynchronizationSetting RENAME TO SynchronizationSetting_Old;
CREATE TABLE SynchronizationSetting (_id INTEGER PRIMARY KEY AUTOINCREMENT, dataSync TEXT, metadataSync TEXT, trackerImporterVersion TEXT);
INSERT INTO SynchronizationSetting (_id, dataSync, metadataSync, trackerImporterVersion) SELECT _id, dataSync, metadataSync, null FROM SynchronizationSetting_Old;

DROP TABLE SynchronizationSetting_Old;