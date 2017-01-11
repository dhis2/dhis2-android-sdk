package org.hisp.dhis.android.core.program;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.program.ProgramStageContract.Columns;
public class CreateProgramStageUtils {
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
}
