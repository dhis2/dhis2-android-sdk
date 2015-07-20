package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

import java.io.Serializable;

/**
 * @author Simen Skogly Russnes on 07.07.15.
 */

@Table(databaseName = Dhis2Database.NAME)
@JsonIgnoreProperties("modelAdapter")
public class Relationship extends BaseModel implements Serializable {

    @JsonProperty
    @Column(name = "relationship")
    @PrimaryKey
    String relationship;

    @JsonProperty
    @Column(name = "trackedEntityInstanceA")
    @PrimaryKey
    String trackedEntityInstanceA;

    @JsonProperty
    @Column(name = "trackedEntityInstanceB")
    @PrimaryKey
    String trackedEntityInstanceB;

    @JsonProperty
    @Column(name = "displayName")
    String displayName;

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getTrackedEntityInstanceA() {
        return trackedEntityInstanceA;
    }

    public void setTrackedEntityInstanceA(String trackedEntityInstanceA) {
        this.trackedEntityInstanceA = trackedEntityInstanceA;
    }

    public String getTrackedEntityInstanceB() {
        return trackedEntityInstanceB;
    }

    public void setTrackedEntityInstanceB(String trackedEntityInstanceB) {
        this.trackedEntityInstanceB = trackedEntityInstanceB;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
