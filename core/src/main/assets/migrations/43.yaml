ALTER TABLE Program ADD COLUMN featureType TEXT;
UPDATE Program set featureType = CASE WHEN captureCoordinates THEN 'POINT' ELSE 'NONE' END;