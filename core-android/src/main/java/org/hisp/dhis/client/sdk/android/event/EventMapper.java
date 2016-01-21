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

package org.hisp.dhis.client.sdk.android.event;

import org.hisp.dhis.client.sdk.android.api.modules.MapperModule;
import org.hisp.dhis.client.sdk.android.api.utils.MapperModuleProvider;
import org.hisp.dhis.client.sdk.android.common.D2;
import org.hisp.dhis.client.sdk.android.common.base.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.Enrollment$Flow;
import org.hisp.dhis.client.sdk.android.flow.Event$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityDataValue$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityInstance$Flow;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

public class EventMapper extends AbsMapper<Event, Event$Flow> {

    @Override
    public Event$Flow mapToDatabaseEntity(Event event) {
        if (event == null) {
            return null;
        }

        Event$Flow eventFlow = new Event$Flow();
        eventFlow.setId(event.getId());
        eventFlow.setEventUid(event.getUId());
        eventFlow.setStatus(event.getStatus());
        eventFlow.setLatitude(event.getLatitude());
        eventFlow.setLongitude(event.getLongitude());
        eventFlow.setTrackedEntityInstance(MapperModuleProvider.getInstance().getTrackedEntityInstanceMapper().mapToDatabaseEntity(event.getTrackedEntityInstance()));
        eventFlow.setEnrollment(MapperModuleProvider.getInstance().getEnrollmentMapper().mapToDatabaseEntity(event.getEnrollment()));
        eventFlow.setProgramId(event.getProgramId());
        eventFlow.setProgramStageId(event.getProgramStageId());
        eventFlow.setOrganisationUnitId(event.getOrganisationUnitId());
        eventFlow.setEventDate(event.getEventDate());
        eventFlow.setDueDate(event.getDueDate());
        eventFlow.setTrackedEntityDataValues(MapperModuleProvider.getInstance().getTrackedEntityDataValueMapper().mapToDatabaseEntities(event.getTrackedEntityDataValues()));
        eventFlow.setName(event.getName());
        eventFlow.setDisplayName(event.getDisplayName());
        eventFlow.setCreated(event.getCreated());
        eventFlow.setLastUpdated(event.getLastUpdated());
        eventFlow.setAccess(event.getAccess());
        return eventFlow;
    }

    @Override
    public Event mapToModel(Event$Flow eventFlow) {
        if (eventFlow == null) {
            return null;
        }

        Event event = new Event();
        event.setId(eventFlow.getId());
        event.setUId(eventFlow.getEventUid());
        event.setStatus(eventFlow.getStatus());
        event.setLatitude(eventFlow.getLatitude());
        event.setLongitude(eventFlow.getLongitude());
        event.setTrackedEntityInstance(MapperModuleProvider.getInstance().getTrackedEntityInstanceMapper().mapToModel(eventFlow.getTrackedEntityInstance()));
        event.setEnrollment(MapperModuleProvider.getInstance().getEnrollmentMapper().mapToModel(eventFlow.getEnrollment()));
        event.setProgramId(eventFlow.getProgramId());
        event.setProgramStageId(eventFlow.getProgramStageId());
        event.setOrganisationUnitId(eventFlow.getOrganisationUnitId());
        event.setEventDate(eventFlow.getEventDate());
        event.setDueDate(eventFlow.getDueDate());
        event.setTrackedEntityDataValues(MapperModuleProvider.getInstance().getTrackedEntityDataValueMapper().mapToModels(eventFlow.getTrackedEntityDataValues()));
        event.setName(eventFlow.getName());
        event.setDisplayName(eventFlow.getDisplayName());
        event.setCreated(eventFlow.getCreated());
        event.setLastUpdated(eventFlow.getLastUpdated());
        event.setAccess(eventFlow.getAccess());
        return event;
    }

    @Override
    public Class<Event> getModelTypeClass() {
        return Event.class;
    }

    @Override
    public Class<Event$Flow> getDatabaseEntityTypeClass() {
        return Event$Flow.class;
    }
}
