package org.hisp.dhis.android.core.user;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;

import org.hisp.dhis.android.core.database.AbsProviderTestCase;
import org.hisp.dhis.android.core.database.DbOpenHelper;
import org.hisp.dhis.android.core.database.DbTestUtils;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContractIntegrationTests;
import org.hisp.dhis.android.core.user.UserContract.Columns;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.database.CursorAssert.assertThatCursor;

public final class UserContractIntegrationTests extends AbsProviderTestCase {
    public static final String[] USER_PROJECTION = {
            Columns.ID,
            Columns.UID,
            Columns.CODE,
            Columns.NAME,
            Columns.DISPLAY_NAME,
            Columns.CREATED,
            Columns.LAST_UPDATED,
            Columns.BIRTHDAY,
            Columns.EDUCATION,
            Columns.GENDER,
            Columns.JOB_TITLE,
            Columns.SURNAME,
            Columns.FIRST_NAME,
            Columns.INTRODUCTION,
            Columns.EMPLOYER,
            Columns.INTERESTS,
            Columns.LANGUAGES,
            Columns.EMAIL,
            Columns.PHONE_NUMBER,
            Columns.NATIONALITY
    };

    public static ContentValues create(long id, String uid) {
        ContentValues user = new ContentValues();
        user.put(Columns.ID, id);
        user.put(Columns.UID, uid);
        user.put(Columns.CODE, "test_code");
        user.put(Columns.NAME, "test_name");
        user.put(Columns.DISPLAY_NAME, "test_display_name");
        user.put(Columns.CREATED, "test_created");
        user.put(Columns.LAST_UPDATED, "test_last_updated");
        user.put(Columns.BIRTHDAY, "test_birthday");
        user.put(Columns.EDUCATION, "test_education");
        user.put(Columns.GENDER, "test_gender");
        user.put(Columns.JOB_TITLE, "test_job_title");
        user.put(Columns.SURNAME, "test_surname");
        user.put(Columns.FIRST_NAME, "test_first_name");
        user.put(Columns.INTRODUCTION, "test_introduction");
        user.put(Columns.EMPLOYER, "test_employer");
        user.put(Columns.INTERESTS, "test_interests");
        user.put(Columns.LANGUAGES, "test_languages");
        user.put(Columns.EMAIL, "test_email");
        user.put(Columns.PHONE_NUMBER, "test_phone_number");
        user.put(Columns.NATIONALITY, "test_nationality");
        return user;
    }

    private ContentValues user;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        user = create(1L, "test_uid");
    }

    public void testGetTypeOnShouldReturnCorrectMimeTypes() {
        assertThat(getProvider().getType(UserContract.users()))
                .isEqualTo(UserContract.CONTENT_TYPE_DIR);
        assertThat(getProvider().getType(UserContract.users(1L)))
                .isEqualTo(UserContract.CONTENT_TYPE_ITEM);
        assertThat(getProvider().getType(UserContract.organisationUnits("test_uid")))
                .isEqualTo(OrganisationUnitContract.CONTENT_TYPE_DIR);
    }

    public void testInsert_shouldPersistRow() {
        Uri itemUri = getProvider().insert(UserContract.users(), user);

        Cursor cursor = database().query(DbOpenHelper.Tables.USER, USER_PROJECTION,
                null, null, null, null, null);
        assertThat(ContentUris.parseId(itemUri)).isEqualTo(1L);
        assertThatCursor(cursor).hasRow(USER_PROJECTION, user).isExhausted();
    }

    public void testInsert_shouldThrowExceptionOnExistingId() {
        try {
            database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);
            getProvider().insert(UserContract.users(), user);

            fail("SQLiteConstraintException was expected, but nothing was thrown");
        } catch (SQLiteConstraintException constraintException) {
            assertThat(constraintException).isNotNull();
        }
    }

    public void testUpdate_shouldUpdateRow() {
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);

        user.put(Columns.FIRST_NAME, "test_name_another");
        int updatedCount = getProvider().update(UserContract.users(), user,
                Columns.ID + " = ?", new String[]{String.valueOf(1L)});

        Cursor cursor = database().query(DbOpenHelper.Tables.USER, USER_PROJECTION,
                null, null, null, null, null);

        assertThat(updatedCount).isEqualTo(1);
        assertThatCursor(cursor).hasRow(USER_PROJECTION, user).isExhausted();
    }

    public void testUpdateByUriWithId_shouldUpdateRow() {
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);

        user.put(Columns.FIRST_NAME, "test_name_another");
        int updatedCount = getProvider().update(UserContract.users(1L), user, null, null);

        Cursor cursor = database().query(DbOpenHelper.Tables.USER, USER_PROJECTION,
                null, null, null, null, null);

        assertThat(updatedCount).isEqualTo(1);
        assertThatCursor(cursor).hasRow(USER_PROJECTION, user).isExhausted();
    }

    public void testDeleteByUriWithId_shouldDeleteRow() {
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);

        int deletedCount = getProvider().delete(UserContract.users(1L), null, null);

        Cursor cursor = database().query(DbOpenHelper.Tables.USER, USER_PROJECTION,
                null, null, null, null, null);
        assertThat(deletedCount).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    public void testDelete_shouldDeleteRow() {
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, user);

        int deletedCount = getProvider().delete(UserContract.users(), Columns.ID + " = ?",
                new String[]{String.valueOf(1L)});

        Cursor cursor = database().query(DbOpenHelper.Tables.USER, USER_PROJECTION,
                null, null, null, null, null);
        assertThat(deletedCount).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    public void testQuery_shouldReturnRows() {
        ContentValues userOne = new ContentValues(user);
        ContentValues userTwo = new ContentValues(user);

        userOne.put(Columns.ID, 1L);
        userOne.put(Columns.UID, "test_uid_one");

        userTwo.put(Columns.ID, 2L);
        userTwo.put(Columns.UID, "test_uid_two");

        database().insertOrThrow(DbOpenHelper.Tables.USER, null, userOne);
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, userTwo);

        Cursor cursor = getProvider().query(UserContract.users(),
                USER_PROJECTION, null, null, null);
        assertThatCursor(cursor)
                .hasRow(USER_PROJECTION, userOne)
                .hasRow(USER_PROJECTION, userTwo)
                .isExhausted();
    }

    public void testQueryByUriWithId_shouldReturnRow() {
        ContentValues userOne = new ContentValues(user);
        ContentValues userTwo = new ContentValues(user);

        userOne.put(Columns.ID, 1L);
        userOne.put(Columns.UID, "test_uid_one");

        userTwo.put(Columns.ID, 2L);
        userTwo.put(Columns.UID, "test_uid_two");

        database().insertOrThrow(DbOpenHelper.Tables.USER, null, userOne);
        database().insertOrThrow(DbOpenHelper.Tables.USER, null, userTwo);

        Cursor cursor = getProvider().query(UserContract.users(2L),
                USER_PROJECTION, null, null, null);
        assertThatCursor(cursor).hasRow(USER_PROJECTION, userTwo).isExhausted();
    }

    public void testQueryOrganisationUnits_shouldReturnRows() {
        // first we need to insert user and organisation units
        ContentValues user = UserContractIntegrationTests.create(1L, "test_user_uid");
        ContentValues organisationUnit = OrganisationUnitContractIntegrationTests.create(1L, "test_organisation_unit_uid");

        database().insert(DbOpenHelper.Tables.USER, null, user);
        database().insert(DbOpenHelper.Tables.ORGANISATION_UNIT, null, organisationUnit);

        ContentValues userOrgUnitLink = UserOrganisationUnitLinkContractIntegrationTests
                .create(2L, "test_user_uid", "test_organisation_unit_uid");
        database().insertOrThrow(DbOpenHelper.Tables.USER_ORGANISATION_UNIT, null, userOrgUnitLink);

        // we need to pass projection where each column is prefixed with
        // table name in order to avoid ambiguity
        String[] projection = DbTestUtils.unambiguousProjection(DbOpenHelper.Tables.ORGANISATION_UNIT,
                OrganisationUnitContractIntegrationTests.ORGANISATION_UNIT_PROJECTION);
        Cursor cursor = getProvider().query(UserContract.organisationUnits("test_user_uid"),
                projection, null, null, null);
        assertThatCursor(cursor)
                .hasRow(OrganisationUnitContractIntegrationTests.ORGANISATION_UNIT_PROJECTION, organisationUnit)
                .isExhausted();
    }
}
