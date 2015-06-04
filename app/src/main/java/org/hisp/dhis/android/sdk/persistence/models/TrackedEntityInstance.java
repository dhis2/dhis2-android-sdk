package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.hisp.dhis.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.utils.Utils;

import java.util.List;

/**
 * @author Simen Skogly Russnes on 03.03.15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(databaseName = Dhis2Database.NAME)
public class TrackedEntityInstance extends BaseSerializableModel {

    public TrackedEntityInstance() {

    }

    public TrackedEntityInstance (Program program, String organisationUnit) {
        fromServer = false;
        trackedEntityInstance = Utils.getTempUid();
        trackedEntity = program.getTrackedEntity().getId();
        //created = Utils.getCurrentTime();
        //lastUpdated = Utils.getCurrentTime();
        orgUnit = organisationUnit;
    }

    @JsonIgnore
    @Column
    private boolean fromServer = true;

    @JsonIgnore
    @Column
    @PrimaryKey(autoincrement = true)
    public long localId = -1;

    @JsonIgnore
    @Column
    @Unique
    public String trackedEntityInstance;

    @JsonProperty("trackedEntityInstance")
    public void setTrackedEntityInstance(String trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    /**
     * Should only be used by Jackson so that event is included only if its non-local generated
     * Use Event.event instead to access it.
     */
    @JsonProperty("trackedEntityInstance")
    public String getTrackedEntityInstance() {
        if(Utils.isLocal(trackedEntityInstance))
            return null;
        else return trackedEntityInstance;
    }

    @JsonProperty("trackedEntity")
    @Column
    private String trackedEntity;

    //@JsonProperty("created")
    @JsonIgnore
    @Column
    private String created;

    //@JsonProperty("lastUpdated")
    @JsonIgnore
    @Column
    private String lastUpdated;

    @JsonProperty("orgUnit")
    @Column
    private String orgUnit;

    @JsonProperty("attributes")
//    @JsonIgnore
    public List<TrackedEntityAttributeValue> getAttributes() {
        return DataValueController.getTrackedEntityAttributeValues(localId);
    }

    @Override
    public void save() {
        /* check if there is an existing tei with the same UID to avoid duplicates */
        TrackedEntityInstance existingTei = DataValueController.
                getTrackedEntityInstance(trackedEntityInstance);
        boolean exists = false;
        if(existingTei != null) {
            localId = existingTei.localId;
            exists = true;
        }
        if(getTrackedEntityInstance() == null && DataValueController.getTrackedEntityInstance(localId) != null) {
            //means that the tei is local and has previosuly been saved
            //then we don't want to update the tei reference in fear of overwriting
            //an updated reference from server while the item has been loaded in memory
            //unfortunately a bit of hard coding I suppose but it's important to verify data integrity
            updateManually();
        } else {
            super.save();
        }
    }

    /**
     * Updates manually without touching UIDs the fields that are modifiable by user.
     * This will and should only be called if the enrollment has a locally created temp event reference
     * and has previously been saved, so that it has a localId.
     */
    public void updateManually() {
        new Update(TrackedEntityInstance.class).set(
                Condition.column(TrackedEntityInstance$Table.FROMSERVER).is(fromServer))
                .where(Condition.column(TrackedEntityInstance$Table.LOCALID).is(localId)).queryClose();
    }

    @Override
    public void update() {
        save();
    }

    public boolean getFromServer() {
        return fromServer;
    }

    public long getLocalId() {
        return localId;
    }

    public String getTrackedEntity() {
        return trackedEntity;
    }

    public String getCreated() {
        return created;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public void setFromServer(boolean fromServer) {
        this.fromServer = fromServer;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public void setTrackedEntity(String trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }
}
