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

package org.hisp.dhis.android.core.trackedentity.internal;

import android.util.Log;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentCollectionRepository;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventCollectionRepository;
import org.hisp.dhis.android.core.event.internal.EventStore;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams;
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface;
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.settings.ProgramSetting;
import org.hisp.dhis.android.core.settings.ProgramSettings;
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class TrackedEntityInstanceTrimmer {

    private final TrackedEntityInstanceCollectionRepository trackedEntityInstanceCollectionRepository;
    private final EnrollmentCollectionRepository enrollmentCollectionRepository;
    private final EventCollectionRepository eventCollectionRepository;
    private final RelationshipCollectionRepository relationshipCollectionRepository;
    private final ProgramSettingsObjectRepository programSettingsRepository;

    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final EnrollmentStore enrollmentStore;
    private final EventStore eventStore;
    private final ProgramStoreInterface programStore;

    @Inject
    TrackedEntityInstanceTrimmer(TrackedEntityInstanceCollectionRepository trackedEntityInstanceCollectionRepository,
                                 EnrollmentCollectionRepository enrollmentCollectionRepository,
                                 EventCollectionRepository eventCollectionRepository,
                                 RelationshipCollectionRepository relationshipCollectionRepository,
                                 ProgramSettingsObjectRepository programSettingsRepository,
                                 TrackedEntityInstanceStore trackedEntityInstanceStore,
                                 EnrollmentStore enrollmentStore,
                                 EventStore eventStore,
                                 ProgramStoreInterface programStore) {
        this.trackedEntityInstanceCollectionRepository = trackedEntityInstanceCollectionRepository;
        this.enrollmentCollectionRepository = enrollmentCollectionRepository;
        this.eventCollectionRepository = eventCollectionRepository;
        this.relationshipCollectionRepository = relationshipCollectionRepository;
        this.programSettingsRepository = programSettingsRepository;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.enrollmentStore = enrollmentStore;
        this.eventStore = eventStore;
        this.programStore = programStore;
    }

    Unit trimTrackedEntityInstances(ProgramDataDownloadParams params) {
        ProgramSettings programSettings = programSettingsRepository.blockingGet();

        if (programSettings == null) {
            return new Unit();
        }

        List<String> trackerPrograms = programStore.getUidsByProgramType(ProgramType.WITH_REGISTRATION);
        if (params.program() == null) {
            for (String programUid : trackerPrograms) {
                trimProgram(programUid, programSettings);
            }

            trimGlobal(programSettings.globalSettings());
        } else if (params.program() != null && trackerPrograms.contains(params.program())) {
            trimProgram(params.program(), programSettings);
        }
        return new Unit();
    }

    private void trimProgram(String programUid, ProgramSettings programSettings) {
        ProgramSetting specificSetting = programSettings.specificSettings().get(programUid);
        if (specificSetting != null && specificSetting.teiDBTrimming() != null) {
            Integer teiDbTrimming = specificSetting.teiDBTrimming();

            List<TrackedEntityInstance> instances = trackedEntityInstanceCollectionRepository
                    .byProgramUids(Collections.singletonList(programUid))
                    .byState().neq(State.RELATIONSHIP)
                    .orderByLastUpdated(RepositoryScope.OrderByDirection.ASC)
                    .blockingGet();

            trimInstances(instances, teiDbTrimming);
        }
    }

    private void trimGlobal(ProgramSetting globalSetting) {
        if (globalSetting != null && globalSetting.teiDBTrimming() != null) {
            Integer teiDbTrimming = globalSetting.teiDBTrimming();

            List<TrackedEntityInstance> instances = trackedEntityInstanceCollectionRepository
                    .byState().neq(State.RELATIONSHIP)
                    .orderByLastUpdated(RepositoryScope.OrderByDirection.ASC)
                    .blockingGet();

            trimInstances(instances, teiDbTrimming);
        }
    }

    private void trimInstances(List<TrackedEntityInstance> instances, Integer limit) {
        int highEndpoint = instances.size() - limit;
        if (highEndpoint > 0) {
            List<TrackedEntityInstance> instancesToTrim = instances.subList(0, highEndpoint);

            for (TrackedEntityInstance instance : instancesToTrim) {
                if (instance.state() == State.SYNCED) {
                    processInstance(instance);
                }
            }
        }
    }

    private void processInstance(TrackedEntityInstance trackedEntityInstance) {
        List<Enrollment> enrollments = enrollmentCollectionRepository
                .byTrackedEntityInstance().eq(trackedEntityInstance.uid())
                .blockingGet();

        boolean hasEnrollmentRelationships = false;
        for (Enrollment enrollment : enrollments) {
            boolean isEnrollmentRelationship = processEnrollmentRelationships(enrollment);
            hasEnrollmentRelationships = hasEnrollmentRelationships || isEnrollmentRelationship;
        }

        RelationshipItem relationshipItem = RelationshipHelper.teiItem(trackedEntityInstance.uid());

        boolean hasTEIRelationships =
                !relationshipCollectionRepository.getByItem(relationshipItem, true).isEmpty();

        if (hasTEIRelationships || hasEnrollmentRelationships) {
            trackedEntityInstanceStore.setState(trackedEntityInstance.uid(), State.RELATIONSHIP);
            String message = String.format("TrackedEntityInstance %s has been marked as %s",
                    trackedEntityInstance.uid(), State.RELATIONSHIP);
            Log.w(this.getClass().getSimpleName(), message);
        } else {
            trackedEntityInstanceStore.delete(trackedEntityInstance.uid());
            String message = String.format("TrackedEntityInstance %s has been deleted.",
                    trackedEntityInstance.uid());
            Log.w(this.getClass().getSimpleName(), message);
        }

    }

    private boolean processEnrollmentRelationships(Enrollment enrollment) {
        List<Event> events = eventCollectionRepository
                .byEnrollmentUid().eq(enrollment.uid())
                .blockingGet();

        boolean hasEventRelationships = false;
        for (Event event : events) {
            boolean isEventRelationship = processEventRelationships(event);
            hasEventRelationships = hasEventRelationships || isEventRelationship;
        }

        // TODO Check enrollment relationships. If any, mark as RELATIONSHIP and return true. If none, delete it.
        enrollmentStore.delete(enrollment.uid());

        return hasEventRelationships;
    }

    private boolean processEventRelationships(Event event) {
        // TODO Check event relationships. If any, mark as RELATIONSHIP and return true. If none, delete it.
        eventStore.delete(event.uid());

        return false;
    }

}