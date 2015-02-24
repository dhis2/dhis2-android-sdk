package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Map;

/**
 * @author Simen Skogly Russnes on 18.02.15.
 */
@Table
public class ProgramStageDataElement extends BaseModel {

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    @JsonProperty("allowFutureDate")
    @Column
    public boolean allowFutureDate;

    @JsonProperty("sortOrder")
    @Column
    public int sortOrder;

    @JsonProperty("displayInReports")
    @Column
    public boolean displayInReports;

    @JsonProperty("allowProvidedElsewhere")
    @Column
    public boolean allowProvidedElsewhere;

    @JsonProperty("compulsory")
    @Column
    public boolean compulsory;

    @JsonProperty("programStage")
    public void setProgramStage(Map<String, Object> programStage) {
        this.programStage = (String) programStage.get("id");
    }

    @JsonProperty("dataElement")
    public void setDataElement(Map<String, Object> dataElement) {
        this.dataElement = (String) dataElement.get("id");
    }

    @Column(columnType = Column.PRIMARY_KEY)
    public String programStage;

    @Column(columnType = Column.PRIMARY_KEY)
    public String dataElement;

    public boolean isAllowFutureDate() {
        return allowFutureDate;
    }

    public void setAllowFutureDate(boolean allowFutureDate) {
        this.allowFutureDate = allowFutureDate;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean getAllowProvidedElsewhere() {
        return allowProvidedElsewhere;
    }

    public void setAllowProvidedElsewhere(boolean allowProvidedElsewhere) {
        this.allowProvidedElsewhere = allowProvidedElsewhere;
    }

    public boolean isCompulsory() {
        return compulsory;
    }

    public void setCompulsory(boolean compulsory) {
        this.compulsory = compulsory;
    }

    public String getProgramStage() {
        return programStage;
    }

    public void setProgramStage(String programStage) {
        this.programStage = programStage;
    }

    public String getDataElement() {
        return dataElement;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }
}
