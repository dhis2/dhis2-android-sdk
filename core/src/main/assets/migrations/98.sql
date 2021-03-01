# Adds column ValidationRule.organisationUnitLevels, which was missing in migration 76
ALTER TABLE ValidationRule ADD COLUMN organisationUnitLevels TEXT;