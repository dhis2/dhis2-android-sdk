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

package org.hisp.dhis.client.sdk.models.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.client.sdk.models.common.Coordinates;
import org.hisp.dhis.client.sdk.models.common.IdentifiableObject;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event implements IdentifiableObject {

    public static final Comparator<Event> DATE_COMPARATOR = new EventDateComparator();

    public static final String EVENT_DATE_KEY = "eventDate";
    public static final String STATUS_KEY = "status";
    public static final String EVENT_DATE_LABEL = "Event date";
    public static final String STATUS_LABEL = "Status";

    @JsonProperty("event")
    private String uid;

    @JsonProperty("name")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    @JsonProperty("displayName")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String displayName;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("lastUpdated")
    private Date lastUpdated;

    @JsonProperty("code")
    private String code;

    @JsonProperty("status")
    private EventStatus status;

    @JsonProperty("coordinate")
    private Coordinates coordinate;

    @JsonProperty("program")
    private String program;

    @JsonProperty("programStage")
    private String programStage;

    @JsonProperty("orgUnit")
    private String orgUnit;

    @JsonProperty("eventDate")
    private Date eventDate;

    @JsonProperty("completedDate")
    private Date completedDate;


    /*
     * This property is optional (used only in tracker)
     */
    @JsonProperty("dueDate")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date dueDate;

    @JsonProperty("dataValues")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TrackedEntityDataValue> dataValues;

    public Event() {
        // explicit empty constructor
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Coordinates getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinates coordinate) {
        this.coordinate = coordinate;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getProgramStage() {
        return programStage;
    }

    public void setProgramStage(String programStage) {
        this.programStage = programStage;
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public List<TrackedEntityDataValue> getDataValues() {
        return dataValues;
    }

    public void setDataValues(List<TrackedEntityDataValue> dataValues) {
        this.dataValues = dataValues;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public enum EventStatus {
        ACTIVE, COMPLETED, SCHEDULE, SKIPPED
    }

    /**
     * Comparator that returns the Event with the latest EventDate
     * as the greater of the two given.
     */
    private static class EventDateComparator implements Comparator<Event> {

        @Override
        public int compare(Event first, Event second) {
            if (first != null && second != null && first.getEventDate() != null) {
                return first.getEventDate().compareTo(second.getEventDate());
            }

            return 0;
        }
    }
}
