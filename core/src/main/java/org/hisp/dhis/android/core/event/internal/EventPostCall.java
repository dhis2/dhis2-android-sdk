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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper;
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.imports.internal.EventWebResponse;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.note.NoteTableInfo;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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
    private final IdentifiableObjectStore<Note> noteStore;
    private final EventImportHandler eventImportHandler;

    private final APICallExecutor apiCallExecutor;

    @Inject
    EventPostCall(@NonNull DHISVersionManager versionManager,
                  @NonNull EventService eventService,
                  @NonNull EventStore eventStore,
                  @NonNull TrackedEntityDataValueStore trackedEntityDataValueStore,
                  @NonNull IdentifiableObjectStore<Note> noteStore,
                  @NonNull APICallExecutor apiCallExecutor,
                  @NonNull EventImportHandler eventImportHandler) {
        this.versionManager = versionManager;
        this.eventService = eventService;
        this.eventStore = eventStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.noteStore = noteStore;
        this.apiCallExecutor = apiCallExecutor;
        this.eventImportHandler = eventImportHandler;
    }

    public Observable<D2Progress> uploadEvents(List<Event> filteredEvents) {
        return Observable.defer(() -> {
            List<Event> eventsToPost = queryDataToSync(filteredEvents);

            // if there is nothing to send, return null
            if (eventsToPost.isEmpty()) {
                return Observable.empty();
            } else {
                D2ProgressManager progressManager = new D2ProgressManager(1);
                return Observable.create(emitter -> {
                    EventPayload eventPayload = new EventPayload();
                    eventPayload.events = eventsToPost;

                    String strategy = versionManager.is2_29() ? "CREATE_AND_UPDATE" : "SYNC";

                    try {
                        EventWebResponse webResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                                eventService.postEvents(eventPayload, strategy), Collections.singletonList(409),
                                EventWebResponse.class);

                        handleWebResponse(webResponse);
                        emitter.onNext(progressManager.increaseProgress(Event.class, true));
                        emitter.onComplete();
                    } catch (D2Error e) {
                        markObjectsAs(eventsToPost, DataStateHelper.errorIfOnline(e));
                        throw e;
                    }
                });
            }
        });
    }

    @NonNull
    List<Event> queryDataToSync(List<Event> filteredEvents) {
        Map<String, List<TrackedEntityDataValue>> dataValueMap =
                trackedEntityDataValueStore.querySingleEventsTrackedEntityDataValues();

        List<Event> events = filteredEvents == null ? eventStore.querySingleEventsToPost() : filteredEvents;
        List<Note> notes = queryNotesToSync();

        List<Event> eventRecreated = new ArrayList<>();
        for (Event event : events) {
            List<TrackedEntityDataValue> dataValuesForEvent = dataValueMap.get(event.uid());
            List<Note> eventNotes = getEventNotes(notes, event.uid());

            Event.Builder eventBuilder = event.toBuilder()
                    .trackedEntityDataValues(dataValuesForEvent)
                    .notes(eventNotes);
            if (versionManager.is2_30()) {
                eventBuilder.geometry(null);
            }
            eventRecreated.add(eventBuilder.build());
        }

        markObjectsAs(eventRecreated, State.UPLOADING);

        return eventRecreated;
    }

    private List<Note> queryNotesToSync() {
        String whereNotesClause = new WhereClauseBuilder()
                .appendInKeyStringValues(
                        DataColumns.STATE, EnumHelper.asStringList(State.uploadableStatesIncludingError()))
                .appendKeyStringValue(NoteTableInfo.Columns.NOTE_TYPE, Note.NoteType.EVENT_NOTE)
                .build();
        return noteStore.selectWhere(whereNotesClause);
    }

    private List<Note> getEventNotes(List<Note> allNotes, String eventUid) {
        List<Note> eventNotes = new ArrayList<>();
        for (Note note : allNotes) {
            if (eventUid.equals(note.event())) {
                eventNotes.add(note);
            }
        }
        return eventNotes;
    }

    private void handleWebResponse(EventWebResponse webResponse) {
        if (webResponse == null || webResponse.response() == null) {
            return;
        }
        eventImportHandler.handleEventImportSummaries(
                webResponse.response().importSummaries(),
                null,
                null
        );
    }

    private void markObjectsAs(Collection<Event> events, @Nullable State forcedState) {
        for (Event e: events) {
            eventStore.setState(e.uid(), DataStateHelper.forcedOrOwn(e, forcedState));
        }
    }
}