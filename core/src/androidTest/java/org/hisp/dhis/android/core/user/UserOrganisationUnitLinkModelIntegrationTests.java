package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class UserOrganisationUnitLinkModelIntegrationTests {
    private static final long ID = 2L;
    private static final String USER = "test_user_uid";
    private static final String ORGANISATION_UNIT = "test_organisation_unit_uid";
    private static final String ORGANISATION_UNIT_SCOPE = "test_organisation_unit_scope";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                UserOrganisationUnitLinkModel.Columns.ID, UserOrganisationUnitLinkModel.Columns.USER, UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT, UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE
        });

        matrixCursor.addRow(new Object[]{
                ID, USER, ORGANISATION_UNIT, ORGANISATION_UNIT_SCOPE
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        UserOrganisationUnitLinkModel userOrganisationUnitLinkModel =
                UserOrganisationUnitLinkModel.create(matrixCursor);

        assertThat(userOrganisationUnitLinkModel.id()).isEqualTo(ID);
        assertThat(userOrganisationUnitLinkModel.user()).isEqualTo(USER);
        assertThat(userOrganisationUnitLinkModel.organisationUnit()).isEqualTo(ORGANISATION_UNIT);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        UserOrganisationUnitLinkModel userOrganisationUnitLinkModel =
                UserOrganisationUnitLinkModel.builder()
                        .id(ID)
                        .user(USER)
                        .organisationUnit(ORGANISATION_UNIT)
                        .organisationUnitScope(ORGANISATION_UNIT_SCOPE)
                        .build();

        ContentValues contentValues = userOrganisationUnitLinkModel.toContentValues();

        assertThat(contentValues.getAsLong(UserOrganisationUnitLinkModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(UserOrganisationUnitLinkModel.Columns.USER)).isEqualTo(USER);
        assertThat(contentValues.getAsString(UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT)).isEqualTo(ORGANISATION_UNIT);
        assertThat(contentValues.getAsString(UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT_SCOPE)).isEqualTo(ORGANISATION_UNIT_SCOPE);
    }
}
