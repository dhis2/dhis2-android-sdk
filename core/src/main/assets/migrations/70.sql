# State and deleted columns in Relationships to track changes and delete them individually
ALTER TABLE Relationship ADD COLUMN state TEXT;
ALTER TABLE Relationship ADD COLUMN deleted INTEGER;
UPDATE Relationship SET state = 'SYNCED', deleted = 0;