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
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidsList
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.note.internal.NoteToPostTransformer
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemStore
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor

@Reusable
@Suppress("LongParameterList")
internal class TrackedEntityInstancePostPayloadGenerator29 @Inject internal constructor(
    private val versionManager: DHISVersionManager,
    private val relationshipDHISVersionManager: RelationshipDHISVersionManager,
    private val relationshipRepository: RelationshipCollectionRepository,
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val relationshipItemStore: RelationshipItemStore,
    private val noteStore: IdentifiableObjectStore<Note>
) {

    private data class ExtraData(
        val dataValueMap: Map<String, List<TrackedEntityDataValue>>,
        val eventMap: Map<String, List<Event>>,
        val enrollmentMap: Map<String, List<Enrollment>>,
        val attributeValueMap: Map<String, List<TrackedEntityAttributeValue>>,
        val notes: List<Note>
    )

    fun getTrackedEntityInstancesPartitions29(
        filteredTrackedEntityInstances: List<TrackedEntityInstance>
    ): List<List<TrackedEntityInstance>> {
        val extraData = getExtraData()
        val trackedEntityInstancesToSync = getPagedTrackedEntityInstances(filteredTrackedEntityInstances)
        return trackedEntityInstancesToSync.map { partition ->
            partition.map { trackedEntityInstance ->
                getTrackedEntityInstance(trackedEntityInstance, extraData)
            }
        }
    }

    private fun getExtraData(): ExtraData {
        return ExtraData(
            dataValueMap = trackedEntityDataValueStore.queryByUploadableEvents(),
            eventMap = eventStore.queryEventsAttachedToEnrollmentToPost(),
            enrollmentMap = enrollmentStore.queryEnrollmentsToPost(),
            attributeValueMap = trackedEntityAttributeValueStore.queryTrackedEntityAttributeValueToPost(),
            notes = noteStore.selectWhere(
                WhereClauseBuilder()
                    .appendInKeyStringValues(
                        DataColumns.SYNC_STATE, State.uploadableStatesIncludingError().map { it.name }
                    )
                    .build()
            )
        )
    }

    private fun getPagedTrackedEntityInstances(
        filteredTrackedEntityInstances: List<TrackedEntityInstance>
    ): List<List<TrackedEntityInstance>> {
        val partitions = filteredTrackedEntityInstances.chunked(TrackedEntityInstanceService.DEFAULT_PAGE_SIZE)

        val includedUids: MutableSet<String> = mutableSetOf()
        val partitionsWithRelationships: MutableList<List<TrackedEntityInstance>> = ArrayList()
        for (partition in partitions) {
            val partitionWithoutDuplicates = partition.filterNot { o -> includedUids.contains(o.uid()) }
            val partitionWithRelationships =
                getTrackedEntityInstancesWithRelationships(partitionWithoutDuplicates.toMutableList(), includedUids)
            partitionsWithRelationships.add(partitionWithRelationships)
            includedUids.addAll(getUidsList(partitionWithRelationships))
        }
        return partitionsWithRelationships
    }

    private fun getTrackedEntityInstancesWithRelationships(
        filteredTrackedEntityInstances: MutableList<TrackedEntityInstance>,
        excludedUids: Set<String>
    ): List<TrackedEntityInstance> {
        val trackedEntityInstancesInDBToSync = trackedEntityInstanceStore.queryTrackedEntityInstancesToSync()
        val filteredUids: List<String> = filteredTrackedEntityInstances.map { it.uid() }
        val teiUidsToPost = trackedEntityInstanceStore.queryTrackedEntityInstancesToPost().map { it.uid() }
        val relatedTeisToPost: MutableList<String> = ArrayList()

        var internalRelatedTeis = filteredUids
        do {
            val relatedTeiUids = relationshipItemStore.getRelatedTeiUids(internalRelatedTeis)
            val filteredToPost = relatedTeiUids
                .filter { teiUidsToPost.contains(it) }
                .filter { !filteredUids.contains(it) }
                .filter { !relatedTeisToPost.contains(it) }
                .filter { !excludedUids.contains(it) }
            relatedTeisToPost.addAll(filteredToPost)
            internalRelatedTeis = filteredToPost
        } while (internalRelatedTeis.isNotEmpty())

        for (trackedEntityInstanceInDB in trackedEntityInstancesInDBToSync) {
            if (relatedTeisToPost.contains(trackedEntityInstanceInDB.uid())) {
                filteredTrackedEntityInstances.add(trackedEntityInstanceInDB)
            }
        }
        return filteredTrackedEntityInstances
    }

    private fun getTrackedEntityInstance(
        trackedEntityInstance: TrackedEntityInstance,
        extraData: ExtraData
    ): TrackedEntityInstance {
        val enrollmentsRecreated = getEnrollments(extraData, trackedEntityInstance.uid())
        val attributeValues = extraData.attributeValueMap[trackedEntityInstance.uid()]
        val dbRelationships =
            relationshipRepository.getByItem(RelationshipHelper.teiItem(trackedEntityInstance.uid()), true)
        val ownedRelationships =
            relationshipDHISVersionManager.getOwnedRelationships(dbRelationships, trackedEntityInstance.uid())
        val versionAwareRelationships =
            relationshipDHISVersionManager.to229Compatible(ownedRelationships, trackedEntityInstance.uid())
        return TrackedEntityInstanceInternalAccessor
            .insertEnrollments(
                TrackedEntityInstanceInternalAccessor
                    .insertRelationships(trackedEntityInstance.toBuilder(), versionAwareRelationships),
                enrollmentsRecreated
            )
            .trackedEntityAttributeValues(attributeValues ?: emptyList())
            .build()
    }

    private fun getEnrollments(
        extraData: ExtraData,
        trackedEntityInstanceUid: String
    ): List<Enrollment> {
        return extraData.enrollmentMap[trackedEntityInstanceUid]?.map { enrollment ->
            val transformer = NoteToPostTransformer(versionManager)
            val events = extraData.eventMap[enrollment.uid()]?.map { event ->
                getEvent(event, extraData, transformer)
            } ?: emptyList()

            EnrollmentInternalAccessor.insertEvents(enrollment.toBuilder(), events)
                .notes(getEnrollmentNotes(extraData.notes, enrollment, transformer))
                .build()
        } ?: emptyList()
    }

    private fun getEvent(event: Event, extraData: ExtraData, transformer: NoteToPostTransformer): Event {
        val eventBuilder = event.toBuilder()
            .trackedEntityDataValues(extraData.dataValueMap[event.uid()])
            .notes(getEventNotes(extraData.notes, event, transformer))

        return eventBuilder.build()
    }

    private fun getEventNotes(notes: List<Note>, event: Event, t: NoteToPostTransformer): List<Note> {
        return notes
            .filter { it.event() == event.uid() }
            .map { t.transform(it) }
    }

    private fun getEnrollmentNotes(notes: List<Note>, enrollment: Enrollment, t: NoteToPostTransformer): List<Note> {
        return notes
            .filter { it.enrollment() == enrollment.uid() }
            .map { t.transform(it) }
    }
}
