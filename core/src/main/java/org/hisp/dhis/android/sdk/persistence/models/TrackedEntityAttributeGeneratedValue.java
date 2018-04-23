package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

@Table(databaseName = Dhis2Database.NAME)
public class TrackedEntityAttributeGeneratedValue extends BaseValue {

    @JsonIgnore
    @PrimaryKey(autoincrement = true)
    @Column(name = "id")
    int id;

    @JsonProperty("created")
    @Column(name = "created")
    String created;

    @JsonProperty("expiryDate")
    @Column(name = "expiryDate")
    String expiryDate;

    @JsonProperty("trackedEntityAttribute")
    @Column
    @ForeignKey(references = {
            @ForeignKeyReference(columnName = "trackedEntityAttribute",
                    columnType = String.class, foreignColumnName = "id")
    },saveForeignKeyModel = false)
    TrackedEntityAttribute trackedEntityAttribute;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    @JsonGetter("trackedEntityAttribute")
    public TrackedEntityAttribute getTrackedEntityAttribute() {
        return trackedEntityAttribute;
    }

    @JsonSetter("trackedEntityAttribute")
    public void setTrackedEntityAttribute(TrackedEntityAttribute trackedEntityAttribute) {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }

    @JsonSetter("ownerUid")
    public void setTrackedEntityAttribute(String trackedEntityAttributeUid){
        TrackedEntityAttribute trackedEntityAttribute = MetaDataController.getTrackedEntityAttribute(trackedEntityAttributeUid);
        if (trackedEntityAttribute != null) {
            this.trackedEntityAttribute = trackedEntityAttribute;
        }
    }
}
