# Add data set custom forms: DataSet.formType, DataSet.dataEntryForm and the DataEntryForm table (B2 spike)
CREATE TABLE DataEntryForm(uid TEXT NOT NULL, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, style TEXT, htmlCode TEXT, format INTEGER, PRIMARY KEY(uid));
ALTER TABLE DataSet ADD COLUMN formType TEXT;
ALTER TABLE DataSet ADD COLUMN dataEntryForm TEXT;
