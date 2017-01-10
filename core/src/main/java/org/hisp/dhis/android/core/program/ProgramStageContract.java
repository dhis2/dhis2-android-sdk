package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectContract;

public class ProgramStageContract {
    public interface Columns extends BaseIdentifiableObjectContract.Columns {
        String EXECUTION_DATE_LABEL = "executionDateLabel";
        String ALLOW_GENERATE_NEXT_VISIT = "allowGenerateNextVisit";
        String VALID_COMPLETE_ONLY = "validCompleteOnly";
        String REPORT_DATE_TO_USE = "reportDateToUse";
        String OPEN_AFTER_ENROLLMENT = "openAfterEnrollment";
        String REPEATABLE = "repeatable";
        String CAPTURE_COORDINATES = "captureCoordinates";
        String FORM_TYPE = "formType";
        String DISPLAY_GENERATE_EVENT_BOX = "displayGenerateEventBox";
        String GENERATED_BY_ENROLMENT_DATE = "generatedByEnrollmentDate";
        String AUTO_GENERATE_EVENT = "autoGenerateEvent";
        String SORT_ORDER = "sortOrder";
        String HIDE_DUE_DATE = "hideDueDate";
        String BLOCK_ENTRY_FORM = "blockEntryForm";
        String MIN_DAYS_FROM_START = "minDaysFromStart";
        String STANDARD_INTERVAL = "standardInterval";
        String PROGRAM = "program";
    }
}
