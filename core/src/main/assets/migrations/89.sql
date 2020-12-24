# Add organisationUnitId hash to TrackedEntityInstanceSync and EventSync
DROP TABLE TrackedEntityInstanceSync;
DROP TABLE EventSync;
CREATE TABLE TrackedEntityInstanceSync (_id INTEGER PRIMARY KEY AUTOINCREMENT, program TEXT, organisationUnitIdsHash INTEGER, downloadLimit INTEGER NOT NULL, lastUpdated TEXT NOT NULL, FOREIGN KEY (program) REFERENCES Program (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (program, organisationUnitIdsHash));
CREATE TABLE EventSync (_id INTEGER PRIMARY KEY AUTOINCREMENT, program TEXT, organisationUnitIdsHash INTEGER, downloadLimit INTEGER NOT NULL, lastUpdated TEXT NOT NULL, FOREIGN KEY (program) REFERENCES Program (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (program, organisationUnitIdsHash));
