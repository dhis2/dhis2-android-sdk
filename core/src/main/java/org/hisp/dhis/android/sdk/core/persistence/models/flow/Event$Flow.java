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
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.common.Access;
import org.hisp.dhis.android.sdk.models.event.Event;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class Event$Flow extends BaseModel {

    final static String TRACKED_ENTITY_INSTANCE_KEY = "tei";
    final static String ENROLLMENT_KEY = "enrollment";

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    String eventUid;

    @Column
    String status;

    @Column
    Double latitude;

    @Column
    Double longitude;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = TRACKED_ENTITY_INSTANCE_KEY, columnType = long.class, foreignColumnName = "id"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    TrackedEntityInstance$Flow trackedEntityInstance;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(columnName = ENROLLMENT_KEY, columnType = long.class, foreignColumnName = "id"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    Enrollment$Flow enrollment;

    @Column
    String programId;

    @Column
    String programStageId;

    @Column
    String organisationUnitId;

    @Column
    DateTime eventDate;

    @Column
    DateTime dueDate;

    @Column
    String name;

    @Column
    String displayName;

    @Column
    DateTime created;

    @Column
    DateTime lastUpdated;

    @Column
    Access access;

    List<TrackedEntityDataValue$Flow> trackedEntityDataValues;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEventUid() {
        return eventUid;
    }

    public void setEventUid(String eventUid) {
        this.eventUid = eventUid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public TrackedEntityInstance$Flow getTrackedEntityInstance() {
        return trackedEntityInstance;
    }

    public void setTrackedEntityInstance(TrackedEntityInstance$Flow trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    public Enrollment$Flow getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment$Flow enrollment) {
        this.enrollment = enrollment;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramStageId() {
        return programStageId;
    }

    public void setProgramStageId(String programStageId) {
        this.programStageId = programStageId;
    }

    public String getOrganisationUnitId() {
        return organisationUnitId;
    }

    public void setOrganisationUnitId(String organisationUnitId) {
        this.organisationUnitId = organisationUnitId;
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

    public List<TrackedEntityDataValue$Flow> getTrackedEntityDataValues() {
        return trackedEntityDataValues;
    }

    public void setTrackedEntityDataValues(List<TrackedEntityDataValue$Flow> trackedEntityDataValues) {
        this.trackedEntityDataValues = trackedEntityDataValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Event$Flow() {
        // empty constructor
    }

    public static Event toModel(Event$Flow eventFlow) {
        if (eventFlow == null) {
            return null;
        }

        Event event = new Event();
        event.setId(eventFlow.getId());
        event.setEventUid(eventFlow.getEventUid());
        event.setStatus(eventFlow.getStatus());
        event.setLatitude(eventFlow.getLatitude());
        event.setLongitude(eventFlow.getLongitude());
        event.setTrackedEntityInstance(TrackedEntityInstance$Flow.toModel(eventFlow.getTrackedEntityInstance()));
        event.setEnrollment(Enrollment$Flow.toModel(eventFlow.getEnrollment()));
        event.setProgramId(eventFlow.getProgramId());
        event.setProgramStageId(eventFlow.getProgramStageId());
        event.setOrganisationUnitId(eventFlow.getOrganisationUnitId());
        event.setEventDate(eventFlow.getEventDate());
        event.setDueDate(eventFlow.getDueDate());
        event.setTrackedEntityDataValues(TrackedEntityDataValue$Flow.toModels(eventFlow.getTrackedEntityDataValues()));
        event.setName(eventFlow.getName());
        event.setDisplayName(eventFlow.getDisplayName());
        event.setCreated(eventFlow.getCreated());
        event.setLastUpdated(eventFlow.getLastUpdated());
        event.setAccess(eventFlow.getAccess());
        return event;
    }

    public static Event$Flow fromModel(Event event) {
        if (event == null) {
            return null;
        }

        Event$Flow eventFlow = new Event$Flow();
        eventFlow.setId(event.getId());
        eventFlow.setEventUid(event.getEventUid());
        eventFlow.setStatus(event.getStatus());
        eventFlow.setLatitude(event.getLatitude());
        eventFlow.setLongitude(event.getLongitude());
        eventFlow.setTrackedEntityInstance(TrackedEntityInstance$Flow.fromModel(event.getTrackedEntityInstance()));
        eventFlow.setEnrollment(Enrollment$Flow.fromModel(event.getEnrollment()));
        eventFlow.setProgramId(event.getProgramId());
        eventFlow.setProgramStageId(event.getProgramStageId());
        eventFlow.setOrganisationUnitId(event.getOrganisationUnitId());
        eventFlow.setEventDate(event.getEventDate());
        eventFlow.setDueDate(event.getDueDate());
        eventFlow.setTrackedEntityDataValues(TrackedEntityDataValue$Flow.fromModels(event.getTrackedEntityDataValues()));
        eventFlow.setName(event.getName());
        eventFlow.setDisplayName(event.getDisplayName());
        eventFlow.setCreated(event.getCreated());
        eventFlow.setLastUpdated(event.getLastUpdated());
        eventFlow.setAccess(event.getAccess());
        return eventFlow;
    }

    public static List<Event> toModels(List<Event$Flow> eventFlows) {
        List<Event> events = new ArrayList<>();

        if (eventFlows != null && !eventFlows.isEmpty()) {
            for (Event$Flow eventFlow : eventFlows) {
                events.add(toModel(eventFlow));
            }
        }

        return events;
    }

    public static List<Event$Flow> fromModels(List<Event> events) {
        List<Event$Flow> eventFlows = new ArrayList<>();

        if (events != null && !events.isEmpty()) {
            for (Event event : events) {
                eventFlows.add(fromModel(event));
            }
        }

        return eventFlows;
    }
}
