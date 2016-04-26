package org.hisp.dhis.android.sdk.network.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportCount2 {

    @JsonProperty("imported")
    private int imported;

    @JsonProperty("updated")
    private int updated;

    @JsonProperty("ignored")
    private int ignored;

    @JsonProperty("deleted")
    private int deleted;

    public ImportCount2() {
        // explicit empty constructor
    }

    public int getImported() {
        return imported;
    }

    public int getUpdated() {
        return updated;
    }

    public int getIgnored() {
        return ignored;
    }

    public int getDeleted() {
        return deleted;
    }
}
