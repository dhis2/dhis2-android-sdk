package org.hisp.dhis.android.core.option;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class OptionSetModelIntegrationTest {
    private static final Long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final Integer VERSION = 51;

    // timestamp
    private static final String DATE = "2016-12-20T16:26:00.007";

    public static ContentValues create(long id, String uid) {
        ContentValues optionSet = new ContentValues();
        optionSet.put(OptionSetModel.Columns.ID, id);
        optionSet.put(OptionSetModel.Columns.UID, uid);
        optionSet.put(OptionSetModel.Columns.CODE, CODE);
        optionSet.put(OptionSetModel.Columns.NAME, NAME);
        optionSet.put(OptionSetModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        optionSet.put(OptionSetModel.Columns.VERSION, VERSION);
        optionSet.put(OptionSetModel.Columns.VALUE_TYPE, VALUE_TYPE.name());
        return optionSet;
    }

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                OptionSetModel.Columns.ID,
                OptionSetModel.Columns.UID,
                OptionSetModel.Columns.CODE,
                OptionSetModel.Columns.NAME,
                OptionSetModel.Columns.DISPLAY_NAME,
                OptionSetModel.Columns.CREATED,
                OptionSetModel.Columns.LAST_UPDATED,
                OptionSetModel.Columns.VERSION,
                OptionSetModel.Columns.VALUE_TYPE
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

        assertThat(contentValues.getAsLong(OptionSetModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(OptionSetModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(OptionSetModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(OptionSetModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(OptionSetModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(OptionSetModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(OptionSetModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsInteger(OptionSetModel.Columns.VERSION)).isEqualTo(VERSION);
        assertThat(contentValues.get(OptionSetModel.Columns.VALUE_TYPE)).isEqualTo(VALUE_TYPE.name());
    }
}
