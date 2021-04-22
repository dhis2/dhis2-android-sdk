# Create filter settings for remote filters configuration
CREATE TABLE FilterSetting (_id INTEGER PRIMARY KEY AUTOINCREMENT, scope TEXT, filterType TEXT, uid TEXT, sort INTEGER, filter INTEGER);
CREATE TABLE CompletionSpinner (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT, visible INTEGER);
