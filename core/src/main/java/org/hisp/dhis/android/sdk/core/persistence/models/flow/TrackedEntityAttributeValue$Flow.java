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
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.trackedentityattributevalue.TrackedEntityAttributeValue;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME, uniqueColumnGroups = {
        @UniqueGroup(groupNumber = TrackedEntityAttributeValue$Flow.UNIQUE_TRACKEDENTITYINSTANCE_ATTRIBUTEVALUE, uniqueConflict = ConflictAction.FAIL)})
public final class TrackedEntityAttributeValue$Flow extends BaseModel$Flow {

    static final int UNIQUE_TRACKEDENTITYINSTANCE_ATTRIBUTEVALUE = 901;
    static final String TRACKED_ENTITY_INSTANCE_KEY = "trackedEntityInstance";

    @Column
    String trackedEntityAttributeUId;

    @Column
    @Unique(unique = true, uniqueGroups = {UNIQUE_TRACKEDENTITYINSTANCE_ATTRIBUTEVALUE})
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = TRACKED_ENTITY_INSTANCE_KEY, columnType = long.class, foreignColumnName = "id"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    TrackedEntityInstance$Flow trackedEntityInstance;

    @Column
    String value;

    public String getTrackedEntityAttributeUId() {
        return trackedEntityAttributeUId;
    }

    public void setTrackedEntityAttributeUId(String trackedEntityAttributeUId) {
        this.trackedEntityAttributeUId = trackedEntityAttributeUId;
    }

    public TrackedEntityInstance$Flow getTrackedEntityInstance() {
        return trackedEntityInstance;
    }

    public void setTrackedEntityInstance(TrackedEntityInstance$Flow trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TrackedEntityAttributeValue$Flow() {
        // empty constructor
    }

    public static TrackedEntityAttributeValue toModel(TrackedEntityAttributeValue$Flow trackedEntityAttributeValueFlow) {
        if (trackedEntityAttributeValueFlow == null) {
            return null;
        }

        TrackedEntityAttributeValue trackedEntityAttributeValue = new TrackedEntityAttributeValue();
        trackedEntityAttributeValue.setId(trackedEntityAttributeValueFlow.getId());
        trackedEntityAttributeValue.setTrackedEntityAttributeUId(trackedEntityAttributeValueFlow.getTrackedEntityAttributeUId());
        trackedEntityAttributeValue.setTrackedEntityInstance(TrackedEntityInstance$Flow.toModel(trackedEntityAttributeValueFlow.getTrackedEntityInstance()));
        trackedEntityAttributeValue.setValue(trackedEntityAttributeValueFlow.getValue());
        trackedEntityAttributeValue.setTrackedEntityInstance(TrackedEntityInstance$Flow.toModel(trackedEntityAttributeValueFlow.getTrackedEntityInstance()));
        trackedEntityAttributeValue.setValue(trackedEntityAttributeValueFlow.getValue());
        return trackedEntityAttributeValue;
    }

    public static TrackedEntityAttributeValue$Flow fromModel(TrackedEntityAttributeValue trackedEntityAttributeValue) {
        if (trackedEntityAttributeValue == null) {
            return null;
        }

        TrackedEntityAttributeValue$Flow trackedEntityAttributeValueFlow = new TrackedEntityAttributeValue$Flow();
        trackedEntityAttributeValueFlow.setId(trackedEntityAttributeValue.getId());
        trackedEntityAttributeValueFlow.setTrackedEntityAttributeUId(trackedEntityAttributeValue.getTrackedEntityAttributeUId());
        trackedEntityAttributeValueFlow.setTrackedEntityInstance(TrackedEntityInstance$Flow.fromModel(trackedEntityAttributeValue.getTrackedEntityInstance()));
        trackedEntityAttributeValueFlow.setValue(trackedEntityAttributeValue.getValue());
        trackedEntityAttributeValueFlow.setTrackedEntityInstance(TrackedEntityInstance$Flow.fromModel(trackedEntityAttributeValue.getTrackedEntityInstance()));
        trackedEntityAttributeValueFlow.setValue(trackedEntityAttributeValue.getValue());
        return trackedEntityAttributeValueFlow;
    }

    public static List<TrackedEntityAttributeValue> toModels(List<TrackedEntityAttributeValue$Flow> trackedEntityAttributeValueFlows) {
        List<TrackedEntityAttributeValue> trackedEntityAttributeValues = new ArrayList<>();

        if (trackedEntityAttributeValueFlows != null && !trackedEntityAttributeValueFlows.isEmpty()) {
            for (TrackedEntityAttributeValue$Flow trackedEntityAttributeValueFlow : trackedEntityAttributeValueFlows) {
                trackedEntityAttributeValues.add(toModel(trackedEntityAttributeValueFlow));
            }
        }

        return trackedEntityAttributeValues;
    }

    public static List<TrackedEntityAttributeValue$Flow> fromModels(List<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
        List<TrackedEntityAttributeValue$Flow> trackedEntityAttributeValueFlows = new ArrayList<>();

        if (trackedEntityAttributeValues != null && !trackedEntityAttributeValues.isEmpty()) {
            for (TrackedEntityAttributeValue trackedEntityAttributeValue : trackedEntityAttributeValues) {
                trackedEntityAttributeValueFlows.add(fromModel(trackedEntityAttributeValue));
            }
        }

        return trackedEntityAttributeValueFlows;
    }
}
