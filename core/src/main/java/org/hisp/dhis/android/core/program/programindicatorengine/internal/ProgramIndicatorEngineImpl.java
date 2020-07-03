/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.program.programindicatorengine.internal;


import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.constant.Constant;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventCollectionRepository;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository;
import org.hisp.dhis.android.core.program.programindicatorengine.ProgramIndicatorEngine;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class ProgramIndicatorEngineImpl implements ProgramIndicatorEngine {

    private final IdentifiableObjectStore<ProgramIndicator> programIndicatorStore;
    private final IdentifiableObjectStore<DataElement> dataElementStore;
    private final IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore;
    private final IdentifiableObjectStore<Constant> constantStore;
    private final EnrollmentStore enrollmentStore;
    private final EventCollectionRepository eventRepository;
    private final ProgramStageCollectionRepository programRepository;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    @Inject
    ProgramIndicatorEngineImpl(IdentifiableObjectStore<ProgramIndicator> programIndicatorStore,
                               IdentifiableObjectStore<DataElement> dataElementStore,
                               IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore,
                               EnrollmentStore enrollmentStore,
                               EventCollectionRepository eventRepository,
                               ProgramStageCollectionRepository programRepository,
                               TrackedEntityAttributeValueStore trackedEntityAttributeValueStore,
                               IdentifiableObjectStore<Constant> constantStore) {
        this.programIndicatorStore = programIndicatorStore;
        this.dataElementStore = dataElementStore;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
        this.enrollmentStore = enrollmentStore;
        this.eventRepository = eventRepository;
        this.programRepository = programRepository;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
        this.constantStore = constantStore;
    }

    public String getProgramIndicatorValue(String enrollmentUid, String eventUid, String programIndicatorUid) {
        ProgramIndicator programIndicator = programIndicatorStore.selectByUid(programIndicatorUid);

        if (programIndicator == null || enrollmentUid == null && eventUid == null) {
            return null;
        }

        ProgramIndicatorContext.Builder contextBuilder = ProgramIndicatorContext.builder()
                .programIndicator(programIndicator);

        if (enrollmentUid == null) {
            contextBuilder.events(getSingleEvent(eventUid));
        } else {
            Enrollment enrollment = enrollmentStore.selectByUid(enrollmentUid);
            contextBuilder
                    .enrollment(enrollment)
                    .attributeValues(getAttributeValues(enrollment.trackedEntityInstance()))
                    .events(getEvents(enrollment));
        }

        ProgramIndicatorExecutor executor = new ProgramIndicatorExecutor(
                getConstantMap(),
                contextBuilder.build(),
                dataElementStore,
                trackedEntityAttributeStore);

        return executor.getProgramIndicatorValue(programIndicator.expression());
    }

    private Map<String, Constant> getConstantMap() {
        List<Constant> constants = constantStore.selectAll();
        return UidsHelper.mapByUid(constants);
    }

    private Map<String, TrackedEntityAttributeValue> getAttributeValues(String teiUid) {
        Map<String, TrackedEntityAttributeValue> attributeToAttributeValues = new HashMap<>();
        List<TrackedEntityAttributeValue> trackedEntityAttributeValues =
                trackedEntityAttributeValueStore.queryByTrackedEntityInstance(teiUid);
        if (trackedEntityAttributeValues != null) {
            for (TrackedEntityAttributeValue value : trackedEntityAttributeValues) {
                attributeToAttributeValues.put(value.trackedEntityAttribute(), value);
            }
        }
        return attributeToAttributeValues;
    }

    private Map<String, List<Event>> getEvents(Enrollment enrollment) {
        List<String> programStageUids = programRepository.byProgramUid().eq(enrollment.program()).blockingGetUids();

        Map<String, List<Event>> eventMap = new HashMap<>();
        for (String programStageUid : programStageUids) {
            List<Event> programStageEvents = eventRepository
                    .byProgramStageUid().eq(programStageUid)
                    .byEnrollmentUid().eq(enrollment.uid())
                    .byDeleted().isFalse()
                    .orderByEventDate(RepositoryScope.OrderByDirection.ASC)
                    .orderByLastUpdated(RepositoryScope.OrderByDirection.ASC)
                    .withTrackedEntityDataValues()
                    .blockingGet();

            eventMap.put(programStageUid, programStageEvents);
        }
        return eventMap;
    }

    private Map<String, List<Event>> getSingleEvent(String eventUid) {
        Map<String, List<Event>> eventMap = new HashMap<>();

        Event event = eventRepository.uid(eventUid).blockingGet();

        if (event != null) {
            eventMap.put(event.programStage(), Collections.singletonList(event));
        }

        return eventMap;
    }

}