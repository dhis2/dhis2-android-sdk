# Migrate settings app definition
CREATE TABLE FilterSetting (_id INTEGER PRIMARY KEY AUTOINCREMENT, scope TEXT, filterType TEXT, uid TEXT, sort TEXT, filter TEXT);