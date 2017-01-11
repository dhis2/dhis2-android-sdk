package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.common.BaseNameableObjectContract;

public class ProgramContract {
    public interface Columns extends BaseNameableObjectContract.Columns {
        String VERSION = "version";
        String ONLY_ENROLL_ONCE = "onlyEnrollOnce";
        String ENROLLMENT_DATE_LABEL = "enrollmentDateLabel";
        String DISPLAY_INCIDENT_DATE = "displayIncidentDate";
        String INCIDENT_DATE_LABEL = "incidentDateLabel";
        String REGISTRATION = "registration";
        String SELECT_ENROLLMENT_DATES_IN_FUTURE = "selectEnrollmentDatesInFuture";
        String DATA_ENTRY_METHOD = "dataEntryMethod";
        String IGNORE_OVERDUE_EVENTS = "ignoreOverdueEvents";
        String RELATIONSHIP_FROM_A = "relationshipFromA";
        String SELECT_INCIDENT_DATES_IN_FUTURE = "selectIncidentDatesInFuture";
        String CAPTURE_COORDINATES = "captureCoordinates";
        String USE_FIRST_STAGE_DURING_REGISTRATION = "useFirstStageDuringRegistration";
        String DISPLAY_FRONT_PAGE_LIST = "displayFrontPageList";
        String PROGRAM_TYPE = "programType";
        String RELATIONSHIP_TYPE = "relationshipType";
        String RELATIONSHIP_TEXT = "relationshipText";
        String RELATED_PROGRAM = "relatedProgram";
        String TRACKED_ENTITY = "trackedEntity";
    }
}
