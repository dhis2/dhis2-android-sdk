# Add organisationUnitId hash to TrackedEntityInstanceSync and EventSync
ALTER TABLE TrackedEntityInstanceSync ADD COLUMN organisationUnitIdsHash TEXT;
ALTER TABLE EventSync ADD COLUMN organisationUnitIdsHash TEXT;