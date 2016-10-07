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

package org.hisp.dhis.client.sdk.models.trackedentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.client.sdk.models.common.BaseModel;
import org.hisp.dhis.client.sdk.models.common.DataModel;
import org.hisp.dhis.client.sdk.models.common.State;

public class TrackedEntityDataValue extends BaseModel implements DataModel {

    @JsonIgnore
    private String eventUid;

    @JsonProperty("dataElement")
    private String dataElement;

    @JsonProperty("storedBy")
    private String storedBy;

    @JsonProperty("value")
    private String value;

    @JsonIgnore
    private State state;

    public static void validate(TrackedEntityDataValue trackedEntityDataValue) {
        if(trackedEntityDataValue.getEventUid() == null) {
            throw new IllegalArgumentException("Event uid must not be null");
        }

        if (trackedEntityDataValue.getStoredBy() == null) {
            throw new IllegalArgumentException("Stored by must not be null");
        }

        if(trackedEntityDataValue.getDataElement() == null) {
            throw new IllegalArgumentException("Data element must not be null");
        }
    }
    public TrackedEntityDataValue() {
        // explicit empty constructor
    }

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

    @Override
    public String toString() {
        return "TrackedEntityDataValue{" +
                "eventUid=" + eventUid +
                ", dataElement='" + dataElement + '\'' +
                ", storedBy='" + storedBy + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
