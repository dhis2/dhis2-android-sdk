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

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.runtime.DBTransactionInfo;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.BaseTransaction;
import com.raizlabs.android.dbflow.sql.Queriable;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Update;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Simen Skogly Russnes on 04.03.15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table
public class Enrollment extends BaseSerializableModel{

    private static final String CLASS_TAG = Enrollment.class.getSimpleName();

    public static final String ACTIVE = "ACTIVE";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED"; //aka TERMINATED

    public Enrollment() {

    }

    public Enrollment (String organisationUnit, String trackedEntityInstance, Program program) {
        orgUnit = organisationUnit;
        status = Enrollment.ACTIVE;
        enrollment = Dhis2.QUEUED + UUID.randomUUID().toString();
        followup = false;
        fromServer = false;
        this.program = program.getId();
        this.trackedEntityInstance = trackedEntityInstance;
        List<Event> events = new ArrayList<>();
        for(ProgramStage programStage: program.getProgramStages()) {
            if(programStage.autoGenerateEvent) {
                String status = Event.STATUS_FUTURE_VISIT;
                Event event = new Event(organisationUnit, status,
                        program.id, programStage.id,
                        trackedEntityInstance, enrollment);
                events.add(event);
            }
        }
        if(!events.isEmpty()) setEvents(events);
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {}

    @JsonIgnore
    @Column
    public boolean fromServer = true;

    @JsonIgnore
    @Column(columnType = Column.PRIMARY_KEY_AUTO_INCREMENT)
    public long localId = -1;

    @JsonIgnore
    @Column(unique = true)
    public String enrollment;

    @JsonProperty("enrollment")
    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    /**
     * Should only be used by Jackson so that event is included only if its non-local generated
     * Use Event.event instead to access it.
     */
    @JsonProperty("enrollment")
    public String getEnrollment() {
        String randomUUID = Dhis2.QUEUED + UUID.randomUUID().toString();
        if(enrollment.length() == randomUUID.length())
            return null;
        else return enrollment;
    }

    @JsonProperty("orgUnit")
    @Column
    public String orgUnit;

    @JsonIgnore
    @Column
    public String trackedEntityInstance;

    @JsonProperty("trackedEntityInstance")
    public void setTrackedEntityInstance(String trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    /**
     * Should only be used by Jackson so that event is included only if its non-local generated
     * Use Event.event instead to access it.
     */
    @JsonProperty("trackedEntityInstance")
    public String getTrackedEntityInstance() {
        String randomUUID = Dhis2.QUEUED + UUID.randomUUID().toString();
        if(trackedEntityInstance.length() == randomUUID.length())
            return null;
        else return trackedEntityInstance;
    }

    @JsonIgnore
    @Column
    public long localTrackedEntityInstanceId;

    @JsonProperty("program")
    @Column
    public String program;

    @JsonProperty("dateOfEnrollment")
    @Column
    public String dateOfEnrollment;

    @JsonProperty("dateOfIncident")
    @Column
    public String dateOfIncident;

    @JsonProperty("followup")
    @Column
    public boolean followup;

    @JsonProperty("status")
    @Column
    public String status;

    @JsonIgnore
    public List<TrackedEntityAttributeValue> attributes;

    @JsonProperty("attributes")
    public List<TrackedEntityAttributeValue> getAttributes() {

        if(attributes == null)
        {
            attributes = new ArrayList<>();

            List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes =
                    MetaDataController.getProgramTrackedEntityAttributes(program);
            for (ProgramTrackedEntityAttribute ptea : programTrackedEntityAttributes) {
                TrackedEntityAttributeValue v = DataValueController.getTrackedEntityAttributeValue
                        (ptea.trackedEntityAttribute, trackedEntityInstance);
                if (v != null && v.getValue() != null && !v.getValue().isEmpty()) {
                    attributes.add(v);
                }
            }
        }
        return attributes;
    }

    @JsonIgnore
    List<Event> events;

    /**
     * gets a list of events for this enrollment
     * @param reLoad true if you want to re-load from database. False if just use what's already
     *               loaded ( faster )
     * @return
     */
    public List<Event> getEvents(boolean reLoad) {
        if(events == null || reLoad) events = DataValueController.getEventsByEnrollment(localId);
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public void save(boolean async) {
        /* check if there is an existing enrollment with the same UID to avoid duplicates */
        Enrollment existingEnrollment = DataValueController.getEnrollment(enrollment);
        boolean exists = false;
        if(existingEnrollment != null) {
            exists = true;
            localId = existingEnrollment.localId;
        }
        if(getEnrollment() == null && DataValueController.getEnrollment(localId) != null) { //means that the enrollment is local and has previosuly been saved
            //then we don't want to update the enrollment reference in fear of overwriting
            //an updated reference from server while the item has been loaded in memory
            //unfortunately a bit of hard coding I suppose but it's important to verify data integrity
            updateManually(async);
        } else {
            super.save(async);
            boolean wait = true;
            if( localId < 0 ) { //workaround to wait for primary autoincrement key to be assigned with async=true
                while(wait) {
                    Enrollment tempEnrollment = DataValueController.getEnrollment(enrollment);
                    if(tempEnrollment==null) continue;
                    else {
                        localId = tempEnrollment.localId;
                        wait = false;
                    }
                    Thread.yield();
                }
            }
        }
        if(events!=null) {
            for(Event event: events) {
                event.localEnrollmentId = localId;
                event.save(async);
            }
        }

        if(attributes != null)
        {
            for(TrackedEntityAttributeValue value : attributes)
            {
                value.localTrackedEntityInstanceId = localTrackedEntityInstanceId;
                value.save(async);
                Log.d(CLASS_TAG, "VALUE: " + value.getValue() + "\n ID: " + value.localTrackedEntityInstanceId + "\n TEI ID:" + value.trackedEntityInstanceId);
            }
        }
    }

    /**
     * Updates manually without touching UIDs the fields that are modifiable by user.
     * This will and should only be called if the enrollment has a locally created temp event reference
     * and has previously been saved, so that it has a localId.
     */
    public void updateManually(boolean async) {
        Queriable q = new Update().table(Enrollment.class).set(
                Condition.column(Enrollment$Table.STATUS).is(status),
                Condition.column(Enrollment$Table.FROMSERVER).is(fromServer),
                Condition.column(Enrollment$Table.FOLLOWUP).is(followup))
                .where(Condition.column(Enrollment$Table.LOCALID).is(localId));
        if(async)
            TransactionManager.getInstance().transactQuery(DBTransactionInfo.create(BaseTransaction.PRIORITY_HIGH), q);
        else
            q.queryClose();
    }

    @JsonIgnore
    public Program getProgram() {
        if(program==null) return null;
        else return MetaDataController.getProgram(program);
    }

    @Override
    public void update(boolean async) {
        save(async);
    }

}
