package org.hisp.dhis.android.core.organisationunit;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.android.core.database.AbsProviderTestCase;
import org.hisp.dhis.android.core.database.DbOpenHelper;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract.Columns;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.database.CursorAssert.assertThatCursor;

public class OrganisationUnitsContractIntegrationTests extends AbsProviderTestCase {
    public static final String[] ORGANISATION_UNIT_PROJECTION = {
            Columns.ID,
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.SHORT_NAME,
            Columns.DISPLAY_SHORT_NAME,
            Columns.DESCRIPTION,
            Columns.DISPLAY_DESCRIPTION,
            Columns.PATH,
            Columns.OPENING_DATE,
            Columns.CLOSED_DATE,
            Columns.PARENT,
            Columns.LEVEL
    };

    public static ContentValues create() {
        ContentValues organisationUnit = new ContentValues();
        organisationUnit.put(Columns.ID, 1L);
        organisationUnit.put(Columns.UID, "test_uid");
        organisationUnit.put(Columns.CODE, "test_code");
        organisationUnit.put(Columns.NAME, "test_name");
        organisationUnit.put(Columns.DISPLAY_NAME, "test_display_name");
        organisationUnit.put(Columns.CREATED, "test_created");
        organisationUnit.put(Columns.LAST_UPDATED, "test_last_updated");
        organisationUnit.put(Columns.SHORT_NAME, "test_short_name");
        organisationUnit.put(Columns.DISPLAY_SHORT_NAME, "test_display_short_name");
        organisationUnit.put(Columns.DESCRIPTION, "test_description");
        organisationUnit.put(Columns.DISPLAY_DESCRIPTION, "test_display_description");
        organisationUnit.put(Columns.PATH, "test_path");
        organisationUnit.put(Columns.OPENING_DATE, "test_opening_date");
        organisationUnit.put(Columns.CLOSED_DATE, "test_closed_date");
        organisationUnit.put(Columns.LEVEL, "test_level");

        // foreign keys
        organisationUnit.putNull(Columns.PARENT);

        return organisationUnit;
    }

    private ContentValues organisationUnit;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // test data
        organisationUnit = create();
    }

    public void testGetType_shouldReturnCorrectMimeTypes() {
        assertThat(getProvider().getType(OrganisationUnitContract.organisationUnits()))
                .isEqualTo(OrganisationUnitContract.CONTENT_TYPE_DIR);
        assertThat(getProvider().getType(OrganisationUnitContract.organisationUnits(1L)))
                .isEqualTo(OrganisationUnitContract.CONTENT_TYPE_ITEM);
    }

    public void testInsert_shouldPersistRow() {
        Uri itemUri = getMockContentResolver().insert(OrganisationUnitContract
                .organisationUnits(), organisationUnit);

        Cursor cursor = database().query(DbOpenHelper.Tables.ORGANISATION_UNIT,
                ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);

        assertThat(ContentUris.parseId(itemUri)).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(ORGANISATION_UNIT_PROJECTION, organisationUnit).isExhausted();
    }

    public void testInsert_shouldNotThrowExceptionOnExistingId() {
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);
        getProvider().insert(OrganisationUnitContract.organisationUnits(), organisationUnit);
    }

    public void testUpdate_shouldUpdateRow() {
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

        organisationUnit.put(Columns.DESCRIPTION, "another_description");
        int updatedCount = getProvider().update(OrganisationUnitContract.organisationUnits(1L),
                organisationUnit, null, null);

        Cursor cursor = database().query(DbOpenHelper.Tables.ORGANISATION_UNIT,
                ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);

        assertThat(updatedCount).isEqualTo(1);
        assertThatCursor(cursor).hasRow(ORGANISATION_UNIT_PROJECTION, organisationUnit).isExhausted();
    }

    public void testUpdateByUriWithId_shouldUpdateRow() {
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

        organisationUnit.put(Columns.DESCRIPTION, "another_description");
        int updatedItemsCount = getProvider().update(OrganisationUnitContract.organisationUnits(1L),
                organisationUnit, null, null);

        Cursor cursor = database().query(DbOpenHelper.Tables.ORGANISATION_UNIT,
                ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);
        assertThat(updatedItemsCount).isEqualTo(1);
        assertThatCursor(cursor)
                .hasRow(ORGANISATION_UNIT_PROJECTION, organisationUnit)
                .isExhausted();
    }

    public void testDelete_shouldDeleteRow() {
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

        int deletedCount = getProvider().delete(OrganisationUnitContract.organisationUnits(),
                Columns.ID + " = ?", new String[]{String.valueOf(1L)});

        Cursor cursor = database().query(DbOpenHelper.Tables.ORGANISATION_UNIT,
                ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);
        assertThat(deletedCount).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    public void testDelete_shouldDeleteSubtree() {
        ContentValues organisationUnitRoot = new ContentValues(organisationUnit);
        ContentValues organisationUnitChildOne = new ContentValues(organisationUnit);
        ContentValues organisationUnitChildTwo = new ContentValues(organisationUnit);
        ContentValues organisationUnitGrandChild = new ContentValues(organisationUnit);

        organisationUnitRoot.put(Columns.UID, "test_uid_root");

        organisationUnitChildOne.put(Columns.ID, 2L);
        organisationUnitChildOne.put(Columns.UID, "test_uid_child_one");
        organisationUnitChildOne.put(Columns.PARENT, "test_uid_root");

        organisationUnitChildTwo.put(Columns.ID, 3L);
        organisationUnitChildTwo.put(Columns.UID, "test_uid_child_two");
        organisationUnitChildTwo.put(Columns.PARENT, "test_uid_root");

        organisationUnitGrandChild.put(Columns.ID, 4L);
        organisationUnitGrandChild.put(Columns.UID, "test_uid_grand_child");
        organisationUnitGrandChild.put(Columns.PARENT, "test_uid_child_two");

        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnitRoot);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnitChildOne);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnitChildTwo);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnitGrandChild);

        // removing root node should trigger removal of all children
        int deleteCount = getProvider().delete(
                OrganisationUnitContract.organisationUnits(1L), null, null);

        assertThat(deleteCount).isEqualTo(1);

        Cursor cursor = database().query(DbOpenHelper.Tables.ORGANISATION_UNIT,
                ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    public void testDeleteByUriWithId_shouldDeleteRow() {
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

        int deletedCount = getProvider().delete(
                OrganisationUnitContract.organisationUnits(1L), null, null);

        Cursor cursor = database().query(DbOpenHelper.Tables.ORGANISATION_UNIT,
                ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);
        assertThat(deletedCount).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    public void testQuery_shouldReturnRows() {
        ContentValues organisationUnitOne = new ContentValues(organisationUnit);
        ContentValues organisationUnitTwo = new ContentValues(organisationUnit);

        // overriding primary and secondary keys in order to avoid conflict
        organisationUnitOne.put(Columns.ID, 1L);
        organisationUnitOne.put(Columns.UID, "test_uid_one");

        organisationUnitTwo.put(Columns.ID, 2L);
        organisationUnitTwo.put(Columns.UID, "test_uid_two");

        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnitOne);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnitTwo);

        Cursor cursor = getProvider().query(OrganisationUnitContract.organisationUnits(),
                ORGANISATION_UNIT_PROJECTION, null, null, null);
        assertThatCursor(cursor)
                .hasRow(ORGANISATION_UNIT_PROJECTION, organisationUnitOne)
                .hasRow(ORGANISATION_UNIT_PROJECTION, organisationUnitTwo)
                .isExhausted();
    }
}
