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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseDataModel that = (BaseDataModel) o;
        return state == that.state;
    }

    @Override
    public int hashCode() {
        return state != null ? state.hashCode() : 0;
    }
}
