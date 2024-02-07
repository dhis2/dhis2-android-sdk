# Add TrackerVisualization model (ANDROSDK-1810)

CREATE TABLE TrackerVisualization(_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, code TEXT, name TEXT, displayName TEXT, created TEXT, lastUpdated TEXT, description TEXT, displayDescription TEXT, type TEXT, outputType TEXT, program TEXT, programStage TEXT, trackedEntityType TEXT);
CREATE TABLE TrackerVisualizationDimension(_id INTEGER PRIMARY KEY AUTOINCREMENT, trackerVisualization TEXT NOT NULL, position TEXT NOT NULL, dimension TEXT NOT NULL, dimensionType TEXT, program TEXT, programStage TEXT, items TEXT, filter TEXT, repetition TEXT, FOREIGN KEY (trackerVisualization) REFERENCES TrackerVisualization (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
