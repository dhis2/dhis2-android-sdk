# Add Add built-in map layers (ANDROSDK-1605);

CREATE TABLE MapLayer (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, name TEXT NOT NULL, displayName TEXT NOT NULL, external INTEGER, mapLayerPosition TEXT NOT NULL, style TEXT, imageUrl TEXT NOT NULL, subdomains TEXT, subdomainPlaceholder TEXT);
CREATE TABLE MapLayerImageryProvider (_id INTEGER PRIMARY KEY AUTOINCREMENT, mapLayer TEXT NOT NULL, attribution TEXT NOT NULL, coverageAreas TEXT, FOREIGN KEY (mapLayer) REFERENCES MapLayer (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
