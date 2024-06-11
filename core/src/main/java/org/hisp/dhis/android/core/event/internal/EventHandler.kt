/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.event.internal

import android.util.Log
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerImpl
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventInternalAccessor
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.internal.NoteDHISVersionManager
import org.hisp.dhis.android.core.note.internal.NoteHandler
import org.hisp.dhis.android.core.note.internal.NoteUniquenessManager
import org.hisp.dhis.android.core.program.ProgramType
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.relationship.internal.EventRelationshipOrphanCleaner
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueHandler
import org.koin.core.annotation.Singleton

@Singleton
internal class EventHandler(
    relationshipVersionManager: RelationshipDHISVersionManager,
    relationshipHandler: RelationshipHandler,
    eventStore: EventStore,
    private val programStore: ProgramStore,
    private val trackedEntityDataValueHandler: TrackedEntityDataValueHandler,
    private val noteHandler: NoteHandler,
    private val noteVersionManager: NoteDHISVersionManager,
    private val noteUniquenessManager: NoteUniquenessManager,
    private val relationshipOrphanCleaner: EventRelationshipOrphanCleaner,
) : IdentifiableDataHandlerImpl<Event>(eventStore, relationshipVersionManager, relationshipHandler) {

    private var programTypes = mutableMapOf<String, ProgramType?>()

    override fun beforeCollectionHandled(
        oCollection: Collection<Event>,
        params: IdentifiableDataHandlerParams,
    ): Collection<Event> {
        programStore.selectAll().forEach {
            programTypes[it.uid()] = it.programType()
        }
        return super.beforeCollectionHandled(oCollection, params)
    }

    override fun beforeObjectHandled(o: Event, params: IdentifiableDataHandlerParams): Event {
        val builder = o.toBuilder()

        if (programTypes[o.program()] == ProgramType.WITHOUT_REGISTRATION) {
            builder.enrollment(null)
        }

        if (!GeometryHelper.isValid(o.geometry())) {
            Log.i(this.javaClass.simpleName, "Event " + o.uid() + " has invalid geometry value")
            builder.geometry(null)
        }

        return builder.build()
    }

    override fun afterObjectHandled(
        o: Event,
        action: HandleAction?,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives?,
    ) {
        val eventUid = o.uid()
        if (action === HandleAction.Delete) {
            Log.d(this.javaClass.simpleName, "$eventUid with no org. unit, invalid eventDate or deleted")
        } else {
            if (o.trackedEntityDataValues() == null || o.trackedEntityDataValues()!!.isEmpty()) {
                trackedEntityDataValueHandler.removeEventDataValues(eventUid)
            } else {
                trackedEntityDataValueHandler.handleMany(
                    o.trackedEntityDataValues(),
                ) { dataValue: TrackedEntityDataValue -> dataValue.toBuilder().event(eventUid).build() }
            }

            o.notes()?.let { notes ->
                val transformed = notes.map { note ->
                    noteVersionManager.transform(Note.NoteType.EVENT_NOTE, o.uid(), note)
                }
                val notesToSync = noteUniquenessManager.buildUniqueCollection(
                    transformed,
                    Note.NoteType.EVENT_NOTE,
                    o.uid(),
                )
                noteHandler.handleMany(notesToSync)
            }

            val relationships = EventInternalAccessor.accessRelationships(o)
            if (relationships != null && !params.asRelationship) {
                handleRelationships(relationships, o, relatives)
                relationshipOrphanCleaner.deleteOrphan(o, relationships)
            }
        }
    }
    override fun afterCollectionHandled(oCollection: Collection<Event>?, params: IdentifiableDataHandlerParams) {
        programTypes.clear()
        super.afterCollectionHandled(oCollection, params)
    }

    override fun deleteIfCondition(o: Event): Boolean {
        val validEventDate = o.eventDate() != null ||
            o.status() == EventStatus.SCHEDULE ||
            o.status() == EventStatus.SKIPPED ||
            o.status() == EventStatus.OVERDUE

        return !validEventDate || o.organisationUnit() == null
    }

    override fun addRelationshipState(o: Event): Event {
        return o.toBuilder().aggregatedSyncState(State.RELATIONSHIP).syncState(State.RELATIONSHIP).build()
    }

    override fun addSyncedState(o: Event): Event {
        return o.toBuilder().aggregatedSyncState(State.SYNCED).syncState(State.SYNCED).build()
    }
}
