CREATE TABLE AuthenticatedUser_temp (_id INTEGER PRIMARY KEY AUTOINCREMENT, user TEXT NOT NULL UNIQUE, hash TEXT, FOREIGN KEY (user) REFERENCES User (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED);
INSERT INTO AuthenticatedUser_temp (_id, user, hash) SELECT _id, user, hash FROM AuthenticatedUser;
DROP TABLE IF EXISTS AuthenticatedUser;
ALTER TABLE AuthenticatedUser_temp RENAME TO AuthenticatedUser;