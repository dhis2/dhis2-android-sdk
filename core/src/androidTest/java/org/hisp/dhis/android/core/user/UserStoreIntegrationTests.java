package org.hisp.dhis.android.core.user;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.models.common.BaseIdentifiableObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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

    @Before
    public void setUp() {
        super.setUp();

        userStore = new UserStoreImpl(database());
    }

    @Test
    public void save_shouldPersistRowInDatabase() {
        Date date = new Date();

        long rowId = userStore.save(
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

        Cursor cursor = database().query(DbOpenHelper.Tables.USER, USER_PROJECTION,
                null, null, null, null, null);

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
    public void close_shouldNotCloseDatabase() {
        userStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
