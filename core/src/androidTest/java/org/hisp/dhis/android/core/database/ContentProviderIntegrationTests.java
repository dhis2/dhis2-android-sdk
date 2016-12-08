package org.hisp.dhis.android.core.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.test.ProviderTestCase2;

import org.hisp.dhis.android.core.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract;
import org.hisp.dhis.android.core.user.UserContract;
import org.hisp.dhis.android.core.user.UserCredentialsContract;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.database.CursorAssert.assertThatCursor;

public class ContentProviderIntegrationTests extends ProviderTestCase2<DbContentProvider> {
    private static final String[] USER_PROJECTION = new String[]{
            UserContract.Columns.ID,
            UserContract.Columns.UID,
            UserContract.Columns.CODE,
            UserContract.Columns.NAME,
            UserContract.Columns.DISPLAY_NAME,
            UserContract.Columns.CREATED,
            UserContract.Columns.LAST_UPDATED,
            UserContract.Columns.BIRTHDAY,
            UserContract.Columns.EDUCATION,
            UserContract.Columns.GENDER,
            UserContract.Columns.JOB_TITLE,
            UserContract.Columns.SURNAME,
            UserContract.Columns.FIRST_NAME,
            UserContract.Columns.INTRODUCTION,
            UserContract.Columns.EMPLOYER,
            UserContract.Columns.INTERESTS,
            UserContract.Columns.LANGUAGES,
            UserContract.Columns.EMAIL,
            UserContract.Columns.PHONE_NUMBER,
            UserContract.Columns.NATIONALITY
    };

    private ContentValues user;

    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;

    public ContentProviderIntegrationTests() {
        super(DbContentProvider.class, DbContract.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());

        super.setUp();

        // using database directly to verify behaviour of content provider
        readableDatabase = getProvider().sqLiteOpenHelper().getReadableDatabase();
        writableDatabase = getProvider().sqLiteOpenHelper().getWritableDatabase();

        // test data
        user = new ContentValues();
        user.put(UserContract.Columns.ID, 1L);
        user.put(UserContract.Columns.UID, "abc");
        user.put(UserContract.Columns.CODE, "code");
        user.put(UserContract.Columns.NAME, "name");
        user.put(UserContract.Columns.DISPLAY_NAME, "displayName");
        user.put(UserContract.Columns.CREATED, "created");
        user.put(UserContract.Columns.LAST_UPDATED, "lastUpdated");
        user.put(UserContract.Columns.BIRTHDAY, "birthday");
        user.put(UserContract.Columns.EDUCATION, "education");
        user.put(UserContract.Columns.GENDER, "gender");
        user.put(UserContract.Columns.JOB_TITLE, "jobTitle");
        user.put(UserContract.Columns.SURNAME, "surname");
        user.put(UserContract.Columns.FIRST_NAME, "firstName");
        user.put(UserContract.Columns.INTRODUCTION, "introduction");
        user.put(UserContract.Columns.EMPLOYER, "employer");
        user.put(UserContract.Columns.INTERESTS, "interests");
        user.put(UserContract.Columns.LANGUAGES, "languages");
        user.put(UserContract.Columns.EMAIL, "email");
        user.put(UserContract.Columns.PHONE_NUMBER, "phoneNumber");
        user.put(UserContract.Columns.NATIONALITY, "nationality");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        // close database in order not to leak resources during tests
        readableDatabase.close();
        writableDatabase.close();
    }

    public void testGetTypeOnUserContract_shouldReturnCorrectMimeTypes() {
        assertThat(getProvider().getType(UserContract.users()))
                .isEqualTo(UserContract.CONTENT_TYPE_DIR);
        assertThat(getProvider().getType(UserContract.users(1L)))
                .isEqualTo(UserContract.CONTENT_TYPE_ITEM);
    }

    public void testGetTypeOnUserCredentialsContract_shouldReturnCorrectMimeTypes() {
        assertThat(getProvider().getType(UserCredentialsContract.userCredentials()))
                .isEqualTo(UserCredentialsContract.CONTENT_TYPE_DIR);
        assertThat(getProvider().getType(UserCredentialsContract.userCredentials(1L)))
                .isEqualTo(UserCredentialsContract.CONTENT_TYPE_ITEM);
    }

    public void testGetTypeOnOrganisationUnitsContract_shouldReturnCorrectMimeTypes() {
        assertThat(getProvider().getType(OrganisationUnitContract.organisationUnits()))
                .isEqualTo(OrganisationUnitContract.CONTENT_TYPE_DIR);
        assertThat(getProvider().getType(OrganisationUnitContract.organisationUnits(1L)))
                .isEqualTo(OrganisationUnitContract.CONTENT_TYPE_ITEM);
    }

    public void testGetTypeOnUnknownUri_shouldThrowException() {
        try {
            getProvider().getType(Uri.withAppendedPath(
                    DbContract.AUTHORITY_URI, "test_resource"));

            fail("Exception was expected but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception).isNotNull();
        }
    }

    public void testInsertOnUnknownUri_shouldThrowException() {
        try {
            getProvider().insert(Uri.withAppendedPath(
                    DbContract.AUTHORITY_URI, "test_resource"), new ContentValues());

            fail("Exception was expected but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception).isNotNull();
        }
    }

    public void testUpdateOnUnknownUri_shouldThrowException() {
        try {
            getProvider().update(Uri.withAppendedPath(DbContract.AUTHORITY_URI, "test_resource"),
                    new ContentValues(), null, null);

            fail("Exception was expected but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception).isNotNull();
        }
    }

    public void testDeleteOnUnknownUri_shouldThrowException() {
        try {
            getProvider().delete(Uri.withAppendedPath(
                    DbContract.AUTHORITY_URI, "test_resource"), null, null);

            fail("Exception was expected but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception).isNotNull();
        }
    }

    public void testQueryOnUnknownUri_shouldThrowException() {
        try {
            getProvider().query(Uri.withAppendedPath(DbContract.AUTHORITY_URI, "test_resource"),
                    null, null, null, null);

            fail("Exception was expected but nothing was thrown");
        } catch (IllegalArgumentException exception) {
            assertThat(exception).isNotNull();
        }
    }

    public void testInsertOnUsers_shouldPersistRow() {
        getProvider().insert(UserContract.users(), user);

        assertThatCursor(readableDatabase.query(Tables.USER, USER_PROJECTION, null, null, null, null, null)).hasRow(
                1L, "abc", "code", "name", "displayName", "created", "lastUpdated", "birthday",
                "education", "gender", "jobTitle", "surname", "firstName", "introduction",
                "employer", "interests", "languages", "email", "phoneNumber", "nationality"
        ).isExhausted();
    }

    public void testInsertOnUsers_shouldNotThrowOnExistingId() {
        try {
            writableDatabase.insert(Tables.USER, null, user);
            getProvider().insert(UserContract.users(), user);
        } catch (SQLiteConstraintException constraintException) {
            fail("Must not fail on duplicate id");
        }
    }

    public void testUpdateOnUsers_shouldUpdateRow() {
        writableDatabase.insert(Tables.USER, null, user);

        user.put(UserContract.Columns.FIRST_NAME, "another_name");
        getProvider().update(UserContract.users(), user, UserContract.Columns.ID + " = ?",
                new String[]{String.valueOf(1L)});

        assertThatCursor(readableDatabase.query(Tables.USER, USER_PROJECTION, null, null, null, null, null)).hasRow(
                1L, "abc", "code", "name", "displayName", "created", "lastUpdated", "birthday",
                "education", "gender", "jobTitle", "surname", "another_name", "introduction",
                "employer", "interests", "languages", "email", "phoneNumber", "nationality"
        ).isExhausted();
    }

    public void testUpdateOnUsersByUriWithId_shouldUpdateRow() {
        writableDatabase.insert(Tables.USER, null, user);

        user.put(UserContract.Columns.FIRST_NAME, "another_name");
        getProvider().update(UserContract.users(1L), user, null, null);

        assertThatCursor(readableDatabase.query(Tables.USER, USER_PROJECTION, null, null, null, null, null)).hasRow(
                1L, "abc", "code", "name", "displayName", "created", "lastUpdated", "birthday",
                "education", "gender", "jobTitle", "surname", "another_name", "introduction",
                "employer", "interests", "languages", "email", "phoneNumber", "nationality"
        ).isExhausted();
    }

    public void testDeleteOnUsersByUriWithId_shouldDeleteRow() {
        writableDatabase.insert(Tables.USER, null, user);

        getProvider().delete(UserContract.users(1L), null, null);
        assertThatCursor(readableDatabase.query(Tables.USER, USER_PROJECTION, null,
                null, null, null, null)).isExhausted();
    }

    public void testDeleteOnUsers_shouldDeleteRow() {
        writableDatabase.insert(Tables.USER, null, user);

        getProvider().delete(UserContract.users(), UserContract.Columns.ID + " = ?",
                new String[]{String.valueOf(1L)});
        assertThatCursor(readableDatabase.query(Tables.USER, USER_PROJECTION, null,
                null, null, null, null)).isExhausted();
    }

    public void testQueryOnUsers_shouldReturnRow() {
        writableDatabase.insert(Tables.USER, null, user);

        user.put(UserContract.Columns.ID, 2L);
        user.put(UserContract.Columns.UID, "efg");

        writableDatabase.insert(Tables.USER, null, user);

        assertThatCursor(getProvider().query(UserContract.users(), USER_PROJECTION, null, null, null))
                .hasRow(
                        1L, "abc", "code", "name", "displayName", "created", "lastUpdated", "birthday",
                        "education", "gender", "jobTitle", "surname", "firstName", "introduction",
                        "employer", "interests", "languages", "email", "phoneNumber", "nationality"
                )
                .hasRow(
                        2L, "efg", "code", "name", "displayName", "created", "lastUpdated", "birthday",
                        "education", "gender", "jobTitle", "surname", "firstName", "introduction",
                        "employer", "interests", "languages", "email", "phoneNumber", "nationality"
                )
                .isExhausted();
    }

    public void testQueryOnUsersByUriWithId_shouldReturnRow() {
        writableDatabase.insert(Tables.USER, null, user);

        user.put(UserContract.Columns.ID, 2L);
        user.put(UserContract.Columns.UID, "efg");

        writableDatabase.insert(Tables.USER, null, user);

        assertThatCursor(getProvider().query(UserContract.users(2L), USER_PROJECTION, null, null, null))
                .hasRow(
                        2L, "efg", "code", "name", "displayName", "created", "lastUpdated", "birthday",
                        "education", "gender", "jobTitle", "surname", "firstName", "introduction",
                        "employer", "interests", "languages", "email", "phoneNumber", "nationality"
                )
                .isExhausted();
    }
}
