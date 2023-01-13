# Remove UserCredentials model (ANDROSDK-1584);

ALTER TABLE User ADD COLUMN username TEXT;
UPDATE TABLE User SET username = (SELECT username FROM UserCredentials);
DELETE TABLE UserCredentials;