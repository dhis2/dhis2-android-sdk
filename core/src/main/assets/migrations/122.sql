# Add messageOfTheDay (ANDROSDK-1500); add ProgramConfigurationSetting (ANDROSDK-1501)

ALTER TABLE GeneralSetting ADD COLUMN messageOfTheDay TEXT;

CREATE TABLE ProgramConfigurationSetting (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT, completionSpinner INTEGER, optionalSearch INTEGER);
INSERT INTO ProgramConfigurationSetting(_id, uid, completionSpinner) SELECT _id, uid, visible FROM CompletionSpinner;

DROP TABLE IF EXISTS CompletionSpinner;