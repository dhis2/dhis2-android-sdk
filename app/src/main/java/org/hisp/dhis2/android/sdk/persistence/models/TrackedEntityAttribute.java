package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
@Table
public class TrackedEntityAttribute extends BaseNameableObject {

    @JsonProperty("unique")
    @Column
    public boolean unique;

    @JsonProperty("programScope")
    @Column
    public boolean programScope;

    @JsonProperty("orgunitScope")
    @Column
    public boolean orgunitScope;

    @JsonProperty("displayInListNoProgram")
    @Column
    public boolean displayInListNoProgram;

    @JsonProperty("displayOnVisitSchedule")
    @Column
    public boolean displayOnVisitSchedule;

    @JsonProperty("externalAccess")
    @Column
    public boolean externalAccess;

    @JsonProperty("valueType")
    @Column
    public String valueType;

    @JsonProperty("confidential")
    @Column
    public boolean confidential;

    @JsonProperty("inherit")
    @Column
    public boolean inherit;

    @JsonProperty("sortOrderVisitSchedule")
    @Column
    public int sortOrderVisitSchedule;

    @JsonProperty("dimension")
    @Column
    public String dimension;

    @JsonProperty("displayName")
    @Column
    public String displayName;

    @JsonProperty("sortOrderInListNoProgram")
    @Column
    public int sortOrderInListNoProgram;

}
