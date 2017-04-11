package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.program.ProgramStageSectionDataElementLinkModel.Columns;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;


@RunWith(AndroidJUnit4.class)
public class ProgramStageSectionDataElementLinkModelTests {
    private static final long ID = 1L;
    private static final String PROGRAM_STAGE_SECTION = "test_program_stage_section_uid";
    private static final String DATA_ELEMENT = "test_data_element_uid";

    @Test
    public void create_shouldCreateModel() {
        MatrixCursor cursor = new MatrixCursor(new String[]{Columns.ID, Columns.PROGRAM_STAGE_SECTION,
                Columns.DATA_ELEMENT});
        cursor.addRow(new Object[]{ID, PROGRAM_STAGE_SECTION, DATA_ELEMENT});
        cursor.moveToFirst();

        ProgramStageSectionDataElementLinkModel model = ProgramStageSectionDataElementLinkModel.create(cursor);
        cursor.close();

        assertThat(model.id()).isEqualTo(ID);
        assertThat(model.programStageSection()).isEqualTo(PROGRAM_STAGE_SECTION);
        assertThat(model.dataElement()).isEqualTo(DATA_ELEMENT);
    }

    @Test
    public void toContentValuesTest() {
        ProgramStageSectionDataElementLinkModel model = ProgramStageSectionDataElementLinkModel.builder()
                .id(ID)
                .programStageSection(PROGRAM_STAGE_SECTION)
                .dataElement(DATA_ELEMENT)
                .build();
        ContentValues contentValues = model.toContentValues();

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.PROGRAM_STAGE_SECTION)).isEqualTo(PROGRAM_STAGE_SECTION);
        assertThat(contentValues.getAsString(Columns.DATA_ELEMENT)).isEqualTo(DATA_ELEMENT);
    }
}
