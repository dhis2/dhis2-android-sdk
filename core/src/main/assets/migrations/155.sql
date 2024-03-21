# Add syncState to TrackedEntityDataValue and TrackedEntityAttributeValue (ANDROSDK-1564)

ALTER TABLE TrackedEntityDataValue ADD COLUMN syncState TEXT;
UPDATE TrackedEntityDataValue SET syncState = (SELECT CASE ev.syncState WHEN 'SYNCED' THEN 'SYNCED' ELSE 'TO_UPDATE' END FROM Event ev WHERE ev.uid = event);

ALTER TABLE TrackedEntityAttributeValue ADD COLUMN syncState TEXT;
UPDATE TrackedEntityAttributeValue SET syncState = (SELECT CASE tei.syncState WHEN 'SYNCED' THEN 'SYNCED' ELSE 'TO_UPDATE' END FROM TrackedEntityInstance tei WHERE tei.uid = trackedEntityInstance);
