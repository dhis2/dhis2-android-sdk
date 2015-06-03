package org.hisp.dhis.android.sdk.persistence.models;

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
    private String name;

    @JsonProperty("column")
    private String column;

    @JsonProperty("type")
    private String type;

    @JsonProperty("hidden")
    private boolean hidden;

    @JsonProperty("meta")
    private boolean meta;


    public boolean getMeta() {
        return meta;
    }

    public boolean getHidden() {
        return hidden;
    }

    public String getType() {
        return type;
    }

    public String getColumn() {
        return column;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMeta() {
        return meta;
    }

    public void setMeta(boolean meta) {
        this.meta = meta;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
