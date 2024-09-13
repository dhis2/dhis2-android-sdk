# Add trackerDataView to RelationshipConstraint (ANDROSDK-1695)

ALTER TABLE RelationshipConstraint ADD COLUMN trackerDataViewAttributes TEXT;
ALTER TABLE RelationshipConstraint ADD COLUMN trackerDataViewDataElements TEXT;