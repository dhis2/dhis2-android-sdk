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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventInternalAccessor
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.*
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerTableInfo

@Reusable
@Suppress("TooManyFunctions", "LongParameterList")
internal class OldTrackerImporterPayloadGenerator @Inject internal constructor(
    private val versionManager: DHISVersionManager,
    private val relationshipRepository: RelationshipCollectionRepository,
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val trackedEntityDataValueStore: TrackedEntityDataValueStore,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val noteStore: IdentifiableObjectStore<Note>,
    private val trackedEntityTypeStore: IdentifiableObjectStore<TrackedEntityType>,
    private val relationshipTypeStore: IdentifiableObjectStore<RelationshipType>,
    private val programStore: ProgramStoreInterface,
    private val programOwnerStore: ObjectWithoutUidStore<ProgramOwner>
) {

    private data class ExtraData(
        val dataValueMap: Map<String, List<TrackedEntityDataValue>>,
        val eventMap: Map<String, List<Event>>,
        val enrollmentMap: Map<String, List<Enrollment>>,
        val attributeValueMap: Map<String, List<TrackedEntityAttributeValue>>,
        val notes: List<Note>,
        val relationships: List<Relationship>
    )

    fun getTrackedEntityInstancePayload(
        trackedEntityInstances: List<TrackedEntityInstance>
    ): OldTrackerImporterPayload {
        val extraData = getExtraData()

        val recreatedTeis = trackedEntityInstances.map {
            getTrackedEntityInstance(it, extraData)
        }

        return generatePayload(
            payload = OldTrackerImporterPayload(trackedEntityInstances = recreatedTeis),
            extraData = extraData
        )
    }

    fun getEventPayload(
        events: List<Event>
    ): OldTrackerImporterPayload {
        val extraData = getExtraData()

        val recreatedEvents = events.map {
            getEvent(it, extraData)
        }

        return generatePayload(
            payload = OldTrackerImporterPayload(events = recreatedEvents),
            extraData = extraData
        )
    }

    private fun generatePayload(
        payload: OldTrackerImporterPayload,
        extraData: ExtraData
    ): OldTrackerImporterPayload {
        return payload
            .run { addRelationships(this, extraData) }
            .run { addProgramOwners(this) }
            .run { pruneNonWritableData(this) }
    }

    private fun addRelationships(
        payload: OldTrackerImporterPayload,
        extraData: ExtraData
    ): OldTrackerImporterPayload {
        val payloadWithRelationships = payload.copy(
            relationships = extractRelationships(payload, extraData)
        )

        return addRelatedItems(payloadWithRelationships, extraData)
    }

    private fun addRelatedItems(
        payload: OldTrackerImporterPayload,
        extraData: ExtraData
    ): OldTrackerImporterPayload {
        var accPayload = payload
        var relatedItems = getMissingItems(payload, extraData)

        while (relatedItems.isNotEmpty()) {
            accPayload = accPayload.concat(relatedItems)
            relatedItems = getMissingItems(accPayload, extraData)
        }

        return accPayload
    }

    @Suppress("NestedBlockDepth")
    private fun getMissingItems(
        payload: OldTrackerImporterPayload,
        extraData: ExtraData
    ): OldTrackerImporterPayload {
        val relatedItems = payload.relationships.flatMap { listOf(it.from(), it.to()) }

        val missingItems = relatedItems.filterNotNull().filter { item ->
            when {
                item.hasTrackedEntityInstance() -> isMissingTei(item.elementUid(), payload)
                item.hasEnrollment() -> isMissingEnrollment(item.elementUid(), payload)
                item.hasEvent() -> isMissingEvent(item.elementUid(), payload)
                else -> false
            }
        }

        val missingTeis = mutableSetOf<String>()
        val missingEvents = mutableSetOf<String>()

        missingItems.forEach { item ->
            when {
                item.hasTrackedEntityInstance() -> missingTeis.add(item.elementUid())
                item.hasEnrollment() -> {
                    enrollmentStore.selectByUid(item.elementUid())?.let {
                        missingTeis.add(it.trackedEntityInstance()!!)
                    }
                }
                item.hasEvent() -> {
                    eventStore.selectByUid(item.elementUid())?.let { event ->
                        if (event.enrollment() == null) {
                            missingEvents.add(event.uid())
                        } else {
                            enrollmentStore.selectByUid(event.enrollment()!!)?.let {
                                missingTeis.add(it.trackedEntityInstance()!!)
                            }
                        }
                    }
                }
            }
        }

        val trackedEntityInstances = trackedEntityInstanceStore.selectByUids(missingTeis.toList()).map {
            getTrackedEntityInstance(it, extraData)
        }
        val events = eventStore.selectByUids(missingEvents.toList()).map {
            getEvent(it, extraData)
        }

        return OldTrackerImporterPayload(
            trackedEntityInstances = trackedEntityInstances,
            events = events
        ).run {
            copy(relationships = extractRelationships(this, extraData))
        }
    }

    private fun isMissingTei(uid: String, payload: OldTrackerImporterPayload): Boolean {
        val isIncludeInPayload = payload.trackedEntityInstances.map { it.uid() }.contains(uid)
        val isPendingToSync: Boolean by lazy {
            val dbTei = trackedEntityInstanceStore.selectByUid(uid)
            State.uploadableStatesIncludingError().contains(dbTei?.aggregatedSyncState())
        }

        return !isIncludeInPayload && isPendingToSync
    }

    private fun isMissingEnrollment(uid: String, payload: OldTrackerImporterPayload): Boolean {
        val enrollments = payload.trackedEntityInstances.flatMap {
            TrackedEntityInstanceInternalAccessor.accessEnrollments(it) ?: emptyList()
        }
        val isIncludeInPayload = enrollments.map { it.uid() }.contains(uid)
        val isPendingToSync: Boolean by lazy {
            val enrollment = enrollmentStore.selectByUid(uid)
            State.uploadableStatesIncludingError().contains(enrollment?.aggregatedSyncState())
        }

        return !isIncludeInPayload && isPendingToSync
    }

    private fun isMissingEvent(uid: String, payload: OldTrackerImporterPayload): Boolean {
        val events = payload.trackedEntityInstances.flatMap {
            TrackedEntityInstanceInternalAccessor.accessEnrollments(it) ?: emptyList()
        }.flatMap {
            EnrollmentInternalAccessor.accessEvents(it) ?: emptyList()
        } + payload.events

        val isIncludedInPayload = events.map { it.uid() }.contains(uid)
        val isPendingToSync: Boolean by lazy {
            val event = eventStore.selectByUid(uid)
            State.uploadableStatesIncludingError().contains(event?.aggregatedSyncState())
        }

        return !isIncludedInPayload && isPendingToSync
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
            ),
            relationships = relationshipRepository.bySyncState()
                .`in`(State.uploadableStatesIncludingError().toList())
                .withItems()
                .blockingGet()
        )
    }

    private fun extractRelationships(
        payload: OldTrackerImporterPayload,
        extraData: ExtraData
    ): List<Relationship> {
        val teiItems = payload.trackedEntityInstances.map {
            RelationshipHelper.teiItem(it.uid())
        }

        val enrollments = payload.trackedEntityInstances.flatMap {
            TrackedEntityInstanceInternalAccessor.accessEnrollments(it) ?: emptyList()
        }

        val enrollmentItems = enrollments.map {
            RelationshipHelper.enrollmentItem(it.uid())
        }

        val events = enrollments.flatMap {
            EnrollmentInternalAccessor.accessEvents(it) ?: emptyList()
        } + payload.events

        val eventItems = events.map {
            RelationshipHelper.eventItem(it.uid())
        }

        val items = teiItems + enrollmentItems + eventItems

        return extraData.relationships.filter {
            items.any { item ->
                val bidirectional = it.relationshipType()?.let { type ->
                    relationshipTypeStore.selectByUid(type)?.bidirectional()
                } ?: false

                RelationshipHelper.areItemsEqual(it.from(), item) ||
                    bidirectional && RelationshipHelper.areItemsEqual(it.to(), item)
            }
        }
    }

    private fun getTrackedEntityInstance(
        trackedEntityInstance: TrackedEntityInstance,
        extraData: ExtraData
    ): TrackedEntityInstance {
        val enrollmentsRecreated = getEnrollments(extraData, trackedEntityInstance.uid())
        val attributeValues = extraData.attributeValueMap[trackedEntityInstance.uid()]
        return TrackedEntityInstanceInternalAccessor
            .insertEnrollments(trackedEntityInstance.toBuilder(), enrollmentsRecreated)
            .trackedEntityAttributeValues(attributeValues ?: emptyList())
            .build()
    }

    private fun getEnrollments(
        extraData: ExtraData,
        trackedEntityInstanceUid: String
    ): List<Enrollment> {
        return extraData.enrollmentMap[trackedEntityInstanceUid]?.map { enrollment ->
            val events = extraData.eventMap[enrollment.uid()]?.map { event ->
                getEvent(event, extraData)
            } ?: emptyList()

            EnrollmentInternalAccessor.insertEvents(enrollment.toBuilder(), events)
                .notes(getEnrollmentNotes(extraData.notes, enrollment))
                .build()
        } ?: emptyList()
    }

    private fun getEvent(event: Event, extraData: ExtraData): Event {
        val eventBuilder = event.toBuilder()
            .trackedEntityDataValues(extraData.dataValueMap[event.uid()])
            .notes(getEventNotes(extraData.notes, event))

        if (versionManager.is2_30) {
            eventBuilder.geometry(null)
        }

        return eventBuilder.build()
    }

    private fun getEventNotes(notes: List<Note>, event: Event): List<Note> {
        return notes.filter { it.event() == event.uid() }
    }

    private fun getEnrollmentNotes(notes: List<Note>, enrollment: Enrollment): List<Note> {
        return notes.filter { it.enrollment() == enrollment.uid() }
    }

    private fun addProgramOwners(payload: OldTrackerImporterPayload): OldTrackerImporterPayload {
        return if (payload.trackedEntityInstances.isNotEmpty()) {
            val programOwnerWhere = WhereClauseBuilder()
                .appendInKeyStringValues(
                    ProgramOwnerTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                    payload.trackedEntityInstances.map { it.uid() }
                )
                .appendInKeyEnumValues(
                    DataColumns.SYNC_STATE, State.uploadableStatesIncludingError().toList()
                )
                .build()

            val programOwnerToPost = programOwnerStore.selectWhere(programOwnerWhere)

            payload.copy(programOwners = programOwnerToPost.groupBy { it.trackedEntityInstance() })
        } else {
            payload
        }
    }

    private fun pruneNonWritableData(payload: OldTrackerImporterPayload): OldTrackerImporterPayload {
        val typeIds = payload.trackedEntityInstances.mapNotNull { it.trackedEntityType() }
        val programIds = payload.trackedEntityInstances.flatMap {
            TrackedEntityInstanceInternalAccessor.accessEnrollments(it).mapNotNull { e -> e.program() }
        }

        val typeAccessMap = typeIds.map { it to trackedEntityTypeStore.selectByUid(it)?.access() }.toMap()
        val programAccessMap = programIds.map { it to programStore.selectByUid(it)?.access() }.toMap()

        val pendingEvents = mutableListOf<Event>()
        val prunedTrackedEntityInstances = payload.trackedEntityInstances.mapNotNull { tei ->
            val enrollments = TrackedEntityInstanceInternalAccessor.accessEnrollments(tei)
            val hasDataWrite = typeAccessMap[tei.trackedEntityType()]?.data()?.write() == true &&
                enrollments.all { programAccessMap[it.program()]?.data()?.write() == true }

            if (hasDataWrite) {
                tei
            } else if (tei.syncState() == State.SYNCED && enrollments.all { it.syncState() == State.SYNCED }) {
                val events = enrollments
                    .flatMap { EnrollmentInternalAccessor.accessEvents(it) }
                    .mapNotNull { EventInternalAccessor.insertTrackedEntityInstance(it.toBuilder(), tei.uid()).build() }
                pendingEvents.addAll(events)
                null
            } else {
                // Sending TrackedEntityInstance although we know that the user has no write access.
                tei
            }
        }

        return payload.copy(
            trackedEntityInstances = prunedTrackedEntityInstances,
            events = payload.events + pendingEvents
        )
    }
}
