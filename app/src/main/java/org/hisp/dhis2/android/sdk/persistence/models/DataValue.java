package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
@JsonIgnoreProperties({"eventId"})
@Table
public class DataValue extends BaseModel {

    @Column(columnType = Column.PRIMARY_KEY)
    public String eventId;

    @JsonProperty("value")
    @Column
    public String value;

    @JsonProperty("dataElement")
    @Column(columnType = Column.PRIMARY_KEY)
    public String dataElement;

    @JsonProperty("providedElsewhere")
    @Column
    public boolean providedElsewhere;

    @JsonProperty("storedBy")
    @Column
    public String storedBy;

    public DataValue() {}

    public DataValue(String eventId, String value, String dataElement, boolean providedElsewhere,
                     String storedBy) {
        this.eventId = eventId;
        this.value = value;
        this.dataElement = dataElement;
        this.providedElsewhere = providedElsewhere;
        this.storedBy = storedBy;
    }

}
