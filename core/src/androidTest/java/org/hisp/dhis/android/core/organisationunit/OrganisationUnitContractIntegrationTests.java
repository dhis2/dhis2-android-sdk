package org.hisp.dhis.android.core.organisationunit;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;

import org.hisp.dhis.android.core.data.database.AbsProviderTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.DbTestUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract.Columns;
import org.hisp.dhis.android.core.user.UserContract;
import org.hisp.dhis.android.core.user.UserContractIntegrationTests;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkContractIntegrationTests;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class OrganisationUnitContractIntegrationTests extends AbsProviderTestCase {
    // using table as a prefix in order to avoid ambiguity in queries against joined tables
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

    public static ContentValues create(long id, String uid) {
        ContentValues organisationUnit = new ContentValues();
        organisationUnit.put(Columns.ID, id);
        organisationUnit.put(Columns.UID, uid);
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
        organisationUnit = create(1L, "test_uid");
    }

    public void testGetType_shouldReturnCorrectMimeTypes() {
        assertThat(getProvider().getType(OrganisationUnitContract.organisationUnits()))
                .isEqualTo(OrganisationUnitContract.CONTENT_TYPE_DIR);
        assertThat(getProvider().getType(OrganisationUnitContract.organisationUnits(1L)))
                .isEqualTo(OrganisationUnitContract.CONTENT_TYPE_ITEM);
        assertThat(getProvider().getType(OrganisationUnitContract.users("test_uid")))
                .isEqualTo(UserContract.CONTENT_TYPE_DIR);
    }

    public void testInsert_shouldPersistRow() {
        Uri itemUri = getMockContentResolver().insert(OrganisationUnitContract
                .organisationUnits(), organisationUnit);

        Cursor cursor = database().query(DbOpenHelper.Tables.ORGANISATION_UNIT,
                ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);

        assertThat(ContentUris.parseId(itemUri)).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(ORGANISATION_UNIT_PROJECTION, organisationUnit).isExhausted();
    }

    public void testInsert_shouldThrowExceptionOnExistingId() {
        try {
            database().insertOrThrow(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);
            getProvider().insert(OrganisationUnitContract.organisationUnits(), organisationUnit);

            fail("SQLiteConstraintException was expected, but nothing was thrown");
        } catch (SQLiteConstraintException constraintException) {
            assertThat(constraintException).isNotNull();
        }
    }

    public void testUpdate_shouldUpdateRow() {
        database().insertOrThrow(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

        organisationUnit.put(Columns.DESCRIPTION, "another_description");
        int updatedCount = getProvider().update(OrganisationUnitContract.organisationUnits(1L),
                organisationUnit, null, null);

        Cursor cursor = database().query(DbOpenHelper.Tables.ORGANISATION_UNIT,
                ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);

        assertThat(updatedCount).isEqualTo(1);
        assertThatCursor(cursor).hasRow(ORGANISATION_UNIT_PROJECTION, organisationUnit).isExhausted();
    }

    public void testUpdateByUriWithId_shouldUpdateRow() {
        database().insertOrThrow(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

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
        database().insertOrThrow(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

        int deletedCount = getProvider().delete(OrganisationUnitContract.organisationUnits(),
                Columns.ID + " = ?", new String[]{String.valueOf(1L)});

        Cursor cursor = database().query(DbOpenHelper.Tables.ORGANISATION_UNIT,
                ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);
        assertThat(deletedCount).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    public void testDeleteByUriWithId_shouldDeleteRow() {
        database().insertOrThrow(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

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

        database().insertOrThrow(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnitOne);
        database().insertOrThrow(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnitTwo);

        Cursor cursor = getProvider().query(OrganisationUnitContract.organisationUnits(),
                ORGANISATION_UNIT_PROJECTION, null, null, null);
        assertThatCursor(cursor)
                .hasRow(ORGANISATION_UNIT_PROJECTION, organisationUnitOne)
                .hasRow(ORGANISATION_UNIT_PROJECTION, organisationUnitTwo)
                .isExhausted();
    }

    public void testQueryUsers_shouldReturnRows() {
        // first we need to insert user and organisation units
        ContentValues user = UserContractIntegrationTests.create(1L, "test_user_uid");
        ContentValues organisationUnit = OrganisationUnitContractIntegrationTests.create(1L, "test_organisation_unit_uid");

        database().insert(DbOpenHelper.Tables.USER, null, user);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

        ContentValues userOrgUnitLink = UserOrganisationUnitLinkContractIntegrationTests
                .create(2L, "test_user_uid", "test_organisation_unit_uid", "test_org_unit_scope");
        database().insertOrThrow(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, userOrgUnitLink);

        // we need to pass projection where each column is prefixed with
        // table name in order to avoid ambiguity
        String[] projection = DbTestUtils.unambiguousProjection(DbOpenHelper.Tables.USER,
                UserContractIntegrationTests.USER_PROJECTION);
        Cursor cursor = getProvider().query(OrganisationUnitContract.users("test_organisation_unit_uid"),
                projection, null, null, null);
        assertThatCursor(cursor)
                .hasRow(UserContractIntegrationTests.USER_PROJECTION, user)
                .isExhausted();
    }
}
