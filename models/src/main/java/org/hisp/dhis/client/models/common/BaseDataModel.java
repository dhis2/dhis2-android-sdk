package org.hisp.dhis.client.models.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BaseDataModel implements DataModel {

    @JsonIgnore
    public abstract State state();



}
