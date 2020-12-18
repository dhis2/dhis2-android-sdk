# Add organisationUnitId hash to TrackedEntityInstanceSync and EventSync
ALTER TABLE TrackedEntityInstanceSync ADD COLUMN organisationUnitIdsHash INTEGER;
ALTER TABLE EventSync ADD COLUMN organisationUnitIdsHash INTEGER;