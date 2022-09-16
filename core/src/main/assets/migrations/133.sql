# Modify user credentials model (ANDROSDK-1583);

ALTER TABLE UserCredentials RENAME TO UserCredentials_Old;
CREATE TABLE UserCredentials (_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, user TEXT NOT NULL UNIQUE, FOREIGN KEY (user) REFERENCES User (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO UserCredentials (_id, username, user) SELECT _id, username, user FROM UserCredentials_Old;
DROP TABLE IF EXISTS UserCredentials_Old;