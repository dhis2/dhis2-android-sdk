package org.hisp.dhis.android.core.user;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;

import org.hisp.dhis.android.core.data.database.AbsProviderTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContractIntegrationTests;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

public class UserOrganisationUnitLinkContractIntegrationTests extends AbsProviderTestCase {
    public static String[] USER_ORGANISATION_UNIT_PROJECTION = {
            UserOrganisationUnitLinkContract.Columns.ID,
            UserOrganisationUnitLinkContract.Columns.USER,
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT,
            UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT_SCOPE,
    };

    public static ContentValues create(long id, String user, String organisationUnit, String orgUnitScope) {
        ContentValues userOrganisationUnitLink = new ContentValues();
        userOrganisationUnitLink.put(UserOrganisationUnitLinkContract.Columns.ID, id);
        userOrganisationUnitLink.put(UserOrganisationUnitLinkContract.Columns.USER, user);
        userOrganisationUnitLink.put(UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT, organisationUnit);
        userOrganisationUnitLink.put(UserOrganisationUnitLinkContract.Columns.ORGANISATION_UNIT_SCOPE, orgUnitScope);
        return userOrganisationUnitLink;
    }

    public void testGetType_shouldReturnCorrectMimeTypes() {
        assertThat(getProvider().getType(UserOrganisationUnitLinkContract.userOrganisationUnits()))
                .isEqualTo(UserOrganisationUnitLinkContract.CONTENT_TYPE_DIR);
        assertThat(getProvider().getType(UserOrganisationUnitLinkContract.userOrganisationUnits(1L)))
                .isEqualTo(UserOrganisationUnitLinkContract.CONTENT_TYPE_ITEM);
    }

    public void testInsert_shouldPersistRow() {
        // first we need to insert user and organisation units
        database().insert(DbOpenHelper.Tables.USER, null,
                UserContractIntegrationTests.create(1L, "test_user_uid"));
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null,
                OrganisationUnitContractIntegrationTests.create(1L, "test_organisation_unit_uid"));

        ContentValues userOrgUnitLink = create(2L, "test_user_uid", "test_organisation_unit_uid", "test_org_unit_scope");
        Uri itemUri = getProvider().insert(UserOrganisationUnitLinkContract
                .userOrganisationUnits(), userOrgUnitLink);

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_ORGANISATION_UNIT,
                USER_ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);
        assertThat(ContentUris.parseId(itemUri)).isEqualTo(2L);
        assertThatCursor(cursor)
                .hasRow(USER_ORGANISATION_UNIT_PROJECTION, userOrgUnitLink)
                .isExhausted();
    }

    public void testInsert_shouldThrowOnExistingId() {
        try {
            // we need to insert row into parent table first
            ContentValues userOrgUnitLink = create(2L, "test_user_uid", "test_organisation_unit_uid", "test_org_unit_scope");
            database().insertOrThrow(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, userOrgUnitLink);

            getProvider().insert(UserOrganisationUnitLinkContract
                    .userOrganisationUnits(), userOrgUnitLink);

            fail("SQLiteConstraintException was expected, but nothing was thrown");
        } catch (SQLiteConstraintException constraintException) {
            assertThat(constraintException).isNotNull();
        }
    }

    public void testDelete_shouldDeleteRow() {
        // first we need to insert user and organisation units
        database().insert(DbOpenHelper.Tables.USER, null,
                UserContractIntegrationTests.create(1L, "test_user_uid"));
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null,
                OrganisationUnitContractIntegrationTests.create(1L, "test_organisation_unit_uid"));

        ContentValues userOrgUnitLink = create(2L, "test_user_uid", "test_organisation_unit_uid", "test_org_unit_scope");
        database().insertOrThrow(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, userOrgUnitLink);

        int deletedCount = getProvider().delete(UserOrganisationUnitLinkContract.userOrganisationUnits(),
                UserOrganisationUnitLinkContract.Columns.ID + " = ?", new String[]{String.valueOf(2L)});

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_ORGANISATION_UNIT,
                USER_ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);
        assertThat(deletedCount).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    public void testDeleteByUriWithId_shouldDeleteRow() {
        // first we need to insert user and organisation units
        database().insert(DbOpenHelper.Tables.USER, null,
                UserContractIntegrationTests.create(1L, "test_user_uid"));
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null,
                OrganisationUnitContractIntegrationTests.create(1L, "test_organisation_unit_uid"));

        ContentValues userOrgUnitLink = create(2L, "test_user_uid", "test_organisation_unit_uid", "test_org_unit_scope");
        database().insertOrThrow(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, userOrgUnitLink);

        int deletedCount = getProvider().delete(UserOrganisationUnitLinkContract
                .userOrganisationUnits(2L), null, null);

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_ORGANISATION_UNIT,
                USER_ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);
        assertThat(deletedCount).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    public void testDeleteOnOrganisationUnit_shouldDeleteRows() {
        // first we need to insert user and organisation units
        database().insert(DbOpenHelper.Tables.USER, null,
                UserContractIntegrationTests.create(1L, "test_user_uid"));
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null,
                OrganisationUnitContractIntegrationTests.create(1L, "test_organisation_unit_uid"));

        ContentValues userOrgUnitLink = create(2L, "test_user_uid", "test_organisation_unit_uid", "test_org_unit_scope");
        database().insertOrThrow(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, userOrgUnitLink);

        // remove organisation unit which is referenced by link row
        database().delete(DbOpenHelper.Tables.ORGANISATION_UNIT,
                OrganisationUnitContract.Columns.ID + " = ?", new String[]{String.valueOf(1L)});

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_ORGANISATION_UNIT,
                USER_ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    public void testDeleteOnUser_shouldDeleteRows() {
        // first we need to insert user and organisation units
        database().insert(DbOpenHelper.Tables.USER, null,
                UserContractIntegrationTests.create(1L, "test_user_uid"));
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null,
                OrganisationUnitContractIntegrationTests.create(1L, "test_organisation_unit_uid"));

        ContentValues userOrgUnitLink = create(2L, "test_user_uid", "test_organisation_unit_uid", "test_org_unit_scope");
        database().insertOrThrow(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, userOrgUnitLink);

        // remove organisation unit which is referenced by link row
        database().delete(DbOpenHelper.Tables.USER,
                UserContract.Columns.ID + " = ?", new String[]{String.valueOf(1L)});

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_ORGANISATION_UNIT,
                USER_ORGANISATION_UNIT_PROJECTION, null, null, null, null, null);
        assertThatCursor(cursor).isExhausted();
    }

    public void testQuery_shouldReturnRows() {
        // first we need to insert user and organisation units
        database().insert(DbOpenHelper.Tables.USER, null,
                UserContractIntegrationTests.create(1L, "test_user_uid"));
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null,
                OrganisationUnitContractIntegrationTests.create(1L, "test_organisation_unit_uid"));

        ContentValues userOrgUnitLink = create(2L, "test_user_uid", "test_organisation_unit_uid", "test_org_unit_scope");
        database().insertOrThrow(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, userOrgUnitLink);

        Cursor cursor = getProvider().query(UserOrganisationUnitLinkContract.userOrganisationUnits(),
                USER_ORGANISATION_UNIT_PROJECTION, null, null, null);
        assertThatCursor(cursor)
                .hasRow(USER_ORGANISATION_UNIT_PROJECTION, userOrgUnitLink)
                .isExhausted();
    }

    public void testQueryByUriWithId_shouldReturnRow() {
        // first we need to insert user and organisation units
        database().insert(DbOpenHelper.Tables.USER, null,
                UserContractIntegrationTests.create(1L, "test_user_uid"));
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null,
                OrganisationUnitContractIntegrationTests.create(1L, "test_organisation_unit_uid"));

        ContentValues userOrgUnitLink = create(2L, "test_user_uid", "test_organisation_unit_uid", "test_org_unit_scope");
        database().insertOrThrow(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, userOrgUnitLink);

        Cursor cursor = getProvider().query(UserOrganisationUnitLinkContract.userOrganisationUnits(2L),
                USER_ORGANISATION_UNIT_PROJECTION, null, null, null);
        assertThatCursor(cursor)
                .hasRow(USER_ORGANISATION_UNIT_PROJECTION, userOrgUnitLink)
                .isExhausted();
    }
}
