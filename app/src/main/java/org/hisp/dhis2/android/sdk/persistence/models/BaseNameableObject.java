package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
public class BaseNameableObject extends BaseIdentifiableObject {

    @JsonProperty("shortName")
    @Column
    public String shortName;

    @JsonProperty("description")
    @Column
    public String description;

}
