package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum HomeFilter {
    @JsonProperty("date")
    DATE,

    @JsonProperty("syncStatus")
    SYNC_STATUS,

    @JsonProperty("organisationUnit")
    ORG_UNIT,

    @JsonProperty("assignedToMe")
    ASSIGNED_TO_ME,
}
