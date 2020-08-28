# Adds displayDescription to TrackerImportConflict
ALTER TABLE TrackerImportConflict ADD COLUMN displayDescription TEXT;
UPDATE TrackerImportConflict SET displayDescription = conflict;