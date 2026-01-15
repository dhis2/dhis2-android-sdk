ALTER TABLE TrackedEntityAttribute ADD COLUMN preferredSearchOperator TEXT;
ALTER TABLE TrackedEntityAttribute ADD COLUMN blockedSearchOperators TEXT;
ALTER TABLE TrackedEntityAttribute ADD COLUMN minCharactersToSearch INTEGER;
