/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.enrollment.internal

import android.util.Log
import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner
import org.hisp.dhis.android.core.arch.handlers.internal.*
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandler
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerImpl
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.internal.NoteDHISVersionManager
import org.hisp.dhis.android.core.note.internal.NoteUniquenessManager
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives

@Reusable
internal class EnrollmentHandler @Inject constructor(
    relationshipVersionManager: RelationshipDHISVersionManager,
    relationshipHandler: RelationshipHandler,
    private val noteVersionManager: NoteDHISVersionManager,
    enrollmentStore: EnrollmentStore,
    private val eventHandler: IdentifiableDataHandler<Event>,
    private val eventOrphanCleaner: OrphanCleaner<Enrollment, Event>,
    private val noteHandler: Handler<Note>,
    private val noteUniquenessManager: NoteUniquenessManager,
    private val relationshipOrphanCleaner: OrphanCleaner<Enrollment, Relationship>
) : IdentifiableDataHandlerImpl<Enrollment>(enrollmentStore, relationshipVersionManager, relationshipHandler) {

    override fun addRelationshipState(o: Enrollment): Enrollment {
        return o.toBuilder().aggregatedSyncState(State.RELATIONSHIP).syncState(State.RELATIONSHIP).build()
    }

    override fun addSyncedState(o: Enrollment): Enrollment {
        return o.toBuilder().aggregatedSyncState(State.SYNCED).syncState(State.SYNCED).build()
    }

    override fun beforeObjectHandled(o: Enrollment, params: IdentifiableDataHandlerParams): Enrollment {
        return if (GeometryHelper.isValid(o.geometry())) {
            o
        } else {
            Log.i(
                this.javaClass.simpleName,
                "Enrollment " + o.uid() + " has invalid geometry value"
            )
            o.toBuilder().geometry(null).build()
        }
    }

    override fun afterObjectHandled(
        o: Enrollment,
        action: HandleAction?,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives?
    ) {
        if (action !== HandleAction.Delete) {
            val events = EnrollmentInternalAccessor.accessEvents(o)
            if (events != null) {
                val thisParams = IdentifiableDataHandlerParams(
                    hasAllAttributes = false,
                    overwrite = params.overwrite,
                    asRelationship = false
                )
                eventHandler.handleMany(events, thisParams, relatives)
                eventOrphanCleaner.deleteOrphan(o, events)
            }

            o.notes()?.let { notes ->
                val transformed = notes.map { note ->
                    noteVersionManager.transform(Note.NoteType.ENROLLMENT_NOTE, o.uid(), note)
                }
                val notesToSync = noteUniquenessManager.buildUniqueCollection(
                    transformed, Note.NoteType.ENROLLMENT_NOTE, o.uid()
                )
                noteHandler.handleMany(notesToSync)
            }

            val relationships = EnrollmentInternalAccessor.accessRelationships(o)
            if (relationships != null && !params.asRelationship) {
                handleRelationships(relationships, o, relatives)
                relationshipOrphanCleaner.deleteOrphan(o, relationships)
            }
        }
    }
}
