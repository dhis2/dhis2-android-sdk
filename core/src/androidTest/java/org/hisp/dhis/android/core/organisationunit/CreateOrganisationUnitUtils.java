package org.hisp.dhis.android.core.organisationunit;

import android.content.ContentValues;

public class CreateOrganisationUnitUtils {

    public static ContentValues createOrgUnit(long id, String uid) {
        ContentValues organisationUnit = new ContentValues();
        organisationUnit.put(OrganisationUnitModel.Columns.ID, id);
        organisationUnit.put(OrganisationUnitModel.Columns.UID, uid);
        organisationUnit.put(OrganisationUnitModel.Columns.CODE, "test_code");
        organisationUnit.put(OrganisationUnitModel.Columns.NAME, "test_name");
        organisationUnit.put(OrganisationUnitModel.Columns.DISPLAY_NAME, "test_display_name");
        organisationUnit.put(OrganisationUnitModel.Columns.CREATED, "test_created");
        organisationUnit.put(OrganisationUnitModel.Columns.LAST_UPDATED, "test_last_updated");
        organisationUnit.put(OrganisationUnitModel.Columns.SHORT_NAME, "test_short_name");
        organisationUnit.put(OrganisationUnitModel.Columns.DISPLAY_SHORT_NAME, "test_display_short_name");
        organisationUnit.put(OrganisationUnitModel.Columns.DESCRIPTION, "test_description");
        organisationUnit.put(OrganisationUnitModel.Columns.DISPLAY_DESCRIPTION, "test_display_description");
        organisationUnit.put(OrganisationUnitModel.Columns.PATH, "test_path");
        organisationUnit.put(OrganisationUnitModel.Columns.OPENING_DATE, "test_opening_date");
        organisationUnit.put(OrganisationUnitModel.Columns.CLOSED_DATE, "test_closed_date");
        organisationUnit.put(OrganisationUnitModel.Columns.LEVEL, "test_level");

        // foreign keys
        organisationUnit.putNull(OrganisationUnitModel.Columns.PARENT);

        return organisationUnit;
    }
}
