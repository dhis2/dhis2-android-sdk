package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.models.common.BaseIdentifiableObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class UserCredentialsStoreIntegrationTests extends AbsStoreTestCase {
    private static final String[] PROJECTION = {
            UserCredentialsContract.Columns.UID,
            UserCredentialsContract.Columns.CODE,
            UserCredentialsContract.Columns.NAME,
            UserCredentialsContract.Columns.DISPLAY_NAME,
            UserCredentialsContract.Columns.CREATED,
            UserCredentialsContract.Columns.LAST_UPDATED,
            UserCredentialsContract.Columns.USERNAME,
            UserCredentialsContract.Columns.USER,
    };

    private UserCredentialsStore userCredentialsStore;

    public void setUp() {
        super.setUp();

        userCredentialsStore = new UserCredentialsStoreImpl(database());

        // row which will be referenced
        ContentValues userRow = UserContractIntegrationTests.create(1L, "test_user_uid");
        database().insert(DbOpenHelper.Tables.USER, null, userRow);
    }

    @Test
    public void save_shouldPersistRowInDatabase() {
        Date date = new Date();

        // inserting authenticated user model item
        long rowId = userCredentialsStore.save(
                "test_user_credentials_uid",
                "test_user_credentials_code",
                "test_user_credentials_name",
                "test_user_credentials_display_name",
                date, date,
                "test_user_credentials_username",
                "test_user_uid");

        Cursor cursor = database().query(DbOpenHelper.Tables.USER_CREDENTIALS,
                PROJECTION, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(
                        "test_user_credentials_uid",
                        "test_user_credentials_code",
                        "test_user_credentials_name",
                        "test_user_credentials_display_name",
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        BaseIdentifiableObject.DATE_FORMAT.format(date),
                        "test_user_credentials_username",
                        "test_user_uid"
                ).isExhausted();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        userCredentialsStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
