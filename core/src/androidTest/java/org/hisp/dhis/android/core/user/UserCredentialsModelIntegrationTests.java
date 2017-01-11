package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class UserCredentialsModelIntegrationTests {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String USERNAME = "test_username";
    private static final String USER = "test_user";

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                UserCredentialsModel.Columns.ID,
                UserCredentialsModel.Columns.UID,
                UserCredentialsModel.Columns.CODE,
                UserCredentialsModel.Columns.NAME,
                UserCredentialsModel.Columns.DISPLAY_NAME,
                UserCredentialsModel.Columns.CREATED,
                UserCredentialsModel.Columns.LAST_UPDATED,
                UserCredentialsModel.Columns.USERNAME,
                UserCredentialsModel.Columns.USER
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE, USERNAME, USER
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        UserCredentialsModel userCredentialsModel = UserCredentialsModel.create(matrixCursor);

        assertThat(userCredentialsModel.id()).isEqualTo(ID);
        assertThat(userCredentialsModel.uid()).isEqualTo(UID);
        assertThat(userCredentialsModel.code()).isEqualTo(CODE);
        assertThat(userCredentialsModel.name()).isEqualTo(NAME);
        assertThat(userCredentialsModel.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(userCredentialsModel.created()).isEqualTo(date);
        assertThat(userCredentialsModel.lastUpdated()).isEqualTo(date);
        assertThat(userCredentialsModel.username()).isEqualTo(USERNAME);
        assertThat(userCredentialsModel.user()).isEqualTo(USER);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        UserCredentialsModel userCredentials = UserCredentialsModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(date)
                .lastUpdated(date)
                .username(USERNAME)
                .user(USER)
                .build();

        ContentValues contentValues = userCredentials.toContentValues();

        assertThat(contentValues.getAsLong(UserCredentialsModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.USERNAME)).isEqualTo(USERNAME);
        assertThat(contentValues.getAsString(UserCredentialsModel.Columns.USER)).isEqualTo(USER);
    }
}
