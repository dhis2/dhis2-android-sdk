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

package org.hisp.dhis.android.sdk.models.relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.sdk.models.state.Action;
import org.hisp.dhis.android.sdk.models.common.IModel;
import org.hisp.dhis.android.sdk.models.trackedentityinstance.TrackedEntityInstance;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Relationship implements Serializable, IModel {

    @JsonIgnore
    private long id;

    @JsonProperty
    private String relationship;

    @JsonIgnore
    private TrackedEntityInstance trackedEntityInstanceA;

    @JsonIgnore
    private TrackedEntityInstance trackedEntityInstanceB;

    @JsonProperty
    private String displayName;

    public Relationship() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    @JsonProperty("trackedEntityInstanceA")
    public String getTrackedEntityInstanceAUid() {
        return trackedEntityInstanceA.getTrackedEntityInstanceUid();
    }

    @JsonProperty("trackedEntityInstanceA")
    public void setTrackedEntityInstanceA(String trackedEntityInstanceA) {
        this.trackedEntityInstanceA = new TrackedEntityInstance();
        this.trackedEntityInstanceA.setTrackedEntityInstanceUid(trackedEntityInstanceA);
    }

    @JsonProperty("trackedEntityInstanceB")
    public String getTrackedEntityInstanceBUid() {
        return trackedEntityInstanceB.getTrackedEntityInstanceUid();
    }

    @JsonProperty("trackedEntityInstanceB")
    public void setTrackedEntityInstanceB(String trackedEntityInstanceB) {
        this.trackedEntityInstanceB = new TrackedEntityInstance();
        this.trackedEntityInstanceB.setTrackedEntityInstanceUid(trackedEntityInstanceB);
    }

    public TrackedEntityInstance getTrackedEntityInstanceA() {
        return trackedEntityInstanceA;
    }

    public void setTrackedEntityInstanceA(TrackedEntityInstance trackedEntityInstanceA) {
        this.trackedEntityInstanceA = trackedEntityInstanceA;
    }

    public TrackedEntityInstance getTrackedEntityInstanceB() {
        return trackedEntityInstanceB;
    }

    public void setTrackedEntityInstanceB(TrackedEntityInstance trackedEntityInstanceB) {
        this.trackedEntityInstanceB = trackedEntityInstanceB;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
