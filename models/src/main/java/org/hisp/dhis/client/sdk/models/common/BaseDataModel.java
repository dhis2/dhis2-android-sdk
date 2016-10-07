package org.hisp.dhis.client.sdk.models.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseDataModel implements DataModel {

    @JsonIgnore
    private State state;

    public BaseDataModel() {
        // Explicitly empty constructor
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }
}
