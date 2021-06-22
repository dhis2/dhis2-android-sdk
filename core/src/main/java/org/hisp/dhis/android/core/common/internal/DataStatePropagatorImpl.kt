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
package org.hisp.dhis.android.core.common.internal

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.event.internal.EventStore
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import java.util.*
import javax.inject.Inject

@Reusable
internal class DataStatePropagatorImpl @Inject internal constructor(
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventStore: EventStore
) : DataStatePropagator {

    override fun propagateEnrollmentUpdate(enrollment: Enrollment?) {
        if (enrollment != null) {
            setTeiState(enrollment.trackedEntityInstance(), getStateForUpdate)
        }
    }

    override fun propagateEventUpdate(event: Event?) {
        if (event?.enrollment() != null) {
            val enrollment = setEnrollmentState(event.enrollment(), getStateForUpdate)
            propagateEnrollmentUpdate(enrollment)
        }
    }

    override fun propagateTrackedEntityDataValueUpdate(dataValue: TrackedEntityDataValue?) {
        val event = setEventState(dataValue!!.event(), getStateForUpdate)
        propagateEventUpdate(event)
    }

    override fun propagateTrackedEntityAttributeUpdate(trackedEntityAttributeValue: TrackedEntityAttributeValue?) {
        setTeiState(trackedEntityAttributeValue!!.trackedEntityInstance(), getStateForUpdate)
    }

    override fun propagateNoteCreation(note: Note?) {
        if (note!!.noteType() == Note.NoteType.ENROLLMENT_NOTE) {
            val enrollment = setEnrollmentState(note.enrollment(), getStateForUpdate)
            propagateEnrollmentUpdate(enrollment)
        } else if (note.noteType() == Note.NoteType.EVENT_NOTE) {
            val event = setEventState(note.event(), getStateForUpdate)
            propagateEventUpdate(event)
        }
    }

    override fun propagateRelationshipUpdate(item: RelationshipItem?) {
        if (item != null) {
            if (item.hasTrackedEntityInstance()) {
                setTeiState(item.trackedEntityInstance()!!.trackedEntityInstance(), getStateForUpdate)
            } else if (item.hasEnrollment()) {
                val enrollment = setEnrollmentState(item.enrollment()!!.enrollment(), getStateForUpdate)
                propagateEnrollmentUpdate(enrollment)
            } else if (item.hasEvent()) {
                val event = setEventState(item.event()!!.event(), getStateForUpdate)
                propagateEventUpdate(event)
            }
        }
    }

    private fun setTeiState(trackedEntityInstanceUid: String?, getState: (State?) -> State): TrackedEntityInstance? {
        var instance = trackedEntityInstanceStore.selectByUid(trackedEntityInstanceUid!!)
        if (instance != null) {
            val now = Date()
            val updatedTEI = instance.toBuilder()
                .state(getState(instance.state()))
                .lastUpdated(getMaxDate(instance.lastUpdated(), now))
                .lastUpdatedAtClient(getMaxDate(instance.lastUpdatedAtClient(), now))
                .build()
            trackedEntityInstanceStore.update(updatedTEI)
            instance = updatedTEI
        }
        return instance
    }

    private fun setEnrollmentState(enrollmentUid: String?, getState: (State?) -> State): Enrollment? {
        var enrollment = enrollmentStore.selectByUid(enrollmentUid!!)
        if (enrollment != null) {
            val now = Date()
            val updatedEnrollment = enrollment.toBuilder()
                .state(getState(enrollment.state()))
                .lastUpdated(getMaxDate(enrollment.lastUpdated(), now))
                .lastUpdatedAtClient(getMaxDate(enrollment.lastUpdatedAtClient(), now))
                .build()
            enrollmentStore.update(updatedEnrollment)
            enrollment = updatedEnrollment
        }
        return enrollment
    }

    private fun setEventState(eventUid: String?, getState: (State?) -> State): Event? {
        var event = eventStore.selectByUid(eventUid!!)
        if (event != null) {
            val now = Date()
            val updatedEvent = event.toBuilder()
                .syncState(getState(event.state()))
                .lastUpdated(getMaxDate(event.lastUpdated(), now))
                .lastUpdatedAtClient(getMaxDate(event.lastUpdatedAtClient(), now))
                .build()
            eventStore.update(updatedEvent)
            event = updatedEvent
        }
        return event
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

    override fun propagateEnrollmentError(enrollmentUid: String?, state: State?) {
        val enrollment = setEnrollmentState(enrollmentUid) {  state!! }
        propagateTrackedEntityInstanceError(enrollment?.trackedEntityInstance(), state)
    }

    override fun propagateTrackedEntityInstanceError(trackedEntityInstanceUid: String?, state: State?) {
        setTeiState(trackedEntityInstanceUid) {  state!! }
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
}