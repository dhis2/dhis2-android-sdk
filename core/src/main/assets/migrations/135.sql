# Add Add built-in map layers (ANDROSDK-1605);

CREATE TABLE MapLayer (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, name TEXT NOT NULL, displayName TEXT NOT NULL, external INTEGER, style TEXT, imageUrl TEXT NOT NULL, subdomains TEXT, subdomainToken TEXT);
CREATE TABLE MapLayerImageryProvider (_id INTEGER PRIMARY KEY AUTOINCREMENT, mapLayer TEXT NOT NULL, attribution TEXT NOT NULL, coverageAreas TEXT);
