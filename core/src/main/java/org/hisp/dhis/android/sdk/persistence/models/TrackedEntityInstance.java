/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.utils.serializers.TrackedEntityInstanceTrackedEntitySerializer;
import org.hisp.dhis.android.sdk.utils.api.CodeGenerator;

import java.io.Serializable;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 03.03.15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(databaseName = Dhis2Database.NAME)
@JsonSerialize(using = TrackedEntityInstanceTrackedEntitySerializer.class)
public class TrackedEntityInstance extends BaseSerializableModel implements Serializable {

    @JsonIgnore
    @Column(name = "trackedEntityInstance")
    @Unique
    String trackedEntityInstance;
    public void setCreated(String created) {
        this.created = created;
    }

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

    public TrackedEntityInstance() { this.trackedEntityInstance = CodeGenerator.generateCode(); }

    public TrackedEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        super(trackedEntityInstance);
        this.trackedEntityInstance = trackedEntityInstance.trackedEntityInstance;
        this.trackedEntity = trackedEntityInstance.trackedEntity;
        this.orgUnit = trackedEntityInstance.orgUnit;
    }

    public TrackedEntityInstance(Program program, String organisationUnit) {
        fromServer = false;
        trackedEntityInstance = CodeGenerator.generateCode();
        trackedEntity = program.getTrackedEntity().getId();
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
        /* check if there is an existing tei with the same UID to avoid duplicates */
        TrackedEntityInstance existingTei = TrackerController.
                getTrackedEntityInstance(trackedEntityInstance);
        if (existingTei != null) {
            localId = existingTei.localId;
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

    @JsonProperty("trackedEntityType")
    public void setTrackedEntityType(String trackedEntityType) {
        this.trackedEntity = trackedEntityType;
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
