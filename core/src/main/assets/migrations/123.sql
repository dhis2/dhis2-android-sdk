# Include SMS config in database (ANDROSDK-1370)

CREATE TABLE SMSConfig (_id INTEGER PRIMARY KEY AUTOINCREMENT, key TEXT NOT NULL UNIQUE, value TEXT);
CREATE TABLE SmsMetadataId (_id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, uid TEXT);