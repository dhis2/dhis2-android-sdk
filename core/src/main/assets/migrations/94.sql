# Migrate settings app definition
ALTER TABLE GeneralSetting RENAME TO GeneralSetting_Old;
CREATE TABLE GeneralSetting (_id INTEGER PRIMARY KEY AUTOINCREMENT, encryptDB INTEGER, lastUpdated TEXT, reservedValues INTEGER, smsGateway TEXT, smsResultSender TEXT, matomoID TEXT, matomoUrl TEXT);
INSERT INTO GeneralSetting (_id, encryptDB, lastUpdated, reservedValues, smsGateway, smsResultSender, matomoID, matomoUrl) SELECT _id, encryptDB, lastUpdated, reservedValues, numberSmsToSend, numberSmsConfirmation, null, null FROM GeneralSetting_Old;


# TODO Insert dataSync, metadataSync in Synchronization

DELETE GeneralSetting_Old;