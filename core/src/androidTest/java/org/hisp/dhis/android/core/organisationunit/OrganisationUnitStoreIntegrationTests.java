package org.hisp.dhis.android.core.organisationunit;

import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
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

    public static ContentValues create(long id, String uid) {
        ContentValues organisationUnit = new ContentValues();
        organisationUnit.put(OrganisationUnitContract.Columns.ID, id);
        organisationUnit.put(OrganisationUnitContract.Columns.UID, uid);
        organisationUnit.put(OrganisationUnitContract.Columns.CODE, "test_code");
        organisationUnit.put(OrganisationUnitContract.Columns.NAME, "test_name");
        organisationUnit.put(OrganisationUnitContract.Columns.DISPLAY_NAME, "test_display_name");
        organisationUnit.put(OrganisationUnitContract.Columns.CREATED, "test_created");
        organisationUnit.put(OrganisationUnitContract.Columns.LAST_UPDATED, "test_last_updated");
        organisationUnit.put(OrganisationUnitContract.Columns.SHORT_NAME, "test_short_name");
        organisationUnit.put(OrganisationUnitContract.Columns.DISPLAY_SHORT_NAME, "test_display_short_name");
        organisationUnit.put(OrganisationUnitContract.Columns.DESCRIPTION, "test_description");
        organisationUnit.put(OrganisationUnitContract.Columns.DISPLAY_DESCRIPTION, "test_display_description");
        organisationUnit.put(OrganisationUnitContract.Columns.PATH, "test_path");
        organisationUnit.put(OrganisationUnitContract.Columns.OPENING_DATE, "test_opening_date");
        organisationUnit.put(OrganisationUnitContract.Columns.CLOSED_DATE, "test_closed_date");
        organisationUnit.put(OrganisationUnitContract.Columns.LEVEL, "test_level");

        // foreign keys
        organisationUnit.putNull(OrganisationUnitContract.Columns.PARENT);

        return organisationUnit;
    }

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

    @Test
    public void delete_shouldDeleteAllRows() {
        ContentValues organisationUnitOne = create(1L, "test_organisation_unit_one");
        ContentValues organisationUnitTwo = create(2L, "test_organisation_unit_two");

        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnitOne);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnitTwo);

        int deleted = organisationUnitStore.delete();
        Cursor cursor = database().query(DbOpenHelper.Tables.ORGANISATION_UNIT,
                null, null, null, null, null, null);

        assertThat(deleted).isEqualTo(2);
        assertThatCursor(cursor).isExhausted();
    }
}
