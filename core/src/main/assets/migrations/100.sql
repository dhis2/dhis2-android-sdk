# Fix for Nigeria issue ANDROSDK-1396
UPDATE Event SET state = 'TO_UPDATE' WHERE state = 'UPLOADING';

UPDATE Enrollment SET state = 'TO_UPDATE' WHERE state = 'UPLOADING' OR uid IN (SELECT enrollment FROM Event WHERE state IN ('TO_POST', 'TO_UPDATE'));
UPDATE Enrollment SET state = 'ERROR' WHERE uid IN (SELECT enrollment FROM Event WHERE state IN ('ERROR', 'WARNING'));

UPDATE TrackedEntityInstance SET state = 'TO_UPDATE' WHERE state = 'UPLOADING' OR uid IN (SELECT trackedEntityInstance FROM Enrollment WHERE state IN ('TO_POST', 'TO_UPDATE'));
UPDATE TrackedEntityInstance SET state = 'ERROR' WHERE uid IN (SELECT trackedEntityInstance FROM Enrollment WHERE state IN ('ERROR', 'WARNING'));