# Adds displayDescription to TrackerImportConflict
ALTER TABLE TrackerImportConflict ADD COLUMN displayDescription TEXT;
ALTER TABLE TrackerImportConflict ADD COLUMN trackedEntityAttribute TEXT;
ALTER TABLE TrackerImportConflict ADD COLUMN dataElement TEXT;
UPDATE TrackerImportConflict SET displayDescription = conflict;