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
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;

@Table(database = DbDhis.class, uniqueColumnGroups = {
        @UniqueGroup(
                groupNumber = TrackedEntityAttributeValueFlow
                        .UNIQUE_TRACKEDENTITYINSTANCE_ATTRIBUTEVALUE,
                uniqueConflict = ConflictAction.FAIL)
})
public final class TrackedEntityAttributeValueFlow extends BaseModelFlow {
    public static final Mapper<TrackedEntityAttributeValue, TrackedEntityAttributeValueFlow>
            MAPPER = new TrackedEntityAttributeValueMapper();

    static final int UNIQUE_TRACKEDENTITYINSTANCE_ATTRIBUTEVALUE = 1;
    static final String TRACKED_ENTITY_INSTANCE_KEY = "trackedEntityInstance";

    @Column
    String trackedEntityAttributeUId;

    @Column
    @Unique(unique = true, uniqueGroups = {UNIQUE_TRACKEDENTITYINSTANCE_ATTRIBUTEVALUE})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = TRACKED_ENTITY_INSTANCE_KEY, columnType = String.class,
                            foreignKeyColumnName = "trackedEntityInstanceUid"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    TrackedEntityInstanceFlow trackedEntityInstance;

    @Column
    String value;

    public TrackedEntityAttributeValueFlow() {
        // empty constructor
    }

    public String getTrackedEntityAttributeUId() {
        return trackedEntityAttributeUId;
    }

    public void setTrackedEntityAttributeUId(String trackedEntityAttributeUId) {
        this.trackedEntityAttributeUId = trackedEntityAttributeUId;
    }

    public TrackedEntityInstanceFlow getTrackedEntityInstance() {
        return trackedEntityInstance;
    }

    public void setTrackedEntityInstance(TrackedEntityInstanceFlow trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static class TrackedEntityAttributeValueMapper extends AbsMapper<TrackedEntityAttributeValue, TrackedEntityAttributeValueFlow> {

        @Override
        public TrackedEntityAttributeValueFlow mapToDatabaseEntity(
                TrackedEntityAttributeValue attributeValue) {
            if (attributeValue == null) {
                return null;
            }

            TrackedEntityAttributeValueFlow valueFlow = new TrackedEntityAttributeValueFlow();
            valueFlow.setId(attributeValue.getId());
            valueFlow.setTrackedEntityAttributeUId(attributeValue.getTrackedEntityAttributeUId());
            valueFlow.setTrackedEntityInstance(TrackedEntityInstanceFlow.MAPPER
                    .mapToDatabaseEntity(attributeValue.getTrackedEntityInstance()));
            valueFlow.setValue(attributeValue.getValue());
            return valueFlow;
        }

        @Override
        public TrackedEntityAttributeValue mapToModel(TrackedEntityAttributeValueFlow valueFlow) {
            if (valueFlow == null) {
                return null;
            }

            TrackedEntityAttributeValue attributeValue = new TrackedEntityAttributeValue();
            attributeValue.setId(valueFlow.getId());
            attributeValue.setTrackedEntityAttributeUId(valueFlow.getTrackedEntityAttributeUId());
            attributeValue.setTrackedEntityInstance(TrackedEntityInstanceFlow.MAPPER
                    .mapToModel(valueFlow.getTrackedEntityInstance()));
            attributeValue.setValue(valueFlow.getValue());
            return attributeValue;
        }

        @Override
        public Class<TrackedEntityAttributeValue> getModelTypeClass() {
            return TrackedEntityAttributeValue.class;
        }

        @Override
        public Class<TrackedEntityAttributeValueFlow> getDatabaseEntityTypeClass() {
            return TrackedEntityAttributeValueFlow.class;
        }
    }
}
