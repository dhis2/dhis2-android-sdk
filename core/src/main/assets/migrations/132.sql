# Remove organisationUnit FK in UserOrganisationUnit (ANDROSDK-1414);

ALTER TABLE UserOrganisationUnit RENAME TO UserOrganisationUnit_Old;
CREATE TABLE UserOrganisationUnit (_id INTEGER PRIMARY KEY AUTOINCREMENT, user TEXT NOT NULL, organisationUnit TEXT NOT NULL, organisationUnitScope TEXT NOT NULL, root INTEGER, userAssigned INTEGER, FOREIGN KEY (user) REFERENCES User (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (organisationUnitScope, user, organisationUnit));
INSERT INTO UserOrganisationUnit (_id, user, organisationUnit, organisationUnitScope, root, userAssigned) SELECT _id, user, organisationUnit, organisationUnitScope, root, userAssigned FROM UserOrganisationUnit_Old;
DROP TABLE IF EXISTS UserOrganisationUnit_Old;
