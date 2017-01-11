package org.hisp.dhis.android.core.systeminfo;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class SystemInfoModelIntegrationTests {

    private static final long ID = 1L;
    private static final String DATE_FORMAT = "testDateFormat";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID,
                Columns.SERVER_DATE,
                Columns.DATE_FORMAT
        });

        matrixCursor.addRow(new Object[]{ID, DATE, DATE_FORMAT});
        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);
        SystemInfoModel systemInfoModel = SystemInfoModel.create(matrixCursor);

        assertThat(systemInfoModel.id()).isEqualTo(ID);
        assertThat(systemInfoModel.serverDate()).isEqualTo(timeStamp);
        assertThat(systemInfoModel.dateFormat()).isEqualTo(DATE_FORMAT);
    }

    @Test
    public void toContentValues_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        SystemInfoModel systemInfoModel = SystemInfoModel.builder()
                .id(ID)
                .serverDate(timeStamp)
                .dateFormat(DATE_FORMAT)
                .build();

        ContentValues contentValues = systemInfoModel.toContentValues();
        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.SERVER_DATE)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.DATE_FORMAT)).isEqualTo(DATE_FORMAT);
    }
}
