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
package org.hisp.dhis.android.core.event.internal

import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.helpers.internal.DataStateHelper
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.imports.internal.EventWebResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.NoteTableInfo
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import java.util.ArrayList
import javax.inject.Inject

@Reusable
internal class EventPostCall @Inject internal constructor(
    private val versionManager: DHISVersionManager,
    private val eventService: EventService,
    private val eventStore: EventStore,
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val noteStore: IdentifiableObjectStore<Note>,
    private val apiCallExecutor: APICallExecutor,
    private val eventImportHandler: EventImportHandler
) {
    fun uploadEvents(filteredEvents: List<Event>?): Observable<D2Progress> {
        return Observable.defer {
            val eventsToPost = queryDataToSync(filteredEvents)
            markObjectsAs(eventsToPost, State.UPLOADING)

            // if there is nothing to send, return null
            if (eventsToPost.isEmpty()) {
                return@defer Observable.empty<D2Progress>()
            } else {
                val progressManager = D2ProgressManager(1)
                return@defer Observable.create { emitter: ObservableEmitter<D2Progress> ->
                    val eventPayload = EventPayload()
                    eventPayload.events = eventsToPost
                    val strategy = if (versionManager.is2_29) "CREATE_AND_UPDATE" else "SYNC"
                    try {
                        val webResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                            eventService.postEvents(eventPayload, strategy), listOf(409),
                            EventWebResponse::class.java
                        )
                        handleWebResponse(webResponse)
                        emitter.onNext(progressManager.increaseProgress(Event::class.java, true))
                        emitter.onComplete()
                    } catch (e: D2Error) {
                        markObjectsAs(eventsToPost, DataStateHelper.errorIfOnline(e))
                        throw e
                    }
                }
            }
        }
    }

    fun queryDataToSync(filteredEvents: List<Event>?): List<Event> {
        val dataValueMap = trackedEntityDataValueStore.querySingleEventsTrackedEntityDataValues()
        val events = filteredEvents ?: eventStore.querySingleEventsToPost()
        val notes = queryNotesToSync()
        val eventRecreated: MutableList<Event> = ArrayList()
        for (event in events) {
            val dataValuesForEvent = dataValueMap[event.uid()]
            val eventNotes = getEventNotes(notes, event.uid())
            val eventBuilder = event.toBuilder()
                .trackedEntityDataValues(dataValuesForEvent)
                .notes(eventNotes)
            if (versionManager.is2_30) {
                eventBuilder.geometry(null)
            }
            eventRecreated.add(eventBuilder.build())
        }
        return eventRecreated
    }

    private fun queryNotesToSync(): List<Note> {
        val whereNotesClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                DataColumns.STATE, EnumHelper.asStringList(*State.uploadableStatesIncludingError())
            )
            .appendKeyStringValue(NoteTableInfo.Columns.NOTE_TYPE, Note.NoteType.EVENT_NOTE)
            .build()
        return noteStore.selectWhere(whereNotesClause)
    }

    private fun getEventNotes(allNotes: List<Note>, eventUid: String): List<Note> {
        val eventNotes: MutableList<Note> = ArrayList()
        for (note in allNotes) {
            if (eventUid == note.event()) {
                eventNotes.add(note)
            }
        }
        return eventNotes
    }

    private fun handleWebResponse(webResponse: EventWebResponse?) {
        if (webResponse?.response() == null) {
            return
        }
        eventImportHandler.handleEventImportSummaries(
            webResponse.response()!!.importSummaries(),
            null,
            null
        )
    }

    private fun markObjectsAs(events: Collection<Event>, forcedState: State?) {
        for (e in events) {
            eventStore.setState(e.uid(), DataStateHelper.forcedOrOwn(e, forcedState))
        }
    }
}