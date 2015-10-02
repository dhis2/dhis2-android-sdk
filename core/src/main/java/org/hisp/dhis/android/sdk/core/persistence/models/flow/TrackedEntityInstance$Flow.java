/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.trackedentityinstance.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class TrackedEntityInstance$Flow extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    String trackedEntityInstanceUid;

    @Column
    String trackedEntity;

    @Column
    String orgUnit;

    List<TrackedEntityAttributeValue$Flow> attributes;

    List<Relationship$Flow> relationships;

    @Column
    DateTime created;

    @Column
    DateTime lastUpdated;

    @Column
    org.hisp.dhis.android.sdk.models.state.Action action;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTrackedEntityInstanceUid() {
        return trackedEntityInstanceUid;
    }

    public void setTrackedEntityInstanceUid(String trackedEntityInstanceUid) {
        this.trackedEntityInstanceUid = trackedEntityInstanceUid;
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

    public List<TrackedEntityAttributeValue$Flow> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<TrackedEntityAttributeValue$Flow> attributes) {
        this.attributes = attributes;
    }

    public List<Relationship$Flow> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<Relationship$Flow> relationships) {
        this.relationships = relationships;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public org.hisp.dhis.android.sdk.models.state.Action getAction() {
        return action;
    }

    public void setAction(org.hisp.dhis.android.sdk.models.state.Action action) {
        this.action = action;
    }

    public TrackedEntityInstance$Flow() {
        // empty constructor
    }

    public static TrackedEntityInstance toModel(TrackedEntityInstance$Flow trackedEntityInstanceFlow) {
        if (trackedEntityInstanceFlow == null) {
            return null;
        }

        TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        trackedEntityInstance.setId(trackedEntityInstanceFlow.getId());
        trackedEntityInstance.setTrackedEntityInstanceUid(trackedEntityInstanceFlow.getTrackedEntityInstanceUid());
        trackedEntityInstance.setTrackedEntity(trackedEntityInstanceFlow.getTrackedEntity());
        trackedEntityInstance.setOrgUnit(trackedEntityInstanceFlow.getOrgUnit());
        trackedEntityInstance.setAttributes(TrackedEntityAttributeValue$Flow.toModels(trackedEntityInstanceFlow.getAttributes()));
        trackedEntityInstance.setRelationships(Relationship$Flow.toModels(trackedEntityInstanceFlow.getRelationships()));
        trackedEntityInstance.setCreated(trackedEntityInstanceFlow.getCreated());
        trackedEntityInstance.setLastUpdated(trackedEntityInstanceFlow.getLastUpdated());
        return trackedEntityInstance;
    }

    public static TrackedEntityInstance$Flow fromModel(TrackedEntityInstance trackedEntityInstance) {
        if (trackedEntityInstance == null) {
            return null;
        }

        TrackedEntityInstance$Flow trackedEntityInstanceFlow = new TrackedEntityInstance$Flow();
        trackedEntityInstanceFlow.setId(trackedEntityInstance.getId());
        trackedEntityInstanceFlow.setTrackedEntityInstanceUid(trackedEntityInstance.getTrackedEntityInstanceUid());
        trackedEntityInstanceFlow.setTrackedEntity(trackedEntityInstance.getTrackedEntity());
        trackedEntityInstanceFlow.setOrgUnit(trackedEntityInstance.getOrgUnit());
        trackedEntityInstanceFlow.setAttributes(TrackedEntityAttributeValue$Flow.fromModels(trackedEntityInstance.getAttributes()));
        trackedEntityInstanceFlow.setRelationships(Relationship$Flow.fromModels(trackedEntityInstance.getRelationships()));
        trackedEntityInstanceFlow.setCreated(trackedEntityInstance.getCreated());
        trackedEntityInstanceFlow.setLastUpdated(trackedEntityInstance.getLastUpdated());
        return trackedEntityInstanceFlow;
    }

    public static List<TrackedEntityInstance> toModels(List<TrackedEntityInstance$Flow> trackedEntityInstanceFlows) {
        List<TrackedEntityInstance> trackedEntityInstances = new ArrayList<>();

        if (trackedEntityInstanceFlows != null && !trackedEntityInstanceFlows.isEmpty()) {
            for (TrackedEntityInstance$Flow trackedEntityInstanceFlow : trackedEntityInstanceFlows) {
                trackedEntityInstances.add(toModel(trackedEntityInstanceFlow));
            }
        }

        return trackedEntityInstances;
    }

    public static List<TrackedEntityInstance$Flow> fromModels(List<TrackedEntityInstance> trackedEntityInstances) {
        List<TrackedEntityInstance$Flow> trackedEntityInstanceFlows = new ArrayList<>();

        if (trackedEntityInstances != null && !trackedEntityInstances.isEmpty()) {
            for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
                trackedEntityInstanceFlows.add(fromModel(trackedEntityInstance));
            }
        }

        return trackedEntityInstanceFlows;
    }
}
