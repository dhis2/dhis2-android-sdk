# Adds accessDataWrite to TrackedEntityType
ALTER TABLE TrackedEntityType ADD COLUMN accessDataWrite INTEGER;
UPDATE TrackedEntityType SET accessDataWrite = 1;
