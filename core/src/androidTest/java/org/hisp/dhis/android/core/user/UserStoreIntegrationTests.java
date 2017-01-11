package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class UserStoreIntegrationTests extends AbsStoreTestCase {
    public static final String[] USER_PROJECTION = {
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

    private UserStore userStore;

    public static ContentValues create(long id, String uid) {
        ContentValues user = new ContentValues();
        user.put(UserContract.Columns.ID, id);
        user.put(UserContract.Columns.UID, uid);
        user.put(UserContract.Columns.CODE, "test_code");
        user.put(UserContract.Columns.NAME, "test_name");
        user.put(UserContract.Columns.DISPLAY_NAME, "test_display_name");
        user.put(UserContract.Columns.CREATED, "test_created");
        user.put(UserContract.Columns.LAST_UPDATED, "test_last_updated");
        user.put(UserContract.Columns.BIRTHDAY, "test_birthday");
        user.put(UserContract.Columns.EDUCATION, "test_education");
        user.put(UserContract.Columns.GENDER, "test_gender");
        user.put(UserContract.Columns.JOB_TITLE, "test_job_title");
        user.put(UserContract.Columns.SURNAME, "test_surname");
        user.put(UserContract.Columns.FIRST_NAME, "test_first_name");
        user.put(UserContract.Columns.INTRODUCTION, "test_introduction");
        user.put(UserContract.Columns.EMPLOYER, "test_employer");
        user.put(UserContract.Columns.INTERESTS, "test_interests");
        user.put(UserContract.Columns.LANGUAGES, "test_languages");
        user.put(UserContract.Columns.EMAIL, "test_email");
        user.put(UserContract.Columns.PHONE_NUMBER, "test_phone_number");
        user.put(UserContract.Columns.NATIONALITY, "test_nationality");
        return user;
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        userStore = new UserStoreImpl(database());
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        Date date = new Date();

        long rowId = userStore.insert(
                "test_user_uid",
                "test_user_code",
                "test_user_name",
                "test_user_display_name",
                date, date,
                "test_user_birthday",
                "test_user_education",
                "test_user_gender",
                "test_user_job_title",
                "test_user_surname",
                "test_user_first_name",
                "test_user_introduction",
                "test_user_employer",
                "test_user_interests",
                "test_user_languages",
                "test_user_email",
                "test_user_phone_number",
                "test_user_nationality"
        );

        Cursor cursor = database().query(DbOpenHelper.Tables.USER,
                USER_PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        "test_user_uid",
                        "test_user_code",
                        "test_user_name",
                        "test_user_display_name",
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        "test_user_birthday",
                        "test_user_education",
                        "test_user_gender",
                        "test_user_job_title",
                        "test_user_surname",
                        "test_user_first_name",
                        "test_user_introduction",
                        "test_user_employer",
                        "test_user_interests",
                        "test_user_languages",
                        "test_user_email",
                        "test_user_phone_number",
                        "test_user_nationality"
                )
                .isExhausted();
    }

    @Test
    public void delete_shouldDeleteAllRows() {
        ContentValues user = create(1L, "test_user_id");
        database().insert(DbOpenHelper.Tables.USER, null, user);

        int deleted = userStore.delete();

        Cursor cursor = database().query(DbOpenHelper.Tables.USER,
                null, null, null, null, null, null);
        assertThat(deleted).isEqualTo(1);
        assertThatCursor(cursor).isExhausted();
    }

    // ToDo: consider introducing conflict resolution strategy
//    @Test
//    public void save_shouldNotTriggerOtherTablesOnDuplicate() {
//        // inserting user
//        ContentValues user = UserContractIntegrationTests.authenticator(1L, "test_user_uid");
//        database().insert(DbOpenHelper.Tables.USER, null, user);
//
//        // inserting user credentials
//        ContentValues userCredentials = UserCredentialsContractIntegrationTests.authenticator(
//                1L, "test_user_credentials", "test_user_uid");
//        database().insert(DbOpenHelper.Tables.USER_CREDENTIALS, null, userCredentials);
//
//        // try to insert duplicate into user table through store
//        Date date = new Date();
//        long rowId = userStore.insert(
//                "test_user_uid",
//                "test_user_code",
//                "test_user_name",
//                "test_user_display_name",
//                date, date,
//                "test_user_birthday",
//                "test_user_education",
//                "test_user_gender",
//                "test_user_job_title",
//                "test_user_surname",
//                "test_user_first_name",
//                "test_user_introduction",
//                "test_user_employer",
//                "test_user_interests",
//                "test_user_languages",
//                "test_user_email",
//                "test_user_phone_number",
//                "test_user_nationality"
//        );
//
//        System.out.println("RowId: " + rowId);
//
//        assertThatCursor(database().query(DbOpenHelper.Tables.USER_CREDENTIALS, UserCredentialsContractIntegrationTests.USER_CREDENTIALS_PROJECTION, null, null, null, null, null))
//                .hasRow(UserCredentialsContractIntegrationTests.USER_CREDENTIALS_PROJECTION, userCredentials)
//                .isExhausted();
//    }

    @Test
    public void close_shouldNotCloseDatabase() {
        userStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
