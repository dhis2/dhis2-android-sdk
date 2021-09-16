# Add deleted to TrackedEntityAttributeValue

ALTER TABLE TrackedEntityAttributeValue ADD COLUMN deleted INTEGER;
ALTER TABLE TrackedEntityDataValue ADD COLUMN deleted INTEGER;
