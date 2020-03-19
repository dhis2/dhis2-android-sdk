ALTER TABLE DataSetOrganisationUnitLink RENAME TO DataSetOrganisationUnitLink_Old;
CREATE TABLE DataSetOrganisationUnitLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, dataSet TEXT NOT NULL, organisationUnit TEXT NOT NULL, FOREIGN KEY (dataSet) REFERENCES DataSet (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (organisationUnit) REFERENCES OrganisationUnit (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (organisationUnit, dataSet));
INSERT INTO DataSetOrganisationUnitLink (_id, dataSet, organisationUnit) SELECT _id, dataSet, organisationUnit FROM DataSetOrganisationUnitLink_Old;
DROP TABLE DataSetOrganisationUnitLink_Old;
ALTER TABLE UserOrganisationUnit RENAME TO UserOrganisationUnit_Old;
CREATE TABLE UserOrganisationUnit (_id INTEGER PRIMARY KEY AUTOINCREMENT, user TEXT NOT NULL, organisationUnit TEXT NOT NULL, organisationUnitScope TEXT NOT NULL, root INTEGER, FOREIGN KEY (user) REFERENCES User (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (organisationUnit) REFERENCES OrganisationUnit (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (organisationUnitScope, user, organisationUnit));
INSERT INTO UserOrganisationUnit (_id, user, organisationUnit, organisationUnitScope, root) SELECT _id, user, organisationUnit, organisationUnitScope, root FROM UserOrganisationUnit_Old;
DROP TABLE UserOrganisationUnit_Old;