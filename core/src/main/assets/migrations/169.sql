# Add disableDataElementAutoGrouping and optionGroup items to Section table (ANDROSDK-1935)

ALTER TABLE Section ADD COLUMN disableDataElementAutoGroup INTEGER;
ALTER TABLE Section ADD COLUMN pivotMode TEXT;
ALTER TABLE Section ADD COLUMN pivotedCategory TEXT;
ALTER TABLE Section ADD COLUMN afterSectionText TEXT;
ALTER TABLE Section ADD COLUMN beforeSectionText TEXT;
