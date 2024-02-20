# Add TrackerVisualization to ASWA (ANDROSDK-1811)

ALTER TABLE AnalyticsDhisVisualization RENAME TO AnalyticsDhisVisualization_Old;
CREATE TABLE AnalyticsDhisVisualization (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL, scopeUid TEXT, scope TEXT, groupUid TEXT, groupName TEXT, timestamp TEXT, name TEXT, type TEXT NOT NULL);
INSERT INTO AnalyticsDhisVisualization(_id, uid, scopeUid, scope, groupUid, groupName, timestamp, name, type) SELECT _id, uid, scopeUid, scope, groupUid, groupName, timestamp, name, 'VISUALIZATION' FROM AnalyticsDhisVisualization_Old;
DROP TABLE IF EXISTS AnalyticsDhisVisualization_Old;