package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProgramFilter {
    @JsonProperty("eventDate")
    EVENT_DATE,

    @JsonProperty("syncStatus")
    SYNC_STATUS,

    @JsonProperty("eventStatus")
    EVENT_STATUS,

    @JsonProperty("assignedToMe")
    ASSIGNED_TO_ME,

    @JsonProperty("enrollmentDate")
    ENROLLMENT_DATE,

    @JsonProperty("enrollmentStatus")
    ENROLLMENT_STATUS,

    @JsonProperty("organisationUnit")
    ORG_UNIT,

    @JsonProperty("categoryCombo")
    CAT_COMBO
}
