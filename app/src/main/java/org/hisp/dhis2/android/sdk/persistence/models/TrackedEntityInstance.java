package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis2.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;

import java.util.List;

/**
 * @author Simen Skogly Russnes on 03.03.15.
 */
@Table
public class TrackedEntityInstance extends BaseModel {

    @JsonIgnore
    @Column
    public boolean fromServer = true;

    @JsonIgnore
    @Column(columnType = Column.PRIMARY_KEY_AUTO_INCREMENT)
    public long localId;

    @JsonProperty("trackedEntityInstance")
    @Column(unique = true)
    public String trackedEntityInstance;

    @JsonProperty("trackedEntity")
    @Column
    public String trackedEntity;

    @JsonProperty("created")
    @Column
    public String created;

    @JsonProperty("lastUpdated")
    @Column
    public String lastUpdated;

    @JsonProperty("orgUnit")
    @Column
    public String orgUnit;

    @JsonProperty("attributes")
    public List<TrackedEntityAttributeValue> getAttributes() {
        return DataValueController.getTrackedEntityAttributeValues(this.trackedEntityInstance);
    }

    @Override
    public void save(boolean async) {
        /* check if there is an existing tei with the same UID to avoid duplicates */
        TrackedEntityInstance existingTei = DataValueController.
                getTrackedEntityInstance(trackedEntityInstance);
        if(existingTei != null) {
            localId = existingTei.localId;
        }
        super.save(async);
    }

}
