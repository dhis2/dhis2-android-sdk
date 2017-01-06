package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.program.ProgramStageSectionContract.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProgramStageSectionModelIntegrationTest {
    private static final long ID = 2L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final Integer SORT_ORDER = 7;
    private static final String PROGRAM_STAGE = "test_program_stage";

    // timestamp
    private static final String DATE = "2017-01-05T10:26:00.000";

    public static ContentValues create(long id, String uid) {
        ContentValues programStageSection = new ContentValues();
        programStageSection.put(Columns.ID, id);
        programStageSection.put(Columns.UID, uid);
        programStageSection.put(Columns.CODE, CODE);
        programStageSection.put(Columns.NAME, NAME);
        programStageSection.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        programStageSection.put(Columns.CREATED, DATE);
        programStageSection.put(Columns.LAST_UPDATED, DATE);
        programStageSection.put(Columns.SORT_ORDER, SORT_ORDER);
        programStageSection.put(Columns.PROGRAM_STAGE, PROGRAM_STAGE);

        return programStageSection;
    }

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                Columns.ID, Columns.UID, Columns.CODE, Columns.NAME,
                Columns.DISPLAY_NAME, Columns.CREATED, Columns.LAST_UPDATED,
                Columns.SORT_ORDER, Columns.PROGRAM_STAGE
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME, DATE, DATE,
                SORT_ORDER, PROGRAM_STAGE
        });

        // move cursor to first item before reading
        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramStageSectionModel programStageSection = ProgramStageSectionModel.create(matrixCursor);
        assertThat(programStageSection.id()).isEqualTo(ID);
        assertThat(programStageSection.uid()).isEqualTo(UID);
        assertThat(programStageSection.code()).isEqualTo(CODE);
        assertThat(programStageSection.name()).isEqualTo(NAME);
        assertThat(programStageSection.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(programStageSection.created()).isEqualTo(timeStamp);
        assertThat(programStageSection.lastUpdated()).isEqualTo(timeStamp);
        assertThat(programStageSection.sortOrder()).isEqualTo(SORT_ORDER);
        assertThat(programStageSection.programStage()).isEqualTo(PROGRAM_STAGE);
    }

    @Test
    public void create_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramStageSectionModel programStageSection = ProgramStageSectionModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .sortOrder(SORT_ORDER)
                .programStage(PROGRAM_STAGE)
                .build();

        ContentValues contentValues = programStageSection.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsInteger(Columns.SORT_ORDER)).isEqualTo(SORT_ORDER);
        assertThat(contentValues.getAsString(Columns.PROGRAM_STAGE)).isEqualTo(PROGRAM_STAGE);
    }
}
