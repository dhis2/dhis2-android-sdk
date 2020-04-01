# Add foreign key for TrackedEntityInstance(uid)
ALTER TABLE TrackedEntityAttributeValue RENAME TO TrackedEntityAttributeValue_Old;
CREATE TABLE TrackedEntityAttributeValue (_id INTEGER PRIMARY KEY AUTOINCREMENT, created TEXT, lastUpdated TEXT, value TEXT, trackedEntityAttribute TEXT NOT NULL, trackedEntityInstance TEXT NOT NULL, FOREIGN KEY (trackedEntityAttribute) REFERENCES trackedEntityAttribute (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (trackedEntityInstance) REFERENCES TrackedEntityInstance (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO TrackedEntityAttributeValue (_id, created, lastUpdated, value, trackedEntityAttribute, trackedEntityInstance) SELECT _id, created, lastUpdated, value, trackedEntityAttribute, trackedEntityInstance FROM TrackedEntityAttributeValue_Old;
DROP TABLE IF EXISTS TrackedEntityAttributeValue_Old;

  # Add index for performance
CREATE UNIQUE INDEX tracked_entity_instance_attribute ON TrackedEntityAttributeValue(trackedEntityInstance, trackedEntityAttribute);