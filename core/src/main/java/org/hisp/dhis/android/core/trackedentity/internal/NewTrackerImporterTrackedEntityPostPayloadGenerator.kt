/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollmentTransformer
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.NewTrackerImporterEventTransformer
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.note.NewTrackerImporterNote
import org.hisp.dhis.android.core.note.NewTrackerImporterNoteTransformer
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationship
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationshipTransformer
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityAttributeValueTransformer
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityDataValueTransformer
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttribute

@Reusable
internal class NewTrackerImporterTrackedEntityPostPayloadGenerator @Inject internal constructor(
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val noteStore: IdentifiableObjectStore<Note>,
    private val relationshipRepository: RelationshipCollectionRepository,
    private val programTrackedEntityAttributeStore: IdentifiableObjectStore<ProgramTrackedEntityAttribute>,
    private val trackedEntityTypeAttributeStore: LinkStore<TrackedEntityTypeAttribute>
) {

    fun getTrackedEntities(
        filteredTrackedEntityInstances: List<TrackedEntityInstance>
    ): NewTrackerImporterPayloadWrapper {
        val dataValueMap = transformMap(
            trackedEntityDataValueStore.queryTrackerTrackedEntityDataValues(),
            NewTrackerImporterTrackedEntityDataValueTransformer()
        )
        val eventMap = transformMap(
            eventStore.queryEventsAttachedToEnrollmentToPost(),
            NewTrackerImporterEventTransformer()
        )
        val enrollmentMap = transformMap(
            enrollmentStore.queryEnrollmentsToPost(),
            NewTrackerImporterEnrollmentTransformer()
        )
        val attributeValueMap = transformMap(
            trackedEntityAttributeValueStore.queryTrackedEntityAttributeValueToPost(),
            NewTrackerImporterTrackedEntityAttributeValueTransformer()
        )
        val notes = getNotes()
        val relationships = getRelationships()
        val programAttributes = getProgramAttributesMap()
        val tetAttributes = getTrackedEntityTypeAttributeMap()

        return NewTrackerImporterTrackedEntityPostPayloadGeneratorTask(
            filteredTrackedEntityInstances,
            dataValueMap,
            eventMap,
            enrollmentMap,
            attributeValueMap,
            relationships,
            notes,
            programAttributes,
            tetAttributes
        ).generate()
    }

    private fun getNotes(): List<NewTrackerImporterNote> {
        val whereNotesClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                DataColumns.SYNC_STATE, State.uploadableStatesIncludingError().map { it.name }
            )
            .build()
        val notesTransformer = NewTrackerImporterNoteTransformer()
        return noteStore.selectWhere(whereNotesClause).map { notesTransformer.transform(it) }
    }

    private fun getRelationships(): Map<String, List<NewTrackerImporterRelationship>> {
        val relationships = relationshipRepository.bySyncState()
            .`in`(State.uploadableStatesIncludingError().toList())
            .withItems()
            .blockingGet()

        val relationshipsTransformer = NewTrackerImporterRelationshipTransformer()
        return relationships
            .filter { it.from()?.elementUid() != null }
            .groupBy({ it.from()?.elementUid()!! }, { relationshipsTransformer.transform(it) })
    }

    private fun getProgramAttributesMap(): Map<String, List<String>> {
        return programTrackedEntityAttributeStore.selectAll()
            .filter { it.program()?.uid() != null }
            .groupBy { it.program()?.uid()!! }
            .mapValues { it.value.mapNotNull { a -> a.trackedEntityAttribute()?.uid() } }
    }

    private fun getTrackedEntityTypeAttributeMap(): Map<String, List<String>> {
        return trackedEntityTypeAttributeStore.selectAll()
            .filter { it.trackedEntityType()?.uid() != null }
            .groupBy { it.trackedEntityType()?.uid()!! }
            .mapValues { it.value.mapNotNull { a -> a.trackedEntityAttribute()?.uid() } }
    }

    private fun <A, B> transformMap(
        map: Map<String, List<A>>,
        transformer: Transformer<A, B>
    ): Map<String, List<B>> {
        return map.mapValues { v ->
            v.value.map { transformer.transform(it) }
        }
    }
}
