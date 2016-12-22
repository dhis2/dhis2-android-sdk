package org.hisp.dhis.android.core.option;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.option.OptionSetContract.Columns;
import org.hisp.dhis.android.models.common.BaseIdentifiableObject;
import org.hisp.dhis.android.models.common.ValueType;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class OptionSetModelIntegrationTest {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final int VERSION = 51;

    // timestamp
    private static final String DATE = "2016-12-20T16:26:00.007";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.UID,
                Columns.CODE,
                Columns.NAME,
                Columns.DISPLAY_NAME,
                Columns.CREATED,
                Columns.LAST_UPDATED,
                Columns.VERSION,
                Columns.VALUE_TYPE
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE, VERSION, VALUE_TYPE
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        OptionSetModel optionSet = OptionSetModel.create(matrixCursor);

        assertThat(optionSet.id()).isEqualTo(ID);
        assertThat(optionSet.uid()).isEqualTo(UID);
        assertThat(optionSet.code()).isEqualTo(CODE);
        assertThat(optionSet.name()).isEqualTo(NAME);
        assertThat(optionSet.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(optionSet.created()).isEqualTo(timeStamp);
        assertThat(optionSet.lastUpdated()).isEqualTo(timeStamp);
        assertThat(optionSet.version()).isEqualTo(VERSION);
        assertThat(optionSet.valueType()).isEqualTo(VALUE_TYPE);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        OptionSetModel optionSetModel = OptionSetModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .version(VERSION)
                .valueType(VALUE_TYPE)
                .build();

        ContentValues contentValues = optionSetModel.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsInteger(Columns.VERSION)).isEqualTo(VERSION);
        assertThat(contentValues.get(Columns.VALUE_TYPE)).isEqualTo(VALUE_TYPE.name());
    }
}
