package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.MatrixCursor;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.program.ProgramStageContract.Columns;
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
        programStage.put(Columns.ID, id);
        programStage.put(Columns.UID, uid);
        programStage.put(Columns.CODE, CODE);
        programStage.put(Columns.NAME, NAME);
        programStage.put(Columns.DISPLAY_NAME, DISPLAY_NAME);
        programStage.put(Columns.CREATED, DATE);
        programStage.put(Columns.LAST_UPDATED, DATE);
        programStage.put(Columns.EXECUTION_DATE_LABEL, EXECUTION_DATE_LABEL);
        programStage.put(Columns.ALLOW_GENERATE_NEXT_VISIT, ALLOW_GENERATE_NEXT_VISIT);
        programStage.put(Columns.VALID_COMPLETE_ONLY, VALID_COMPLETE_ONLY);
        programStage.put(Columns.REPORT_DATE_TO_USE, REPORT_DATE_TO_USE);
        programStage.put(Columns.OPEN_AFTER_ENROLLMENT, OPEN_AFTER_ENROLLMENT);
        programStage.put(Columns.REPEATABLE, REPEATABLE);
        programStage.put(Columns.CAPTURE_COORDINATES, CAPTURE_COORDINATES);
        programStage.put(Columns.FORM_TYPE, FORM_TYPE.name());
        programStage.put(Columns.DISPLAY_GENERATE_EVENT_BOX, DISPLAY_GENERATE_EVENT_BOX);
        programStage.put(Columns.GENERATED_BY_ENROLMENT_DATE, GENERATED_BY_ENROLMENT_DATE);
        programStage.put(Columns.AUTO_GENERATE_EVENT, AUTO_GENERATE_EVENT);
        programStage.put(Columns.SORT_ORDER, SORT_ORDER);
        programStage.put(Columns.HIDE_DUE_DATE, HIDE_DUE_DATE);
        programStage.put(Columns.BLOCK_ENTRY_FORM, BLOCK_ENTRY_FORM);
        programStage.put(Columns.MIN_DAYS_FROM_START, MIN_DAYS_FROM_START);
        programStage.put(Columns.STANDARD_INTERVAL, STANDARD_INTERVAL);
        programStage.put(Columns.PROGRAM, programId);

        return programStage;
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
                Columns.EXECUTION_DATE_LABEL,
                Columns.ALLOW_GENERATE_NEXT_VISIT,
                Columns.VALID_COMPLETE_ONLY,
                Columns.REPORT_DATE_TO_USE,
                Columns.OPEN_AFTER_ENROLLMENT,
                Columns.REPEATABLE,
                Columns.CAPTURE_COORDINATES,
                Columns.FORM_TYPE,
                Columns.DISPLAY_GENERATE_EVENT_BOX,
                Columns.GENERATED_BY_ENROLMENT_DATE,
                Columns.AUTO_GENERATE_EVENT,
                Columns.SORT_ORDER,
                Columns.HIDE_DUE_DATE,
                Columns.BLOCK_ENTRY_FORM,
                Columns.MIN_DAYS_FROM_START,
                Columns.STANDARD_INTERVAL,
                Columns.PROGRAM
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

        assertThat(contentValues.getAsLong(Columns.ID)).isEqualTo(ID);
        assertThat(contentValues.getAsString(Columns.UID)).isEqualTo(UID);
        assertThat(contentValues.getAsString(Columns.CODE)).isEqualTo(CODE);
        assertThat(contentValues.getAsString(Columns.NAME)).isEqualTo(NAME);
        assertThat(contentValues.getAsString(Columns.DISPLAY_NAME)).isEqualTo(DISPLAY_NAME);
        assertThat(contentValues.getAsString(Columns.CREATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.LAST_UPDATED)).isEqualTo(DATE);
        assertThat(contentValues.getAsString(Columns.EXECUTION_DATE_LABEL)).isEqualTo(EXECUTION_DATE_LABEL);
        assertThat(contentValues.getAsBoolean(Columns.ALLOW_GENERATE_NEXT_VISIT)).isFalse();
        assertThat(contentValues.getAsBoolean(Columns.VALID_COMPLETE_ONLY)).isFalse();
        assertThat(contentValues.getAsString(Columns.REPORT_DATE_TO_USE)).isEqualTo(REPORT_DATE_TO_USE);
        assertThat(contentValues.getAsBoolean(Columns.OPEN_AFTER_ENROLLMENT)).isFalse();
        assertThat(contentValues.getAsBoolean(Columns.REPEATABLE)).isFalse();
        assertThat(contentValues.getAsBoolean(Columns.CAPTURE_COORDINATES)).isTrue();
        assertThat(contentValues.getAsString(Columns.FORM_TYPE)).isEqualTo(FORM_TYPE.name());
        assertThat(contentValues.getAsBoolean(Columns.DISPLAY_GENERATE_EVENT_BOX)).isTrue();
        assertThat(contentValues.getAsBoolean(Columns.GENERATED_BY_ENROLMENT_DATE)).isTrue();
        assertThat(contentValues.getAsBoolean(Columns.AUTO_GENERATE_EVENT)).isFalse();
        assertThat(contentValues.getAsInteger(Columns.SORT_ORDER)).isEqualTo(SORT_ORDER);
        assertThat(contentValues.getAsBoolean(Columns.HIDE_DUE_DATE)).isTrue();
        assertThat(contentValues.getAsBoolean(Columns.BLOCK_ENTRY_FORM)).isFalse();
        assertThat(contentValues.getAsInteger(Columns.MIN_DAYS_FROM_START)).isEqualTo(MIN_DAYS_FROM_START);
        assertThat(contentValues.getAsInteger(Columns.STANDARD_INTERVAL)).isEqualTo(STANDARD_INTERVAL);
        assertThat(contentValues.getAsString(Columns.PROGRAM)).isEqualTo(PROGRAM);
    }
}
