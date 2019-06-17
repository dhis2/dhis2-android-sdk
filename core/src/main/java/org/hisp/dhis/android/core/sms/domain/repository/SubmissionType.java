package org.hisp.dhis.android.core.sms.domain.repository;

public enum SubmissionType {
    SIMPLE_EVENT("simple"),
    TRACKER_EVENT("tracker"),
    ENROLLMENT("enrollment"),
    DATA_SET("dataset"),
    RELATIONSHIP("relationship"),
    DELETION("deletion");

    private final String text;

    SubmissionType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}