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
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;

import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.models.common.Access;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.joda.time.DateTime;

import java.util.List;

@Table(database = DbDhis.class)
public final class EnrollmentFlow extends BaseModelFlow {
    public static final Mapper<Enrollment, EnrollmentFlow> MAPPER = new EnrollmentMapper();

    private final static String TRACKED_ENTITY_INSTANCE_KEY = "tei";

    @Column
    @Unique
    String enrollmentUid;

    @Column
    String orgUnit;

    @Column
    @ForeignKey(
            references = {
                    @ForeignKeyReference(
                            columnName = TRACKED_ENTITY_INSTANCE_KEY, columnType = String.class,
                            foreignKeyColumnName = "trackedEntityInstanceUid"),
            }, saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE
    )
    TrackedEntityInstanceFlow trackedEntityInstance;

    @Column
    String program;

    @Column
    DateTime dateOfEnrollment;

    @Column
    DateTime dateOfIncident;

    @Column
    boolean followup;

    @Column
    String status;

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

    List<EventFlow> events;

    List<TrackedEntityAttributeValueFlow> trackedEntityAttributeValues;

    public EnrollmentFlow() {
        // empty constructor
    }

    public String getEnrollmentUid() {
        return enrollmentUid;
    }

    public void setEnrollmentUid(String enrollmentUid) {
        this.enrollmentUid = enrollmentUid;
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    public TrackedEntityInstanceFlow getTrackedEntityInstance() {
        return trackedEntityInstance;
    }

    public void setTrackedEntityInstance(TrackedEntityInstanceFlow trackedEntityInstance) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<EventFlow> getEvents() {
        return events;
    }

    public void setEvents(List<EventFlow> events) {
        this.events = events;
    }

    public List<TrackedEntityAttributeValueFlow> getTrackedEntityAttributeValues() {
        return trackedEntityAttributeValues;
    }

    public void setTrackedEntityAttributeValues(
            List<TrackedEntityAttributeValueFlow> trackedEntityAttributeValues) {
        this.trackedEntityAttributeValues = trackedEntityAttributeValues;
    }

    private static class EnrollmentMapper extends AbsMapper<Enrollment, EnrollmentFlow> {

        @Override
        public EnrollmentFlow mapToDatabaseEntity(Enrollment enrollment) {
            if (enrollment == null) {
                return null;
            }

            EnrollmentFlow enrollmentFlow = new EnrollmentFlow();
            enrollmentFlow.setId(enrollment.getId());
            enrollmentFlow.setEnrollmentUid(enrollment.getUId());
            enrollmentFlow.setOrgUnit(enrollment.getOrgUnit());
            enrollmentFlow.setTrackedEntityInstance(TrackedEntityInstanceFlow.MAPPER
                    .mapToDatabaseEntity(enrollment.getTrackedEntityInstance()));
            enrollmentFlow.setProgram(enrollment.getProgram());
            enrollmentFlow.setDateOfEnrollment(enrollment.getDateOfEnrollment());
            enrollmentFlow.setDateOfIncident(enrollment.getDateOfIncident());
            enrollmentFlow.setFollowup(enrollment.isFollowup());
            enrollmentFlow.setStatus(enrollment.getStatus());
            enrollmentFlow.setName(enrollment.getName());
            enrollmentFlow.setDisplayName(enrollment.getDisplayName());
            enrollmentFlow.setCreated(enrollment.getCreated());
            enrollmentFlow.setLastUpdated(enrollment.getLastUpdated());
            enrollmentFlow.setAccess(enrollment.getAccess());
            return enrollmentFlow;
        }

        @Override
        public Enrollment mapToModel(EnrollmentFlow enrollmentFlow) {
            if (enrollmentFlow == null) {
                return null;
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setId(enrollmentFlow.getId());
            enrollment.setUId(enrollmentFlow.getEnrollmentUid());
            enrollment.setOrgUnit(enrollmentFlow.getOrgUnit());
            enrollment.setTrackedEntityInstance(TrackedEntityInstanceFlow.MAPPER
                    .mapToModel(enrollmentFlow.getTrackedEntityInstance()));
            enrollment.setProgram(enrollmentFlow.getProgram());
            enrollment.setDateOfEnrollment(enrollmentFlow.getDateOfEnrollment());
            enrollment.setDateOfIncident(enrollmentFlow.getDateOfIncident());
            enrollment.setFollowup(enrollmentFlow.isFollowup());
            enrollment.setStatus(enrollmentFlow.getStatus());
            enrollment.setName(enrollmentFlow.getName());
            enrollment.setDisplayName(enrollmentFlow.getDisplayName());
            enrollment.setCreated(enrollmentFlow.getCreated());
            enrollment.setLastUpdated(enrollmentFlow.getLastUpdated());
            enrollment.setAccess(enrollmentFlow.getAccess());
            return enrollment;
        }

        @Override
        public Class<Enrollment> getModelTypeClass() {
            return Enrollment.class;
        }

        @Override
        public Class<EnrollmentFlow> getDatabaseEntityTypeClass() {
            return EnrollmentFlow.class;
        }
    }
}
