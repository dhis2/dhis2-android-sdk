package org.hisp.dhis.android.core.organisationunit;

import android.database.Cursor;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class OrganisationUnitStoreIntegrationTests extends AbsStoreTestCase {
    public static final String[] ORGANISATION_UNIT_PROJECTION = {
            OrganisationUnitContract.Columns.UID,
            OrganisationUnitContract.Columns.CODE,
            OrganisationUnitContract.Columns.NAME,
            OrganisationUnitContract.Columns.DISPLAY_NAME,
            OrganisationUnitContract.Columns.CREATED,
            OrganisationUnitContract.Columns.LAST_UPDATED,
            OrganisationUnitContract.Columns.SHORT_NAME,
            OrganisationUnitContract.Columns.DISPLAY_SHORT_NAME,
            OrganisationUnitContract.Columns.DESCRIPTION,
            OrganisationUnitContract.Columns.DISPLAY_DESCRIPTION,
            OrganisationUnitContract.Columns.PATH,
            OrganisationUnitContract.Columns.OPENING_DATE,
            OrganisationUnitContract.Columns.CLOSED_DATE,
            OrganisationUnitContract.Columns.PARENT,
            OrganisationUnitContract.Columns.LEVEL
    };

    private OrganisationUnitStore organisationUnitStore;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        organisationUnitStore = new OrganisationUnitStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        Date date = new Date();

        long rowId = organisationUnitStore.insert(
                "test_organisation_unit_uid",
                "test_organisation_unit_code",
                "test_organisation_unit_name",
                "test_organisation_unit_display_name",
                date, date,
                "test_organisation_unit_short_name",
                "test_organisation_unit_display_short_name",
                "test_organisation_unit_description",
                "test_organisation_unit_display_description",
                "test_organisation_unit_path",
                date, date, null, 11
        );

        Cursor cursor = database().query(DbOpenHelper.Tables.ORGANISATION_UNIT,
                ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        "test_organisation_unit_uid",
                        "test_organisation_unit_code",
                        "test_organisation_unit_name",
                        "test_organisation_unit_display_name",
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        "test_organisation_unit_short_name",
                        "test_organisation_unit_display_short_name",
                        "test_organisation_unit_description",
                        "test_organisation_unit_display_description",
                        "test_organisation_unit_path",
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        null, 11
                )
                .isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        organisationUnitStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
