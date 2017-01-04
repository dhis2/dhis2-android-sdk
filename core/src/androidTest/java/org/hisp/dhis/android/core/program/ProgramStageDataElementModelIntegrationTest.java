package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.program.ProgramStageDataElementContract.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

;

@RunWith(AndroidJUnit4.class)
public class ProgramStageDataElementModelIntegrationTest {
    private static final long ID = 3L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Integer DISPLAY_IN_REPORTS = 1;
    private static final Integer COMPULSORY = 0;
    private static final Integer ALLOW_PROVIDED_ELSEWHERE = 0;
    private static final Integer SORT_ORDER = 7;
    private static final Integer ALLOW_FUTURE_DATE = 1;
    private static final String DATA_ELEMENT = "test_dataElement";

    // timestamp
    private static final String DATE = "2017-01-04T16:40:02.007";

    public static ContentValues create(long id, String uid) {
        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(Columns.ID, id);
        programStageDataElement.put(Columns.UID, uid);
        programStageDataElement.put(Columns.CODE, CODE);
        programStageDataElement.put(Columns.NAME, NAME);
        programStageDataElement.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        programStageDataElement.put(Columns.CREATED, DATE);
        programStageDataElement.put(Columns.LAST_UPDATED, DATE);
        programStageDataElement.put(Columns.DISPLAY_IN_REPORTS, DISPLAY_IN_REPORTS);
        programStageDataElement.put(Columns.COMPULSORY, COMPULSORY);
        programStageDataElement.put(Columns.ALLOW_PROVIDED_ELSEWHERE, ALLOW_PROVIDED_ELSEWHERE);
        programStageDataElement.put(Columns.SORT_ORDER, SORT_ORDER);
        programStageDataElement.put(Columns.ALLOW_FUTURE_DATE, ALLOW_FUTURE_DATE);
        programStageDataElement.put(Columns.DATA_ELEMENT, DATA_ELEMENT);

        return programStageDataElement;
    }

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
                Columns.DISPLAY_IN_REPORTS,
                Columns.COMPULSORY,
                Columns.ALLOW_PROVIDED_ELSEWHERE,
                Columns.SORT_ORDER,
                Columns.ALLOW_FUTURE_DATE,
                Columns.DATA_ELEMENT
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE,
                DISPLAY_IN_REPORTS, COMPULSORY, ALLOW_PROVIDED_ELSEWHERE,
                SORT_ORDER, ALLOW_FUTURE_DATE, DATA_ELEMENT
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramStageDataElementModel programStageDataElement = ProgramStageDataElementModel.create(matrixCursor);
        assertThat(programStageDataElement.id()).isEqualTo(ID);
        assertThat(programStageDataElement.uid()).isEqualTo(UID);
        assertThat(programStageDataElement.code()).isEqualTo(CODE);
        assertThat(programStageDataElement.name()).isEqualTo(NAME);
        assertThat(programStageDataElement.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(programStageDataElement.created()).isEqualTo(timeStamp);
        assertThat(programStageDataElement.lastUpdated()).isEqualTo(timeStamp);
        assertThat(programStageDataElement.displayInReports()).isTrue();
        assertThat(programStageDataElement.compulsory()).isFalse();
        assertThat(programStageDataElement.allowProvidedElsewhere()).isFalse();
        assertThat(programStageDataElement.sortOrder()).isEqualTo(SORT_ORDER);
        assertThat(programStageDataElement.allowFutureDate()).isTrue();
        assertThat(programStageDataElement.dataElement()).isEqualTo(DATA_ELEMENT);
    }

    @Test
    public void create_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramStageDataElementModel programStageDataElementModel = ProgramStageDataElementModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .displayInReports(DISPLAY_IN_REPORTS != 0 ? Boolean.TRUE : Boolean.FALSE)
                .compulsory(COMPULSORY != 0 ? Boolean.TRUE : Boolean.FALSE)
                .allowProvidedElsewhere(ALLOW_PROVIDED_ELSEWHERE != 0 ? Boolean.TRUE : Boolean.FALSE)
                .sortOrder(SORT_ORDER)
                .allowFutureDate(ALLOW_FUTURE_DATE != 0 ? Boolean.TRUE : Boolean.FALSE)
                .dataElement(DATA_ELEMENT)
                .build();

        ContentValues contentValues = programStageDataElementModel.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsBoolean(Columns.DISPLAY_IN_REPORTS)).isTrue();
        assertThat(contentValues.getAsBoolean(Columns.COMPULSORY)).isFalse();
        assertThat(contentValues.getAsBoolean(Columns.ALLOW_PROVIDED_ELSEWHERE)).isFalse();
        assertThat(contentValues.getAsInteger(Columns.SORT_ORDER)).isEqualTo(SORT_ORDER);
        assertThat(contentValues.getAsBoolean(Columns.ALLOW_FUTURE_DATE)).isTrue();
        assertThat(contentValues.getAsString(Columns.DATA_ELEMENT)).isEqualTo(DATA_ELEMENT);
    }

}
