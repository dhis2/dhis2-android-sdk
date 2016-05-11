package org.hisp.dhis.android.sdk.network.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Conflict2 {


    @JsonProperty("object")
    private String object;

    @JsonProperty("value")
    private String value;

    public Conflict2() {
        // explicit empty constructor
    }

    public String getObject() {
        return object;
    }

    public String getValue() {
        return value;
    }
}
