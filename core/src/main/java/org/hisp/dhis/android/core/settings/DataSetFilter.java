package org.hisp.dhis.android.core.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DataSetFilter {
    @JsonProperty("syncStatus")
    SYN_STATUS,

    @JsonProperty("organisationUnit")
    ORG_UNIT,

    @JsonProperty("assignedToMe")
    ASSIGNED_TO_ME,

    @JsonProperty("period")
    PERIOD,

    @JsonProperty("categoryCombo")
    CAT_COMBO
}
