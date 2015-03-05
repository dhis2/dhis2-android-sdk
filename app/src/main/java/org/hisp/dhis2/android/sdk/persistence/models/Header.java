package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Header representation as seen in the webAPI endpoint trackedEntityInstances/
 * @author Simen Skogly Russnes on 03.03.15.
 */
public class Header {

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {}

    @JsonProperty("name")
    public String name;

    @JsonProperty("column")
    public String column;

    @JsonProperty("type")
    public String type;

    @JsonProperty("hidden")
    public boolean hidden;

    @JsonProperty("meta")
    public boolean meta;

}
