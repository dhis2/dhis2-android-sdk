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

package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.imports.EventWebResponse;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class EventPostCall implements Callable<WebResponse> {
    // retrofit service
    private final EventService eventService;

    // adapter and stores
    private final EventStore eventStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;

    private final APICallExecutor apiCallExecutor;

    @Inject
    EventPostCall(@NonNull EventService eventService,
                          @NonNull EventStore eventStore,
                          @NonNull TrackedEntityDataValueStore trackedEntityDataValueStore,
                          @NonNull APICallExecutor apiCallExecutor) {
        this.eventService = eventService;
        this.eventStore = eventStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public EventWebResponse call() throws Exception {
        List<Event> eventsToPost = queryEventsToPost();

        // if there is nothing to send, return null
        if (eventsToPost.isEmpty()) {
            return EventWebResponse.empty();
        }

        EventPayload eventPayload = new EventPayload();
        eventPayload.events = eventsToPost;

        String strategy = "CREATE_AND_UPDATE";

        EventWebResponse webResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                eventService.postEvents(eventPayload, strategy), Collections.singletonList(409),
                EventWebResponse.class);

        handleWebResponse(webResponse);
        return webResponse;
    }

    @NonNull
    private List<Event> queryEventsToPost() {
        Map<String, List<TrackedEntityDataValue>> dataValueMap =
                trackedEntityDataValueStore.querySingleEventsTrackedEntityDataValues();
        List<Event> events = eventStore.querySingleEventsToPost();
        int eventSize = events.size();

        List<Event> eventRecreated = new ArrayList<>(eventSize);

        for (int i = 0; i < eventSize; i++) {
            Event event = events.get(i);
            List<TrackedEntityDataValue> dataValuesForEvent = dataValueMap.get(event.uid());

            eventRecreated.add(event.toBuilder().trackedEntityDataValues(dataValuesForEvent).build());
        }

        return eventRecreated;
    }

    private void handleWebResponse(EventWebResponse webResponse) {
        if (webResponse == null || webResponse.response() == null) {
            return;
        }
        EventImportHandler eventImportHandler = new EventImportHandler(eventStore);
        eventImportHandler.handleEventImportSummaries(
                webResponse.response().importSummaries()
        );
    }
}
