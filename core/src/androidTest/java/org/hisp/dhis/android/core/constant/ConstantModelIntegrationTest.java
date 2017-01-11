package org.hisp.dhis.android.core.constant;

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
public class ConstantModelIntegrationTest {

    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final double VALUE = 0.18;

    private static final String DATE_STRING = "2017-01-10T12:59:40.083";

    private ConstantModel createConstantModel() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[] {
                ConstantContract.Columns.ID, ConstantContract.Columns.UID, ConstantContract.Columns.CODE, ConstantContract.Columns.NAME,
                ConstantContract.Columns.DISPLAY_NAME, ConstantContract.Columns.CREATED, ConstantContract.Columns.LAST_UPDATED, ConstantContract.Columns.VALUE
        });

        matrixCursor.addRow(new Object[]{ID, UID, CODE, NAME, DISPLAY_NAME, DATE_STRING, DATE_STRING, VALUE});
        matrixCursor.moveToFirst();

        return ConstantModel.create(matrixCursor);
    }

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE_STRING);

        ConstantModel constantModel = createConstantModel();

        assertThat(constantModel.id()).isEqualTo(ID);
        assertThat(constantModel.uid()).isEqualTo(UID);
        assertThat(constantModel.code()).isEqualTo(CODE);
        assertThat(constantModel.name()).isEqualTo(NAME);
        assertThat(constantModel.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(constantModel.created()).isEqualTo(date);
        assertThat(constantModel.lastUpdated()).isEqualTo(date);
        assertThat(constantModel.value()).isEqualTo(VALUE); // Undeprecated in later versions of Truth
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() {
        ConstantModel constantModel = createConstantModel();
        ContentValues contentValues = constantModel.toContentValues();

        assertThat(contentValues.getAsLong(ConstantContract.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(ConstantContract.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(ConstantContract.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(ConstantContract.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(ConstantContract.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(ConstantContract.Columns.CREATED)).isEqualTo(DATE_STRING);
        assertThat(contentValues.getAsString(ConstantContract.Columns.LAST_UPDATED)).isEqualTo(DATE_STRING);
        assertThat(contentValues.getAsDouble(ConstantContract.Columns.VALUE)).isEqualTo(VALUE); // Undeprecated in later versions of Truth

    }
}
