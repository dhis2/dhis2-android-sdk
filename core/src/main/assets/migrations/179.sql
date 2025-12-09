# Add workingListsHash column to TrackedEntityInstanceSync and EventSync tables (ANDROSDK-2208)

ALTER TABLE TrackedEntityInstanceSync ADD COLUMN workingListsHash INTEGER;
ALTER TABLE EventSync ADD COLUMN workingListsHash INTEGER;