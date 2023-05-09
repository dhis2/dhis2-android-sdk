# Remove UserCredentials model (ANDROSDK-1584);

ALTER TABLE User ADD COLUMN username TEXT;
UPDATE User SET username = (SELECT username FROM UserCredentials) WHERE uid IS NOT NULL;
DROP TABLE IF EXISTS UserCredentials;