# Add foreign key for visualization(uid)

ALTER TABLE AnalyticsDhisVisualization RENAME TO AnalyticsDhisVisualization_Old;
CREATE TABLE AnalyticsDhisVisualization (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL, scopeUid TEXT, scope TEXT, groupUid TEXT, groupName TEXT, timestamp TEXT, FOREIGN KEY (uid) REFERENCES Visualization (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO AnalyticsDhisVisualization (_id, uid, scopeUid, scope, groupUid, groupName, timestamp) SELECT _id, uid, scopeUid, scope, groupUid, groupName, timestamp FROM AnalyticsDhisVisualization_Old;
DROP TABLE IF EXISTS AnalyticsDhisVisualization_Old;