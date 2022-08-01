# Add restriction (ANDROSDK-1523);

ALTER TABLE CategoryOptionOrganisationUnitLink RENAME TO CategoryOptionOrganisationUnitLink_Old;
CREATE TABLE CategoryOptionOrganisationUnitLink (_id INTEGER PRIMARY KEY AUTOINCREMENT, categoryOption TEXT NOT NULL, organisationUnit TEXT, restriction TEXT, FOREIGN KEY (categoryOption) REFERENCES CategoryOption (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY (organisationUnit) REFERENCES OrganisationUnit (uid) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, UNIQUE (categoryOption, organisationUnit));
INSERT INTO CategoryOptionOrganisationUnitLink (_id, organisationUnit, categoryOption) SELECT _id, organisationUnit, categoryOption FROM CategoryOptionOrganisationUnitLink_Old;
DROP TABLE IF EXISTS  CategoryOptionOrganisationUnitLink_Old;