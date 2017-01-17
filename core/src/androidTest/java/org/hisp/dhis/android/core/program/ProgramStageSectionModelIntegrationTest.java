package org.hisp.dhis.android.core.program;

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

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                ProgramStageSectionModel.Columns.ID, ProgramStageSectionModel.Columns.UID, ProgramStageSectionModel.Columns.CODE, ProgramStageSectionModel.Columns.NAME,
                ProgramStageSectionModel.Columns.DISPLAY_NAME, ProgramStageSectionModel.Columns.CREATED, ProgramStageSectionModel.Columns.LAST_UPDATED,
                ProgramStageSectionModel.Columns.SORT_ORDER, ProgramStageSectionModel.Columns.PROGRAM_STAGE
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

        assertThat(contentValues.getAsLong(ProgramStageSectionModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsInteger(ProgramStageSectionModel.Columns.SORT_ORDER)).isEqualTo(SORT_ORDER);
        assertThat(contentValues.getAsString(ProgramStageSectionModel.Columns.PROGRAM_STAGE)).isEqualTo(PROGRAM_STAGE);
    }
}
