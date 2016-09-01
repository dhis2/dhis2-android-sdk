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

package org.hisp.dhis.client.sdk.models.enrollment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.client.sdk.models.common.Access;
import org.hisp.dhis.client.sdk.models.common.base.BaseModel;
import org.hisp.dhis.client.sdk.models.common.base.IdentifiableObject;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Enrollment extends BaseModel implements IdentifiableObject {

    @JsonProperty("enrollment")
    private String uId;

    @JsonProperty("orgUnit")
    private String orgUnit;

    @JsonProperty("program")
    private String program;

    @JsonProperty("dateOfEnrollment")
    private DateTime dateOfEnrollment;

    @JsonProperty("dateOfIncident")
    private DateTime dateOfIncident;

    @JsonProperty("followup")
    private boolean followup;

    @JsonProperty("status")
    private EnrollmentStatus status;

    @JsonProperty("name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    @JsonProperty("displayName")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String displayName;

    @JsonProperty("created")
    private DateTime created;

    @JsonProperty("lastUpdated")
    private DateTime lastUpdated;

    @JsonProperty("access")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Access access;

    @JsonIgnore
    int sortOrder;

    @JsonIgnore
    private TrackedEntityInstance trackedEntityInstance;

    @JsonProperty("attributes")
    private List<TrackedEntityAttributeValue> trackedEntityAttributeValues;

    @JsonIgnore
    private List<Event> events;

    public Enrollment() {
        // explicit empty constructor
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    @JsonProperty("trackedEntityInstance")
    public String getTrackedEntityInstanceUid() {
        return trackedEntityInstance.getTrackedEntityInstanceUid();
    }

    public void setTrackedEntityInstanceUid(String trackedEntityInstanceUid) {
        this.trackedEntityInstance = new TrackedEntityInstance();
        this.trackedEntityInstance.setTrackedEntityInstanceUid(trackedEntityInstanceUid);
    }

    public TrackedEntityInstance getTrackedEntityInstance() {
        return trackedEntityInstance;
    }

    public void setTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public DateTime getDateOfEnrollment() {
        return dateOfEnrollment;
    }

    public void setDateOfEnrollment(DateTime dateOfEnrollment) {
        this.dateOfEnrollment = dateOfEnrollment;
    }

    public DateTime getDateOfIncident() {
        return dateOfIncident;
    }

    public void setDateOfIncident(DateTime dateOfIncident) {
        this.dateOfIncident = dateOfIncident;
    }

    public boolean isFollowup() {
        return followup;
    }

    public void setFollowup(boolean followup) {
        this.followup = followup;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    @Override
    public String getUId() {
        return uId;
    }

    @Override
    public void setUId(String uId) {
        this.uId = uId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public DateTime getCreated() {
        return created;
    }

    @Override
    public void setCreated(DateTime created) {
        this.created = created;
    }

    @Override
    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public Access getAccess() {
        return access;
    }

    @Override
    public void setAccess(Access access) {
        this.access = access;
    }

    @Override
    public void setApiSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public int getApiSortOrder() {
        return sortOrder;
    }

    public List<TrackedEntityAttributeValue> getTrackedEntityAttributeValues() {
        return trackedEntityAttributeValues;
    }

    public void setTrackedEntityAttributeValues(
            List<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
        this.trackedEntityAttributeValues = trackedEntityAttributeValues;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public enum EnrollmentStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED //aka TERMINATED
    }
}
