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
import org.hisp.dhis.client.sdk.android.common.IMapper;
import org.hisp.dhis.client.sdk.models.common.Coordinates;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.event.Event.EventStatus;
import org.joda.time.DateTime;

@Table(database = DbDhis.class)
public final class EventFlow extends BaseIdentifiableObjectFlow {
    public static final IMapper<Event, EventFlow> MAPPER = new EventMapper();

    @Column(name = "status")
    EventStatus status;

    @Column(name = "latitude")
    Double latitude;

    @Column(name = "longitude")
    Double longitude;

    @Column(name = "program")
    String program;

    @Column(name = "programStage")
    String programStage;

    @Column(name = "orgUnit")
    String orgUnit;

    @Column(name = "eventDate")
    DateTime eventDate;

    @Column(name = "dueDate")
    DateTime dueDate;

    public EventFlow() {
        // explicit empty constructor
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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

    public DateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(DateTime eventDate) {
        this.eventDate = eventDate;
    }

    public DateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(DateTime dueDate) {
        this.dueDate = dueDate;
    }

    private static class EventMapper extends AbsMapper<Event, EventFlow> {

        @Override
        public EventFlow mapToDatabaseEntity(Event event) {
            if (event == null) {
                return null;
            }

            EventFlow eventFlow = new EventFlow();
            eventFlow.setId(event.getId());
            eventFlow.setUId(event.getUId());
            eventFlow.setName(event.getName());
            eventFlow.setDisplayName(event.getDisplayName());
            eventFlow.setCreated(event.getCreated());
            eventFlow.setLastUpdated(event.getLastUpdated());
            eventFlow.setAccess(event.getAccess());
            eventFlow.setStatus(event.getStatus());

            // un-wrapping coordinates
            Coordinates coordinates = event.getCoordinate();
            if (coordinates != null) {
                eventFlow.setLatitude(coordinates.getLatitude());
                eventFlow.setLongitude(coordinates.getLongitude());
            }

            eventFlow.setProgram(event.getProgram());
            eventFlow.setProgramStage(event.getProgramStage());
            eventFlow.setOrgUnit(event.getOrgUnit());
            eventFlow.setEventDate(event.getEventDate());
            eventFlow.setDueDate(event.getDueDate());

            return eventFlow;
        }

        @Override
        public Event mapToModel(EventFlow eventFlow) {
            if (eventFlow == null) {
                return null;
            }

            Event event = new Event();
            event.setId(eventFlow.getId());
            event.setUId(eventFlow.getUId());
            event.setName(eventFlow.getName());
            event.setDisplayName(eventFlow.getDisplayName());
            event.setCreated(eventFlow.getCreated());
            event.setLastUpdated(eventFlow.getLastUpdated());
            event.setAccess(eventFlow.getAccess());
            event.setStatus(eventFlow.getStatus());

            // wrapping coordinates
            Coordinates coordinates = new Coordinates(
                    eventFlow.getLatitude(),
                    eventFlow.getLongitude());
            event.setCoordinate(coordinates);

            event.setProgram(eventFlow.getProgram());
            event.setProgramStage(eventFlow.getProgramStage());
            event.setOrgUnit(eventFlow.getOrgUnit());
            event.setEventDate(eventFlow.getEventDate());
            event.setDueDate(eventFlow.getDueDate());

            return event;
        }

        @Override
        public Class<Event> getModelTypeClass() {
            return Event.class;
        }

        @Override
        public Class<EventFlow> getDatabaseEntityTypeClass() {
            return EventFlow.class;
        }
    }
}
