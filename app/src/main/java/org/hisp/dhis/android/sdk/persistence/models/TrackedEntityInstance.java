package org.hisp.dhis.android.sdk.persistence.models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.utils.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 03.03.15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(databaseName = Dhis2Database.NAME)
public class TrackedEntityInstance extends BaseSerializableModel implements Serializable {

    @JsonIgnore
    @Column(name = "trackedEntityInstance")
    @Unique
    String trackedEntityInstance;

    @JsonProperty("trackedEntity")
    @Column(name = "trackedEntity")
    String trackedEntity;

    @JsonProperty("orgUnit")
    @Column(name = "orgUnit")
    String orgUnit;

    @JsonProperty("attributes")
    List<TrackedEntityAttributeValue> attributes;

    @JsonProperty("relationships")
    List<Relationship> relationships;

    public TrackedEntityInstance() {
    }

    public TrackedEntityInstance(Program program, String organisationUnit) {
        fromServer = false;
        trackedEntityInstance = Utils.getTempUid();
        trackedEntity = program.getTrackedEntity().getId();
        //created = Utils.getCurrentTime();
        //lastUpdated = Utils.getCurrentTime();
        orgUnit = organisationUnit;
    }

    /**
     * Should only be used by Jackson so that event is included only if its non-local generated
     * Use Event.event instead to access it.
     */
    @JsonProperty("trackedEntityInstance")
    public String getTrackedEntityInstance() {
        return trackedEntityInstance;
    }

    @JsonProperty("trackedEntityInstance")
    public void setTrackedEntityInstance(String trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    @JsonProperty("attributes")
    public List<TrackedEntityAttributeValue> getAttributes() {
        if (attributes == null) {
            attributes = TrackerController.getTrackedEntityAttributeValues(localId);
        }
        return attributes;
    }

    @JsonIgnore
    public void setAttributes(List<TrackedEntityAttributeValue> attributes) {
        this.attributes = attributes;
    }

    public List<Relationship> getRelationships() {
        if (relationships == null) {
            relationships = TrackerController.getRelationships(trackedEntityInstance);
        }
        return relationships;
    }

    public void setRelationships(List<Relationship> relationships) {
        this.relationships = relationships;
    }

    @Override
    public void save() {
        Log.d("TrackedEntityInstance", "save is actually being called..");
        /* check if there is an existing tei with the same UID to avoid duplicates */
        TrackedEntityInstance existingTei = TrackerController.
                getTrackedEntityInstance(trackedEntityInstance);
        boolean exists = false;
        if (existingTei != null) {
            localId = existingTei.localId;
            exists = true;
        }
        if (getTrackedEntityInstance() == null && TrackerController.getTrackedEntityInstance(localId) != null) {
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
        new Update<>(TrackedEntityInstance.class)
                .set(Condition.column(TrackedEntityInstance$Table.FROMSERVER).is(fromServer))
                .where(Condition.column(TrackedEntityInstance$Table.LOCALID).is(localId))
                .queryClose();
    }

    @Override
    public void update() {
        save();
    }

    public String getTrackedEntity() {
        return trackedEntity;
    }

    public void setTrackedEntity(String trackedEntity) {
        this.trackedEntity = trackedEntity;
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    public void setFromServer(boolean fromServer) {
        this.fromServer = fromServer;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    @Override
    @JsonIgnore
    public String getUid() {
        return trackedEntityInstance;
    }

    @Override
    @JsonIgnore
    public void setUid(String uid) {
        this.trackedEntityInstance = uid;
    }
}
