package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

import java.io.Serializable;

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

    public TrackedEntityAttribute getTrackedEntityAttribute() {
        return trackedEntityAttribute;
    }

    public void setTrackedEntityAttribute(TrackedEntityAttribute trackedEntityAttribute) {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }
}
