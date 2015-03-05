package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * @author Simen Skogly Russnes on 03.03.15.
 */
@Table
public class TrackedEntityInstance extends BaseModel {

    @JsonProperty("trackedEntity")
    @Column
    public String trackedEntity;

    @JsonProperty("created")
    @Column
    public String created;

    @JsonProperty("lastUpdated")
    @Column
    public String lastUpdated;

    @JsonProperty("trackedEntityInstance")
    @Column(columnType = Column.PRIMARY_KEY)
    public String trackedEntityInstance;

    @JsonProperty("orgUnit")
    @Column
    public String orgUnit;

}
