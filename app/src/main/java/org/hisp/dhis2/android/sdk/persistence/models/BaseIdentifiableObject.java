package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * @author Simen Skogly Russnes on 18.02.15.
 */
public class BaseIdentifiableObject extends BaseModel{

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    @JsonProperty("id")
    @Column(columnType = Column.PRIMARY_KEY)
    public String id;

    @JsonProperty("name")
    @Column
    public String name;

    @JsonProperty("created")
    @Column
    public String created;

    @JsonProperty("lastUpdated")
    @Column
    public String lastUpdated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
