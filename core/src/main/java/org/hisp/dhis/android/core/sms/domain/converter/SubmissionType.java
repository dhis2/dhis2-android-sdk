package org.hisp.dhis.android.core.sms.domain.converter;

final class SubmissionType {
    public final static String AGGREGATE_DATA_SET = "DS";
    public final static String PROGRAM_EVENT_NO_REG = "SE";
    public final static String TRACKED_ENTITY_INSTANCE = "EN";
    public final static String ONE_WAY_RELATIONSHIP = "RS";
    public final static String TRACKER_EVENT = "TE";
    public final static String DELETE = "DE";

    private SubmissionType() {
    }
}