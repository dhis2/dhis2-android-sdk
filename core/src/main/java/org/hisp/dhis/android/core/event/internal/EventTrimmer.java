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

package org.hisp.dhis.android.core.event.internal;

import android.util.Log;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventCollectionRepository;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.core.program.internal.ProgramDataDownloadParams;
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface;
import org.hisp.dhis.android.core.settings.ProgramSetting;
import org.hisp.dhis.android.core.settings.ProgramSettings;
import org.hisp.dhis.android.core.settings.ProgramSettingsObjectRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class EventTrimmer {

    private final EventCollectionRepository eventCollectionRepository;
    private final ProgramSettingsObjectRepository programSettingsRepository;

    private final EventStore eventStore;
    private final ProgramStoreInterface programStore;

    @Inject
    EventTrimmer(EventCollectionRepository eventCollectionRepository,
                 ProgramSettingsObjectRepository programSettingsRepository,
                 EventStore eventStore,
                 ProgramStoreInterface programStore) {
        this.eventCollectionRepository = eventCollectionRepository;
        this.programSettingsRepository = programSettingsRepository;
        this.eventStore = eventStore;
        this.programStore = programStore;
    }

    Unit trimEvents(ProgramDataDownloadParams params) {
        ProgramSettings programSettings = programSettingsRepository.blockingGet();

        if (programSettings == null) {
            return new Unit();
        }

        List<String> eventPrograms = programStore.getUidsByProgramType(ProgramType.WITHOUT_REGISTRATION);
        if (params.program() == null) {
            for (String programUid : eventPrograms) {
                trimProgram(programUid, programSettings);
            }

            trimGlobal(programSettings.globalSettings());
        } else if (params.program() != null && eventPrograms.contains(params.program())) {
            trimProgram(params.program(), programSettings);
        }
        return new Unit();
    }

    private void trimProgram(String programUid, ProgramSettings programSettings) {
        ProgramSetting specificSetting = programSettings.specificSettings().get(programUid);
        if (specificSetting != null && specificSetting.eventsDBTrimming() != null) {
            Integer eventsDBTrimming = specificSetting.eventsDBTrimming();

            List<Event> events = eventCollectionRepository
                    .byProgramUid().eq(programUid)
                    .byState().neq(State.RELATIONSHIP)
                    .orderByLastUpdated(RepositoryScope.OrderByDirection.ASC)
                    .blockingGet();

            trimEvents(events, eventsDBTrimming);
        }
    }

    private void trimGlobal(ProgramSetting globalSetting) {
        if (globalSetting != null && globalSetting.eventsDBTrimming() != null) {
            Integer eventsDBTrimming = globalSetting.eventsDBTrimming();

            List<Event> events = eventCollectionRepository
                    .byState().neq(State.RELATIONSHIP)
                    .orderByLastUpdated(RepositoryScope.OrderByDirection.ASC)
                    .blockingGet();

            trimEvents(events, eventsDBTrimming);
        }
    }

    private void trimEvents(List<Event> events, Integer limit) {
        int highEndpoint = events.size() - limit;
        if (highEndpoint > 0) {
            List<Event> eventsToTrim = events.subList(0, highEndpoint);

            for (Event event : eventsToTrim) {
                if (event.state() == State.SYNCED) {
                    processEvent(event);
                }
            }
        }
    }

    private void processEvent(Event event) {
        // TODO Check event relationships
        boolean hasEventRelationships = false;

        if (hasEventRelationships) {
            eventStore.setState(event.uid(), State.RELATIONSHIP);
            String message = String.format("Event %s has been marked as %s", event.uid(), State.RELATIONSHIP);
            Log.w(this.getClass().getSimpleName(), message);
        } else {
            eventStore.delete(event.uid());
            String message = String.format("Event %s has been deleted.", event.uid());
            Log.w(this.getClass().getSimpleName(), message);
        }
    }
}