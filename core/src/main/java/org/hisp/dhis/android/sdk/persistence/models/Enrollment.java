/*
 *  Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Update;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.utils.api.CodeGenerator;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 04.03.15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(databaseName = Dhis2Database.NAME)
public class Enrollment extends BaseSerializableModel {

    public static final String ACTIVE = "ACTIVE";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED"; //aka TERMINATED
    private static final String CLASS_TAG = Enrollment.class.getSimpleName();

    @JsonProperty("orgUnit")
    @Column(name = "orgUnit")
    String orgUnit;

    @JsonIgnore
    @Column(name = "trackedEntityInstance")
    String trackedEntityInstance;

    @JsonIgnore
    @Column(name = "localTrackedEntityInstanceId")
    long localTrackedEntityInstanceId;

    @JsonProperty("program")
    @Column(name = "program")
    String program;

    @JsonProperty("enrollmentDate")
    @Column(name = "enrollmentDate")
    String enrollmentDate;

    @JsonProperty("incidentDate")
    @Column(name = "incidentDate")
    String incidentDate;

    @JsonProperty("followup")
    @Column(name = "followup")
    boolean followup;

    @JsonProperty("status")
    @Column(name = "status")
    String status;

    @JsonIgnore
    List<TrackedEntityAttributeValue> attributes;

    @JsonIgnore
    @Column(name = "enrollment")
    @Unique
    String enrollment;

    @JsonIgnore
    List<Event> events;

    public Enrollment() {
        enrollment = CodeGenerator.generateCode();
    }

    public Enrollment(Enrollment enrollment) {
        this.orgUnit = enrollment.orgUnit;
        this.trackedEntityInstance = enrollment.trackedEntityInstance;
        this.localTrackedEntityInstanceId = enrollment.localTrackedEntityInstanceId;
        this.program = enrollment.program;
        this.enrollmentDate = enrollment.enrollmentDate;
        this.incidentDate = enrollment.incidentDate;
        this.followup = enrollment.followup;
        this.status = enrollment.status;
        this.enrollment = enrollment.enrollment;
    }

    public Enrollment(String organisationUnit, String trackedEntityInstance, Program program, String enrollmentDate, String incidentDate) {
        orgUnit = organisationUnit;
        status = Enrollment.ACTIVE;
        enrollment = CodeGenerator.generateCode();
        followup = false;
        fromServer = false;
        this.program = program.getUid();
        this.trackedEntityInstance = trackedEntityInstance;
        this.enrollmentDate = enrollmentDate;
        this.incidentDate = incidentDate;
        List<Event> events = new ArrayList<>();
        for (ProgramStage programStage : program.getProgramStages()) {
            if (programStage.getAutoGenerateEvent()) {
                String status = Event.STATUS_FUTURE_VISIT;
                DateTime dueDate = new DateTime(enrollmentDate);
                dueDate = dueDate.plusDays(programStage.getMinDaysFromStart());
                Event event = new Event(organisationUnit, status,
                        program.id, programStage,
                        trackedEntityInstance, enrollment, dueDate.toString());
                events.add(event);
            }
        }
        if (!events.isEmpty()) {
            setEvents(events);
        }
    }

    /**
     * Should only be used by Jackson so that event is included only if its non-local generated
     * Use Event.event instead to access it.
     */
    @JsonProperty("enrollment")
    public String getEnrollment() {
        return enrollment;
    }

    @JsonProperty("enrollment")
    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
    }

    /**
     * Should only be used by Jackson so that event is included only if its non-local generated
     * Use Event.event instead to access it.
     */
    @JsonProperty("trackedEntityInstance")
    public String getTrackedEntityInstance() {
        return trackedEntityInstance;
    }

    @JsonProperty("trackedEntityInstance")
    public void setTrackedEntityInstance(String trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    @JsonProperty("attributes")
    public List<TrackedEntityAttributeValue> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<>();
            List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes =
                    MetaDataController.getProgramTrackedEntityAttributes(program);
            for (ProgramTrackedEntityAttribute ptea : programTrackedEntityAttributes) {
                TrackedEntityAttributeValue v = TrackerController.getTrackedEntityAttributeValue
                        (ptea.trackedEntityAttribute, localTrackedEntityInstanceId);
                if (v != null && v.getValue() != null && !v.getValue().isEmpty()) {
                    attributes.add(v);
                }
            }
        }
        return attributes;
    }

    public void setAttributes(List<TrackedEntityAttributeValue> attributes) {
        this.attributes = attributes;
    }

    /**
     * gets a list of events for this enrollment
     *
     * @param reLoad true if you want to re-load from database. False if just use what's already
     *               loaded ( faster )
     * @return List of events.
     */
    public List<Event> getEvents(boolean reLoad) {
        if (events == null || reLoad) events = TrackerController.getEventsByEnrollment(localId);
        return events;
    }

    @Override
    public void save() {
        /* check if there is an existing enrollment with the same UID to avoid duplicates */
        Enrollment existingEnrollment = TrackerController.getEnrollment(enrollment);
        boolean exists = false;
        if (existingEnrollment != null) {
            exists = true;
            localId = existingEnrollment.localId;
        }
        if (getEnrollment() == null && TrackerController.getEnrollment(localId) != null) {
            //means that the enrollment is local and has previosuly been saved
            //then we don't want to update the enrollment reference in fear of overwriting
            //an updated reference from server while the item has been loaded in memory
            //unfortunately a bit of hard coding I suppose but it's important to verify data integrity
            updateManually();
        } else {
            //saving the enrollment first to get a autoincrement id from db
            super.save();
        }
        if (events != null) {
            for (Event event : events) {
                event.setLocalEnrollmentId(localId);
                event.save();
            }
        }

        if (attributes != null) {
            for (TrackedEntityAttributeValue value : attributes) {
                value.setLocalTrackedEntityInstanceId(localTrackedEntityInstanceId);
                value.save();
            }
        }
    }

    @Override
    public void update() {
        save();
    }

    /**
     * Updates manually without touching UIDs the fields that are modifiable by user.
     * This will and should only be called if the enrollment has a locally created temp event reference
     * and has previously been saved, so that it has a localId.
     */
    public void updateManually() {
        new Update<>(Enrollment.class).set(
                Condition.column(Enrollment$Table.STATUS).is(status),
                Condition.column(Enrollment$Table.FROMSERVER).is(fromServer),
                Condition.column(Enrollment$Table.FOLLOWUP).is(followup))
                .where(Condition.column(Enrollment$Table.LOCALID).is(localId)).queryClose();
    }

    @JsonIgnore
    public Program getProgram() {
        if (program == null) {
            return null;
        } else {
            return MetaDataController.getProgram(program);
        }
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    public long getLocalTrackedEntityInstanceId() {
        return localTrackedEntityInstanceId;
    }

    public void setLocalTrackedEntityInstanceId(long localTrackedEntityInstanceId) {
        this.localTrackedEntityInstanceId = localTrackedEntityInstanceId;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(String incidentDate) {
        this.incidentDate = incidentDate;
    }

    public boolean getFollowup() {
        return followup;
    }

    public void setFollowup(boolean followup) {
        this.followup = followup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    @JsonIgnore
    public String getUid() {
        return enrollment;
    }

    @Override
    @JsonIgnore
    public void setUid(String uid) {
        this.enrollment = uid;
    }

    public boolean equals(Enrollment enrollment) {
        if(enrollment == null) {
            return false;
        } else if(enrollmentDate == null && enrollment.getEnrollmentDate() != null) {
            return false;
        } else if(enrollmentDate != null && !enrollmentDate.equals(enrollment.getEnrollmentDate())) {
            return false;
        } else if(incidentDate == null && enrollment.getIncidentDate() != null) {
            return false;
        } else if(incidentDate != null && !incidentDate.equals(enrollment.getIncidentDate())) {
            return false;
        } else if(status == null && enrollment.getStatus() != null) {
            return false;
        } else if(status != null && !status.equals(enrollment.getStatus())) {
            return false;
        }
        return true;
    }

    public static class EnrollmentComparator implements Comparator<Enrollment> {

        @Override
        public int compare(Enrollment e1, Enrollment e2) {
            if(e1.getStatus().equals(CANCELLED) || e1.getStatus().equals(COMPLETED)) {
                if(e2.getStatus().equals(CANCELLED) || e1.getStatus().equals(COMPLETED)){
                    if(e1.getCreated()!=null){
                        return 0;
                    }else if (e2.getCreated()!=null){
                        return 1;
                    }
                }
                return 0;
            }
            if(e2.getStatus().equals(CANCELLED) || e1.getStatus().equals(COMPLETED)){
                return 1;
            }
            return 0;
        }
    }
}
