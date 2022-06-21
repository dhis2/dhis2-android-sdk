# Manage program ownership (ANDROSDK-1545);

CREATE TABLE ProgramOwner (_id INTEGER PRIMARY KEY AUTOINCREMENT, program TEXT NOT NULL, trackedEntityInstance TEXT NOT NULL, ownerOrgUnit TEXT NOT NULL, syncState TEXT, FOREIGN KEY (program) REFERENCES Program (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (trackedEntityInstance) REFERENCES TrackedEntityInstance (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (ownerOrgUnit) REFERENCES OrganisationUnit (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (program, trackedEntityInstance));
INSERT INTO ProgramOwner (program, trackedEntityInstance, ownerOrgUnit, syncState) SELECT program, trackedEntityInstance, organisationUnit, 'SYNCED' FROM Enrollment;
