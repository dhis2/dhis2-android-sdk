ALTER TABLE CategoryOption ADD COLUMN accessDataWrite INTEGER;
UPDATE CategoryOption SET accessDataWrite = 1;