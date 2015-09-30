/*
 *  Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.trackedentityattributevalue.TrackedEntityAttributeValue;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class TrackedEntityAttributeValue$Flow extends BaseModel {

    @Column
    @PrimaryKey
    String trackedEntityAttributeUId;

    @Column
    @PrimaryKey
    String trackedEntityInstanceUId;

    @Column
    long trackedEntityInstanceId;

    @Column
    String value;

    @Column
    org.hisp.dhis.android.sdk.models.state.Action action;

    public String getTrackedEntityAttributeUId() {
        return trackedEntityAttributeUId;
    }

    public void setTrackedEntityAttributeUId(String trackedEntityAttributeUId) {
        this.trackedEntityAttributeUId = trackedEntityAttributeUId;
    }

    public String getTrackedEntityInstanceUId() {
        return trackedEntityInstanceUId;
    }

    public void setTrackedEntityInstanceUId(String trackedEntityInstanceUId) {
        this.trackedEntityInstanceUId = trackedEntityInstanceUId;
    }

    public long getTrackedEntityInstanceId() {
        return trackedEntityInstanceId;
    }

    public void setTrackedEntityInstanceId(long trackedEntityInstanceId) {
        this.trackedEntityInstanceId = trackedEntityInstanceId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public org.hisp.dhis.android.sdk.models.state.Action getAction() {
        return action;
    }

    public void setAction(org.hisp.dhis.android.sdk.models.state.Action action) {
        this.action = action;
    }

    public TrackedEntityAttributeValue$Flow() {
        // empty constructor
    }

    public static TrackedEntityAttributeValue toModel(TrackedEntityAttributeValue$Flow trackedEntityAttributeValueFlow) {
        if (trackedEntityAttributeValueFlow == null) {
            return null;
        }

        TrackedEntityAttributeValue trackedEntityAttributeValue = new TrackedEntityAttributeValue();
        trackedEntityAttributeValue.setTrackedEntityAttributeUId(trackedEntityAttributeValueFlow.getTrackedEntityAttributeUId());
        // trackedEntityAttributeValue.setTrackedEntityInstanceId(trackedEntityAttributeValueFlow.getTrackedEntityInstanceId());
        // trackedEntityAttributeValue.setTrackedEntityInstanceUId(trackedEntityAttributeValueFlow.getTrackedEntityInstanceUId());
        trackedEntityAttributeValue.setValue(trackedEntityAttributeValueFlow.getValue());
        // trackedEntityAttributeValue.setAction(trackedEntityAttributeValueFlow.getAction());
        return trackedEntityAttributeValue;
    }

    public static TrackedEntityAttributeValue$Flow fromModel(TrackedEntityAttributeValue trackedEntityAttributeValue) {
        if (trackedEntityAttributeValue == null) {
            return null;
        }

        TrackedEntityAttributeValue$Flow trackedEntityAttributeValueFlow = new TrackedEntityAttributeValue$Flow();
        trackedEntityAttributeValueFlow.setTrackedEntityAttributeUId(trackedEntityAttributeValue.getTrackedEntityAttributeUId());
        // trackedEntityAttributeValueFlow.setTrackedEntityInstanceId(trackedEntityAttributeValue.getTrackedEntityInstanceId());
        // trackedEntityAttributeValueFlow.setTrackedEntityInstanceUId(trackedEntityAttributeValue.getTrackedEntityInstanceUId());
        trackedEntityAttributeValueFlow.setValue(trackedEntityAttributeValue.getValue());
        // trackedEntityAttributeValueFlow.setAction(trackedEntityAttributeValue.getAction());
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
