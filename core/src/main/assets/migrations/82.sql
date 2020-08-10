# Adds pattern to TrackedEntityAttributeReservedValue
ALTER TABLE TrackedEntityAttributeReservedValue ADD COLUMN pattern TEXT;
update TrackedEntityAttributeReservedValue set pattern = key;