package org.hisp.dhis.android.core.option;

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
public class OptionModelIntegrationTest {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String OPTION_SET = "test_option_set";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                OptionModel.Columns.ID,
                OptionModel.Columns.UID,
                OptionModel.Columns.CODE,
                OptionModel.Columns.NAME,
                OptionModel.Columns.DISPLAY_NAME,
                OptionModel.Columns.CREATED,
                OptionModel.Columns.LAST_UPDATED,
                OptionModel.Columns.OPTION_SET
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE, OPTION_SET
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        OptionModel option = OptionModel.create(matrixCursor);

        assertThat(option.id()).isEqualTo(ID);
        assertThat(option.uid()).isEqualTo(UID);
        assertThat(option.code()).isEqualTo(CODE);
        assertThat(option.name()).isEqualTo(NAME);
        assertThat(option.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(option.created()).isEqualTo(timeStamp);
        assertThat(option.lastUpdated()).isEqualTo(timeStamp);
        assertThat(option.optionSet()).isEqualTo(OPTION_SET);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        OptionModel option = OptionModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .optionSet(OPTION_SET)
                .build();

        ContentValues contentValues = option.toContentValues();
        assertThat(contentValues.getAsLong(OptionModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(OptionModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(OptionModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(OptionModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(OptionModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(OptionModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(OptionModel.Columns.OPTION_SET)).isEqualTo(OPTION_SET);
    }

}
