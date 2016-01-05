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

package org.hisp.dhis.client.sdk.android.enrollment;

import org.hisp.dhis.client.sdk.android.common.base.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.Enrollment$Flow;
import org.hisp.dhis.client.sdk.android.flow.Event$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityAttributeValue$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityInstance$Flow;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

public class EnrollmentMapper extends AbsMapper<Enrollment, Enrollment$Flow> {

    private final IMapper<TrackedEntityInstance, TrackedEntityInstance$Flow> trackedEntityInstanceMapper;
    private final IMapper<Event, Event$Flow> eventMapper;
    private final IMapper<TrackedEntityAttributeValue, TrackedEntityAttributeValue$Flow> trackedEntityAttributeValueMapper;

    public EnrollmentMapper(IMapper<TrackedEntityInstance, TrackedEntityInstance$Flow> trackedEntityInstanceMapper, IMapper<Event, Event$Flow> eventMapper, IMapper<TrackedEntityAttributeValue, TrackedEntityAttributeValue$Flow> trackedEntityAttributeValueMapper) {
        this.trackedEntityInstanceMapper = trackedEntityInstanceMapper;
        this.eventMapper = eventMapper;
        this.trackedEntityAttributeValueMapper = trackedEntityAttributeValueMapper;
    }

    @Override
    public Enrollment$Flow mapToDatabaseEntity(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

        Enrollment$Flow enrollmentFlow = new Enrollment$Flow();
        enrollmentFlow.setId(enrollment.getId());
        enrollmentFlow.setEnrollmentUid(enrollment.getUId());
        enrollmentFlow.setOrgUnit(enrollment.getOrgUnit());
        enrollmentFlow.setTrackedEntityInstance(trackedEntityInstanceMapper.mapToDatabaseEntity(enrollment.getTrackedEntityInstance()));
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
        enrollmentFlow.setEvents(eventMapper.mapToDatabaseEntities(enrollment.getEvents()));
        enrollmentFlow.setTrackedEntityAttributeValues(trackedEntityAttributeValueMapper.mapToDatabaseEntities(enrollment.getTrackedEntityAttributeValues()));
        return enrollmentFlow;
    }

    @Override
    public Enrollment mapToModel(Enrollment$Flow enrollmentFlow) {
        if (enrollmentFlow == null) {
            return null;
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentFlow.getId());
        enrollment.setUId(enrollmentFlow.getEnrollmentUid());
        enrollment.setOrgUnit(enrollmentFlow.getOrgUnit());
        enrollment.setTrackedEntityInstance(trackedEntityInstanceMapper.mapToModel(enrollmentFlow.getTrackedEntityInstance()));
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
        enrollment.setEvents(eventMapper.mapToModels(enrollmentFlow.getEvents()));
        enrollment.setTrackedEntityAttributeValues(trackedEntityAttributeValueMapper.mapToModels(enrollmentFlow.getTrackedEntityAttributeValues()));
        return enrollment;
    }

    @Override
    public Class<Enrollment> getModelTypeClass() {
        return Enrollment.class;
    }

    @Override
    public Class<Enrollment$Flow> getDatabaseEntityTypeClass() {
        return Enrollment$Flow.class;
    }
}
