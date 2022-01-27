# Mark pending elements as TO_UPDATE (ANDROSDK-1473)

# Event relationships
UPDATE Event SET aggregatedSyncState = 'TO_UPDATE' WHERE uid IN (SELECT ri.event FROM RelationshipItem ri INNER JOIN Relationship r ON ri.relationship = r.uid WHERE r.syncState IN ('TO_POST', 'TO_UPDATE') AND ri.relationshipItemType = 'FROM') AND aggregatedSyncState NOT IN ('RELATIONSHIP', 'ERROR', 'WARNING');
UPDATE Enrollment SET aggregatedSyncState = 'TO_UPDATE' WHERE uid IN (SELECT ev.enrollment FROM Event ev INNER JOIN RelationshipItem ri ON ev.uid = ri.event INNER JOIN Relationship r ON ri.relationship = r.uid WHERE r.syncState IN ('TO_POST', 'TO_UPDATE') AND ri.relationshipItemType = 'FROM') AND aggregatedSyncState NOT IN ('RELATIONSHIP', 'ERROR', 'WARNING');
UPDATE TrackedEntityInstance SET aggregatedSyncState = 'TO_UPDATE' WHERE uid IN (SELECT en.trackedEntityInstance FROM Enrollment en INNER JOIN Event ev ON en.uid = ev.enrollment INNER JOIN RelationshipItem ri ON ev.uid = ri.event INNER JOIN Relationship r ON ri.relationship = r.uid WHERE r.syncState IN ('TO_POST', 'TO_UPDATE') AND ri.relationshipItemType = 'FROM') AND aggregatedSyncState NOT IN ('RELATIONSHIP', 'ERROR', 'WARNING');

# Enrollment relationships
UPDATE Enrollment SET aggregatedSyncState = 'TO_UPDATE' WHERE uid IN (SELECT ri.enrollment FROM RelationshipItem ri INNER JOIN Relationship r ON ri.relationship = r.uid WHERE r.syncState IN ('TO_POST', 'TO_UPDATE') AND ri.relationshipItemType = 'FROM') AND aggregatedSyncState NOT IN ('RELATIONSHIP', 'ERROR', 'WARNING');
UPDATE TrackedEntityInstance SET aggregatedSyncState = 'TO_UPDATE' WHERE uid IN (SELECT en.trackedEntityInstance FROM Enrollment en INNER JOIN RelationshipItem ri ON en.uid = ri.enrollment INNER JOIN Relationship r ON ri.relationship = r.uid WHERE r.syncState IN ('TO_POST', 'TO_UPDATE') AND ri.relationshipItemType = 'FROM') AND aggregatedSyncState NOT IN ('RELATIONSHIP', 'ERROR', 'WARNING');

# TrackedEntityInstance relationships
UPDATE TrackedEntityInstance SET aggregatedSyncState = 'TO_UPDATE' WHERE uid IN (SELECT ri.trackedEntityInstance FROM RelationshipItem ri INNER JOIN Relationship r ON ri.relationship = r.uid WHERE r.syncState IN ('TO_POST', 'TO_UPDATE') AND ri.relationshipItemType = 'FROM') AND aggregatedSyncState NOT IN ('RELATIONSHIP', 'ERROR', 'WARNING');
