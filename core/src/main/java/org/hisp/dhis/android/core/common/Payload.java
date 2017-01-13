package org.hisp.dhis.android.core.common;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Payload<T> {

    @JsonProperty("pager")
    Pager pager;

    @JsonIgnore
    List<T> items;

    public Payload() {
        // explicit empty constructor
    }

    @JsonAnySetter
    @SuppressWarnings("unused")
    /* package */ void processItems(String key, List<T> values) {
        this.items = values;
    }

    public Pager pager() {
        return this.pager;
    }

    public List<T> items() {
        return this.items;
    }
}
