package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.FormType;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProgramStageModelIntegrationTest {
    private static final long ID = 11L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";

    private static final String EXECUTION_DATE_LABEL = "test_executionDateLabel";
    private static final Integer ALLOW_GENERATE_NEXT_VISIT = 0;
    private static final Integer VALID_COMPLETE_ONLY = 0;
    private static final String REPORT_DATE_TO_USE = "test_reportDateToUse";
    private static final Integer OPEN_AFTER_ENROLLMENT = 0;

    private static final Integer REPEATABLE = 0;
    private static final Integer CAPTURE_COORDINATES = 1;
    private static final FormType FORM_TYPE = FormType.DEFAULT;
    private static final Integer DISPLAY_GENERATE_EVENT_BOX = 1;
    private static final Integer GENERATED_BY_ENROLMENT_DATE = 1;
    private static final Integer AUTO_GENERATE_EVENT = 0;
    private static final Integer SORT_ORDER = 0;
    private static final Integer HIDE_DUE_DATE = 1;
    private static final Integer BLOCK_ENTRY_FORM = 0;
    private static final Integer MIN_DAYS_FROM_START = 5;
    private static final Integer STANDARD_INTERVAL = 7;
    private static final String PROGRAM = "test_program";

    // used for timestamps
    private static final String DATE = "2017-01-05T15:39:00.000";

    public static ContentValues create(long id, String uid, String programId) {
        ContentValues programStage = new ContentValues();
        programStage.put(ProgramStageModel.Columns.ID, id);
        programStage.put(ProgramStageModel.Columns.UID, uid);
        programStage.put(ProgramStageModel.Columns.CODE, CODE);
        programStage.put(ProgramStageModel.Columns.NAME, NAME);
        programStage.put(ProgramStageModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programStage.put(ProgramStageModel.Columns.CREATED, DATE);
        programStage.put(ProgramStageModel.Columns.LAST_UPDATED, DATE);
        programStage.put(ProgramStageModel.Columns.EXECUTION_DATE_LABEL, EXECUTION_DATE_LABEL);
        programStage.put(ProgramStageModel.Columns.ALLOW_GENERATE_NEXT_VISIT, ALLOW_GENERATE_NEXT_VISIT);
        programStage.put(ProgramStageModel.Columns.VALID_COMPLETE_ONLY, VALID_COMPLETE_ONLY);
        programStage.put(ProgramStageModel.Columns.REPORT_DATE_TO_USE, REPORT_DATE_TO_USE);
        programStage.put(ProgramStageModel.Columns.OPEN_AFTER_ENROLLMENT, OPEN_AFTER_ENROLLMENT);
        programStage.put(ProgramStageModel.Columns.REPEATABLE, REPEATABLE);
        programStage.put(ProgramStageModel.Columns.CAPTURE_COORDINATES, CAPTURE_COORDINATES);
        programStage.put(ProgramStageModel.Columns.FORM_TYPE, FORM_TYPE.name());
        programStage.put(ProgramStageModel.Columns.DISPLAY_GENERATE_EVENT_BOX, DISPLAY_GENERATE_EVENT_BOX);
        programStage.put(ProgramStageModel.Columns.GENERATED_BY_ENROLMENT_DATE, GENERATED_BY_ENROLMENT_DATE);
        programStage.put(ProgramStageModel.Columns.AUTO_GENERATE_EVENT, AUTO_GENERATE_EVENT);
        programStage.put(ProgramStageModel.Columns.SORT_ORDER, SORT_ORDER);
        programStage.put(ProgramStageModel.Columns.HIDE_DUE_DATE, HIDE_DUE_DATE);
        programStage.put(ProgramStageModel.Columns.BLOCK_ENTRY_FORM, BLOCK_ENTRY_FORM);
        programStage.put(ProgramStageModel.Columns.MIN_DAYS_FROM_START, MIN_DAYS_FROM_START);
        programStage.put(ProgramStageModel.Columns.STANDARD_INTERVAL, STANDARD_INTERVAL);
        programStage.put(ProgramStageModel.Columns.PROGRAM, programId);

        return programStage;
    }

    @Test
    public void create_shouldConvertToModel() throws ParseException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                ProgramStageModel.Columns.ID,
                ProgramStageModel.Columns.UID,
                ProgramStageModel.Columns.CODE,
                ProgramStageModel.Columns.NAME,
                ProgramStageModel.Columns.DISPLAY_NAME,
                ProgramStageModel.Columns.CREATED,
                ProgramStageModel.Columns.LAST_UPDATED,
                ProgramStageModel.Columns.EXECUTION_DATE_LABEL,
                ProgramStageModel.Columns.ALLOW_GENERATE_NEXT_VISIT,
                ProgramStageModel.Columns.VALID_COMPLETE_ONLY,
                ProgramStageModel.Columns.REPORT_DATE_TO_USE,
                ProgramStageModel.Columns.OPEN_AFTER_ENROLLMENT,
                ProgramStageModel.Columns.REPEATABLE,
                ProgramStageModel.Columns.CAPTURE_COORDINATES,
                ProgramStageModel.Columns.FORM_TYPE,
                ProgramStageModel.Columns.DISPLAY_GENERATE_EVENT_BOX,
                ProgramStageModel.Columns.GENERATED_BY_ENROLMENT_DATE,
                ProgramStageModel.Columns.AUTO_GENERATE_EVENT,
                ProgramStageModel.Columns.SORT_ORDER,
                ProgramStageModel.Columns.HIDE_DUE_DATE,
                ProgramStageModel.Columns.BLOCK_ENTRY_FORM,
                ProgramStageModel.Columns.MIN_DAYS_FROM_START,
                ProgramStageModel.Columns.STANDARD_INTERVAL,
                ProgramStageModel.Columns.PROGRAM
        });

        matrixCursor.addRow(new Object[]{
                ID, UID, CODE, NAME, DISPLAY_NAME,
                DATE, DATE,
                EXECUTION_DATE_LABEL,
                ALLOW_GENERATE_NEXT_VISIT,
                VALID_COMPLETE_ONLY,
                REPORT_DATE_TO_USE,
                OPEN_AFTER_ENROLLMENT,
                REPEATABLE,
                CAPTURE_COORDINATES,
                FORM_TYPE,
                DISPLAY_GENERATE_EVENT_BOX,
                GENERATED_BY_ENROLMENT_DATE,
                AUTO_GENERATE_EVENT,
                SORT_ORDER,
                HIDE_DUE_DATE,
                BLOCK_ENTRY_FORM,
                MIN_DAYS_FROM_START,
                STANDARD_INTERVAL,
                PROGRAM
        });

        matrixCursor.moveToFirst();

        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramStageModel programStage = ProgramStageModel.create(matrixCursor);
        assertThat(programStage.id()).isEqualTo(ID);
        assertThat(programStage.uid()).isEqualTo(UID);
        assertThat(programStage.code()).isEqualTo(CODE);
        assertThat(programStage.name()).isEqualTo(NAME);
        assertThat(programStage.displayName()).isEqualTo(DISPLAY_NAME);
        assertThat(programStage.created()).isEqualTo(timeStamp);
        assertThat(programStage.lastUpdated()).isEqualTo(timeStamp);
        assertThat(programStage.executionDateLabel()).isEqualTo(EXECUTION_DATE_LABEL);
        assertThat(programStage.allowGenerateNextVisit()).isFalse();
        assertThat(programStage.validCompleteOnly()).isFalse();
        assertThat(programStage.reportDateToUse()).isEqualTo(REPORT_DATE_TO_USE);
        assertThat(programStage.openAfterEnrollment()).isFalse();
        assertThat(programStage.repeatable()).isFalse();
        assertThat(programStage.captureCoordinates()).isTrue();
        assertThat(programStage.formType()).isEqualTo(FORM_TYPE);
        assertThat(programStage.displayGenerateEventBox()).isTrue();
        assertThat(programStage.generatedByEnrollmentDate()).isTrue();
        assertThat(programStage.autoGenerateEvent()).isFalse();
        assertThat(programStage.sortOrder()).isEqualTo(SORT_ORDER);
        assertThat(programStage.hideDueDate()).isTrue();
        assertThat(programStage.blockEntryForm()).isFalse();
        assertThat(programStage.minDaysFromStart()).isEqualTo(MIN_DAYS_FROM_START);
        assertThat(programStage.standardInterval()).isEqualTo(STANDARD_INTERVAL);
        assertThat(programStage.program()).isEqualTo(PROGRAM);
    }

    @Test
    public void create_shouldConvertToContentValues() throws ParseException {
        Date timeStamp = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        ProgramStageModel programStage = ProgramStageModel.builder()
                .id(ID)
                .uid(UID)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(timeStamp)
                .lastUpdated(timeStamp)
                .executionDateLabel(EXECUTION_DATE_LABEL)
                .allowGenerateNextVisit(ALLOW_GENERATE_NEXT_VISIT != 0 ? Boolean.TRUE : Boolean.FALSE)
                .validCompleteOnly(VALID_COMPLETE_ONLY != 0 ? Boolean.TRUE : Boolean.FALSE)
                .reportDateToUse(REPORT_DATE_TO_USE)
                .openAfterEnrollment(OPEN_AFTER_ENROLLMENT != 0 ? Boolean.TRUE : Boolean.FALSE)
                .repeatable(REPEATABLE != 0 ? Boolean.TRUE : Boolean.FALSE)
                .captureCoordinates(CAPTURE_COORDINATES != 0 ? Boolean.TRUE : Boolean.FALSE)
                .formType(FORM_TYPE)
                .displayGenerateEventBox(DISPLAY_GENERATE_EVENT_BOX != 0 ? Boolean.TRUE : Boolean.FALSE)
                .generatedByEnrollmentDate(GENERATED_BY_ENROLMENT_DATE != 0 ? Boolean.TRUE : Boolean.FALSE)
                .autoGenerateEvent(AUTO_GENERATE_EVENT != 0 ? Boolean.TRUE : Boolean.FALSE)
                .sortOrder(SORT_ORDER)
                .hideDueDate(HIDE_DUE_DATE != 0 ? Boolean.TRUE : Boolean.FALSE)
                .blockEntryForm(BLOCK_ENTRY_FORM != 0 ? Boolean.TRUE : Boolean.FALSE)
                .minDaysFromStart(MIN_DAYS_FROM_START)
                .standardInterval(STANDARD_INTERVAL)
                .program(PROGRAM)
                .build();

        ContentValues contentValues = programStage.toContentValues();

        assertThat(contentValues.getAsLong(ProgramStageModel.Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(ProgramStageModel.Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(ProgramStageModel.Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(ProgramStageModel.Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(ProgramStageModel.Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(ProgramStageModel.Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramStageModel.Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(ProgramStageModel.Columns.EXECUTION_DATE_LABEL)).isEqualTo(EXECUTION_DATE_LABEL);
        assertThat(contentValues.getAsBoolean(ProgramStageModel.Columns.ALLOW_GENERATE_NEXT_VISIT)).isFalse();
        assertThat(contentValues.getAsBoolean(ProgramStageModel.Columns.VALID_COMPLETE_ONLY)).isFalse();
        assertThat(contentValues.getAsString(ProgramStageModel.Columns.REPORT_DATE_TO_USE)).isEqualTo(REPORT_DATE_TO_USE);
        assertThat(contentValues.getAsBoolean(ProgramStageModel.Columns.OPEN_AFTER_ENROLLMENT)).isFalse();
        assertThat(contentValues.getAsBoolean(ProgramStageModel.Columns.REPEATABLE)).isFalse();
        assertThat(contentValues.getAsBoolean(ProgramStageModel.Columns.CAPTURE_COORDINATES)).isTrue();
        assertThat(contentValues.getAsString(ProgramStageModel.Columns.FORM_TYPE)).isEqualTo(FORM_TYPE.name());
        assertThat(contentValues.getAsBoolean(ProgramStageModel.Columns.DISPLAY_GENERATE_EVENT_BOX)).isTrue();
        assertThat(contentValues.getAsBoolean(ProgramStageModel.Columns.GENERATED_BY_ENROLMENT_DATE)).isTrue();
        assertThat(contentValues.getAsBoolean(ProgramStageModel.Columns.AUTO_GENERATE_EVENT)).isFalse();
        assertThat(contentValues.getAsInteger(ProgramStageModel.Columns.SORT_ORDER)).isEqualTo(SORT_ORDER);
        assertThat(contentValues.getAsBoolean(ProgramStageModel.Columns.HIDE_DUE_DATE)).isTrue();
        assertThat(contentValues.getAsBoolean(ProgramStageModel.Columns.BLOCK_ENTRY_FORM)).isFalse();
        assertThat(contentValues.getAsInteger(ProgramStageModel.Columns.MIN_DAYS_FROM_START)).isEqualTo(MIN_DAYS_FROM_START);
        assertThat(contentValues.getAsInteger(ProgramStageModel.Columns.STANDARD_INTERVAL)).isEqualTo(STANDARD_INTERVAL);
        assertThat(contentValues.getAsString(ProgramStageModel.Columns.PROGRAM)).isEqualTo(PROGRAM);
    }
}
