package org.hisp.dhis.client.models.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.Nullable;

// TODO: Tests
public abstract class BaseDataModel extends BaseModel implements DataModel {

    @Override
    @Nullable
    @JsonIgnore
    public abstract State state();

    protected static abstract class Builder<T extends Builder> extends BaseModel.Builder<Builder> {
        public abstract T state(State state);
    }
}
