/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.api.persistence.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.util.List;

@Table(database = DbDhis.class)
public final class TrackedEntityInstanceFlow extends BaseIdentifiableObjectFlow {
    public static final Mapper<TrackedEntityInstance, TrackedEntityInstanceFlow>
            MAPPER = new InstanceMapper();

    @Column
    String trackedEntityInstanceUid;

    @Column
    String trackedEntity;

    @Column
    String orgUnit;

    List<TrackedEntityAttributeValueFlow> attributes;

    List<RelationshipFlow> relationships;

    public TrackedEntityInstanceFlow() {
        // empty constructor
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

    public List<TrackedEntityAttributeValueFlow> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<TrackedEntityAttributeValueFlow> attributes) {
        this.attributes = attributes;
    }

    public List<RelationshipFlow> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<RelationshipFlow> relationships) {
        this.relationships = relationships;
    }

    private static class InstanceMapper
            extends AbsMapper<TrackedEntityInstance, TrackedEntityInstanceFlow> {

        @Override
        public TrackedEntityInstanceFlow mapToDatabaseEntity(TrackedEntityInstance entityInstance) {
            if (entityInstance == null) {
                return null;
            }

            TrackedEntityInstanceFlow trackedEntityInstanceFlow = new TrackedEntityInstanceFlow();
            trackedEntityInstanceFlow.setId(entityInstance.getId());
            trackedEntityInstanceFlow.setTrackedEntityInstanceUid(
                    entityInstance.getTrackedEntityInstanceUid());
            trackedEntityInstanceFlow.setTrackedEntity(entityInstance.getTrackedEntity());
            trackedEntityInstanceFlow.setOrgUnit(entityInstance.getOrgUnit());
            trackedEntityInstanceFlow.setCreated(entityInstance.getCreated());
            trackedEntityInstanceFlow.setLastUpdated(entityInstance.getLastUpdated());
            return trackedEntityInstanceFlow;
        }

        @Override
        public TrackedEntityInstance mapToModel(TrackedEntityInstanceFlow instanceFlow) {
            if (instanceFlow == null) {
                return null;
            }

            TrackedEntityInstance entityInstance = new TrackedEntityInstance();
            entityInstance.setId(instanceFlow.getId());
            entityInstance.setTrackedEntityInstanceUid(instanceFlow.getTrackedEntityInstanceUid());
            entityInstance.setTrackedEntity(instanceFlow.getTrackedEntity());
            entityInstance.setOrgUnit(instanceFlow.getOrgUnit());
            entityInstance.setCreated(instanceFlow.getCreated());
            entityInstance.setLastUpdated(instanceFlow.getLastUpdated());
            return entityInstance;
        }

        @Override
        public Class<TrackedEntityInstance> getModelTypeClass() {
            return TrackedEntityInstance.class;
        }

        @Override
        public Class<TrackedEntityInstanceFlow> getDatabaseEntityTypeClass() {
            return TrackedEntityInstanceFlow.class;
        }
    }
}
