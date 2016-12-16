package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class AuthenticatedUserStoreIntegrationTests extends AbsStoreTestCase {
    private AuthenticatedUserStore authenticatedUserStore;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        authenticatedUserStore = new AuthenticatedUserStoreImpl(database());

        // row which will be referenced
        ContentValues userRow = UserContractIntegrationTests.create(1L, "test_user_uid");
        database().insert(DbOpenHelper.Tables.USER, null, userRow);
    }

    @Test
    public void insert_shouldPersistRowInDatabase() {
        // inserting authenticated user model item
        long rowId = authenticatedUserStore.insert("test_user_uid", "test_user_credentials");

        Cursor cursor = database().query(DbOpenHelper.Tables.AUTHENTICATED_USER,
                null, null, null, null, null, null);

        assertThat(rowId).isEqualTo(1L);
        assertThatCursor(cursor)
                .hasRow(1L, "test_user_uid", "test_user_credentials")
                .isExhausted();
    }

    @Test
    public void query_shouldReturnPersistedRows() {
        ContentValues authenticatedUser = new ContentValues();
        authenticatedUser.put(AuthenticatedUserContract.Columns.USER, "test_user_uid");
        authenticatedUser.put(AuthenticatedUserContract.Columns.CREDENTIALS, "test_user_credentials");

        database().insert(DbOpenHelper.Tables.AUTHENTICATED_USER,
                null, authenticatedUser);

        AuthenticatedUserModel authenticatedUserModel = AuthenticatedUserModel.builder()
                .id(1L).user("test_user_uid").credentials("test_user_credentials")
                .build();

        assertThat(authenticatedUserStore.query().size()).isEqualTo(1);
        assertThat(authenticatedUserStore.query()).contains(authenticatedUserModel);
    }

    @Test
    public void query_shouldReturnEmptyListOnEmptyTable() {
        assertThat(authenticatedUserStore.query()).isEmpty();
    }

    @Test
    public void close_shouldNotCloseDatabase() {
        authenticatedUserStore.close();

        assertThat(database().isOpen()).isTrue();
    }
}
