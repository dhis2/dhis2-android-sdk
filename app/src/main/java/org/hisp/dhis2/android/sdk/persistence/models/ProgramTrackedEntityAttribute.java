package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Simen Skogly Russnes on 20.02.15.
 */
@Table
public class ProgramTrackedEntityAttribute extends BaseModel {

    @JsonProperty("allowFutureDate")
    @Column
    public boolean allowFutureDate;

    @JsonProperty("displayInList")
    @Column
    public boolean displayInList;

    @JsonProperty("mandatory")
    @Column
    public boolean mandatory;

    @Column(columnType = Column.PRIMARY_KEY)
    public String program;

    @JsonProperty("trackedEntityAttribute")
    public void setTrackedEntityAttribute(Map<String, Object> trackedEntityAttribute) {
            this.trackedEntityAttribute =  (String) trackedEntityAttribute.get("id") ;
    }

    @Column(columnType = Column.PRIMARY_KEY)
    public String trackedEntityAttribute;

    public boolean isAllowFutureDate() {
        return allowFutureDate;
    }

    public void setAllowFutureDate(boolean allowFutureDate) {
        this.allowFutureDate = allowFutureDate;
    }

    public boolean isDisplayInList() {
        return displayInList;
    }

    public void setDisplayInList(boolean displayInList) {
        this.displayInList = displayInList;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getTrackedEntityAttribute() {
        return trackedEntityAttribute;
    }

    public void setTrackedEntityAttribute(String trackedEntityAttribute) {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }
}
