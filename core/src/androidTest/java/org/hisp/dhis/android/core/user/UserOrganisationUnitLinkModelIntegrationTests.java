package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkContract.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class UserOrganisationUnitLinkModelIntegrationTests {
    private static final long ID = 2L;
    private static final String USER = "test_user_uid";
    private static final String ORGANISATION_UNIT = "test_organisation_unit_uid";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.USER, Columns.ORGANISATION_UNIT,
        });

        matrixCursor.addRow(new Object[]{
                ID, USER, ORGANISATION_UNIT
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
                        .build();

        ContentValues contentValues = userOrganisationUnitLinkModel.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.USER)).isEqualTo(USER);
        assertThat(contentValues.getAsString(Columns.ORGANISATION_UNIT)).isEqualTo(ORGANISATION_UNIT);
    }
}
