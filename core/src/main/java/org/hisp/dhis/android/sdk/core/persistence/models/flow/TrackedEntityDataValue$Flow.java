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
import org.hisp.dhis.android.sdk.models.common.meta.State;
import org.hisp.dhis.android.sdk.models.trackedentitydatavalue.TrackedEntityDataValue;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class TrackedEntityDataValue$Flow extends BaseModel {

    @Column
    @PrimaryKey
    String eventUid;

    @Column
    @PrimaryKey
    String dataElement;

    @Column
    boolean providedElsewhere;

    @Column
    String storedBy;

    @Column
    String value;

    @Column
    State state;

    public String getEventUid() {
        return eventUid;
    }

    public void setEventUid(String eventUid) {
        this.eventUid = eventUid;
    }

    public String getDataElement() {
        return dataElement;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public boolean isProvidedElsewhere() {
        return providedElsewhere;
    }

    public void setProvidedElsewhere(boolean providedElsewhere) {
        this.providedElsewhere = providedElsewhere;
    }

    public String getStoredBy() {
        return storedBy;
    }

    public void setStoredBy(String storedBy) {
        this.storedBy = storedBy;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public TrackedEntityDataValue$Flow() {
        // empty constructor
    }

    public static TrackedEntityDataValue toModel(TrackedEntityDataValue$Flow trackedEntityDataValueFlow) {
        if (trackedEntityDataValueFlow == null) {
            return null;
        }

        TrackedEntityDataValue trackedEntityDataValue = new TrackedEntityDataValue();
        trackedEntityDataValue.setEventUid(trackedEntityDataValueFlow.getEventUid());
        trackedEntityDataValue.setDataElement(trackedEntityDataValueFlow.getDataElement());
        trackedEntityDataValue.setProvidedElsewhere(trackedEntityDataValueFlow.isProvidedElsewhere());
        trackedEntityDataValue.setStoredBy(trackedEntityDataValueFlow.getStoredBy());
        trackedEntityDataValue.setValue(trackedEntityDataValueFlow.getValue());
        trackedEntityDataValue.setState(trackedEntityDataValueFlow.getState());
        return trackedEntityDataValue;
    }

    public static TrackedEntityDataValue$Flow fromModel(TrackedEntityDataValue trackedEntityDataValue) {
        if (trackedEntityDataValue == null) {
            return null;
        }

        TrackedEntityDataValue$Flow trackedEntityDataValueFlow = new TrackedEntityDataValue$Flow();
        trackedEntityDataValueFlow.setEventUid(trackedEntityDataValue.getEventUid());
        trackedEntityDataValueFlow.setDataElement(trackedEntityDataValue.getDataElement());
        trackedEntityDataValueFlow.setProvidedElsewhere(trackedEntityDataValue.isProvidedElsewhere());
        trackedEntityDataValueFlow.setStoredBy(trackedEntityDataValue.getStoredBy());
        trackedEntityDataValueFlow.setValue(trackedEntityDataValue.getValue());
        trackedEntityDataValueFlow.setState(trackedEntityDataValue.getState());
        return trackedEntityDataValueFlow;
    }

    public static List<TrackedEntityDataValue> toModels(List<TrackedEntityDataValue$Flow> trackedEntityDataValueFlows) {
        List<TrackedEntityDataValue> trackedEntityAttributeValues = new ArrayList<>();

        if (trackedEntityDataValueFlows != null && !trackedEntityDataValueFlows.isEmpty()) {
            for (TrackedEntityDataValue$Flow trackedEntityDataValueFlow : trackedEntityDataValueFlows) {
                trackedEntityAttributeValues.add(toModel(trackedEntityDataValueFlow));
            }
        }

        return trackedEntityAttributeValues;
    }

    public static List<TrackedEntityDataValue$Flow> fromModels(List<TrackedEntityDataValue> trackedEntityDataValues) {
        List<TrackedEntityDataValue$Flow> trackedEntityDataValueFlows = new ArrayList<>();

        if (trackedEntityDataValues != null && !trackedEntityDataValues.isEmpty()) {
            for (TrackedEntityDataValue trackedEntityDataValue : trackedEntityDataValues) {
                trackedEntityDataValueFlows.add(fromModel(trackedEntityDataValue));
            }
        }

        return trackedEntityDataValueFlows;
    }
}
