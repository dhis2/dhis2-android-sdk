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
package org.hisp.dhis.android.core.common.internal

import dagger.Reusable
import java.util.*
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.relationship.*
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemStore
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerTableInfo

@Reusable
@Suppress("TooManyFunctions")
internal class DataStatePropagatorImpl @Inject internal constructor(
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore,
    private val relationshipStore: RelationshipStore,
    private val relationshipItemStore: RelationshipItemStore,
    private val relationshipTypeStore: IdentifiableObjectStore<RelationshipType>,
    private val programOwner: ObjectWithoutUidStore<ProgramOwner>
) : DataStatePropagator {

    override fun propagateTrackedEntityInstanceUpdate(tei: TrackedEntityInstance?) {
        tei?.let {
            refreshTrackedEntityInstanceAggregatedSyncState(it.uid())
            refreshTrackedEntityInstanceLastUpdated(it.uid())
        }
    }

    override fun propagateEnrollmentUpdate(enrollment: Enrollment?) {
        enrollment?.let {
            refreshEnrollmentAggregatedSyncState(it.uid())
            refreshEnrollmentLastUpdated(it.uid())

            val tei = trackedEntityInstanceStore.selectByUid(it.trackedEntityInstance()!!)
            propagateTrackedEntityInstanceUpdate(tei)
        }
    }

    override fun propagateEventUpdate(event: Event?) {
        event?.let {
            refreshEventAggregatedSyncState(it.uid())
            refreshEventLastUpdated(it.uid())

            it.enrollment()?.let { enrollmentUid ->
                val enrollment = enrollmentStore.selectByUid(enrollmentUid)
                propagateEnrollmentUpdate(enrollment)
            }
        }
    }

    override fun propagateTrackedEntityDataValueUpdate(dataValue: TrackedEntityDataValue?) {
        setEventSyncState(dataValue!!.event()!!, getStateForUpdate)
    }

    override fun propagateTrackedEntityAttributeUpdate(trackedEntityAttributeValue: TrackedEntityAttributeValue?) {
        trackedEntityAttributeValue!!.trackedEntityInstance()?.let { trackedEntityInstanceUid ->
            val enrollments = enrollmentStore.selectByTrackedEntityInstanceAndAttribute(
                trackedEntityInstanceUid,
                trackedEntityAttributeValue.trackedEntityAttribute()!!
            )
            enrollments.forEach {
                enrollmentStore.setSyncState(it.uid(), getStateForUpdate(it.syncState()))
                refreshEnrollmentAggregatedSyncState(it.uid())
                refreshEnrollmentLastUpdated(it.uid())
            }
            setTeiSyncState(trackedEntityInstanceUid, getStateForUpdate)
        }
    }

    override fun propagateNoteCreation(note: Note?) {
        if (note!!.noteType() == Note.NoteType.ENROLLMENT_NOTE) {
            setEnrollmentSyncState(note.enrollment()!!, getStateForUpdate)
        } else if (note.noteType() == Note.NoteType.EVENT_NOTE) {
            setEventSyncState(note.event()!!, getStateForUpdate)
        }
    }

    override fun propagateRelationshipUpdate(relationship: Relationship?) {
        if (relationship != null) {
            val bidirectional = relationship.relationshipType()?.let {
                relationshipTypeStore.selectByUid(it)?.bidirectional()
            } ?: false

            propagateRelationshipUpdate(relationship.from())

            if (bidirectional) {
                propagateRelationshipUpdate(relationship.to())
            }
        }
    }

    override fun propagateOwnershipUpdate(programOwner: ProgramOwner) {
        programOwner.trackedEntityInstance()?.let {
            setTeiSyncState(it, getStateForUpdate)
        }
    }

    private fun propagateRelationshipUpdate(item: RelationshipItem?) {
        if (item != null) {
            if (item.hasTrackedEntityInstance()) {
                val tei = trackedEntityInstanceStore.selectByUid(item.elementUid())
                propagateTrackedEntityInstanceUpdate(tei)
            } else if (item.hasEnrollment()) {
                val enrollment = enrollmentStore.selectByUid(item.elementUid())
                propagateEnrollmentUpdate(enrollment)
            } else if (item.hasEvent()) {
                val event = eventStore.selectByUid(item.elementUid())
                propagateEventUpdate(event)
            }
        }
    }

    private fun setTeiSyncState(trackedEntityInstanceUid: String?, getState: (State?) -> State) {
        trackedEntityInstanceStore.selectByUid(trackedEntityInstanceUid!!)?.let { instance ->
            trackedEntityInstanceStore.setSyncState(trackedEntityInstanceUid, getState(instance.syncState()))
            propagateTrackedEntityInstanceUpdate(instance)
        }
    }

    private fun setEnrollmentSyncState(enrollmentUid: String, getState: (State?) -> State) {
        enrollmentStore.selectByUid(enrollmentUid)?.let { enrollment ->
            enrollmentStore.setSyncState(enrollmentUid, getState(enrollment.syncState()))
            propagateEnrollmentUpdate(enrollment)
        }
    }

    private fun setEventSyncState(eventUid: String, getState: (State?) -> State) {
        eventStore.selectByUid(eventUid)?.let { event ->
            eventStore.setSyncState(eventUid, getState(event.syncState()))
            propagateEventUpdate(event)
        }
    }

    private fun refreshEventLastUpdated(eventUid: String) {
        eventStore.selectByUid(eventUid)?.let { event ->
            val now = Date()
            val updatedEvent = event.toBuilder()
                .lastUpdated(getMaxDate(event.lastUpdated(), now))
                .lastUpdatedAtClient(getMaxDate(event.lastUpdatedAtClient(), now))
                .build()
            eventStore.update(updatedEvent)
        }
    }

    private fun refreshEnrollmentLastUpdated(enrollmentUid: String) {
        enrollmentStore.selectByUid(enrollmentUid)?.let { enrollment ->
            val now = Date()
            val updatedEnrollment = enrollment.toBuilder()
                .lastUpdated(getMaxDate(enrollment.lastUpdated(), now))
                .lastUpdatedAtClient(getMaxDate(enrollment.lastUpdatedAtClient(), now))
                .build()
            enrollmentStore.update(updatedEnrollment)
        }
    }

    private fun refreshTrackedEntityInstanceLastUpdated(trackedEntityInstanceUid: String) {
        trackedEntityInstanceStore.selectByUid(trackedEntityInstanceUid)?.let { instance ->
            val now = Date()
            val updatedInstance = instance.toBuilder()
                .lastUpdated(getMaxDate(instance.lastUpdated(), now))
                .lastUpdatedAtClient(getMaxDate(instance.lastUpdatedAtClient(), now))
                .build()
            trackedEntityInstanceStore.update(updatedInstance)
        }
    }

    override fun resetUploadingEnrollmentAndEventStates(trackedEntityInstanceUid: String?) {
        if (trackedEntityInstanceUid == null) {
            return
        }
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUid)
            .build()
        val enrollments = enrollmentStore.selectWhere(whereClause)
        for (enrollment in enrollments) {
            if (State.UPLOADING == enrollment.syncState()) {
                enrollmentStore.setSyncState(enrollment.uid(), State.TO_UPDATE)
                resetUploadingEventStates(enrollment.uid())
            }
        }
    }

    override fun resetUploadingEventStates(enrollmentUid: String?) {
        if (enrollmentUid == null) {
            return
        }
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(EventTableInfo.Columns.ENROLLMENT, enrollmentUid)
            .build()
        val events = eventStore.selectWhere(whereClause)
        for (event in events) {
            if (State.UPLOADING == event.syncState()) {
                eventStore.setSyncState(event.uid(), State.TO_UPDATE)
            }
        }
    }

    private fun getMaxDate(existing: Date?, today: Date?): Date? {
        return if (existing == null) {
            today
        } else if (today == null || existing.after(today)) {
            existing
        } else {
            today
        }
    }

    private val getStateForUpdate = { existingState: State? ->
        if (State.TO_POST == existingState || State.RELATIONSHIP == existingState) {
            existingState
        } else {
            State.TO_UPDATE
        }
    }

    override fun refreshTrackedEntityInstanceAggregatedSyncState(trackedEntityInstanceUid: String) {
        trackedEntityInstanceStore.selectByUid(trackedEntityInstanceUid)?.let { instance ->
            val whereClause = WhereClauseBuilder()
                .appendKeyStringValue(EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUid)
                .build()
            val enrollmentStates = enrollmentStore.selectAggregatedSyncStateWhere(whereClause)

            val relationships = getRelationshipsByItem(RelationshipHelper.teiItem(trackedEntityInstanceUid))
            val relationshipStates = relationships.map { it.syncState()!! }

            val programOwnerWhere = WhereClauseBuilder()
                .appendKeyStringValue(ProgramOwnerTableInfo.Columns.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUid)
                .build()
            val programOwnerStates = programOwner.selectWhere(programOwnerWhere).map { it.syncState()!! }

            val teiAggregatedSyncState = getAggregatedSyncState(
                enrollmentStates +
                    relationshipStates +
                    programOwnerStates +
                    instance.syncState()!!
            )

            trackedEntityInstanceStore.setAggregatedSyncState(trackedEntityInstanceUid, teiAggregatedSyncState)
        }
    }

    override fun refreshEnrollmentAggregatedSyncState(enrollmentUid: String) {
        enrollmentStore.selectByUid(enrollmentUid)?.let { enrollment ->
            val whereClause = WhereClauseBuilder()
                .appendKeyStringValue(EventTableInfo.Columns.ENROLLMENT, enrollmentUid)
                .build()
            val eventStates = eventStore.selectAggregatedSyncStateWhere(whereClause)

            val relationships = getRelationshipsByItem(RelationshipHelper.enrollmentItem(enrollmentUid))
            val relationshipStates = relationships.map { it.syncState()!! }

            val enrollmentAggregatedSyncState =
                getAggregatedSyncState(eventStates + relationshipStates + enrollment.syncState()!!)
            enrollmentStore.setAggregatedSyncState(enrollmentUid, enrollmentAggregatedSyncState)
        }
    }

    override fun refreshEventAggregatedSyncState(eventUid: String) {
        eventStore.selectByUid(eventUid)?.let { event ->
            val relationships = getRelationshipsByItem(RelationshipHelper.eventItem(eventUid))
            val relationshipStates = relationships.map { it.syncState()!! }

            val eventAggregatedSyncState = getAggregatedSyncState(relationshipStates + event.syncState()!!)
            eventStore.setAggregatedSyncState(eventUid, eventAggregatedSyncState)
        }
    }

    private fun getRelationshipsByItem(relationshipItem: RelationshipItem): List<Relationship> {
        val relationships = relationshipStore.getRelationshipsByItem(relationshipItem)

        return relationships.filter { relationship ->
            relationship.relationshipType()?.let { type ->
                val bidirectional = relationshipTypeStore.selectByUid(type)?.bidirectional() ?: false
                if (bidirectional) {
                    true
                } else {
                    val fromItem = relationshipItemStore
                        .getForRelationshipUidAndConstraintType(relationship.uid()!!, RelationshipConstraintType.FROM)
                    fromItem?.elementUid() == relationshipItem.elementUid()
                }
            } ?: false
        }
    }

    override fun refreshAggregatedSyncStates(uidHolder: DataStateUidHolder) {
        uidHolder.events.forEach {
            refreshEventAggregatedSyncState(it)
        }

        uidHolder.enrollments.forEach {
            refreshEnrollmentAggregatedSyncState(it)
        }

        uidHolder.trackedEntities.forEach {
            refreshTrackedEntityInstanceAggregatedSyncState(it)
        }
    }

    override fun getRelatedUids(
        trackedEntityInstanceUids: List<String>,
        enrollmentUids: List<String>,
        eventUids: List<String>,
        relationshipUids: List<String>
    ): DataStateUidHolder {
        val enrollmentsFromEvents = eventStore.selectByUids(eventUids).mapNotNull { it.enrollment() }

        val enrollments = enrollmentStore.selectByUids(enrollmentUids + enrollmentsFromEvents)

        val trackedEntitiesFromEnrollments = enrollments.mapNotNull { it.trackedEntityInstance() }

        val relationshipItems = relationshipUids.flatMap { relationshipItemStore.getForRelationshipUid(it) }

        return DataStateUidHolder(
            events = eventUids +
                relationshipItems.filter { it.hasEvent() }.map { it.elementUid() },
            enrollments = enrollmentUids +
                enrollmentsFromEvents +
                relationshipItems.filter { it.hasEnrollment() }.map { it.elementUid() },
            trackedEntities = trackedEntityInstanceUids +
                trackedEntitiesFromEnrollments +
                relationshipItems.filter { it.hasTrackedEntityInstance() }.map { it.elementUid() }
        )
    }

    private fun getAggregatedSyncState(states: List<State>): State {
        return when {
            states.contains(State.RELATIONSHIP) -> State.RELATIONSHIP
            states.contains(State.ERROR) -> State.ERROR
            states.contains(State.WARNING) -> State.WARNING
            states.contains(State.UPLOADING) ||
                states.contains(State.TO_POST) ||
                states.contains(State.TO_UPDATE) -> State.TO_UPDATE
            states.contains(State.SENT_VIA_SMS) -> State.SENT_VIA_SMS
            states.contains(State.SYNCED_VIA_SMS) -> State.SYNCED_VIA_SMS
            else -> State.SYNCED
        }
    }
}
