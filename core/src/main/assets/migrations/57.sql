# Add foreign key for dataElement(uid)
ALTER TABLE TrackedEntityDataValue RENAME TO TrackedEntityDataValue_Old;
CREATE TABLE TrackedEntityDataValue (_id INTEGER PRIMARY KEY AUTOINCREMENT, event TEXT NOT NULL, dataElement TEXT NOT NULL, storedBy TEXT, value TEXT, created TEXT, lastUpdated TEXT, providedElsewhere INTEGER, FOREIGN KEY (event) REFERENCES Event (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (dataElement) REFERENCES DataElement (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO TrackedEntityDataValue (_id, event, dataElement, storedBy, value, created, lastUpdated, providedElsewhere) SELECT _id, event, dataElement, storedBy, value, created, lastUpdated, providedElsewhere FROM TrackedEntityDataValue_Old;
DROP TABLE IF EXISTS TrackedEntityDataValue_Old;

  # Add index for performance
CREATE UNIQUE INDEX event_data_element ON TrackedEntityDataValue(event, dataElement);