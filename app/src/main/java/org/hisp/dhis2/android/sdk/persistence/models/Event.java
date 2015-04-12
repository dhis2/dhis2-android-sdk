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

package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis2.android.sdk.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table
public class Event extends BaseModel {

    private static final String CLASS_TAG = "Event";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_VISITED = "VISITED";
    public static final String STATUS_FUTURE_VISIT = "SCHEDULE";
    public static final String STATUS_LATE_VISIT = "OVERDUE";
    public static final String STATUS_SKIPPED = "SKIPPED";

    /**
     * used to tell whether or not an event has been updated locally and needs to be sent to server.
     */
    @JsonIgnore
    @Column
    public boolean fromServer = true;

    @JsonIgnore
    @Column(columnType = Column.PRIMARY_KEY_AUTO_INCREMENT)
    public long localId;

    @JsonIgnore
    @Column(unique = true)
    public String event;

    @JsonProperty("lastUpdated")
    @Column
    public String lastUpdated;

    @JsonProperty("created")
    @Column
    public String created;

    @JsonProperty("status")
    @Column

    public String status;

    @JsonIgnore
    @Column
    public Double latitude;

    @JsonIgnore
    @Column
    public Double longitude;

    @JsonProperty("trackedEntityInstance")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column
    public String trackedEntityInstance;

    @JsonIgnore
    @Column
    public long localEnrollmentId;

    @JsonProperty("enrollment")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column
    public String enrollment;

    @JsonProperty("program")
    @Column
    public String programId;

    @JsonProperty("programStage")
    @Column
    public String programStageId;

    @JsonProperty("orgUnit")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column
    public String organisationUnitId;

    @JsonProperty("eventDate")
    @Column
    public String eventDate;

    @JsonProperty("dueDate")
    @Column
    public String dueDate;

    @JsonProperty("dataValues")
    public List<DataValue> dataValues;

    public Event() {
    }

    public Event(String organisationUnitId,
                 String status,
                 String programId,
                 String programStageId,
                 String trackedEntityInstanceId,
                 String enrollmentId) {
        this.event = Dhis2.QUEUED + UUID.randomUUID().toString();
        this.fromServer = false;
        this.dueDate = Utils.getCurrentDate();
        this.eventDate = Utils.getCurrentDate();
        this.organisationUnitId = organisationUnitId;
        this.programId = programId;
        this.programStageId = programStageId;
        this.status = status;
        this.lastUpdated = Utils.getCurrentDate();
        this.trackedEntityInstance = trackedEntityInstanceId;
        this.enrollment = enrollmentId;
        this.dataValues = new ArrayList<DataValue>();
    }

    @Override
    public void delete(boolean async) {
        if (dataValues != null) {
            for (DataValue dataValue : dataValues)
                dataValue.delete(async);
        }
        super.delete(async);
    }

    @Override
    public void save(boolean async) {
        /* check if there is an existing event with the same UID to avoid duplicates */
        Event existingEvent = DataValueController.getEventByUid(event);
        if (existingEvent != null) {
            localId = existingEvent.localId;
        }
        super.save(async);
        if (dataValues != null) {
            for (DataValue dataValue : dataValues) {
                dataValue.localEventId = localId;
                dataValue.save(async);
            }
        }
    }

    @JsonProperty("event")
    public void setEvent(String event) {
        this.event = event;
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
    }

    /**
     * Should only be used by Jackson so that event is included only if its non-local generated
     * Use Event.event instead to access it.
     */
    @JsonProperty("event")
    public String getEvent() {
        String randomUUID = Dhis2.QUEUED + UUID.randomUUID().toString();
        if (event.length() == randomUUID.length())
            return null;
        else return event;
    }

    @JsonProperty("coordinate")
    public void setCoordinate(Map<String, Object> coordinate) {
        this.latitude = (double) coordinate.get("latitude");
        this.longitude = (double) coordinate.get("longitude");
    }

    @JsonProperty("coordinate")
    public Map<String, Object> getCoordinate() {
        Map<String, Object> coordinate = new HashMap<>();
        coordinate.put("latitude", latitude);
        coordinate.put("longitude", longitude);
        return coordinate;
    }

    public String getEnrollment() {
        String randomUUID = Dhis2.QUEUED + UUID.randomUUID().toString();
        if (enrollment == null || enrollment.length() == randomUUID.length())
            return null;
        else return enrollment;
    }

    public List<DataValue> getDataValues() {
        if (dataValues == null) dataValues = Select.all(DataValue.class,
                Condition.column(DataValue$Table.LOCALEVENTID).is(localId));
        return dataValues;
    }

    public boolean isFromServer() {
        return fromServer;
    }

    public void setFromServer(boolean fromServer) {
        this.fromServer = fromServer;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
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

    public String getTrackedEntityInstance() {
        return trackedEntityInstance;
    }

    public void setTrackedEntityInstance(String trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    public long getLocalEnrollmentId() {
        return localEnrollmentId;
    }

    public void setLocalEnrollmentId(long localEnrollmentId) {
        this.localEnrollmentId = localEnrollmentId;
    }

    public void setEnrollment(String enrollment) {
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

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setDataValues(List<DataValue> dataValues) {
        this.dataValues = dataValues;
    }
}