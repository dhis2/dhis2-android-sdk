# Related to ANDROSDK-831
ALTER TABLE TrackedEntityAttribute ADD COLUMN displayFormName TEXT;
UPDATE TrackedEntityAttribute SET displayFormName=displayName;