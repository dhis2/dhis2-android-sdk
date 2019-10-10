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

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.hisp.dhis.android.core.imports.internal.EventWebResponse;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoModuleDownloader;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.Reusable;
import io.reactivex.Observable;

@Reusable
public final class EventPostCall {
    // retrofit service
    private final DHISVersionManager versionManager;
    private final EventService eventService;

    // adapter and stores
    private final EventStore eventStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final EventImportHandler eventImportHandler;

    private final APICallExecutor apiCallExecutor;
    private final SystemInfoModuleDownloader systemInfoDownloader;

    @Inject
    EventPostCall(@NonNull DHISVersionManager versionManager,
                  @NonNull EventService eventService,
                  @NonNull EventStore eventStore,
                  @NonNull TrackedEntityDataValueStore trackedEntityDataValueStore,
                  @NonNull APICallExecutor apiCallExecutor,
                  @NonNull EventImportHandler eventImportHandler,
                  @NonNull SystemInfoModuleDownloader systemInfoDownloader) {
        this.versionManager = versionManager;
        this.eventService = eventService;
        this.eventStore = eventStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.apiCallExecutor = apiCallExecutor;
        this.eventImportHandler = eventImportHandler;
        this.systemInfoDownloader = systemInfoDownloader;
    }

    public Observable<D2Progress> uploadEvents(List<Event> filteredEvents) {
        return Observable.defer(() -> {
            List<Event> eventsToPost = queryDataToSync(filteredEvents);

            // if there is nothing to send, return null
            if (eventsToPost.isEmpty()) {
                return Observable.empty();
            } else {
                D2ProgressManager progressManager = new D2ProgressManager(2);
                return systemInfoDownloader.downloadMetadata().andThen(Observable.create(emitter -> {

                    emitter.onNext(progressManager.increaseProgress(SystemInfo.class, false));

                    EventPayload eventPayload = new EventPayload();
                    eventPayload.events = eventsToPost;

                    String strategy = versionManager.is2_29() ? "CREATE_AND_UPDATE" : "SYNC";

                    EventWebResponse webResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                            eventService.postEvents(eventPayload, strategy), Collections.singletonList(409),
                            EventWebResponse.class);

                    handleWebResponse(webResponse);
                    emitter.onNext(progressManager.increaseProgress(Event.class, true));
                    emitter.onComplete();
                }));
            }
        });
    }

    @NonNull
    List<Event> queryDataToSync(List<Event> filteredEvents) {
        Map<String, List<TrackedEntityDataValue>> dataValueMap =
                trackedEntityDataValueStore.querySingleEventsTrackedEntityDataValues();

        List<Event> events = filteredEvents == null ? eventStore.querySingleEventsToPost() : filteredEvents;
        List<Event> eventRecreated = new ArrayList<>();

        for (Event event : events) {
            List<TrackedEntityDataValue> dataValuesForEvent = dataValueMap.get(event.uid());
            if (versionManager.is2_30()) {
                eventRecreated.add(event.toBuilder()
                        .trackedEntityDataValues(dataValuesForEvent)
                        .geometry(null)
                        .build());
            } else {
                eventRecreated.add(event.toBuilder().trackedEntityDataValues(dataValuesForEvent).build());
            }
        }

        markPartitionsAsUploading(eventRecreated);

        return eventRecreated;
    }

    private void handleWebResponse(EventWebResponse webResponse) {
        if (webResponse == null || webResponse.response() == null) {
            return;
        }
        eventImportHandler.handleEventImportSummaries(
                webResponse.response().importSummaries(),
                TrackerImportConflict.builder(),
                null,
                null
        );
    }

    private void markPartitionsAsUploading(List<Event> events) {
        List<String> eventUids = UidsHelper.getUidsList(events);
        eventStore.setState(eventUids, State.UPLOADING);
    }
}