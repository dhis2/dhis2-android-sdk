package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class AuthenticatedUserModelIntegrationTests {
    private static final long ID = 2L;
    private static final String USER = "test_user";
    private static final String CREDENTIALS = "test_credentials";

    @Test
    public void create_shouldConvertToModel() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                AuthenticatedUserModel.Columns.ID, AuthenticatedUserModel.Columns.USER, AuthenticatedUserModel.Columns.CREDENTIALS
        });

        matrixCursor.addRow(new Object[]{
                ID, USER, CREDENTIALS
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        AuthenticatedUserModel authenticatedUserModel =
                AuthenticatedUserModel.create(matrixCursor);

        assertThat(authenticatedUserModel.id()).isEqualTo(ID);
        assertThat(authenticatedUserModel.user()).isEqualTo(USER);
        assertThat(authenticatedUserModel.credentials()).isEqualTo(CREDENTIALS);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() {
        AuthenticatedUserModel authenticatedUserModel = AuthenticatedUserModel.builder()
                .id(ID).user(USER).credentials(CREDENTIALS).build();

        ContentValues contentValues = authenticatedUserModel.toContentValues();
        assertThat(contentValues.getAsLong(AuthenticatedUserModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(AuthenticatedUserModel.Columns.USER)).isEqualTo(USER);
        assertThat(contentValues.getAsString(AuthenticatedUserModel.Columns.CREDENTIALS)).isEqualTo(CREDENTIALS);
    }
}
