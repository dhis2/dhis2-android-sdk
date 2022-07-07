# Add TrackedEntityAttributeLegendSetLink (ANDROSDK-1546);

CREATE TABLE TrackedEntityAttributeLegendSetLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, trackedEntityAttribute TEXT NOT NULL, legendSet TEXT NOT NULL, sortOrder INTEGER, FOREIGN KEY (trackedEntityAttribute) REFERENCES TrackedEntityAttribute (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (legendSet) REFERENCES LegendSet (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (trackedEntityAttribute, legendSet));
